
package com.akwabasystems.asakusa.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import java.util.UUID;


@Entity
@CqlName("media")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class Media {

    @PartitionKey
    private UUID id;
    
    private String title;
    private String thumbnail;
    private String url;
    private MediaType type;
    private String createdDate;
    private String lastModifiedDate;
            
    public Media() {}
    
    public Media(UUID id, MediaType type, String title, String url) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.url = url;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
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
        return String.format("Media { id: %s, type: %s, title: %s, url: %s }", 
                getId(), getType(), getTitle(), getUrl());
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Media)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        Media media = (Media) object;
        return (media.getId() != null && media.getId().equals(getId())) &&
               (media.getType() != null && media.getType().equals(getType())) &&
               (media.getUrl() != null && media.getUrl().equals(getUrl()));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result * ((getId() != null) ? getId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getType() != null) ? getType().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getUrl()!= null) ? getUrl().hashCode() : Integer.hashCode(1));

        return result;
    }

}
