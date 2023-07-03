
package com.akwabasystems.asakusa.rest.utils;

import com.akwabasystems.asakusa.model.Gender;
import com.akwabasystems.asakusa.model.User;
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
    private boolean emailVerified = false;
    private Gender gender = Gender.FEMALE;
    private String locale;
    private String phoneNumber;
    
    public UserResponse() {}
    
    public UserResponse(String userId, String firstName, String lastName) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public static UserResponse fromUser(User user) {
        UserResponse userResponse = new UserResponse(user.getUserId(), 
                    user.getGivenName(), user.getFamilyName());
        userResponse.setEmail(user.getEmail());
        userResponse.setEmailVerified(user.isEmailVerified());
        userResponse.setUsername(user.getPreferredUsername());
        userResponse.setGender(user.getGender());
        userResponse.setPicture(user.getPicture());
        userResponse.setLocale(user.getLocale());
        userResponse.setPhoneNumber(user.getPhoneNumber());

        return userResponse;
    }
    
}
