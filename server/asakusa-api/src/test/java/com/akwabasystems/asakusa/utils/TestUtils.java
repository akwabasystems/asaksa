
package com.akwabasystems.asakusa.utils;

import com.akwabasystems.asakusa.model.Project;
import com.akwabasystems.asakusa.model.Team;
import com.akwabasystems.asakusa.model.User;
import java.util.Locale;
import java.util.UUID;


public class TestUtils {

    public static String randomID() {
        String[] components = UUID.randomUUID().toString().split("-");
        return String.format("%s%s", components[0], components[1]);
    }
    
    
    public static int randomSuffix() {
        return (int) Math.floor(Math.random() * 100000);
    }
    
    public static User defaultUser() {
        String userId = String.format("user-%s", TestUtils.randomID());

        User user = new User(userId, "John", "Smith", 
                userId + "@example.com");
        user.setEmailVerified(true);
        user.setLocale(Locale.ENGLISH.getLanguage());
        user.setProfile("https://akwaba.systems");
        user.setPicture("https://akwaba.systems");
        user.setPhoneNumber("+1-800-555-1212");
        user.setPhoneNumberVerified(true);
        
        return user;
    }
    
    public static Team defaultTeam() {
        String teamName = String.format("Team %s", randomSuffix());
        
        Team team = new Team(UUID.randomUUID(), teamName);
        team.setDescription(teamName);
        
        return team;
    }
    
    public static Project defaultProject() {
        return new Project(UUID.randomUUID(), UUID.randomUUID(), 
                "Project " + TestUtils.randomSuffix());
    }
    
}
