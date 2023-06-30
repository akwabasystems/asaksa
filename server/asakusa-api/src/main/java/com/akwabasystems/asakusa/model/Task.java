
package com.akwabasystems.asakusa.model;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;


@Entity
@CqlName("tasks")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class Task {

    @PartitionKey
    private UUID projectId;
    
    @ClusteringColumn
    private UUID id;
    
    private String title;
    private String description;
    
    @Nullable
    private String assigneeId;
    private UUID dependsOn;
    private String startDate;
    private String endDate;
    private int estimatedDuration = 86400;
    private ItemStatus status = ItemStatus.TODO;
    private ItemPriority priority = ItemPriority.MEDIUM;
    private Set<String> tags = new HashSet<>();
    private String createdDate;
    private String lastModifiedDate;
            
    public Task() {}
    
    public Task(UUID projectId, UUID id, String title) {
        this.projectId = projectId;
        this.id = id;
        this.title = title;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public UUID getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(UUID dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(int estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public ItemPriority getPriority() {
        return priority;
    }

    public void setPriority(ItemPriority priority) {
        this.priority = priority;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
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
        return String.format("Task { id: %s, title: %s, projectId: %s }", 
            getId(), getTitle(), getProjectId());
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Task)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        Task task = (Task) object;
        return (task.getId() != null && task.getId().equals(getId())) &&
               (task.getTitle() != null && task.getTitle().equals(getTitle())) &&
               (task.getProjectId() != null && task.getProjectId().equals(getProjectId()));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result * ((getId() != null) ? getId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getTitle() != null) ? getTitle().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getProjectId() != null) ? getProjectId().hashCode() : Integer.hashCode(1));

        return result;
    }

}
