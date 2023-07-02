
package com.akwabasystems.asakusa.rest.utils;

import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;


/**
 * A class that contains the authorization ticket for a given user. This ticket 
 * includes the user ID, the application ID, the application secret, and 
 * the application realm. Those details are used to authorize requests to 
 * the different services.
 */
@Getter
@Setter
public class AuthorizationTicket {

    private String userId;
    private String realmId;
    private String appId;
    private String appSecret;
    private String accessToken = "";
    private static final String[] systemUserIds = { "akwaba7" };
    
    public AuthorizationTicket() {
        super();
    }
    
    public AuthorizationTicket(String userId, String realmId) {
        this.userId = userId;
        this.realmId = realmId;
    }

    
    /**
     * Returns true if the current user is a system user; otherwise, returns false
     * 
     * @return true if the current user is a system user; otherwise, returns false
     */
    public boolean isSystemUser() {
        return Arrays.asList(systemUserIds).contains(userId);
    }
    

    @Override
    public boolean equals(Object object) {
        if(!(object instanceof AuthorizationTicket)) {
            return false;
        }

        if (object == this) {
            return true;
        }
        
        AuthorizationTicket ticket = (AuthorizationTicket) object;
        return (ticket.getUserId()!= null && ticket.getUserId().equals(this.getUserId())) &&
               (ticket.getRealmId()!= null && ticket.getRealmId().equals(this.getRealmId())) &&
               (ticket.getAccessToken() != null && ticket.getAccessToken().equals(this.getAccessToken()));
    }


    @Override
    public int hashCode() {
        int result = 17 * ((getUserId()!= null) ? getUserId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getRealmId()!= null) ? getRealmId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getAccessToken() != null) ? getAccessToken().hashCode() : Integer.hashCode(1));
        
        return result;
    }

    @Override
    public String toString() {
        return String.format("AuthorizationTicket { appId: %s, userId: %s, realmId: %s }", 
                appId, userId, realmId);
    }
    
}
