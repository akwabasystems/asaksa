
package com.akwabasystems.asakusa.rest.utils;

import com.akwabasystems.asakusa.model.Gender;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserResponse {

    private String userId;    
    private String firstName;
    private String lastName;
    private String username;
    private String picture;
    private String email;
    private Gender gender = Gender.FEMALE;
    private String locale;
    private String phoneNumber;
    
    public UserResponse() {}
    
    public UserResponse(String userId, String firstName, String lastName) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
}
