
package com.akwabasystems.asakusa.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import java.util.UUID;


@Entity
@CqlName("device_tokens")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class DeviceToken {

    @PartitionKey
    private String userId;

    private UUID id;
    
    @CqlName("device_token")
    private String token;
    
    private String createdDate;
    private String lastModifiedDate;

    public DeviceToken() {}

    public DeviceToken(String userId, UUID id, String token) {
        this.userId = userId;
        this.id = id;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
        return String.format("DeviceToken { userId: %s, token: %s }", 
                getUserId(), getToken());
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
        return (token.getId() != null && token.getId().equals(getId())) &&
               (token.getUserId() != null && token.getUserId().equals(getUserId())) &&
               (token.getToken() != null && token.getToken().equals(getToken()));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result * ((getId() != null) ? getId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getUserId() != null) ? getUserId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getToken() != null) ? getToken().hashCode() : Integer.hashCode(1));

        return result;
    }

}
