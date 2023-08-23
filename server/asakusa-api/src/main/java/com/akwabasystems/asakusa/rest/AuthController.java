
package com.akwabasystems.asakusa.rest;

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
@RequestMapping("/api/v3/auth")
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
        String client = (String) QueryUtils.getValueWithDefault(map, QueryParameter.CLIENT, "N/A");

        LoginResponse loginInfo = authService.login(getAuthorizationTicket(userId), context, client);
        return ResponseEntity.ok(loginInfo);

    }
    
}
