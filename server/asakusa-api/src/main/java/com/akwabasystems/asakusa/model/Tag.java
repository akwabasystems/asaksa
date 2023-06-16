
package com.akwabasystems.asakusa.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import java.util.UUID;


@Entity
@CqlName("tags")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class Tag {

    @PartitionKey
    private UUID id;
    
    private String name;
    private String colorCode;
            
    public Tag() {}
    
    public Tag(UUID id, String name, String colorCode) {
        this.id = id;
        this.name = name;
        this.colorCode = colorCode;
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

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    
    @Override
    public String toString() {
        return String.format("Tag { id: %s, name: %s, colorCode: %s", 
                getId(), getName(), getColorCode());
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Tag)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        Tag tag = (Tag) object;
        return (tag.getId() != null && tag.getId().equals(getId())) &&
               (tag.getColorCode() != null && tag.getColorCode().equals(getColorCode()));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result * ((getId() != null) ? getId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getColorCode() != null) ? getColorCode().hashCode() : Integer.hashCode(1));

        return result;
    }

    
}
