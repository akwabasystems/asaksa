
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
@CqlName("projects")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class Project {

    @PartitionKey
    private UUID teamId;
    
    @ClusteringColumn
    private UUID id;
    
    private String name;
    private String description;
    
    @Nullable
    private String ownerId;
    private String startDate;
    private String endDate;
    private String deadline;
    private int capacity = 40;
    private ItemStatus status = ItemStatus.TODO;
    private ItemPriority priority = ItemPriority.MEDIUM;
    private Set<String> tags = new HashSet<>();
    private String createdDate;
    private String lastModifiedDate;
            
    public Project() {}
    
    public Project(UUID teamId, UUID id, String name) {
        this.teamId = teamId;
        this.id = id;
        this.name = name;
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

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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
        return String.format("Project { id: %s, name: %s, teamId: %s, ownerId: %s }",
                getId(), getName(), getTeamId(), getOwnerId());
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Project)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        Project project = (Project) object;
        return (project.getId() != null && project.getId().equals(getId())) &&
               (project.getName() != null && project.getName().equals(getName())) &&
               (project.getTeamId() != null && project.getTeamId().equals(getTeamId()));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result * ((getId() != null) ? getId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getName() != null) ? getName().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getTeamId() != null) ? getTeamId().hashCode() : Integer.hashCode(1));

        return result;
    }

}
