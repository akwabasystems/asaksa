
package com.akwabasystems.asakusa.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;


@Entity
@CqlName("device_tokens")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class DeviceToken {

    @PartitionKey
    private String deviceId;
    
    @CqlName("device_token")
    private String token;
    
    private String createdDate;
    private String lastModifiedDate;

    public DeviceToken() {}

    public DeviceToken(String deviceId, String token) {
        this.deviceId = deviceId;
        this.token = token;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return String.format("DeviceToken { deviceId: %s, token: %s }", 
                getDeviceId(), getToken());
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DeviceToken)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        DeviceToken token = (DeviceToken) object;
        return (token.getDeviceId() != null && token.getDeviceId().equals(getDeviceId())) &&
               (token.getToken() != null && token.getToken().equals(getToken()));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result * ((getDeviceId() != null) ? getDeviceId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getToken() != null) ? getToken().hashCode() : Integer.hashCode(1));

        return result;
    }

}
