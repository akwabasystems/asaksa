
package com.akwabasystems.asakusa.rest.service;

import com.akwabasystems.asakusa.dao.TeamDao;
import com.akwabasystems.asakusa.model.Team;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.akwabasystems.asakusa.repository.SchemaNames;
import com.akwabasystems.asakusa.rest.utils.AuthorizationTicket;
import com.akwabasystems.asakusa.rest.utils.ApplicationError;
import com.akwabasystems.asakusa.rest.utils.QueryParameter;
import com.akwabasystems.asakusa.utils.Timeline;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Log
public class TeamService {

    @Autowired
    private CqlSession cqlSession;
    
    private RepositoryMapper mapper;
    
    
    @PostConstruct
    public void initialize() {
        mapper = RepositoryMapper.builder(cqlSession).build();
    }
    
    
    /**
     * Creates a team
     * 
     * @param authTicket    the ticket used to authorize the request
     * @param teamDetails   an object with the details of the team to create
     * @return the newly created team
     * @throws Exception if the request fails
     */
    public Team createTeam(AuthorizationTicket authTicket,
                           Map<String,Object> teamDetails) throws Exception {
        TeamDao teamDao = mapper.teamDao();
        
        String name = (String) teamDetails.get(QueryParameter.NAME);
        String createdBy = (String) teamDetails.get(QueryParameter.CREATED_BY);
        String description = (String) teamDetails.get(QueryParameter.DESCRIPTION);
        
        Optional<Team> existingTeam = teamDao.findByName(name);
        
        if (existingTeam.isPresent()) {
            throw new Exception(ApplicationError.TEAM_ALREADY_EXISTS);
        }

        Team team = new Team(UUID.randomUUID(), name);
        team.setDescription(description);
        team.setCreatedBy(createdBy);
        team.setCreatedDate(Timeline.currentDateTimeUTCString());
        team.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        
        return teamDao.create(team);

    }
    
    
    /**
     * Finds a team by ID
     * 
     * @param authTicket    the ticket used to authorize the request
     * @param teamId
     * @return the team with the specified ID
     */
    public Team findTeamById(AuthorizationTicket authTicket, UUID teamId) {
        TeamDao teamDao = mapper.teamDao();
        return teamDao.findById(teamId);
    }


    /**
     * Finds all the teams in the application
     * 
     * @param authTicket   the ticket used to authorize the request
     * @return all the teams in the application
     */
    public List<Team> findAll(AuthorizationTicket authTicket) {
        TeamDao teamDao = mapper.teamDao();
        PagingIterable<Team> teams = teamDao.findAll();
        
        return teams.all();
    }
    
    
    /**
     * Updates a team
     * 
     * @param authTicket    the ticket used to authorize the request
     * @param teamDetails   an object with the updated team details
     * @return the updated team
     * @throws Exception if the request fails
     */
    public Team updateTeam(AuthorizationTicket authTicket,
                           Map<String,Object> teamDetails) throws Exception {
        
        TeamDao teamDao = mapper.teamDao();
        
        String teamId = (String) teamDetails.get(QueryParameter.ID);
        UUID teamUUID = UUID.fromString(teamId);

        Team teamById = teamDao.findById(teamUUID);

        if (teamById == null) {
            throw new Exception(ApplicationError.TEAM_NOT_FOUND);
        }
        
        String name = (String) teamDetails.get(QueryParameter.NAME);
        Optional<Team> teamByName = teamDao.findByName(name);
        
        if (teamByName.isPresent()) {
            throw new Exception(ApplicationError.TEAM_ALREADY_EXISTS);
        }

        teamById.setName(name);
        
        if (teamDetails.containsKey(QueryParameter.DESCRIPTION)) {
            teamById.setDescription((String) teamDetails.get(QueryParameter.DESCRIPTION));
        }
        
        teamById.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        teamDao.save(teamById);
                
        return teamById;
        
    }
    
    
    /**
     * Finds all the members of the specified team
     * 
     * @param authTicket    the ticket used to authorize the request
     * @param teamId        the ID of the team for which to find the members
     * @return all the members of the specified team
     */
    public List<String> findTeamMembers(AuthorizationTicket authTicket, UUID teamId) {
        TeamDao teamDao = mapper.teamDao();

        ResultSet teamMembersResult = teamDao.teamMembers(teamId);
        return teamMembersResult.all()
                        .stream()
                        .map((row) -> row.getString(SchemaNames.COLUMN_USER_ID))
                        .collect(Collectors.toList());
    }
    
    
    /**
     * Adds a member to the specified team
     * 
     * @param authTicket    the ticket used to authorize the request
     * @param teamId        the ID of the team to add the member to
     * @param memberId      the ID of the member to add
     * @return the updated members list
     */
    public List<String> addTeamMember(AuthorizationTicket authTicket, 
                                      UUID teamId,
                                      String memberId) {
        TeamDao teamDao = mapper.teamDao();
        teamDao.addTeamMember(teamId, memberId);
        
        /** Return the updated members list for convenience */
        ResultSet teamMembersResult = teamDao.teamMembers(teamId);
        return teamMembersResult.all()
                        .stream()
                        .map((row) -> row.getString(SchemaNames.COLUMN_USER_ID))
                        .collect(Collectors.toList());
    }
    
    
    /**
     * Removes a member from the specified team
     * 
     * @param authTicket    the ticket used to authorize the request
     * @param teamId        the ID of the team from which to remove to member
     * @param memberId      the ID of the member to remove
     * @return the updated members list
     */
    public List<String> removeTeamMember(AuthorizationTicket authTicket, 
                                         UUID teamId,
                                         String memberId) {
        TeamDao teamDao = mapper.teamDao();
        teamDao.removeTeamMember(teamId, memberId);
        
        /** Return the updated members list for convenience */
        ResultSet teamMembersResult = teamDao.teamMembers(teamId);
        return teamMembersResult.all()
                        .stream()
                        .map((row) -> row.getString(SchemaNames.COLUMN_USER_ID))
                        .collect(Collectors.toList());
    }
    
    
}
