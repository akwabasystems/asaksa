
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
@CqlName("discussion_messages")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class Message {

    @PartitionKey(1)
    private UUID projectId;
    
    @PartitionKey(2)
    private UUID discussionId;
    
    @ClusteringColumn
    private UUID id;
    
    private String body;
    private String authorId;
    private Instant createdDate = Instant.now();
            
    public Message() {}
    
    public Message(UUID projectId, 
                   UUID discussionId, 
                   UUID id, 
                   String authorId, 
                   String body) {
        this.projectId = projectId;
        this.discussionId = discussionId;
        this.id = id;
        this.authorId = authorId;
        this.body = body;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
    
    public UUID getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(UUID discussionId) {
        this.discussionId = discussionId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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
        return String.format("Message { id: %s, authorId: %s, body: %s }", 
                getId(), getAuthorId(), getBody());
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Message)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        Message message = (Message) object;
        return (message.getId() != null && message.getId().equals(getId())) &&
               (message.getAuthorId() != null && message.getAuthorId().equals(getAuthorId())) &&
               (message.getBody() != null && message.getBody().equals(getBody()));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result * ((getId() != null) ? getId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getAuthorId()!= null) ? getAuthorId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getBody()!= null) ? getBody().hashCode() : Integer.hashCode(1));

        return result;
    }

}
