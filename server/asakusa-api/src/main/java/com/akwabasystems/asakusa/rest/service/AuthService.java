
package com.akwabasystems.asakusa.rest.service;

import com.akwabasystems.asakusa.dao.UserDao;
import com.akwabasystems.asakusa.model.User;
import com.akwabasystems.asakusa.model.UserCredentials;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.akwabasystems.asakusa.rest.utils.ApplicationError;
import com.akwabasystems.asakusa.rest.utils.AuthorizationTicket;
import com.akwabasystems.asakusa.rest.utils.LoginResponse;
import com.akwabasystems.asakusa.rest.utils.UserResponse;
import com.akwabasystems.asakusa.utils.PasswordUtils;
import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
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
        
        UserResponse userInfo = UserResponse.fromUser(user);
        return new LoginResponse(userInfo, new HashMap<String, Object>(), new HashMap<String,Object>());
        
    }
    
}
