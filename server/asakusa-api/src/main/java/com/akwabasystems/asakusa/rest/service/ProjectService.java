
package com.akwabasystems.asakusa.rest.service;

import com.akwabasystems.asakusa.dao.ProjectDao;
import com.akwabasystems.asakusa.dao.TeamDao;
import com.akwabasystems.asakusa.model.ItemPriority;
import com.akwabasystems.asakusa.model.ItemStatus;
import com.akwabasystems.asakusa.model.Project;
import com.akwabasystems.asakusa.model.Team;
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
public class ProjectService {

    @Autowired
    private CqlSession cqlSession;
    
    private RepositoryMapper mapper;
    
    
    @PostConstruct
    public void initialize() {
        mapper = RepositoryMapper.builder(cqlSession).build();
    }
    
    
    /**
     * Creates a project
     * 
     * @param authTicket        the ticket used to authorize the request
     * @param teamId            the ID of the team for the project
     * @param projectDetails    an object with the details of the project to create
     * @return the newly created project
     * @throws Exception if the request fails
     */
    public Project createProject(AuthorizationTicket authTicket,
                                 String teamId,
                                 Map<String,Object> projectDetails) throws Exception {
        TeamDao teamDao = mapper.teamDao();
        ProjectDao projectDao = mapper.projectDao();
        
        Team team = teamDao.findById(UUID.fromString(teamId));
        
        if (team == null) {
            throw new Exception(ApplicationError.TEAM_NOT_FOUND);
        }
        
        String name = (String) projectDetails.get(QueryParameter.NAME);
        String description = (String) projectDetails.get(QueryParameter.DESCRIPTION);
        String timezoneId = (String) projectDetails.get(QueryParameter.TIMEZONE);
        
        Project project = new Project(team.getId(), UUID.randomUUID(), name);
        project.setDescription(description);
        
        if (projectDetails.containsKey(QueryParameter.OWNER_ID)) {
            project.setOwnerId((String) projectDetails.get(QueryParameter.OWNER_ID));
        }
        
        if (projectDetails.containsKey(QueryParameter.CAPACITY)) {
            project.setCapacity((int) projectDetails.get(QueryParameter.CAPACITY));
        }
        
        if (projectDetails.containsKey(QueryParameter.PRIORITY)) {
            ItemPriority priority = ItemPriority.fromString((String) projectDetails.get(QueryParameter.PRIORITY));
            project.setPriority(priority);
        }
        
        if (projectDetails.containsKey(QueryParameter.START_DATE)) {
            String startDate = (String) projectDetails.get(QueryParameter.START_DATE);
            project.setStartDate(parseDateTime(startDate, timezoneId));
        }
        
        if (projectDetails.containsKey(QueryParameter.DEADLINE)) {
            String deadline = (String) projectDetails.get(QueryParameter.DEADLINE);
            project.setDeadline(parseDateTime(deadline, timezoneId));
        }
        
        if (projectDetails.containsKey(QueryParameter.TAGS)) {
            String tags = (String) projectDetails.get(QueryParameter.TAGS);
            Set<String> tagList = Stream.of(tags.split(",")).collect(toSet());
            project.setTags(tagList);
        }
        
        project.setCreatedDate(Timeline.currentDateTimeUTCString());
        project.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        projectDao.create(project);
        
        return project;

    }
    
    
    private String parseDateTime(String dateComponents, String timezoneId) {
        ZonedDateTime parsedDate = Timeline.dateTimeFromComponents(ZoneId.of(timezoneId),
                dateComponents.split(","));
        return parsedDate.format(DateTimeFormatter.ISO_INSTANT);
    }
    
    
    /**
     * Finds all the projects for the specified team
     * 
     * @param authTicket    the ticket used to authorize the request
     * @param teamId        the ID of the team for which to find the projects
     * @return all the projects of the specified team
     */
    public List<Project> findTeamProjects(AuthorizationTicket authTicket, UUID teamId) {
        ProjectDao projectDao = mapper.projectDao();
        PagingIterable<Project> teamProjects = projectDao.findProjectsByTeam(teamId);
        
        return teamProjects.all();
    }
    
    
    /**
     * Finds a project by ID
     * 
     * @param authTicket    the ticket used to authorize the request
     * @param teamId        the ID of the team for the project
     * @param projectId     the ID of the project to find
     * @return the project with the specified ID
     */
    public Project findProjectById(AuthorizationTicket authTicket, 
                                   UUID teamId,
                                   UUID projectId) {
        ProjectDao projectDao = mapper.projectDao();
        return projectDao.findById(teamId, projectId);
    }


    /**
     * Updates a project
     * 
     * @param authTicket        the ticket used to authorize the request
     * @param teamId            the ID of the team for the project
     * @param projectId         the ID of the project to update
     * @param projectDetails    an object with the updated details for the project
     * @return the updated project
     * @throws Exception if the request fails
     */
    public Project updateProject(AuthorizationTicket authTicket,
                                 String teamId,
                                 String projectId,
                                 Map<String,Object> projectDetails) throws Exception {
        ProjectDao projectDao = mapper.projectDao();
        
        Project project = projectDao.findById(
                UUID.fromString(teamId), UUID.fromString(projectId));
        
        if (project == null) {
            throw new Exception(ApplicationError.PROJECT_NOT_FOUND);
        }
        
        String timezoneId = (String) projectDetails.get(QueryParameter.TIMEZONE);
        
        if (projectDetails.containsKey(QueryParameter.NAME)) {
            project.setName((String) projectDetails.get(QueryParameter.NAME));
        }
        
        if (projectDetails.containsKey(QueryParameter.DESCRIPTION)) {
            project.setDescription((String) projectDetails.get(QueryParameter.DESCRIPTION));
        }
        
        if (projectDetails.containsKey(QueryParameter.OWNER_ID)) {
            project.setOwnerId((String) projectDetails.get(QueryParameter.OWNER_ID));
        }
        
        if (projectDetails.containsKey(QueryParameter.CAPACITY)) {
            project.setCapacity((int) projectDetails.get(QueryParameter.CAPACITY));
        }
        
        if (projectDetails.containsKey(QueryParameter.PRIORITY)) {
            ItemPriority priority = ItemPriority.fromString((String) projectDetails.get(QueryParameter.PRIORITY));
            project.setPriority(priority);
        }
        
        if (projectDetails.containsKey(QueryParameter.STATUS)) {
            ItemStatus status = ItemStatus.fromString((String) projectDetails.get(QueryParameter.STATUS));
            project.setStatus(status);
        }
        
        if (projectDetails.containsKey(QueryParameter.START_DATE)) {
            String startDate = (String) projectDetails.get(QueryParameter.START_DATE);
            project.setStartDate(parseDateTime(startDate, timezoneId));
        }
        
        if (projectDetails.containsKey(QueryParameter.END_DATE)) {
            String endDate = (String) projectDetails.get(QueryParameter.END_DATE);
            project.setEndDate(parseDateTime(endDate, timezoneId));
        }
        
        if (projectDetails.containsKey(QueryParameter.DEADLINE)) {
            String deadline = (String) projectDetails.get(QueryParameter.DEADLINE);
            project.setDeadline(parseDateTime(deadline, timezoneId));
        }
        
        if (projectDetails.containsKey(QueryParameter.TAGS)) {
            String tags = (String) projectDetails.get(QueryParameter.TAGS);
            Set<String> tagList = Stream.of(tags.split(",")).collect(toSet());
            project.setTags(tagList);
        }
        
        project.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        projectDao.save(project);
        
        return project;

    }
    
    
}
