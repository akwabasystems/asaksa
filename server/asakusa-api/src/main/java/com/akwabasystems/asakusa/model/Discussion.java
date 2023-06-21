
package com.akwabasystems.asakusa.model;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import java.time.Instant;
import java.util.UUID;


@Entity
@CqlName("project_discussions")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class Discussion {

    @PartitionKey
    private UUID projectId;
    
    @ClusteringColumn
    private UUID id;
    
    private String title;
    private String authorId;
    private Instant createdDate = Instant.now();
            
    public Discussion() {}
    
    public Discussion(UUID projectId, UUID id, String title, String authorId) {
        this.projectId = projectId;
        this.id = id;
        this.title = title;
        this.authorId = authorId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return String.format("Discussion { id: %s, title: %s, authorId: %s }", 
                getId(), getTitle(), getAuthorId());
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Discussion)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        Discussion discussion = (Discussion) object;
        return (discussion.getId() != null && discussion.getId().equals(getId())) &&
               (discussion.getProjectId() != null && discussion.getProjectId().equals(getProjectId())) &&
               (discussion.getAuthorId()!= null && discussion.getAuthorId().equals(getAuthorId()));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result * ((getId() != null) ? getId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getProjectId() != null) ? getProjectId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getAuthorId() != null) ? getAuthorId().hashCode() : Integer.hashCode(1));

        return result;
    }

}
