
package com.akwabasystems.asakusa.rest.service;

import com.akwabasystems.asakusa.dao.MembershipDao;
import com.akwabasystems.asakusa.dao.UserDao;
import com.akwabasystems.asakusa.dao.UserSessionDao;
import com.akwabasystems.asakusa.model.Gender;
import com.akwabasystems.asakusa.model.Membership;
import com.akwabasystems.asakusa.model.MembershipType;
import com.akwabasystems.asakusa.model.Role;
import com.akwabasystems.asakusa.model.User;
import com.akwabasystems.asakusa.model.UserPreferences;
import com.akwabasystems.asakusa.model.UserSession;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.akwabasystems.asakusa.rest.utils.AuthorizationTicket;
import com.akwabasystems.asakusa.rest.utils.ApplicationError;
import com.akwabasystems.asakusa.rest.utils.QueryParameter;
import com.akwabasystems.asakusa.utils.Timeline;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.extern.java.Log;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Log
public class UserService {

    @Autowired
    private CqlSession cqlSession;
    
    private RepositoryMapper mapper;
    
    private static final String DEFAULT_AVATAR = "https://s3-us-west-1.amazonaws.com/akwaba/assets/avatar-female.png";
    
    @PostConstruct
    public void initialize() {
        mapper = RepositoryMapper.builder(cqlSession).build();
    }
    
    
    /**
     * Creates a user account
     * 
     * @param authorizationTicket       the ticket used to authorize the request
     * @param accountDetails            an object with the details of the account to create
     * @return true if the user account is created
     * @throws Exception if the request fails
     */
    public boolean createAccount(AuthorizationTicket authorizationTicket,
                                 Map<String,Object> accountDetails) throws Exception {
        UserDao userDao = mapper.userDao();
        
        String userId = (String) accountDetails.get(QueryParameter.USER_ID);
        String email = (String) accountDetails.get(QueryParameter.EMAIL);
        
        User existingUser = userDao.findById(userId);

        if (existingUser != null) {
            throw new Exception(ApplicationError.USER_ALREADY_EXISTS);
        }

        Optional<User> existingUserByEmail = userDao.findByEmail(email);

        if (existingUserByEmail.isPresent()) {
            throw new Exception(ApplicationError.EMAIL_ALREADY_EXISTS);
        }
        
        String password = (String) accountDetails.get(QueryParameter.PASSWORD);
        String firstName = (String) accountDetails.get(QueryParameter.FIRST_NAME);
        String lastName = (String) accountDetails.get(QueryParameter.LAST_NAME);
        String gender = (String) accountDetails.get(QueryParameter.GENDER);
        String phoneNumber = (String) accountDetails.get(QueryParameter.PHONE_NUMBER);
        String locale = (String) accountDetails.get(QueryParameter.LOCALE);
        
        User user = new User(userId, firstName, lastName, email);
        user.setPreferredUsername(userId);
        user.setGender(Gender.fromString(gender));
        user.setPicture(DEFAULT_AVATAR);
        user.setPhoneNumberVerified(false);
        user.setEmailVerified(false);
        user.setLocale(locale);
        
        user.setPhoneNumber(phoneNumber);
        
        if (phoneNumber != null) {
            user.setPhoneNumberVerified(true);
        }
        
        /** Set the default roles for this user */
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);
        
        userDao.create(user, password.toCharArray(), roles);
        
        /** 
         * Starts a new session for the newly created user. This is necessary
         * since the iOS app redirects the user to the login view if he/she
         * doesn't have an active session.
         */
        String client = (String) accountDetails.get(QueryParameter.CLIENT);
        startNewSessionForUser(user, client);
        
        /** Add the default preferences for the user */
        addPreferencesForUser(userId, locale);
        
        /** Add the default membership details for the user */
        addMembershipForUser(user);
        
        return true;
    }
    

    private void startNewSessionForUser(User user, String client) {
        UserSessionDao sessionDao = mapper.userSessionDao();
        
        UserSession session = new UserSession(user.getUserId(), Uuids.timeBased());
        session.setClient(client);

        sessionDao.create(session);
    }
    
    
    private void addMembershipForUser(User user) {
        MembershipDao membershipDao = mapper.membershipDao();
        
        Membership membership = new Membership(user.getUserId(), 
                UUID.randomUUID(), MembershipType.FREE);
        membership.setCreatedDate(Timeline.currentDateTimeUTCString());
        membership.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        membershipDao.add(membership);
    }
    
    
    private void addPreferencesForUser(String userId, String language) {
        UserDao userDao = mapper.userDao();
        
        Map<String,Object> settings = new HashMap<>();
        settings.put("preferredLanguage", language);
        settings.put("timezone", "America/Los_Angeles");
        settings.put("acceptedTermsAndConditions", "false");

        Map<String,String> notifications = new HashMap<>();
        notifications.put("turnOffNotifications", "false");
        settings.put("notifications", notifications);

        UserPreferences preferences = new UserPreferences(userId, new JSONObject(settings));
        preferences.setLastModifiedDate(Timeline.currentDateTimeUTCString());

        userDao.savePreferences(preferences);
    }
    
    
    /**
     * Returns the details for a given user
     * 
     * @param authTicket        the ticket used to authorize the request
     * @return the details for a given user
     */
    public User getUserInfo(AuthorizationTicket authTicket) {
        UserDao userDao = mapper.userDao();
        return userDao.findById(authTicket.getUserId());
    }
    
    
}