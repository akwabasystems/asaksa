
package com.akwabasystems.asakusa.rest;

import com.akwabasystems.asakusa.model.Project;
import com.akwabasystems.asakusa.rest.service.ProjectService;
import com.akwabasystems.asakusa.rest.utils.ApplicationError;
import com.akwabasystems.asakusa.rest.utils.AuthorizationTicket;
import com.akwabasystems.asakusa.rest.utils.QueryParameter;
import com.akwabasystems.asakusa.rest.utils.QueryUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.time.ZoneId;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v3/teams")
@Log
public class ProjectController extends BaseController {

    @Autowired
    private ProjectService projectService;
    
    
    /**
     * Handles a request to create a new project
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param id            the ID of the team for the project
     * @param map           the request body
     * @return the details of the newly created project
     * @throws Exception if the request fails
     */
    @PostMapping("/{id}/projects")
    public ResponseEntity<?> createProject(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @PathVariable String id,
                                           @RequestBody LinkedHashMap<String,Object> map) 
                                                         throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) QueryUtils.getValueRequired(map, QueryParameter.USER_ID);

        String name = (String) QueryUtils.getValueRequired(map, QueryParameter.NAME);
        String description = (String) QueryUtils.getValueWithDefault(map, QueryParameter.DESCRIPTION, "");
        String timezone = (String) QueryUtils.getValueWithDefault(map, QueryParameter.TIMEZONE, 
                ZoneId.systemDefault().getId());
        
        Map<String,Object> parameterMap = new LinkedHashMap<>();
        parameterMap.put(QueryParameter.NAME, StringEscapeUtils.escapeJava(name));
        
        if (!description.isEmpty()) {
            parameterMap.put(QueryParameter.DESCRIPTION, StringEscapeUtils.escapeJava(description));
        }
        
        parameterMap.put(QueryParameter.TIMEZONE, timezone);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.OWNER_ID);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.START_DATE);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.DEADLINE);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.PRIORITY);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.CAPACITY);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.TAGS);

        AuthorizationTicket authTicket = getAuthorizationTicket(userId, accessToken);
        Project project = projectService.createProject(authTicket, id, parameterMap);
        
        if (project != null) {
            String location = String.format("/api/v3/team/%s/projects/%s", 
                    id, project.getId().toString());
        
            /** Return an HTTP 201 (Created) response with the user details */
            return ResponseEntity.created(new URI(location)).body(project);
            
        } else {
            log.severe(String.format("[projectController#createProject] - Error creating project - Name: %s - Team: %s%n",
                    name, id));
            
            ProblemDetail details = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
            details.setTitle(ApplicationError.HTTP_ERROR);
            details.setInstance(new URI(request.getRequestURI()));
            return ResponseEntity.of(details).build();
        }
        
    }


    /**
     * Handles a request to retrieve the projects for the specified team
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param id            the ID of the team for whom to retrieve the projects
     * @return the list of projects for the specified team
     * @throws Exception if the request fails
     */
    @GetMapping("/{id}/projects")
    public ResponseEntity<List<Project>> teamProjects(HttpServletRequest request,
                                                      HttpServletResponse response,
                                                      @PathVariable String id) 
                                                      throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) request.getParameter(QueryParameter.USER_ID);
        
        List<Project> teamProjects = projectService.findTeamProjects(
            getAuthorizationTicket(userId, accessToken),
            UUID.fromString(id)
        );
        
        return ResponseEntity.ok(teamProjects);
    }
    


    /**
     * Handles a request to retrieve the details of a project
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param id            the ID of the team for the project
     * @param projectId     the ID of the project to find
     * @return the project with the specified ID
     * @throws Exception if the request fails
     */
    @GetMapping("/{id}/projects/{projectId}")
    public ResponseEntity<Project> projectById(HttpServletRequest request,
                                               HttpServletResponse response,
                                               @PathVariable("id") String id,
                                               @PathVariable("projectId") String projectId) 
                                               throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) request.getParameter(QueryParameter.USER_ID);
        
        Project project = projectService.findProjectById(
            getAuthorizationTicket(userId, accessToken),
            UUID.fromString(id), 
            UUID.fromString(projectId)
        );
        
        if (project == null) {
            ProblemDetail details = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
            details.setTitle(ApplicationError.PROJECT_NOT_FOUND);
            details.setInstance(new URI(request.getRequestURI()));
            return ResponseEntity.of(details).build();
        } else {
            return ResponseEntity.ok(project);
        }
    }
    
    
    /**
     * Handles a request to update a new project
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param id            the ID of the team for the project
     * @param projectId     the ID of the project to update
     * @param map           the request body
     * @return the details of the updated project
     * @throws Exception if the request fails
     */
    @PutMapping("/{id}/projects/{projectId}")
    public ResponseEntity<?> updateProject(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @PathVariable("id") String id,
                                           @PathVariable("projectId") String projectId, 
                                           @RequestBody LinkedHashMap<String,Object> map) 
                                           throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) QueryUtils.getValueRequired(map, QueryParameter.USER_ID);
        String timezone = (String) QueryUtils.getValueWithDefault(map, QueryParameter.TIMEZONE, 
                ZoneId.systemDefault().getId());
        
        Map<String,Object> parameterMap = new LinkedHashMap<>();
        parameterMap.put(QueryParameter.TIMEZONE, timezone);
        
        if (map.containsKey(QueryParameter.NAME)) {
            String name = (String) map.get(QueryParameter.NAME);
            parameterMap.put(QueryParameter.NAME, StringEscapeUtils.escapeJava(name));
        }
        
        if (map.containsKey(QueryParameter.DESCRIPTION)) {
            String description = (String) map.get(QueryParameter.DESCRIPTION);
            parameterMap.put(QueryParameter.DESCRIPTION, StringEscapeUtils.escapeJava(description));
        }
        
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.OWNER_ID);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.START_DATE);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.END_DATE);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.DEADLINE);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.PRIORITY);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.CAPACITY);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.TAGS);

        AuthorizationTicket authTicket = getAuthorizationTicket(userId, accessToken);
        Project updatedProject = projectService.updateProject(authTicket, id, projectId, parameterMap);
        
        return ResponseEntity.ok(updatedProject);
        
    }

}
