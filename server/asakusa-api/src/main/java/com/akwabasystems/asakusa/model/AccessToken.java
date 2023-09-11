
package com.akwabasystems.asakusa.model;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;


@Entity
@CqlName("access_tokens")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class AccessToken {

    @PartitionKey
    private String userId;
    
    @ClusteringColumn
    private UUID id;
    
    private String tokenKey;
    private ItemStatus status = ItemStatus.ACTIVE;
    private Instant createdDate = Instant.now(Clock.systemUTC());
    
    public AccessToken() {}
    
    public AccessToken(String userId, UUID id) {
        this.userId = userId;
        this.id = id;
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

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }


    @Override
    public String toString() {
        return String.format("AccessToken { userId: %s, createdDate: %s, status: %s }", 
                getUserId(), getCreatedDate(), getStatus());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AccessToken)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        AccessToken session = (AccessToken) object;
        return (session.getUserId() != null && session.getId().equals(getId())) &&
               (session.getId() != null && session.getId().equals(getId())) &&
               (session.getCreatedDate() != null && session.getCreatedDate().equals(getCreatedDate()));
    }

    @Override
    public int hashCode() {
        int result = 17 * ((getUserId() != null) ? getUserId().hashCode() : Integer.hashCode(1));
        result += 31 * ((getId() != null) ? getId().hashCode() : Integer.hashCode(1));
        result += 31 * ((getCreatedDate() != null) ? getCreatedDate().hashCode() : Integer.hashCode(1));

        return result;
    }

}
