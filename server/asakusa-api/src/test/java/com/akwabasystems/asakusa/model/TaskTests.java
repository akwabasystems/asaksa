
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.BaseTestSuite;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class TaskTests extends BaseTestSuite {
    
    @Test
    public void testTaskCreation() {        
        UUID projectId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        
        Task task = new Task(projectId, taskId, "Do UI Research");
        
        assertThat(task.getId()).isEqualTo(taskId);
        assertThat(task.getProjectId()).isEqualTo(projectId);
        assertThat(task.getAssigneeId()).isNull();
        assertThat(task.getDependsOn()).isNull();
        assertThat(task.getStatus()).isEqualTo(ItemStatus.TODO);
        assertThat(task.getPriority()).isEqualTo(ItemPriority.MEDIUM);
        assertThat(task.getEstimatedDuration()).isEqualTo(86400);
        assertThat(task.getStartDate()).isNull();
        assertThat(task.getEndDate()).isNull();
        assertThat(task.getCreatedDate()).isNull();
    }

    @Test
    public void testTaskEquality() {
        Task task = new Task(UUID.randomUUID(), UUID.randomUUID(), "Do UI Research");
        
        assertThat(task.equals(task)).isTrue();
        assertThat(task.equals(new Object())).isFalse();
        
        Task anotherTask = new Task(UUID.randomUUID(), UUID.randomUUID(), "Create Git repo");
        assertThat(anotherTask.equals(task)).isFalse();
        
        anotherTask.setId(task.getId());
        anotherTask.setProjectId(task.getProjectId());
        anotherTask.setTitle(task.getTitle());
        assertThat(anotherTask.equals(task)).isTrue();
        
    }
    
    @Test
    public void testTaskHashCode() {
        Task task = new Task(UUID.randomUUID(), UUID.randomUUID(), "Do UI Research");
        Task anotherTask = new Task(UUID.randomUUID(), UUID.randomUUID(), "Create Git repo");
        
        assertThat(anotherTask.equals(task)).isFalse();
        assertThat(anotherTask.hashCode() == task.hashCode()).isFalse();
        
        anotherTask.setId(task.getId());
        anotherTask.setProjectId(task.getProjectId());
        anotherTask.setTitle(task.getTitle());
        
        assertThat(anotherTask.hashCode() == task.hashCode()).isTrue();
        
    }
    
}
