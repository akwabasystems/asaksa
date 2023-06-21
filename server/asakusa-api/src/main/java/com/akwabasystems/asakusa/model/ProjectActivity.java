
package com.akwabasystems.asakusa.model;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import java.time.Instant;
import java.util.UUID;
import org.json.JSONObject;


@Entity
@CqlName("project_activity")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class ProjectActivity {
    
    @PartitionKey
    private UUID projectId;
    
    @ClusteringColumn
    private UUID id;
    
    private String actor;
    private ActivityType type = ActivityType.NONE;
    private JSONObject details = new JSONObject();
    private Instant createdDate = Instant.now();

    public ProjectActivity() {}
    
    public ProjectActivity(UUID projectId, 
                           UUID id,
                           String actor, 
                           ActivityType type) {
        this.projectId = projectId;
        this.id = id;
        this.actor = actor;
        this.type = type;
    }
    
    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public JSONObject getDetails() {
        return details;
    }

    public void setDetails(JSONObject details) {
        this.details = details;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }
    
    @Override
    public String toString() {
       return String.format("ProjectActivity { id: %s, actor: '%s', type: '%s' }", 
               getId(), getActor(), getType());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ProjectActivity)) {
            return false;
        }

        if (object == this) {
            return true;
        }
        
        ProjectActivity activity = (ProjectActivity) object;
        return (activity.getProjectId() != null && activity.getProjectId().equals(getProjectId())) &&
               (activity.getId() != null && activity.getId().equals(getId())) &&
               (activity.getActor() != null && activity.getActor().equals(getActor())) &&
               (activity.getType()!= null && activity.getType().equals(getType()));
    }

    @Override
    public int hashCode() {
        int result = 17 * ((getProjectId() != null) ? getProjectId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getId() != null) ? getId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getActor() != null) ? getActor().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getType() != null) ? getType().hashCode() : Integer.hashCode(1));
        
        return result;
    }

}
