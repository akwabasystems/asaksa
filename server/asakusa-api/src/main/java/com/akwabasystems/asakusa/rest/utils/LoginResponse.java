
package com.akwabasystems.asakusa.rest.utils;

import com.akwabasystems.asakusa.model.AccessToken;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LoginResponse {

    private UserResponse userInfo;
    private Map<String,Object> accountSummary;
    private Map<String,Object> accountSettings;
    private AccessToken accessToken;
    
    public LoginResponse() {}
    
    public LoginResponse(UserResponse userInfo, 
                         Map<String,Object> accountSummary,
                         Map<String,Object> accountSettings,
                         AccessToken accessToken) {
        this.userInfo = userInfo;
        this.accountSummary = accountSummary;
        this.accountSettings = accountSettings;
        this.accessToken = accessToken;
    }
    
}
