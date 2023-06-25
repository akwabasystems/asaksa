
package com.akwabasystems.asakusa.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import java.util.HashSet;
import java.util.Set;


@Entity
@CqlName("user_credentials")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class UserCredentials {

    @PartitionKey
    private String userId;
    
    private String password;
    private Set<Role> roles = new HashSet<>();
            
    public UserCredentials() {}
    
    public UserCredentials(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    
    @Override
    public String toString() {
        return String.format("UserCredentials { userId: %s, password: %s, roles: %s }", 
            getUserId(), getPassword(), getRoles());
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserCredentials)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        UserCredentials credentials = (UserCredentials) object;
        return (credentials.getUserId() != null && credentials.getUserId().equals(getUserId())) &&
               (credentials.getPassword() != null && credentials.getPassword().equals(getPassword()));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result * ((getUserId() != null) ? getUserId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getPassword() != null) ? getPassword().hashCode() : Integer.hashCode(1));

        return result;
    }

}
