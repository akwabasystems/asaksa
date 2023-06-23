
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.BaseTestSuite;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class DeviceTokenTests extends BaseTestSuite {
    
    @Test
    public void testDeviceTokenCreation() {
        UUID tokenId = UUID.randomUUID();
        String token = UUID.randomUUID().toString();
        DeviceToken deviceToken = new DeviceToken("jsmith", tokenId, token);
        
        assertThat(deviceToken.getUserId()).isEqualTo("jsmith");
        assertThat(deviceToken.getId()).isEqualTo(tokenId);
        assertThat(deviceToken.getToken()).isEqualTo(token);
        assertThat(deviceToken.getCreatedDate()).isNull();
        assertThat(deviceToken.getLastModifiedDate()).isNull();
        
    }

    @Test
    public void testDeviceTokenEquality() {
        UUID tokenId = UUID.randomUUID();
        String token = UUID.randomUUID().toString();
        
        DeviceToken deviceToken = new DeviceToken("jsmith", tokenId, token);
        
        assertThat(deviceToken.equals(deviceToken)).isTrue();
        assertThat(deviceToken.equals(new Object())).isFalse();
        
        DeviceToken anotherDeviceToken = new DeviceToken("jsmith", tokenId, "A1B2C3D4");
        assertThat(anotherDeviceToken.equals(deviceToken)).isFalse();
        
        anotherDeviceToken.setToken(deviceToken.getToken());
        assertThat(anotherDeviceToken.equals(deviceToken)).isTrue();
        
    }
    
    @Test
    public void testDeviceTokenHashCode() {
        UUID tokenId = UUID.randomUUID();
        String token = UUID.randomUUID().toString();
        
        DeviceToken deviceToken = new DeviceToken("jsmith", tokenId, token);
        DeviceToken anotherDeviceToken = new DeviceToken("jsmith02", UUID.randomUUID(), "A1B2C3D4");
        
        assertThat(anotherDeviceToken.equals(deviceToken)).isFalse();
        assertThat(anotherDeviceToken.hashCode() == deviceToken.hashCode()).isFalse();
        
        anotherDeviceToken.setUserId(deviceToken.getUserId());
        anotherDeviceToken.setId(deviceToken.getId());
        anotherDeviceToken.setToken(deviceToken.getToken());
        
        assertThat(anotherDeviceToken.hashCode() == deviceToken.hashCode()).isTrue();
        
    }
    
}
