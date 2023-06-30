
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.BaseTestSuite;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class DiscussionTests extends BaseTestSuite {
    
    @Test
    public void testDiscussionCreation() {
        UUID projectId = UUID.randomUUID();
        UUID discussionId = UUID.randomUUID();
        String title = "What is the status of this project?";
        String authorId = "jsmith";
        
        Discussion discussion = new Discussion(projectId, discussionId,
            title, authorId);
        
        assertThat(discussion.getId()).isEqualTo(discussionId);
        assertThat(discussion.getProjectId()).isEqualTo(projectId);
        assertThat(discussion.getTitle()).isEqualTo(title);
        assertThat(discussion.getAuthorId()).isEqualTo(authorId);
        assertThat(discussion.getCreatedDate()).isNull();
        
    }

    @Test
    public void testDiscussionEquality() {
        UUID projectId = UUID.randomUUID();
        UUID discussionId = UUID.randomUUID();
        
        Discussion discussion = new Discussion(projectId, discussionId,
            "What is the status of this project?", "jsmith");
        
        assertThat(discussion.equals(discussion)).isTrue();
        assertThat(discussion.equals(new Object())).isFalse();
        
        Discussion anotherDiscussion = new Discussion(projectId, UUID.randomUUID(),
            "What is the estimated deadline for this project?", "joansmith");
        assertThat(anotherDiscussion.equals(discussion)).isFalse();
        
        anotherDiscussion.setId(discussion.getId());
        anotherDiscussion.setProjectId(discussion.getProjectId());
        anotherDiscussion.setAuthorId(discussion.getAuthorId());
        assertThat(anotherDiscussion.equals(discussion)).isTrue();
        
    }
    
    @Test
    public void testDiscussionHashCode() {
        UUID projectId = UUID.randomUUID();
        UUID discussionId = UUID.randomUUID();
        
        Discussion discussion = new Discussion(projectId, discussionId,
            "What is the status of this project?", "jsmith");
        
        Discussion anotherDiscussion = new Discussion(projectId, UUID.randomUUID(),
            "What is the estimated deadline for this project?", "joansmith");
        
        assertThat(anotherDiscussion.equals(discussion)).isFalse();
        assertThat(anotherDiscussion.hashCode() == discussion.hashCode()).isFalse();
        
        anotherDiscussion.setId(discussion.getId());
        anotherDiscussion.setProjectId(discussion.getProjectId());
        anotherDiscussion.setAuthorId(discussion.getAuthorId());
        
        assertThat(anotherDiscussion.hashCode() == discussion.hashCode()).isTrue();
        
    }
    
}
