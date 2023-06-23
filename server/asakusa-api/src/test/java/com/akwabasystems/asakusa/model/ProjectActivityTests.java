
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.BaseTestSuite;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class ProjectActivityTests extends BaseTestSuite {
    
    @Test
    public void testProjectActivityCreation() {        
        UUID projectId = UUID.randomUUID();
        UUID activityId = UUID.randomUUID();
        
        ProjectActivity activity = new ProjectActivity(projectId, activityId,
            "jsmith", ActivityType.DISCUSSION);
        
        assertThat(activity.getId()).isEqualTo(activityId);
        assertThat(activity.getProjectId()).isEqualTo(projectId);
        assertThat(activity.getActor()).isEqualTo("jsmith");
        assertThat(activity.getType()).isEqualTo(ActivityType.DISCUSSION);
        assertThat(activity.getDetails()).isNotNull();
        assertThat(activity.getCreatedDate()).isNull();
    }

    @Test
    public void testProjectActivityEquality() {
        ProjectActivity activity = new ProjectActivity(UUID.randomUUID(), 
                UUID.randomUUID(), "jsmith", ActivityType.DISCUSSION);
        
        assertThat(activity.equals(activity)).isTrue();
        assertThat(activity.equals(new Object())).isFalse();
        
        ProjectActivity anotherActivity = new ProjectActivity(UUID.randomUUID(), 
                UUID.randomUUID(), "joansmith", ActivityType.TASK);
        
        assertThat(anotherActivity.equals(activity)).isFalse();
        
        anotherActivity.setId(activity.getId());
        anotherActivity.setProjectId(activity.getProjectId());
        anotherActivity.setActor(activity.getActor());
        anotherActivity.setType(activity.getType());
        assertThat(anotherActivity.equals(activity)).isTrue();
        
    }
    
    @Test
    public void testDiscussionHashCode() {
        ProjectActivity activity = new ProjectActivity(UUID.randomUUID(), 
                UUID.randomUUID(), "jsmith", ActivityType.DISCUSSION);
        
        ProjectActivity anotherActivity = new ProjectActivity(UUID.randomUUID(), 
                UUID.randomUUID(), "joansmith", ActivityType.TASK);
        
        assertThat(anotherActivity.equals(activity)).isFalse();
        assertThat(anotherActivity.hashCode() == activity.hashCode()).isFalse();
        
        anotherActivity.setId(activity.getId());
        anotherActivity.setProjectId(activity.getProjectId());
        anotherActivity.setActor(activity.getActor());
        anotherActivity.setType(activity.getType());
        
        assertThat(anotherActivity.hashCode() == activity.hashCode()).isTrue();
        
    }
    
}
