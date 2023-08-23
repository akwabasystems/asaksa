
package com.akwabasystems.asakusa.rest;

import com.akwabasystems.asakusa.model.Team;
import com.akwabasystems.asakusa.rest.service.TeamService;
import com.akwabasystems.asakusa.rest.utils.ApplicationError;
import com.akwabasystems.asakusa.rest.utils.AuthorizationTicket;
import com.akwabasystems.asakusa.rest.utils.QueryParameter;
import com.akwabasystems.asakusa.rest.utils.QueryUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.java.Log;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/teams")
@Log
public class TeamController extends BaseController {

    @Autowired
    private TeamService teamService;
    
    
    /**
     * Handles a request to create a new team
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param map           the request body
     * @return the details of the newly created team
     * @throws Exception if the request fails
     */
    @PostMapping("")
    public ResponseEntity<?> createTeam(HttpServletRequest request,
                                        HttpServletResponse response,
                                        @RequestBody LinkedHashMap<String,Object> map) 
                                        throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) QueryUtils.getValueRequired(map, QueryParameter.USER_ID);

        String name = (String) QueryUtils.getValueRequired(map, QueryParameter.NAME);
        String createdBy = (String) QueryUtils.getValueRequired(map, QueryParameter.CREATED_BY);
        String description = (String) QueryUtils.getValueWithDefault(map, 
                QueryParameter.DESCRIPTION, "");
        
        Map<String,Object> parameterMap = new LinkedHashMap<>();
        parameterMap.put(QueryParameter.NAME, StringEscapeUtils.escapeJava(name));
        parameterMap.put(QueryParameter.CREATED_BY, createdBy);
        parameterMap.put(QueryParameter.DESCRIPTION, StringEscapeUtils.escapeJava(description));

        AuthorizationTicket authTicket = getAuthorizationTicket(userId, accessToken);
        Team team = teamService.createTeam(authTicket, parameterMap);
        
        if (team != null) {
            String location = String.format("/api/v3/teams/%s", team.getId().toString());
        
            /** Return an HTTP 201 (Created) response with the team details */
            return ResponseEntity.created(new URI(location)).body(team);
        }
        
        log.severe(String.format("[teamController#createTeam] - Error creating team with name %s", name));

        ProblemDetail details = problemDetails(HttpStatus.BAD_REQUEST, 
                request.getRequestURI(), ApplicationError.HTTP_ERROR);
        return ResponseEntity.of(details).build();

    }
    
    
    /**
     * Handles a request to retrieve all the teams
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @return a list of all the teams
     * @throws Exception if the request fails
     */
    @GetMapping("")
    public ResponseEntity<List<Team>> findAllTeams(HttpServletRequest request,
                                                   HttpServletResponse response) 
                                                   throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) request.getParameter(QueryParameter.USER_ID);
        
        List<Team> teams = teamService.findAll(getAuthorizationTicket(userId, accessToken));
        return ResponseEntity.ok(teams);
    }


    /**
     * Handles a request to retrieve a team by ID
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param id            the ID of the team to retrieve
     * @return the team with the specified ID
     * @throws Exception if the request fails
     */
    @GetMapping("/{id}")
    public ResponseEntity<Team> teamById(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @PathVariable String id) throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) request.getParameter(QueryParameter.USER_ID);
        
        Team team = teamService.findTeamById(
                getAuthorizationTicket(userId, accessToken),
                UUID.fromString(id));

        if (team == null) {
            ProblemDetail details = problemDetails(HttpStatus.NOT_FOUND, 
                    request.getRequestURI(), ApplicationError.TEAM_NOT_FOUND);
            return ResponseEntity.of(details).build();
        }
        
        return ResponseEntity.ok(team);
    }


    /**
     * Handles a request to update a team
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param map           the request body
     * @return the updated team
     * @throws Exception if the request fails
     */
    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @PathVariable String id,
                                           @RequestBody LinkedHashMap<String,Object> map) 
                                           throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) QueryUtils.getValueRequired(map, QueryParameter.USER_ID);
        
        String name = (String) QueryUtils.getValueRequired(map, QueryParameter.NAME);
        String description = (String) QueryUtils.getValueIfPresent(map, QueryParameter.DESCRIPTION);
        
        Map<String,Object> parameterMap = new HashMap<>();
        parameterMap.put(QueryParameter.ID, id);
        parameterMap.put(QueryParameter.NAME, StringEscapeUtils.escapeJava(name));
        
        if (description != null) {
            parameterMap.put(QueryParameter.DESCRIPTION, StringEscapeUtils.escapeJava(description));
        }
        
        AuthorizationTicket authTicket = getAuthorizationTicket(userId, accessToken);
        Team updatedTeam = teamService.updateTeam(authTicket, parameterMap);

        return ResponseEntity.ok(updatedTeam);
  
    }
    
    
    /**
     * Handles a request to retrieve the members of the specified team
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param id            the ID of the team for which to retrieve the members
     * @return the list of members for the specified team
     * @throws Exception if the request fails
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<String>> teamMembers(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    @PathVariable String id) 
                                                    throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) request.getParameter(QueryParameter.USER_ID);
        
        List<String> teamMemberIds = teamService.findTeamMembers(
                getAuthorizationTicket(userId, accessToken),
                UUID.fromString(id));
        
        return ResponseEntity.ok(teamMemberIds);
    }
    
    
    /**
     * Handles a request to add a member to the specified team
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param id            the ID of the team to which to add the member
     * @param map           the request body
     * @return the updated members list for the specified team
     * @throws Exception if the request fails
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<List<String>> addTeamMember(HttpServletRequest request,
                                                      HttpServletResponse response,
                                                      @PathVariable String id,
                                                      @RequestBody LinkedHashMap<String,Object> map) 
                                                      throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) QueryUtils.getValueRequired(map, QueryParameter.USER_ID);
        String memberId = (String) QueryUtils.getValueRequired(map, QueryParameter.MEMBER_ID);
        
        AuthorizationTicket authTicket = getAuthorizationTicket(userId, accessToken);
        List<String> teamMembers = teamService.addTeamMember(authTicket, UUID.fromString(id), memberId);

        return ResponseEntity.ok(teamMembers);
  
    }
    
    
    /**
     * Handles a request to remove a member from the specified team
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param id            the ID of the team from which to remove the member
     * @param memberId      the ID of the member to remove
     * @param map           the request body
     * @return the updated members list for the specified team
     * @throws Exception if the request fails
     */
    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<List<String>> removeTeamMember(HttpServletRequest request,
                                                         HttpServletResponse response,
                                                         @PathVariable("id") String id,
                                                         @PathVariable("memberId") String memberId,
                                                         @RequestBody LinkedHashMap<String,Object> map) 
                                                         throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) QueryUtils.getValueRequired(map, QueryParameter.USER_ID);
        
        AuthorizationTicket authTicket = getAuthorizationTicket(userId, accessToken);
        List<String> teamMembers = teamService.removeTeamMember(authTicket, 
                UUID.fromString(id), memberId);

        return ResponseEntity.ok(teamMembers);
  
    }

}
