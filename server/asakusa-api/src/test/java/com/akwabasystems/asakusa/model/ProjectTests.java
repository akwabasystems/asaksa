
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.BaseTestSuite;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class ProjectTests extends BaseTestSuite {
    
    @Test
    public void testProjectCreation() {        
        UUID projectId = UUID.randomUUID();
        String projectName = "Language App";
        Project project = new Project(projectId, projectName);
        
        assertThat(project.getId()).isEqualTo(projectId);
        assertThat(project.getOwnerId()).isNull();
        assertThat(project.getTeamId()).isNull();
        assertThat(project.getName()).isEqualTo(projectName);
        assertThat(project.getStatus()).isEqualTo(ItemStatus.TODO);
        assertThat(project.getPriority()).isEqualTo(ItemPriority.MEDIUM);
        assertThat(project.getCapacity()).isEqualTo(10);
        assertThat(project.getStartDate()).isNotNull();
        assertThat(project.getEndDate()).isNotNull();
        assertThat(project.getDeadline()).isNotNull();
        assertThat(project.getCreatedDate()).isNotNull();
    }

    @Test
    public void testProjectEquality() {
        Project project = new Project(UUID.randomUUID(), "Language App");
        project.setTeamId(UUID.randomUUID());
        
        assertThat(project.equals(project)).isTrue();
        assertThat(project.equals(new Object())).isFalse();
        
        Project anotherProject = new Project(UUID.randomUUID(), "Assignment Game");
        assertThat(anotherProject.equals(project)).isFalse();
        
        anotherProject.setId(project.getId());
        anotherProject.setName(project.getName());
        anotherProject.setTeamId(project.getTeamId());
        assertThat(anotherProject.equals(project)).isTrue();
        
    }
    
    @Test
    public void testProjectHashCode() {
        Project project = new Project(UUID.randomUUID(), "Language App");
        project.setTeamId(UUID.randomUUID());
        
        Project anotherProject = new Project(UUID.randomUUID(), "Assignment Game");
        
        assertThat(anotherProject.equals(project)).isFalse();
        assertThat(anotherProject.hashCode() == project.hashCode()).isFalse();
        
        anotherProject.setId(project.getId());
        anotherProject.setName(project.getName());
        anotherProject.setTeamId(project.getTeamId());
        
        assertThat(anotherProject.hashCode() == project.hashCode()).isTrue();
        
    }
    
}
