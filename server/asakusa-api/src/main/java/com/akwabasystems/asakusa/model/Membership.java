
package com.akwabasystems.asakusa.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import java.util.UUID;


@Entity
@CqlName("membership")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class Membership {

    @PartitionKey
    private String userId;
    
    private UUID id;
    private MembershipType type = MembershipType.FREE;
    private ItemStatus status = ItemStatus.ACTIVE;
    private String createdDate;
    private String lastModifiedDate;
    
    public Membership() {}
    
    public Membership(String userId, UUID id, MembershipType type) {
        this.userId = userId;
        this.id = id;
        this.type = type;
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

    public MembershipType getType() {
        return type;
    }

    public void setType(MembershipType type) {
        this.type = type;
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
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof Membership)) {
            return false;
        }

        Membership membership = (Membership) object;
        return (membership.getUserId() != null && membership.getUserId().equals(getUserId())) &&
               (membership.getId() != null && membership.getId().equals(getId()));
    }
    

    @Override
    public int hashCode() {
        int result = 17 * ((getId() != null)? getId().hashCode() : Integer.hashCode(1));
        result += 31 * result * ((getUserId() != null) ? getUserId().hashCode() : Integer.hashCode(1));
        
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("Membership: { Type: %s, Status: %s }",
                getType(), getStatus());
    }

}
