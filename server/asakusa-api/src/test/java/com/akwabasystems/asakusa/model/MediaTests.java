
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.BaseTestSuite;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class MediaTests extends BaseTestSuite {
    
    @Test
    public void testMediaCreation() {
        UUID mediaId = UUID.randomUUID();
        Media media = new Media(mediaId, MediaType.IMAGE,
            "Akwaba logo", "https://akwaba.systems/src/styles/images/akwaba-logo.png");
        
        assertThat(media.getId()).isEqualTo(mediaId);
        assertThat(media.getType()).isEqualTo(MediaType.IMAGE);
        assertThat(media.getCreatedDate()).isNull();
        
    }

    @Test
    public void testMediaEquality() {
        UUID mediaId = UUID.randomUUID();
        Media image = new Media(mediaId, MediaType.IMAGE,
            "Akwaba logo", "https://akwaba.systems/src/styles/images/akwaba-logo.png");
        
        assertThat(image.equals(image)).isTrue();
        assertThat(image.equals(new Object())).isFalse();
        
        Media anotherImage = new Media(UUID.randomUUID(), MediaType.VIDEO,
            "Akwaba intro", "https://akwaba.systems/src/media/gamified-classroom-960.mp4");
        assertThat(anotherImage.equals(image)).isFalse();
        
        anotherImage.setId(image.getId());
        anotherImage.setType(image.getType());
        anotherImage.setUrl(image.getUrl());
        assertThat(anotherImage.equals(image)).isTrue();
        
    }
    
    @Test
    public void testMediaHashCode() {
        UUID mediaId = UUID.randomUUID();
        Media image = new Media(mediaId, MediaType.IMAGE,
            "Akwaba logo", "https://akwaba.systems/src/styles/images/akwaba-logo.png");
        
        Media anotherImage = new Media(UUID.randomUUID(), MediaType.VIDEO,
            "Akwaba intro", "https://akwaba.systems/src/media/gamified-classroom-960.mp4");
        
        assertThat(anotherImage.equals(image)).isFalse();
        assertThat(anotherImage.hashCode() == image.hashCode()).isFalse();
        
        anotherImage.setId(image.getId());
        anotherImage.setType(image.getType());
        anotherImage.setUrl(image.getUrl());
        
        assertThat(anotherImage.hashCode() == image.hashCode()).isTrue();
        
    }
    
}
