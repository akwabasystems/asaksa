
package com.akwabasystems.asakusa.rest.service;

import com.akwabasystems.asakusa.dao.ProjectDao;
import com.akwabasystems.asakusa.dao.TaskDao;
import com.akwabasystems.asakusa.model.ItemPriority;
import com.akwabasystems.asakusa.model.ItemStatus;
import com.akwabasystems.asakusa.model.Project;
import com.akwabasystems.asakusa.model.Task;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.akwabasystems.asakusa.rest.utils.AuthorizationTicket;
import com.akwabasystems.asakusa.rest.utils.ApplicationError;
import com.akwabasystems.asakusa.rest.utils.QueryParameter;
import com.akwabasystems.asakusa.utils.Timeline;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import jakarta.annotation.PostConstruct;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import static java.util.stream.Collectors.toSet;
import java.util.stream.Stream;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Log
public class TaskService {

    @Autowired
    private CqlSession cqlSession;
    
    private RepositoryMapper mapper;
    
    
    @PostConstruct
    public void initialize() {
        mapper = RepositoryMapper.builder(cqlSession).build();
    }
    
    
    /**
     * Creates a task
     * 
     * @param authTicket    the ticket used to authorize the request
     * @param teamId        the ID of the team for the project
     * @param projectId     the ID of the project for which to create the task
     * @param taskDetails   an object with the details of the task to create
     * @return the newly created task
     * @throws Exception if the request fails
     */
    public Task createTask(AuthorizationTicket authTicket,
                           String teamId,
                           String projectId,
                           Map<String,Object> taskDetails) throws Exception {
        ProjectDao projectDao = mapper.projectDao();
        TaskDao taskDao = mapper.taskDao();
        
        Project project = projectDao.findById(UUID.fromString(teamId), UUID.fromString(projectId));
        
        if (project == null) {
            throw new Exception(ApplicationError.PROJECT_NOT_FOUND);
        }
        
        String title = (String) taskDetails.get(QueryParameter.TITLE);
        String description = (String) taskDetails.get(QueryParameter.DESCRIPTION);
        String timezoneId = (String) taskDetails.get(QueryParameter.TIMEZONE);
        
        Task task = new Task(project.getId(), UUID.randomUUID(), title);
        task.setDescription(description);
        
        if (taskDetails.containsKey(QueryParameter.ASSIGNEE_ID)) {
            task.setAssigneeId((String) taskDetails.get(QueryParameter.ASSIGNEE_ID));
        }
        
        if (taskDetails.containsKey(QueryParameter.DEPENDS_ON)) {
            String dependsOn = (String) taskDetails.get(QueryParameter.DEPENDS_ON);
            task.setDependsOn(UUID.fromString(dependsOn));
        }
        
        if (taskDetails.containsKey(QueryParameter.ESTIMATED_DURATION)) {
            task.setEstimatedDuration((int) taskDetails.get(QueryParameter.ESTIMATED_DURATION));
        }
        
        if (taskDetails.containsKey(QueryParameter.START_DATE)) {
            String startDate = (String) taskDetails.get(QueryParameter.START_DATE);
            task.setStartDate(parseDateTime(startDate, timezoneId));
        }
        
        if (taskDetails.containsKey(QueryParameter.PRIORITY)) {
            ItemPriority priority = ItemPriority.fromString((String) taskDetails.get(QueryParameter.PRIORITY));
            task.setPriority(priority);
        }
        
        if (taskDetails.containsKey(QueryParameter.TAGS)) {
            String tags = (String) taskDetails.get(QueryParameter.TAGS);
            Set<String> tagList = Stream.of(tags.split(",")).collect(toSet());
            task.setTags(tagList);
        }
        
        task.setCreatedDate(Timeline.currentDateTimeUTCString());
        task.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        taskDao.create(task);
        
        return task;

    }
    
    
    private String parseDateTime(String dateComponents, String timezoneId) {
        ZonedDateTime parsedDate = Timeline.dateTimeFromComponents(ZoneId.of(timezoneId),
                dateComponents.split(","));
        return parsedDate.format(DateTimeFormatter.ISO_INSTANT);
    }
    
    
    /**
     * Finds all the tasks for the specified project
     * 
     * @param authTicket    the ticket used to authorize the request
     * @param projectId     the ID of the project for which to find the tasks
     * @return all the tasks for the specified project
     */
    public List<Task> findProjectTasks(AuthorizationTicket authTicket, UUID projectId) {
        TaskDao taskDao = mapper.taskDao();
        PagingIterable<Task> projectTasks = taskDao.findTasksByProject(projectId);
        
        return projectTasks.all();
    }
    
    
    /**
     * Finds a tasks by ID
     * 
     * @param authTicket    the ticket used to authorize the request
     * @param projectId     the ID of the project for the task
     * @param taskId        the ID of the task to find
     * @return the task with the specified ID
     */
    public Task findTaskById(AuthorizationTicket authTicket, 
                                UUID projectId,
                                UUID taskId) {
        TaskDao taskDao = mapper.taskDao();
        return taskDao.findById(projectId, taskId);
    }


    /**
     * Updates a project
     * 
     * @param authTicket    the ticket used to authorize the request
     * @param projectId     the ID of the project for the task
     * @param taskId        the ID of the task to update
     * @param taskDetails   an object with the updated details for the task
     * @return the updated task
     * @throws Exception if the request fails
     */
    public Task updateTask(AuthorizationTicket authTicket,
                           String projectId,
                           String taskId,
                           Map<String,Object> taskDetails) throws Exception {
        TaskDao taskDao = mapper.taskDao();
        
        Task task = taskDao.findById(UUID.fromString(projectId), UUID.fromString(taskId));
        
        if (task == null) {
            throw new Exception(ApplicationError.TASK_NOT_FOUND);
        }
        
        String timezoneId = (String) taskDetails.get(QueryParameter.TIMEZONE);
        
        if (taskDetails.containsKey(QueryParameter.TITLE)) {
            task.setTitle((String) taskDetails.get(QueryParameter.TITLE));
        }
        
        if (taskDetails.containsKey(QueryParameter.DESCRIPTION)) {
            task.setDescription((String) taskDetails.get(QueryParameter.DESCRIPTION));
        }
        
        if (taskDetails.containsKey(QueryParameter.ASSIGNEE_ID)) {
            task.setAssigneeId((String) taskDetails.get(QueryParameter.ASSIGNEE_ID));
        }
        
        if (taskDetails.containsKey(QueryParameter.DEPENDS_ON)) {
            String dependsOn = (String) taskDetails.get(QueryParameter.DEPENDS_ON);
            task.setDependsOn(UUID.fromString(dependsOn));
        }
        
        if (taskDetails.containsKey(QueryParameter.ESTIMATED_DURATION)) {
            task.setEstimatedDuration((int) taskDetails.get(QueryParameter.ESTIMATED_DURATION));
        }
        
        if (taskDetails.containsKey(QueryParameter.PRIORITY)) {
            ItemPriority priority = ItemPriority.fromString((String) taskDetails.get(QueryParameter.PRIORITY));
            task.setPriority(priority);
        }
        
        if (taskDetails.containsKey(QueryParameter.STATUS)) {
            ItemStatus status = ItemStatus.fromString((String) taskDetails.get(QueryParameter.STATUS));
            task.setStatus(status);
        }
        
        if (taskDetails.containsKey(QueryParameter.START_DATE)) {
            String startDate = (String) taskDetails.get(QueryParameter.START_DATE);
            task.setStartDate(parseDateTime(startDate, timezoneId));
        }
        
        if (taskDetails.containsKey(QueryParameter.END_DATE)) {
            String endDate = (String) taskDetails.get(QueryParameter.END_DATE);
            task.setEndDate(parseDateTime(endDate, timezoneId));
        }
        
        if (taskDetails.containsKey(QueryParameter.TAGS)) {
            String tags = (String) taskDetails.get(QueryParameter.TAGS);
            Set<String> tagList = Stream.of(tags.split(",")).collect(toSet());
            task.setTags(tagList);
        }
        
        task.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        taskDao.save(task);

        return task;

    }
    
    
    /**
     * Deletes a task
     * 
     * @param authTicket    the ticket used to authorize the request
     * @param projectId     the ID of the project for the task
     * @param taskId        the ID of the task to delete
     * @throws Exception if the request fails
     */
    public void deleteTask(AuthorizationTicket authTicket, 
                              UUID projectId,
                              UUID taskId) throws Exception {
        TaskDao taskDao = mapper.taskDao();
        Task task = findTaskById(authTicket, projectId, taskId);
        
        if (task == null) {
            throw new Exception(ApplicationError.TASK_NOT_FOUND);
        }
        
        taskDao.deleteTask(task);
    }
    
    
}
