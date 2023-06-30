
package com.akwabasystems.asakusa.dao.impl;

import com.akwabasystems.asakusa.BaseTestSuite;
import com.akwabasystems.asakusa.dao.TeamDao;
import com.akwabasystems.asakusa.model.Team;
import com.akwabasystems.asakusa.model.User;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.akwabasystems.asakusa.utils.TestUtils;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class TeamDaoTests extends BaseTestSuite {

    @Autowired
    private CqlSession cqlSession;
    
    private RepositoryMapper mapper;
    
    
    @BeforeAll
    public void setup() {
        mapper = RepositoryMapper.builder(cqlSession).build();
    }
    
    
    @Test
    public void testDaoInitialization() {
        TeamDao teamDao = mapper.teamDao();
        assertThat(teamDao).isNotNull();
    }
    
    
    @Test
    public void testCreateTeamWithInvalidAttributes() {
        TeamDao teamDao = mapper.teamDao();
        Team team = new Team();
        
        assertThatThrownBy(() -> teamDao.create(team))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("error.invalidParameters");
    }
    
    
    @Test
    public void testCreateAndRetrieveTeam() throws Exception {
        TeamDao teamDao = mapper.teamDao();
        
        Team team = TestUtils.defaultTeam();
        teamDao.create(team);
        
        Team teamById = teamDao.findById(team.getId());
        
        assertThat(teamById).isNotNull();
        assertThat(teamById.getName()).isEqualTo(team.getName());
        assertThat(teamById.getCreatedDate()).isNotNull();
        assertThat(teamById.getLastModifiedDate()).isNotNull();
        
        UUID teamId = teamById.getId();
        teamDao.delete(teamById);
        
        teamById = teamDao.findById(teamId);
        assertThat(teamById).isNull();
        
    }
    
    
    @Test
    public void testCreateTeamWithExistingName() throws Exception {
        TeamDao teamDao = mapper.teamDao();
        
        Team team = TestUtils.defaultTeam();
        teamDao.create(team);
        
        Optional<Team> teamByName = teamDao.findByName(team.getName());
        
        assertThat(teamByName.isPresent()).isTrue();
        assertThat(teamByName.get().getName()).isEqualTo(team.getName());
        
        Team anotherTeam = TestUtils.defaultTeam();
        anotherTeam.setName(team.getName());
        
        assertThatThrownBy(() -> teamDao.create(anotherTeam))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("error.teamAlreadyExists");
        
        UUID teamId = team.getId();
        Team teamById = teamDao.findById(teamId);
        teamDao.delete(teamById);
        
        teamById = teamDao.findById(teamId);
        assertThat(teamById).isNull();
        
    }
    
    
    @Test
    public void testUpdateTeam() throws Exception {
        TeamDao teamDao = mapper.teamDao();
        
        Team team = TestUtils.defaultTeam();
        teamDao.create(team);
        
        Team teamById = teamDao.findById(team.getId());
        
        assertThat(teamById).isNotNull();
        assertThat(teamById.getName()).isEqualTo(team.getName());
        
        String updatedName = team.getName() + " - Updated";
        String updatedDescription = team.getDescription() + " (Updated)";
        
        teamById.setName(updatedName);
        teamById.setDescription(updatedDescription);
        teamDao.save(teamById);
        
        teamById = teamDao.findById(team.getId());
        
        assertThat(teamById.getName()).isEqualTo(updatedName);
        assertThat(teamById.getDescription()).isEqualTo(updatedDescription);
        
        UUID teamId = team.getId();
        teamById = teamDao.findById(teamId);
        teamDao.delete(teamById);
        
        teamById = teamDao.findById(teamId);
        assertThat(teamById).isNull();
        
    }
    
    
    @Test
    public void testFindAllTeams() throws Exception {
        TeamDao teamDao = mapper.teamDao();
        
        Team team = TestUtils.defaultTeam();
        teamDao.create(team);
        
        PagingIterable<Team> teams = teamDao.findAll();
        int count = teams.all().size();
        
        assertThat(count >= 1).isTrue();
        
        UUID teamId = team.getId();
        Team teamById = teamDao.findById(teamId);
        teamDao.delete(teamById);
        
        teamById = teamDao.findById(teamId);
        assertThat(teamById).isNull();
        
    }
    
    
    @Test
    public void testAddTeamMember() throws Exception {
        TeamDao teamDao = mapper.teamDao();
        
        Team team = TestUtils.defaultTeam();
        teamDao.create(team);
        
        User user = TestUtils.defaultUser();
        teamDao.addTeamMember(team.getId(), user.getUserId());
        
        ResultSet teamMembersResult = teamDao.teamMembers(team.getId());
        List<Row> rows = teamMembersResult.all();
        
        assertThat(rows.isEmpty()).isFalse();
        
        teamDao.removeTeamMember(team.getId(), user.getUserId());
        
        teamMembersResult = teamDao.teamMembers(team.getId());
        rows = teamMembersResult.all();
        
        assertThat(rows.isEmpty()).isTrue();
        
        UUID teamId = team.getId();
        Team teamById = teamDao.findById(teamId);
        teamDao.delete(teamById);
        
        teamById = teamDao.findById(teamId);
        assertThat(teamById).isNull();
        
    }
    
}
