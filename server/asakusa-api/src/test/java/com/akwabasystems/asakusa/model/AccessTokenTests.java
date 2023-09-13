
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.BaseTestSuite;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class AccessTokenTests extends BaseTestSuite {
    
    @Test
    public void testAccessTokenCreation() {
        String deviceId = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        
        AccessToken accessToken = new AccessToken(deviceId, token);
        
        assertThat(accessToken.getDeviceId()).isEqualTo(deviceId);
        assertThat(accessToken.getTokenKey()).isEqualTo(token);
        assertThat(accessToken.getCreatedDate()).isNull();
        assertThat(accessToken.getLastModifiedDate()).isNull();
        
    }

    @Test
    public void testAccessTokenEquality() {
        String deviceId = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        
        AccessToken accessToken = new AccessToken(deviceId, token);
        
        assertThat(accessToken.equals(accessToken)).isTrue();
        assertThat(accessToken.equals(new Object())).isFalse();
        
        AccessToken anotherAccessToken = new AccessToken(deviceId, "A1B2C3D4");
        assertThat(anotherAccessToken.equals(accessToken)).isFalse();
        
        anotherAccessToken.setTokenKey(accessToken.getTokenKey());
        assertThat(anotherAccessToken.equals(accessToken)).isTrue();
        
    }
    
    @Test
    public void testDeviceTokenHashCode() {
        String deviceId = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        
        AccessToken accessToken = new AccessToken(deviceId, token);
        AccessToken anotherAccessToken = new AccessToken("deviceId2", "A1B2C3D4");
        
        assertThat(anotherAccessToken.equals(accessToken)).isFalse();
        assertThat(anotherAccessToken.hashCode() == accessToken.hashCode()).isFalse();
        
        anotherAccessToken.setDeviceId(accessToken.getDeviceId());
        anotherAccessToken.setTokenKey(accessToken.getTokenKey());
        
        assertThat(anotherAccessToken.hashCode() == accessToken.hashCode()).isTrue();
        
    }
    
}
