
package com.akwabasystems.asakusa.rest;

import com.akwabasystems.asakusa.rest.utils.ApplicationError;
import com.akwabasystems.asakusa.rest.utils.AuthorizationTicket;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;


public abstract class BaseController {

    @Value("${security.akwaba.app-id}")
    protected String appId;
    
    @Value("${security.akwaba.app-secret}")
    protected String appSecret;
    
    @Value("${security.akwaba.realm}")
    protected String realm;
    
    
    /**
     * Generates an authorization ticket for the user with the specified ID
     * 
     * @param userId        the ID of the user for whom to generate the ticket
     * @return an authorization ticket for the user with the specified ID
     */
    protected AuthorizationTicket getAuthorizationTicket(String userId) {
        return getAuthorizationTicket(userId, null);
    }
    
    
    /**
     * Generates an authorization ticket for the user with the specified ID
     * 
     * @param userId        the ID of the user for whom to generate the ticket
     * @param accessToken   the access token for the current user
     * @return an authorization ticket for the user with the specified ID
     */
    protected AuthorizationTicket getAuthorizationTicket(String userId, String accessToken) {
        AuthorizationTicket authTicket = new AuthorizationTicket(userId, realm);
        authTicket.setAppId(appId);
        authTicket.setAppSecret(appSecret);
        
        if (accessToken != null) {
            authTicket.setAccessToken(accessToken);
        }
        
        return authTicket;
    }
    
    
    /**
     * Returns true if this resource requires authorization; otherwise, returns false
     * 
     * @return true if this resource requires authorization; otherwise, returns false 
     */
    protected boolean authorizationRequired() {
        return false;
    }
    
    
    /**
     * Returns the request problem with the specified details
     * 
     * @param status    the HTTP status of the problem
     * @param uri       the request URI    
     * @param title     the title of the problem
     * @return the request problem with the specified details
     * @throws Exception if the problem cannot be generated
     */
    protected ProblemDetail problemDetails(HttpStatus status,
                                           String uri,
                                           String title) throws Exception {
        ProblemDetail details = ProblemDetail.forStatus(status);
        details.setTitle(title);
        details.setInstance(new URI(uri));
        
        return details;
    }
    
    
    /**
     * Handles exceptions that are thrown while sending a request
     * 
     * @param exception         the exception thrown during the request
     * @return a JSON string with the details of the exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAllExceptions(Exception exception) {
        ProblemDetail details = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        details.setTitle(exception.getMessage());
        return ResponseEntity.of(details).build();
    }
    
}
