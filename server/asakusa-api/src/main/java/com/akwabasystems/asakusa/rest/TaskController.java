
package com.akwabasystems.asakusa.rest;

import com.akwabasystems.asakusa.model.Task;
import com.akwabasystems.asakusa.rest.service.TaskService;
import com.akwabasystems.asakusa.rest.utils.ApplicationError;
import com.akwabasystems.asakusa.rest.utils.AuthorizationTicket;
import com.akwabasystems.asakusa.rest.utils.QueryParameter;
import com.akwabasystems.asakusa.rest.utils.QueryUtils;
import com.akwabasystems.asakusa.utils.PrintUtils;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v3/projects")
@Log
public class TaskController extends BaseController {

    @Autowired
    private TaskService taskService;
    
    
    /**
     * Handles a request to create a new task
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param id            the ID of the project for which to create the task
     * @param map           the request body
     * @return the details of the newly created task
     * @throws Exception if the request fails
     */
    @PostMapping("/{id}/tasks")
    public ResponseEntity<?> createTask(HttpServletRequest request,
                                        HttpServletResponse response,
                                        @PathVariable String id,
                                        @RequestBody LinkedHashMap<String,Object> map) 
                                        throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) QueryUtils.getValueRequired(map, QueryParameter.USER_ID);

        String title = (String) QueryUtils.getValueRequired(map, QueryParameter.TITLE);
        String teamId = (String) QueryUtils.getValueRequired(map, QueryParameter.TEAM_ID);
        String description = (String) QueryUtils.getValueWithDefault(map, QueryParameter.DESCRIPTION, "");
        String timezone = (String) QueryUtils.getValueWithDefault(map, QueryParameter.TIMEZONE, 
                ZoneId.systemDefault().getId());
        
        Map<String,Object> parameterMap = new LinkedHashMap<>();
        parameterMap.put(QueryParameter.TITLE, StringEscapeUtils.escapeJava(title));
        
        if (!description.isEmpty()) {
            parameterMap.put(QueryParameter.DESCRIPTION, StringEscapeUtils.escapeJava(description));
        }
        
        parameterMap.put(QueryParameter.TIMEZONE, timezone);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.ASSIGNEE_ID);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.DEPENDS_ON);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.ESTIMATED_DURATION);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.START_DATE);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.PRIORITY);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.TAGS);

        AuthorizationTicket authTicket = getAuthorizationTicket(userId, accessToken);
        Task task = taskService.createTask(authTicket, teamId, id, parameterMap);
        
        if (task != null) {
            String location = String.format("/api/v3/projects/%s/tasks/%s", 
                    id, task.getId().toString());
        
            /** Return an HTTP 201 (Created) response with the task details */
            return ResponseEntity.created(new URI(location)).body(task);
            
        } else {
            log.severe(String.format("[taskController#createTask] - Error creating task - Title: %s - Project: %s%n",
                    title, id));
            
            ProblemDetail details = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
            details.setTitle(ApplicationError.HTTP_ERROR);
            details.setInstance(new URI(request.getRequestURI()));
            return ResponseEntity.of(details).build();
        }
        
    }


    /**
     * Handles a request to retrieve the tasks for the specified project
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param id            the ID of the project for whom to retrieve the tasks
     * @return the list of tasks for the specified project
     * @throws Exception if the request fails
     */
    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<Task>> projectTasks(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   @PathVariable String id) 
                                                   throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) request.getParameter(QueryParameter.USER_ID);
        
        List<Task> projectTasks = taskService.findProjectTasks(
            getAuthorizationTicket(userId, accessToken),
            UUID.fromString(id)
        );
        
        return ResponseEntity.ok(projectTasks);
    }
    

    /**
     * Handles a request to retrieve a task by ID
     * 
     * @param request   the incoming request
     * @param response  the outgoing response
     * @param id        the ID of the project for the task
     * @param taskId    the ID of the task to find
     * @return the task with the specified ID
     * @throws Exception if the request fails
     */
    @GetMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<Task> taskById(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @PathVariable("id") String id,
                                         @PathVariable("taskId") String taskId) 
                                         throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) request.getParameter(QueryParameter.USER_ID);
        
        Task task = taskService.findTaskById(
            getAuthorizationTicket(userId, accessToken),
            UUID.fromString(id), 
            UUID.fromString(taskId)
        );
        
        if (task == null) {
            ProblemDetail details = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
            details.setTitle(ApplicationError.TASK_NOT_FOUND);
            details.setInstance(new URI(request.getRequestURI()));
            return ResponseEntity.of(details).build();
        } else {
            return ResponseEntity.ok(task);
        }
    }
    
    
    /**
     * Handles a request to update a task
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param id            the ID of the project for the task
     * @param taskId        the ID of the task to update
     * @param map           the request body
     * @return the details of the updated task
     * @throws Exception if the request fails
     */
    @PutMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<?> updateTask(HttpServletRequest request,
                                        HttpServletResponse response,
                                        @PathVariable("id") String id,
                                        @PathVariable("taskId") String taskId, 
                                        @RequestBody LinkedHashMap<String,Object> map) 
                                        throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) QueryUtils.getValueRequired(map, QueryParameter.USER_ID);
        String timezone = (String) QueryUtils.getValueWithDefault(map, QueryParameter.TIMEZONE, 
                ZoneId.systemDefault().getId());
        
        Map<String,Object> parameterMap = new LinkedHashMap<>();
        parameterMap.put(QueryParameter.TIMEZONE, timezone);
        
        if (map.containsKey(QueryParameter.TITLE)) {
            String title = (String) map.get(QueryParameter.TITLE);
            parameterMap.put(QueryParameter.TITLE, StringEscapeUtils.escapeJava(title));
        }
        
        if (map.containsKey(QueryParameter.DESCRIPTION)) {
            String description = (String) map.get(QueryParameter.DESCRIPTION);
            parameterMap.put(QueryParameter.DESCRIPTION, StringEscapeUtils.escapeJava(description));
        }
        
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.ASSIGNEE_ID);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.DEPENDS_ON);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.ESTIMATED_DURATION);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.START_DATE);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.END_DATE);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.PRIORITY);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.STATUS);
        QueryUtils.populateMapIfPresent(map, parameterMap, QueryParameter.TAGS);
        
        AuthorizationTicket authTicket = getAuthorizationTicket(userId, accessToken);
        Task updatedTask = taskService.updateTask(authTicket, id, taskId, parameterMap);
        
        return ResponseEntity.ok(updatedTask);
        
    }
    
    
    /**
     * Handles a request to delete a task
     * 
     * @param request       the incoming request
     * @param response      the outgoing response
     * @param id            the ID of the project for the task
     * @param taskId        the ID of the task to delete
     * @param map           the request body
     * @return the status of the delete operation
     * @throws Exception if the request fails
     */
    @DeleteMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<?> deleteTask(HttpServletRequest request,
                                        HttpServletResponse response,
                                        @PathVariable("id") String id,
                                        @PathVariable("taskId") String taskId, 
                                        @RequestBody LinkedHashMap<String,Object> map) 
                                        throws Exception {
        String accessToken = (String) request.getHeader(QueryParameter.ACCESS_TOKEN);
        String userId = (String) QueryUtils.getValueRequired(map, QueryParameter.USER_ID);
        
        AuthorizationTicket authTicket = getAuthorizationTicket(userId, accessToken);
        taskService.deleteTask(authTicket, UUID.fromString(id), UUID.fromString(taskId));
        
        return ResponseEntity.noContent().build();
        
    }

}
