
package com.akwabasystems.asakusa.dao.impl;

import com.akwabasystems.asakusa.BaseTestSuite;
import com.akwabasystems.asakusa.dao.ProjectDao;
import com.akwabasystems.asakusa.model.ItemPriority;
import com.akwabasystems.asakusa.model.ItemStatus;
import com.akwabasystems.asakusa.model.Project;
import com.akwabasystems.asakusa.model.Team;
import com.akwabasystems.asakusa.model.User;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.akwabasystems.asakusa.utils.TestUtils;
import com.akwabasystems.asakusa.utils.Timeline;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class ProjectDaoTests extends BaseTestSuite {

    @Autowired
    private CqlSession cqlSession;
    
    private RepositoryMapper mapper;
    
    
    @BeforeAll
    public void setup() {
        mapper = RepositoryMapper.builder(cqlSession).build();
    }
    
    
    @Test
    public void testTeamDaoInitialization() {
        ProjectDao projectDao = mapper.projectDao();
        assertThat(projectDao).isNotNull();
    }
    
    
    @Test
    public void testCreateAndRetrieverProject() throws Exception {
        ProjectDao projectDao = mapper.projectDao();
        Team team = TestUtils.defaultTeam();
        
        Project project = new Project(team.getId(), UUID.randomUUID(), 
                "Project " + TestUtils.randomSuffix());
        project.setCreatedDate(Timeline.currentDateTimeUTCString());
        project.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        projectDao.create(project);
        
        Project projectById = projectDao.findById(team.getId(), project.getId());
        
        assertThat(projectById).isNotNull();
        assertThat(projectById.getName()).isEqualTo(project.getName());
        assertThat(projectById.getTeamId()).isEqualTo(team.getId());
        assertThat(projectById.getStatus()).isEqualTo(ItemStatus.TODO);
        assertThat(projectById.getPriority()).isEqualTo(ItemPriority.MEDIUM);
        assertThat(projectById.getCapacity()).isEqualTo(10);
        
    }
    
    
    @Test
    public void testSaveProject() throws Exception {
        ProjectDao projectDao = mapper.projectDao();
        Team team = TestUtils.defaultTeam();
        
        Project project = new Project(team.getId(), UUID.randomUUID(), 
                "Project " + TestUtils.randomSuffix());
        project.setCreatedDate(Timeline.currentDateTimeUTCString());
        project.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        projectDao.create(project);
        
        Project projectById = projectDao.findById(team.getId(), project.getId());
        
        projectById.setName(projectById.getName() + " (Updated)");
        projectById.setStatus(ItemStatus.DONE);
        projectById.setPriority(ItemPriority.HIGH);
        projectDao.save(projectById);
        
        projectById = projectDao.findById(team.getId(), project.getId());
        
        assertThat(projectById.getName()).contains("(Updated)");
        assertThat(projectById.getStatus()).isEqualTo(ItemStatus.DONE);
        assertThat(projectById.getPriority()).isEqualTo(ItemPriority.HIGH);
        
    }
    
    
    @Test
    public void testRetrieveProjectsByTeam() throws Exception {
        ProjectDao projectDao = mapper.projectDao();
        
        Team team = TestUtils.defaultTeam();
        
        Project project = new Project(team.getId(), UUID.randomUUID(), 
                "Project " + TestUtils.randomSuffix());
        project.setCreatedDate(Timeline.currentDateTimeUTCString());
        project.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        projectDao.create(project);
                
        PagingIterable<Project> teamProjects = projectDao.findProjectsByTeam(team.getId());
        List<Project> projectList = teamProjects.all();
        
        assertThat(projectList.isEmpty()).isFalse();
        assertThat(projectList.get(0).getTeamId()).isEqualTo(team.getId());
        
    }
    
    
    @Test
    public void testRetrieveProjectsByOwner() throws Exception {
        ProjectDao projectDao = mapper.projectDao();
        
        Team team = TestUtils.defaultTeam();
        User user = TestUtils.defaultUser();
        
        Project project = new Project(team.getId(), UUID.randomUUID(), 
                "Project " + TestUtils.randomSuffix());
        project.setOwnerId(user.getUserId());
        project.setCreatedDate(Timeline.currentDateTimeUTCString());
        project.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        projectDao.create(project);
                
        PagingIterable<Project> userProjects = projectDao.findProjectsByOwner(user.getUserId());
        List<Project> projectList = userProjects.all();
        
        assertThat(projectList.isEmpty()).isFalse();
        assertThat(projectList.get(0).getOwnerId()).isEqualTo(user.getUserId());
        
    }
    
}
