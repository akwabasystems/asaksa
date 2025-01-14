
package com.akwabasystems.asakusa.rest.service;

import com.akwabasystems.asakusa.dao.AccessTokenDao;
import com.akwabasystems.asakusa.dao.PhoneVerificationDao;
import com.akwabasystems.asakusa.dao.UserDao;
import com.akwabasystems.asakusa.dao.UserSessionDao;
import com.akwabasystems.asakusa.model.AccessToken;
import com.akwabasystems.asakusa.model.ItemStatus;
import com.akwabasystems.asakusa.model.PhoneNumberVerification;
import com.akwabasystems.asakusa.model.User;
import com.akwabasystems.asakusa.model.UserCredentials;
import com.akwabasystems.asakusa.model.UserSession;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.akwabasystems.asakusa.rest.utils.ApplicationError;
import com.akwabasystems.asakusa.rest.utils.AuthorizationTicket;
import com.akwabasystems.asakusa.rest.utils.LoginResponse;
import com.akwabasystems.asakusa.rest.utils.UserResponse;
import com.akwabasystems.asakusa.utils.PasswordUtils;
import com.akwabasystems.asakusa.utils.Timeline;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import jakarta.annotation.PostConstruct;
import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;


@Service
@Log
public class AuthService {

    @Value("${security.akwaba.app-id}")
    protected String appId;
    
    @Value("${security.akwaba.app-secret}")
    protected String appKey;
    
    @Value("${security.akwaba.realm}")
    protected String appRealm;
    
    @Autowired
    private CqlSession cqlSession;
    
    @Autowired
    private SMSService smsService;
    
    @Autowired
    private UserService userService;
    
    private RepositoryMapper mapper;
    private String nonce;
    private static final String QOP = "auth";
    
    @PostConstruct
    public void initialize() {
        mapper = RepositoryMapper.builder(cqlSession).build();
        generateAuthenticationNonce();
    }
    
    
    /**
     * Generates the authentication nonce used for processing client login
     */
    private void generateAuthenticationNonce() {
        nonce = DigestUtils.md5DigestAsHex(UUID.randomUUID().toString().getBytes());
    }
    
    public Map<String,String> getAuthChallenge() {
        Map<String,String> challenge = new HashMap<>();
        challenge.put("appId", appId);
        challenge.put("appKey", appKey);
        challenge.put("realm", appRealm);
        challenge.put("nonce", nonce);
        challenge.put("qop", QOP);

        return challenge;
    }
    
    
    /**
     * Processes the user login with the specified credentials
     * 
     * @param authTicket        the authorization ticket for the request
     * @param context           an object that contains the login context
     * @param client            the client making the request
     * @return the user details on successful login
     * @throws Exception if the operation fails
     */
    public LoginResponse login(AuthorizationTicket authTicket,
                               String context,
                               String client) throws Exception {
        UserDao userDao = mapper.userDao();
        
        /**
         * Step 1: Extract the contents of the login context. This contents
         * is formatted as follows:
         *  appId:appKey:realm:nonce:password
         */
        String[] parts = context.split(":");
        
        if (parts.length != 5) {
            throw new Exception(ApplicationError.INVALID_PARAMETERS);
        }
        
        String clientAppId = parts[0];
        String clientAppKey = parts[1];
        String clientRealm = parts[2];
        String clientNonce = parts[3];
        String userPassword = parts[4];
        
        /** 
         * Step 2: Check that the authentication context has the correct parameters.
         * To do this, create a concatenated string of those parameters, along
         * with the user ID, and compare it with the expected values.
         */
        String expectedContext = String.format("%s:%s:%s:%s:%s", 
            authTicket.getUserId(), appId, appKey, appRealm, nonce);
        String receivedContext = String.format("%s:%s:%s:%s:%s", 
            authTicket.getUserId(), clientAppId, clientAppKey,
                clientRealm, clientNonce);
        boolean isCorrectContext = expectedContext.equals(receivedContext);
        
        if (!isCorrectContext) {
            throw new Exception(ApplicationError.INVALID_CREDENTIALS);
        }
        
        /** Step 3: Verify that the user exists */
        User user = userDao.findById(authTicket.getUserId());
        
        if (user == null) {
            throw new Exception(ApplicationError.USER_NOT_FOUND);
        }
        
        /** Step 4: Validate the user's password */
        UserCredentials credentials = userDao.getCredentials(user.getUserId());
        boolean isValidCredentials = PasswordUtils.matches(userPassword.toCharArray(), credentials.getPassword());
        
        if (!isValidCredentials) {
            throw new Exception(ApplicationError.INVALID_CREDENTIALS);
        }
        
        /** Step 5: Start a new session for the user */
        startNewSessionForUser(user, client);
        
        /** Step 6: Create a new access token for the user */
        AccessToken accessToken = createAccessToken(client);
        
        Map<String,Object> accountSummary = userService.getAccountSummary(user);
        Map<String,Object> accountSettings = userService.getUserPreferences(user).getSettings().toMap();
        UserResponse userInfo = UserResponse.fromUser(user);
        
        return new LoginResponse(userInfo, accountSummary, accountSettings, accessToken);
        
    }
    
    
    public void startNewSessionForUser(AuthorizationTicket authTicket, String client) throws Exception {
        UserDao userDao = mapper.userDao();
        User user = userDao.findById(authTicket.getUserId());
        
        if (user == null) {
            throw new Exception(ApplicationError.USER_NOT_FOUND);
        }
        
        startNewSessionForUser(user, client);
    }
    
    
    public void startNewSessionForUser(User user, String client) throws Exception {
        UserSessionDao sessionDao = mapper.userSessionDao();
        UserSession lastSession = getLastSessionForUser(user);
        
        /**
         * If the last session has been active for more than 8 hours, end it and 
         * start a new one
         */
        Instant currentTimeUTC = Instant.now(Clock.systemUTC());
        boolean hasExpired = (lastSession != null && 
                (lastSession.getStatus() == ItemStatus.EXPIRED || 
                 lastSession.getStatus() == ItemStatus.INACTIVE ||
                 lastSession.getStartDate().plus(8, ChronoUnit.HOURS).isBefore(currentTimeUTC)));
        
        if (hasExpired) {
            endSession(lastSession);
            
            UserSession newSession = new UserSession(user.getUserId(), Uuids.timeBased());
            newSession.setClient(client);
            sessionDao.create(newSession);
        }

    }
    
    
    public UserSession getLastSessionForUser(User user) {
        UserSessionDao sessionDao = mapper.userSessionDao();
        
        PagingIterable<UserSession> userSessions = sessionDao.findAll(user.getUserId());
        List<UserSession> userSessionList = userSessions.all();
        
        return userSessionList.isEmpty() ? null : userSessionList.get(0);
    }
    
    
    private void endSession(UserSession session) throws Exception {
        UserSessionDao sessionDao = mapper.userSessionDao();
            
        session.setEndDate(Instant.now(Clock.systemUTC()));
        session.setStatus(ItemStatus.EXPIRED);
        sessionDao.save(session);
    }
    
    
    public AccessToken createAccessToken(String clientId) throws Exception {
        AccessTokenDao accessTokenDao = mapper.accessTokenDao();
        
        AccessToken accessToken = new AccessToken(clientId, UUID.randomUUID().toString());
        accessToken.setTokenKey(UUID.randomUUID().toString());
        accessToken.setCreatedDate(Timeline.currentDateTimeUTCString());
        accessToken.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        
        accessTokenDao.create(accessToken);
        
        return accessToken;
    }
    
    
    public AccessToken getAccessTokenForClient(String clientId) {
        return mapper.accessTokenDao().findById(clientId);
    }
    
    
    private void deactivateAccessToken(AccessToken accessToken) throws Exception {
        accessToken.setStatus(ItemStatus.EXPIRED);
        accessToken.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        mapper.accessTokenDao().save(accessToken);
    }
    
    
    /**
     * Sends a verification code to the specified phone number
     * 
     * @param phoneNumber   the phone number to send the verification code to
     * @param context       an object that contains the request context
     * @param language      the user's preferred language
     * @return the phone verification details
     * @throws Exception if the operation fails
     */
    public PhoneNumberVerification sendPhoneNumberVerificationCode(String phoneNumber, 
                                                                   String context,
                                                                   String language) 
        throws Exception {
        
        /**
         * Step 1: Extract the contents of the verification context. This contents
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
        Locale preferredLocale = new Locale(language);

        /** 
         * Step 2: Check that the context has the correct parameters.
         * To do this, create a concatenated string of those parameters and 
         * compare it with the expected values.
         */
        String expectedContext = String.format("%s:%s:%s", 
                appId, appKey, appRealm);
        String receivedContext = String.format("%s:%s:%s", 
            clientAppId, clientAppKey,clientRealm);
        boolean isCorrectContext = expectedContext.equals(receivedContext);
        
        if (!isCorrectContext) {
            throw new Exception(ApplicationError.INVALID_CREDENTIALS);
        }
        
        /**
         * Step 3: Generate the code for the given phone number, store it, and
         * send an SMS notification to the device.  
         */
        PhoneVerificationDao phoneVerificationDao = mapper.phoneVerificationDao();
        
        PhoneNumberVerification phoneVerification = new PhoneNumberVerification(
                phoneNumber, smsService.generateSMSCode(6), UUID.randomUUID());
        phoneVerificationDao.addVerificationCode(phoneVerification);
        
        String body = smsService.generateBodyForPhoneVerificationCode(
                preferredLocale, phoneVerification.getCode());
        smsService.sendMessageToPhone(phoneNumber, body);
        
        return phoneVerification;
        
    }
    
    
    /**
     * Verifies the given phone verification code
     * 
     * @param phoneNumber   the phone number for which to verify the code
     * @param code          the code to verify
     * @param context       an object that contains the request context
     * @return the code verification status
     * @throws Exception if the operation fails
     */
    public Map<String,Object> verifyPhoneCode(String phoneNumber, 
                                              String code,
                                              String context) throws Exception {
        
        /**
         * Step 1: Extract the contents of the verification context. This contents
         * is formatted as follows:
         *  appId:appKey:realm
         */
        validateRequestContext(context);
        
        String[] parts = context.split(":");
        
        String clientAppId = parts[0];
        String clientAppKey = parts[1];
        String clientRealm = parts[2];

        String expectedContext = String.format("%s:%s:%s", 
                appId, appKey, appRealm);
        String receivedContext = String.format("%s:%s:%s", 
            clientAppId, clientAppKey,clientRealm);
        boolean isCorrectContext = expectedContext.equals(receivedContext);
        
        if (!isCorrectContext) {
            throw new Exception(ApplicationError.INVALID_CREDENTIALS);
        }
        
        PhoneVerificationDao phoneVerificationDao = mapper.phoneVerificationDao();
        PhoneNumberVerification phoneVerification = phoneVerificationDao.findByPhoneNumber(phoneNumber);
        
        if (phoneVerification == null) {
            throw new Exception(ApplicationError.INVALID_PARAMETERS);
        }
        
        ZonedDateTime currentTimeUTC = ZonedDateTime.now(Timeline.timezoneUTC());
        ZonedDateTime expirationTimeUTC = Timeline.fromUTCFormat(phoneVerification.getExpirationDate());
        boolean hasExpired = currentTimeUTC.isAfter(expirationTimeUTC);
        boolean isVerified = hasExpired ? false : phoneVerification.getCode().equals(code);
        
        Map<String,Object> status = new HashMap<>();
        status.put("activeStatus", hasExpired ? ItemStatus.EXPIRED.toString() : 
                ItemStatus.ACTIVE.toString());
        status.put("verificationStatus", isVerified ? ItemStatus.VERIFIED.toString() : 
                ItemStatus.UNVERIFIED.toString());
        
        return status;
        
    }
    
    
    /**
     * Ends the session for the user with the specified credentials
     * 
     * @param authTicket        the authorization ticket for the request
     * @param client            the client making the request
     * @return the outcome of the logout operation
     * @throws Exception if the operation fails
     */
    public Map<String,Object> logout(AuthorizationTicket authTicket, String client) throws Exception {
        UserDao userDao = mapper.userDao();
        
        User user = userDao.findById(authTicket.getUserId());
        
        if (user == null) {
            throw new Exception(ApplicationError.USER_NOT_FOUND);
        }
        
        /** End the user's session */
        UserSession lastSession = getLastSessionForUser(user);
        boolean isActiveSession = (lastSession != null && lastSession.getStatus() == ItemStatus.ACTIVE);

        if (isActiveSession) {
            endSession(lastSession);
        }
        
        /** deactivate the access token for the user */
        deactivateAccessToken(getAccessTokenForClient(client));
        
        Map<String,Object> response = new HashMap<>();
        response.put("authenticated", false);
        return response;
    }
    
    
    /**
     * Renews an access token for a client
     * 
     * @param authTicket    the authorization ticket for the request
     * @param token         the current access token for the user
     * @param context       an object that contains the request context
     * @return a new access token for the user
     * @throws Exception if the operation fails
     */
    public AccessToken renewAccessToken(AuthorizationTicket authTicket, 
                                        String client,
                                        String token,
                                        String context) throws Exception {
        // validateRequestContext(context);
        
        UserDao userDao = mapper.userDao();
        User user = userDao.findById(authTicket.getUserId());
        
        if (user == null) {
            throw new Exception(ApplicationError.USER_NOT_FOUND);
        }
        
        /**
         * Verify the access token to renew is actually the user's last access
         * token; otherwise, throw an exception
         */
        AccessToken currentAccessToken = getAccessTokenForClient(client);
        boolean isValidToken = (currentAccessToken != null && currentAccessToken.getTokenKey().equals(token));
        
        if (!isValidToken) {
            throw new Exception(ApplicationError.INVALID_PARAMETERS);
        }
        
        return createAccessToken(client);
    }
    
    
    private void validateRequestContext(String context) throws Exception {
        /**
         * Extract the contents of the request context. This contents is formatted 
         * as follows:
         *  appId:appKey:realm
         * 
         * If the format is incorrect, throw an exception
         */
        String[] parts = context.split(":");
        
        if (parts.length < 3) {
            throw new Exception(ApplicationError.INVALID_PARAMETERS);
        }
        
        String clientAppId = parts[0];
        String clientAppKey = parts[1];
        String clientRealm = parts[2];

        String expectedContext = String.format("%s:%s:%s", 
                appId, appKey, appRealm);
        String receivedContext = String.format("%s:%s:%s", 
            clientAppId, clientAppKey,clientRealm);
        boolean isCorrectContext = expectedContext.equals(receivedContext);
        
        if (!isCorrectContext) {
            throw new Exception(ApplicationError.INVALID_CREDENTIALS);
        }
    }
    
}
