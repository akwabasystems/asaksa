
package com.akwabasystems.asakusa.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import org.json.JSONObject;


@Entity
@CqlName("user_preferences")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class UserPreferences {

    @PartitionKey
    private String userId;
    
    private JSONObject settings = new JSONObject();
    private String lastModifiedDate;
    
    public UserPreferences() {}
    
    
    public UserPreferences(String userId, JSONObject settings) {
        this.userId = userId;
        this.settings = settings;
    }
    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public JSONObject getSettings() {
        return settings;
    }

    public void setSettings(JSONObject settings) {
        this.settings = settings;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    
    @Override
    public String toString() {
        return String.format("UserPreferences { settings: %s }", getSettings().toString());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserPreferences)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        UserPreferences preferences = (UserPreferences) object;
        return (preferences.getUserId() != null && preferences.getUserId().equals(getUserId()) &&
                preferences.getSettings() != null && preferences.getSettings().toString().equals(
                        getSettings().toString()));
    }


    @Override
    public int hashCode() {
        int result = 17 * ((getUserId() != null) ? getUserId().hashCode() : Integer.hashCode(1));
        result += 31 * ((getSettings() != null) ? getSettings().hashCode() : Integer.hashCode(1));

        return result;
    }

}
