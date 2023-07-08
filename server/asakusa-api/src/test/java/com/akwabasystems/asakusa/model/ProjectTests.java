
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.BaseTestSuite;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class ProjectTests extends BaseTestSuite {
    
    @Test
    public void testProjectCreation() {        
        UUID teamId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        String projectName = "Language App";
        Project project = new Project(teamId, projectId, projectName);
        
        assertThat(project.getId()).isEqualTo(projectId);
        assertThat(project.getOwnerId()).isNull();
        assertThat(project.getTeamId()).isEqualTo(teamId);
        assertThat(project.getName()).isEqualTo(projectName);
        assertThat(project.getStatus()).isEqualTo(ItemStatus.TODO);
        assertThat(project.getPriority()).isEqualTo(ItemPriority.MEDIUM);
        assertThat(project.getCapacity() == 8.0).isTrue();
        assertThat(project.getStartDate()).isNull();
        assertThat(project.getEndDate()).isNull();
        assertThat(project.getDeadline()).isNull();
        assertThat(project.getCreatedDate()).isNull();
    }

    @Test
    public void testProjectEquality() {
        Project project = new Project(UUID.randomUUID(), UUID.randomUUID(), "Language App");
        project.setTeamId(UUID.randomUUID());
        
        assertThat(project.equals(project)).isTrue();
        assertThat(project.equals(new Object())).isFalse();
        
        Project anotherProject = new Project(UUID.randomUUID(), UUID.randomUUID(), "Assignment Game");
        assertThat(anotherProject.equals(project)).isFalse();
        
        anotherProject.setId(project.getId());
        anotherProject.setName(project.getName());
        anotherProject.setTeamId(project.getTeamId());
        assertThat(anotherProject.equals(project)).isTrue();
        
    }
    
    @Test
    public void testProjectHashCode() {
        Project project = new Project(UUID.randomUUID(), UUID.randomUUID(), "Language App");
        project.setTeamId(UUID.randomUUID());
        
        Project anotherProject = new Project(UUID.randomUUID(), UUID.randomUUID(), "Assignment Game");
        
        assertThat(anotherProject.equals(project)).isFalse();
        assertThat(anotherProject.hashCode() == project.hashCode()).isFalse();
        
        anotherProject.setId(project.getId());
        anotherProject.setName(project.getName());
        anotherProject.setTeamId(project.getTeamId());
        
        assertThat(anotherProject.hashCode() == project.hashCode()).isTrue();
        
    }
    
}
