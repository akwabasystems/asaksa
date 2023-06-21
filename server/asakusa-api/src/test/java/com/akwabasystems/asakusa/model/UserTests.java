
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.BaseTestSuite;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class UserTests extends BaseTestSuite {
    
    @Test
    public void testUserCreation() {
        User user = new User("jsmith", "John", 
                "Smith", "jsmith01@example.com");
        
        assertThat(user.getUserId()).isEqualTo("jsmith");
        assertThat(user.getEmail()).isEqualTo("jsmith01@example.com");
        assertThat(user.isEmailVerified()).isFalse();
        assertThat(user.isEmailVerified()).isFalse();
        
    }

    @Test
    public void testUserEquality() {
        User user = new User("jsmith", "John", 
                "Smith", "jsmith01@example.com");
        
        assertThat(user.equals(user)).isTrue();
        assertThat(user.equals(new Object())).isFalse();
        
        User anotherUser = new User();
        anotherUser.setUserId(user.getUserId());
        anotherUser.setFamilyName(user.getFamilyName());
        assertThat(anotherUser.equals(user)).isFalse();
        
        anotherUser.setEmail(user.getEmail());
        assertThat(anotherUser.equals(user)).isTrue();
        
    }
    
    @Test
    public void testUserHashCode() {
        User user = new User("jsmith", "John", 
                "Smith", "jsmith01@example.com");
        
        User anotherUser = new User("jsmith02", "Jane", 
                "Smith", "jsmith02@example.com");
        
        assertThat(anotherUser.equals(user)).isFalse();
        assertThat(anotherUser.hashCode() == user.hashCode()).isFalse();
        
        anotherUser.setUserId(user.getUserId());
        anotherUser.setFamilyName(user.getFamilyName());
        anotherUser.setEmail(user.getEmail());
        
        assertThat(anotherUser.hashCode() == user.hashCode()).isTrue();
        
    }
}
