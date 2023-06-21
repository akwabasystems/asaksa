
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.BaseTestSuite;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class MessageTests extends BaseTestSuite {
    
    @Test
    public void testMessageCreation() {
        UUID projectId = UUID.randomUUID();
        UUID discussionId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        
        String body = "I have just updated the status of the project";
        String authorId = "jsmith";
        
        Message message = new Message(projectId, discussionId, messageId, 
                authorId, body);
        
        assertThat(message.getId()).isEqualTo(messageId);
        assertThat(message.getProjectId()).isEqualTo(projectId);
        assertThat(message.getDiscussionId()).isEqualTo(discussionId);
        assertThat(message.getAuthorId()).isEqualTo(authorId);
        assertThat(message.getBody()).isEqualTo(body);
        assertThat(message.getCreatedDate()).isNotNull();
    }

    @Test
    public void testMessageEquality() {
        UUID projectId = UUID.randomUUID();
        UUID discussionId = UUID.randomUUID();
        
        Message message = new Message(projectId, discussionId, UUID.randomUUID(), 
                "jsmith", "I have just updated the status of the project");
        
        assertThat(message.equals(message)).isTrue();
        assertThat(message.equals(new Object())).isFalse();
        
        Message anotherMessage = new Message(projectId, discussionId, UUID.randomUUID(),
                "joansmith", "Great! Thanks :-)");
        assertThat(anotherMessage.equals(message)).isFalse();
        
        anotherMessage.setId(message.getId());
        anotherMessage.setAuthorId(message.getAuthorId());
        anotherMessage.setBody(message.getBody());
        assertThat(anotherMessage.equals(message)).isTrue();
        
    }
    
    @Test
    public void testMessageHashCode() {
        UUID projectId = UUID.randomUUID();
        UUID discussionId = UUID.randomUUID();
        
        Message message = new Message(projectId, discussionId, UUID.randomUUID(), 
                "jsmith", "I have just updated the status of the project");
        
        assertThat(message.equals(message)).isTrue();
        assertThat(message.equals(new Object())).isFalse();
        
        Message anotherMessage = new Message(projectId, discussionId, UUID.randomUUID(),
                "joansmith", "Great! Thanks :-)");
        
        assertThat(anotherMessage.hashCode() == message.hashCode()).isFalse();
        
        anotherMessage.setId(message.getId());
        anotherMessage.setAuthorId(message.getAuthorId());
        anotherMessage.setBody(message.getBody());
        
        assertThat(anotherMessage.hashCode() == message.hashCode()).isTrue();
        
    }
    
}
