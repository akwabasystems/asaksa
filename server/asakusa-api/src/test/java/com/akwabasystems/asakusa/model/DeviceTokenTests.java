
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.BaseTestSuite;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class DeviceTokenTests extends BaseTestSuite {
    
    @Test
    public void testDeviceTokenCreation() {
        String deviceId = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        DeviceToken deviceToken = new DeviceToken(deviceId, token);
        
        assertThat(deviceToken.getDeviceId()).isEqualTo(deviceId);
        assertThat(deviceToken.getToken()).isEqualTo(token);
        assertThat(deviceToken.getCreatedDate()).isNull();
        assertThat(deviceToken.getLastModifiedDate()).isNull();
        
    }

    @Test
    public void testDeviceTokenEquality() {
        String deviceId = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        
        DeviceToken deviceToken = new DeviceToken(deviceId, token);
        
        assertThat(deviceToken.equals(deviceToken)).isTrue();
        assertThat(deviceToken.equals(new Object())).isFalse();
        
        DeviceToken anotherDeviceToken = new DeviceToken(deviceId, "A1B2C3D4");
        assertThat(anotherDeviceToken.equals(deviceToken)).isFalse();
        
        anotherDeviceToken.setToken(deviceToken.getToken());
        assertThat(anotherDeviceToken.equals(deviceToken)).isTrue();
        
    }
    
    @Test
    public void testDeviceTokenHashCode() {
        String deviceId = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        
        DeviceToken deviceToken = new DeviceToken(deviceId, token);
        DeviceToken anotherDeviceToken = new DeviceToken("deviceId2", "A1B2C3D4");
        
        assertThat(anotherDeviceToken.equals(deviceToken)).isFalse();
        assertThat(anotherDeviceToken.hashCode() == deviceToken.hashCode()).isFalse();
        
        anotherDeviceToken.setDeviceId(deviceToken.getDeviceId());
        anotherDeviceToken.setToken(deviceToken.getToken());
        
        assertThat(anotherDeviceToken.hashCode() == deviceToken.hashCode()).isTrue();
        
    }
    
}
