
package com.akwabasystems.asakusa.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import java.time.Instant;
import java.util.UUID;


@Entity
@CqlName("teams")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class Team {

    @PartitionKey
    private UUID id;
    
    private String name;
    private String description;
    private Instant createdDate = Instant.now();
    private Instant lastModifiedDate = Instant.now();
    
    public Team() {}
    
    public Team(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("Team { id: %s, name: %s", getId(), getName());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Team)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        Team team = (Team) object;
        return (team.getId()!= null && team.getId().equals(getId())) &&
               (team.getName() != null && team.getName().equals(getName()));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result * ((getId()!= null) ? getId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getName()!= null) ? getName().hashCode() : Integer.hashCode(1));

        return result;
    }

}
