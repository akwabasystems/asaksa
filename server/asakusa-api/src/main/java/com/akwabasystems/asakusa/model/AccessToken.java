
package com.akwabasystems.asakusa.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;


@Entity
@CqlName("access_tokens")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class AccessToken {

    @PartitionKey
    private String deviceId;
    
    private String tokenKey;
    private ItemStatus status = ItemStatus.ACTIVE;
    private String createdDate;
    private String lastModifiedDate;
    
    public AccessToken() {}
    
    public AccessToken(String deviceId, String tokenKey) {
        this.deviceId = deviceId;
        this.tokenKey = tokenKey;
    }
    
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
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
        return String.format("AccessToken { deviceId: %s, token: %s, lastModifiedDate: %s, status: %s }", 
                getDeviceId(), getTokenKey(), getLastModifiedDate(), getStatus());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AccessToken)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        AccessToken token = (AccessToken) object;
        return (token.getDeviceId() != null && token.getDeviceId().equals(getDeviceId())) &&
               (token.getTokenKey() != null && token.getTokenKey().equals(getTokenKey()));
    }

    @Override
    public int hashCode() {
        int result = 17 * ((getDeviceId() != null) ? getDeviceId().hashCode() : Integer.hashCode(1));
        result += 31 * ((getTokenKey() != null) ? getTokenKey().hashCode() : Integer.hashCode(1));

        return result;
    }

}
