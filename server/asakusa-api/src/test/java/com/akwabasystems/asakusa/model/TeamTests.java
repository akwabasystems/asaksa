
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.BaseTestSuite;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class TeamTests extends BaseTestSuite {
    
    @Test
    public void testTeamCreation() {        
        Team team = new Team(UUID.randomUUID(), "Team Akwaba 1");
        
        assertThat(team.getId()).isNotNull();
        assertThat(team.getName()).isEqualTo("Team Akwaba 1");
        assertThat(team.getCreatedDate()).isNotNull();
        assertThat(team.getLastModifiedDate()).isNotNull();
    }

    @Test
    public void testTeamEquality() {
        Team team = new Team(UUID.randomUUID(), "Team Akwaba 1");
        
        assertThat(team.equals(team)).isTrue();
        assertThat(team.equals(new Object())).isFalse();
        
        Team anotherTeam = new Team(UUID.randomUUID(), "Team Akwaba 2");
        assertThat(anotherTeam.equals(team)).isFalse();
        
        anotherTeam.setId(team.getId());
        anotherTeam.setName(team.getName());
        assertThat(anotherTeam.equals(team)).isTrue();
        
    }
    
    @Test
    public void testTeamHashCode() {
        Team team = new Team(UUID.randomUUID(), "Team Akwaba 1");
        Team anotherTeam = new Team(UUID.randomUUID(), "Team Akwaba 2");
        
        assertThat(anotherTeam.equals(team)).isFalse();
        assertThat(anotherTeam.hashCode() == team.hashCode()).isFalse();
        
        anotherTeam.setId(team.getId());
        anotherTeam.setName(team.getName());
        
        assertThat(anotherTeam.hashCode() == team.hashCode()).isTrue();

    }
    
}
