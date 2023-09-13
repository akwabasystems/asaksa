
package com.akwabasystems.asakusa.rest.service;

import com.akwabasystems.asakusa.dao.MembershipDao;
import com.akwabasystems.asakusa.dao.UserDao;
import com.akwabasystems.asakusa.dao.UserSessionDao;
import com.akwabasystems.asakusa.model.Gender;
import com.akwabasystems.asakusa.model.ItemStatus;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.extern.java.Log;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Log
public class UserService {

    @Value("${security.akwaba.app-id}")
    protected String appId;
    
    @Value("${security.akwaba.app-secret}")
    protected String appKey;
    
    @Value("${security.akwaba.realm}")
    protected String appRealm;
    
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
     * @param accountDetails    an object with the details of the account to create
     * @param context           an object that contains the request context
     * @return true if the user account is created
     * @throws Exception if the request fails
     */
    public User createAccount(Map<String,Object> accountDetails, String context) 
            throws Exception {
        boolean isValidContext = verifyAuthContext(context);
        
        if (!isValidContext) {
            throw new Exception(ApplicationError.INVALID_CREDENTIALS);
        }
        
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
        
        return user;
    }
    

    private boolean verifyAuthContext(String context) throws Exception {
        /**
         * Extract the contents of the account creation context. This contents
         * is formatted as follows:
         *  appId:appKey:realm
         */
        String[] parts = context.split(":");
        
        if (parts.length < 3) {
            throw new Exception(ApplicationError.INVALID_PARAMETERS);
        }
        
        String clientAppId = parts[0];
        String clientAppKey = parts[1];
        String clientRealm = parts[2];
        
        /** 
         * Check that the authentication context has the correct parameters.
         * To do this, create a concatenated string of those parameters and 
         * compare it with the expected values.
         */
        String expectedContext = String.format("%s:%s:%s", 
            appId, appKey, appRealm);
        String receivedContext = String.format("%s:%s:%s", 
            clientAppId, clientAppKey, clientRealm);
        
        return expectedContext.equals(receivedContext);
        
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
     * Finds a user by ID
     * 
     * @param authTicket        the auth ticket that contains the ID of the user to find
     * @return the user with the ID specified in the request
     */
    public User findUserById(AuthorizationTicket authTicket) {
        UserDao userDao = mapper.userDao();
        return userDao.findById(authTicket.getUserId());
    }
    
    
    /**
     * Updates a user account
     * 
     * @param authorizationTicket       the ticket used to authorize the request
     * @param accountDetails            an object with the updated account details
     * @return true if the user account is updated
     * @throws Exception if the request fails
     */
    public boolean updateAccount(AuthorizationTicket authorizationTicket,
                                 Map<String,Object> accountDetails) throws Exception {
        UserDao userDao = mapper.userDao();
        User user = userDao.findById(authorizationTicket.getUserId());
        
        if (user == null) {
            throw new Exception(ApplicationError.USER_NOT_FOUND);
        }

        String firstName = (String) accountDetails.get(QueryParameter.FIRST_NAME);
        String lastName = (String) accountDetails.get(QueryParameter.LAST_NAME);
        String gender = (String) accountDetails.get(QueryParameter.GENDER);
        String locale = (String) accountDetails.get(QueryParameter.LOCALE);
        
        user.setGivenName(firstName);
        user.setFamilyName(lastName);
        user.setGender(Gender.fromString(gender));
        user.setLocale(locale);
        user.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        
        userDao.update(user);
        
        return true;
        
    }
    
    
    /**
     * Returns the preferences for the specified user
     * 
     * @param user      the user for whom to return the preferences
     * @return the preferences for the specified user
     */
    public UserPreferences getUserPreferences(User user) {
        UserDao userDao = mapper.userDao();
        return userDao.getPreferences(user.getUserId());
    }
    
    
    /**
     * Updates the preferences for the given user
     * 
     * @param user          the user for whom to update the preferences
     * @param settings      a serialized string of the preferences to set
     * @return the updated user preferences
     * @throws Exception if the operation fails
     */
    public UserPreferences updateUserPreferences(User user, LinkedHashMap settings) throws Exception {
        UserDao userDao = mapper.userDao();
        UserPreferences userPreferences = userDao.getPreferences(user.getUserId());
        JSONObject preferences = new JSONObject(settings);

        preferences.keySet().stream().forEach((String key) -> {
            userPreferences.getSettings().put(key, preferences.get(key));
        });
        
        userDao.savePreferences(userPreferences);
        
        return userPreferences;
    }
    
    
    
    public Map<String,Object> getAccountSummary(User user) throws Exception {
        Map<String,Object> accountSummary = new HashMap<>();
        accountSummary.put("followers", 0);
        accountSummary.put("following", 0);
        accountSummary.put("accountStatus", ItemStatus.ACTIVE.toString());
        
        return accountSummary;
    }
    
    
}
