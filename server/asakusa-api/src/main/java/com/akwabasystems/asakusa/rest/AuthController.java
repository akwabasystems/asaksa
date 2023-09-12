
package com.akwabasystems.asakusa.rest;

import com.akwabasystems.asakusa.model.AccessToken;
import com.akwabasystems.asakusa.model.PhoneNumberVerification;
import com.akwabasystems.asakusa.rest.service.AuthService;
import com.akwabasystems.asakusa.rest.utils.LoginResponse;
import com.akwabasystems.asakusa.rest.utils.QueryParameter;
import com.akwabasystems.asakusa.rest.utils.QueryUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
@Log
public class AuthController extends BaseController {

    @Autowired
    private AuthService authService;
    
    
    /**
     * Handles a request to retrieve the authentication challenge used for
     * generating the payload for client login
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @return the authentication challenge used for client login
     * @throws Exception if the request fails
     */
    @GetMapping("/challenge")
    public ResponseEntity<Map<String,String>> authChallenge(HttpServletRequest request,
                                                            HttpServletResponse response) 
                                                      throws Exception {
        Map<String,String> challenge = authService.getAuthChallenge();
        return ResponseEntity.ok(challenge);
    }
    
    /**
     * Handles user login
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param map           the request body
     * @return the details of the current user on successful login
     * @throws Exception if the request fails
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request,
                                   HttpServletResponse response,
                                   @RequestBody LinkedHashMap<String,Object> map) 
                                   throws Exception {
        String context = (String) request.getHeader(QueryParameter.AUTH_CONTEXT);         
        String userId = (String) QueryUtils.getValueRequired(map, QueryParameter.USER_ID);
        String client = (String) QueryUtils.getValueRequired(map, QueryParameter.CLIENT);

        LoginResponse loginInfo = authService.login(getAuthorizationTicket(userId), context, client);
        return ResponseEntity.ok(loginInfo);

    }
    
    
    /**
     * Handles a request to send a verification code to a phone number
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param map           the request body
     * @return the number verification details
     * @throws Exception if the request fails
     */
    @PostMapping("/phones/number-verification-code")
    public ResponseEntity<PhoneNumberVerification> sendPhoneVerificationCode(HttpServletRequest request,
                                                       HttpServletResponse response,
                                                       @RequestBody LinkedHashMap<String,Object> map) 
                                                       throws Exception {
        String context = (String) request.getHeader(QueryParameter.AUTH_CONTEXT); 
        String phoneNumber = (String) QueryUtils.getValueRequired(map, QueryParameter.PHONE_NUMBER);
        String language = (String) QueryUtils.getValueWithDefault(map, QueryParameter.LOCALE, "en");
        
        PhoneNumberVerification verificationDetails = authService.sendPhoneNumberVerificationCode(
                phoneNumber, context, language);
        return ResponseEntity.ok(verificationDetails);

    }
    
    
    /**
     * Handles a request to verify a phone code
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param map           the request body
     * @return the number verification details
     * @throws Exception if the request fails
     */
    @PostMapping("/phones/code-verification")
    public ResponseEntity<Map<String,Object>> verifyCode(HttpServletRequest request,
                                                         HttpServletResponse response,
                                                         @RequestBody LinkedHashMap<String,Object> map) 
                                                         throws Exception {
        String context = (String) request.getHeader(QueryParameter.AUTH_CONTEXT); 
        String phoneNumber = (String) QueryUtils.getValueRequired(map, QueryParameter.PHONE_NUMBER);
        String code = (String) QueryUtils.getValueRequired(map, QueryParameter.CODE);
        
        Map<String,Object> verificationStatus = authService.verifyPhoneCode(phoneNumber, code, context);
        return ResponseEntity.ok(verificationStatus);

    }
    
    
    /**
     * Handles user logout
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param map           the request body
     * @return the outcome of the logout operation
     * @throws Exception if the request fails
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response,
                                    @RequestBody LinkedHashMap<String,Object> map) 
                                    throws Exception {        
        String userId = (String) QueryUtils.getValueRequired(map, QueryParameter.USER_ID);
        String client = (String) QueryUtils.getValueRequired(map, QueryParameter.CLIENT);

        Map<String,Object> logoutResponse = authService.logout(getAuthorizationTicket(userId), client);
        return ResponseEntity.ok(logoutResponse);

    }
    
    
    /**
     * Handles access token renewals
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param map           the request body
     * @return a new access token object
     * @throws Exception if the request fails
     */
    @PostMapping("/access-tokens")
    public ResponseEntity<AccessToken> renewAccessToken(HttpServletRequest request,
                                                        HttpServletResponse response,
                                                        @RequestBody LinkedHashMap<String,Object> map) 
                                                        throws Exception {
        String context = (String) request.getHeader(QueryParameter.AUTH_CONTEXT);         
        String userId = (String) QueryUtils.getValueRequired(map, QueryParameter.USER_ID);
        String client = (String) QueryUtils.getValueRequired(map, QueryParameter.CLIENT);
        String token = (String) QueryUtils.getValueRequired(map, QueryParameter.TOKEN);
        
        AccessToken accessToken = authService.renewAccessToken(getAuthorizationTicket(userId), 
                client, token, context);
        return ResponseEntity.ok(accessToken);

    }
    
}
