
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.BaseTestSuite;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class UserCredentialsTests extends BaseTestSuite {
    
    @Test
    public void testUserCredentialsCreation() {        
        UserCredentials credentials = new UserCredentials("jsmith", "jsmith01");
        
        assertThat(credentials.getUserId()).isEqualTo("jsmith");
        assertThat(credentials.getPassword()).isEqualTo("jsmith01");
        assertThat(credentials.getRoles()).isEmpty();
    }

    @Test
    public void testUserCredentialsEquality() {
        UserCredentials credentials = new UserCredentials("jsmith", "jsmith01");
        
        assertThat(credentials.equals(credentials)).isTrue();
        assertThat(credentials.equals(new Object())).isFalse();
        
        UserCredentials otherCredentials = new UserCredentials("janesmith", "jsmith02");
        assertThat(otherCredentials.equals(credentials)).isFalse();
        
        otherCredentials.setUserId(credentials.getUserId());
        otherCredentials.setPassword(credentials.getPassword());
        assertThat(otherCredentials.equals(credentials)).isTrue();
        
    }
    
    @Test
    public void testUserCredentialsHashCode() {
        UserCredentials credentials = new UserCredentials("jsmith", "jsmith01");
        UserCredentials otherCredentials = new UserCredentials("janesmith", "jsmith02");
        
        assertThat(otherCredentials.equals(credentials)).isFalse();
        assertThat(otherCredentials.hashCode() == credentials.hashCode()).isFalse();
        
        otherCredentials.setUserId(credentials.getUserId());
        otherCredentials.setPassword(credentials.getPassword());
        
        assertThat(otherCredentials.hashCode() == credentials.hashCode()).isTrue();

    }
    
}
