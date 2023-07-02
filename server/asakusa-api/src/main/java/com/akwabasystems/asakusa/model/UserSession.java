
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
@CqlName("user_sessions")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class UserSession {

    @PartitionKey
    private String userId;
    
    @ClusteringColumn
    private UUID id;
    
    private String client;
    private ItemStatus status = ItemStatus.ACTIVE;
    private Instant startDate = Instant.now(Clock.systemUTC());
    private Instant endDate;
    
    public UserSession() {}
    
    public UserSession(String userId, UUID id) {
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

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return String.format("UserSession { userId: %s, startDate: %s, status: %s }", 
                getUserId(), getStartDate(), getStatus());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserSession)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        UserSession session = (UserSession) object;
        return (session.getUserId() != null && session.getId().equals(getId())) &&
               (session.getId() != null && session.getId().equals(getId())) &&
               (session.getStartDate() != null && session.getStartDate().equals(getStartDate()));
    }

    @Override
    public int hashCode() {
        int result = 17 * ((getUserId() != null) ? getUserId().hashCode() : Integer.hashCode(1));
        result += 31 * ((getId() != null) ? getId().hashCode() : Integer.hashCode(1));
        result += 31 * ((getStartDate() != null) ? getStartDate().hashCode() : Integer.hashCode(1));

        return result;
    }

}
