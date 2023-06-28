
package com.akwabasystems.asakusa.dao.impl;

import com.akwabasystems.asakusa.BaseTestSuite;
import com.akwabasystems.asakusa.dao.ProjectActivityDao;
import com.akwabasystems.asakusa.model.ActivityType;
import com.akwabasystems.asakusa.model.Project;
import com.akwabasystems.asakusa.model.ProjectActivity;
import com.akwabasystems.asakusa.model.Team;
import com.akwabasystems.asakusa.model.User;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.akwabasystems.asakusa.utils.TestUtils;
import com.akwabasystems.asakusa.utils.Timeline;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class ProjectActivityDaoTests extends BaseTestSuite {

    @Autowired
    private CqlSession cqlSession;
    
    private RepositoryMapper mapper;
    
    
    @BeforeAll
    public void setup() {
        mapper = RepositoryMapper.builder(cqlSession).build();
    }
    
    
    @Test
    public void testTeamDaoInitialization() {
        ProjectActivityDao activityDao = mapper.projectActivityDao();
        assertThat(activityDao).isNotNull();
    }
    
    
    @Test
    public void testAddProjectActivity() throws Exception {
        ProjectActivityDao activityDao = mapper.projectActivityDao();
        
        Team team = TestUtils.defaultTeam();
        Project project = new Project(team.getId(), UUID.randomUUID(), 
                "Project " + TestUtils.randomSuffix());
        
        User user = TestUtils.defaultUser();
        
        ProjectActivity activity = new ProjectActivity(project.getId(), 
                UUID.randomUUID(), user.getUserId(), ActivityType.PROJECT);
        
        Map<String,String> details = new HashMap<>();
        details.put("projectId", project.getId().toString());
        details.put("projectName", project.getName());
        activity.setDetails(new JSONObject(details));
        activity.setCreatedDate(Timeline.currentDateTimeUTCString());
        activityDao.create(activity);
        
        ProjectActivity projectActivity = activityDao.findById(project.getId(), activity.getId());
        
        assertThat(projectActivity).isNotNull();
        assertThat(projectActivity.getActor()).isEqualTo(user.getUserId());
        assertThat(projectActivity.getDetails().has("projectId")).isTrue();
        assertThat(projectActivity.getProjectId()).isEqualTo(project.getId());
        
    }
    
    
    @Test
    public void testGetRecentProjectActivities() throws Exception {
        ProjectActivityDao activityDao = mapper.projectActivityDao();
        
        Team team = TestUtils.defaultTeam();
        Project project = new Project(team.getId(), UUID.randomUUID(), 
                "Project " + TestUtils.randomSuffix());
        
        User user = TestUtils.defaultUser();
        
        ProjectActivity activity1 = new ProjectActivity(project.getId(), 
                UUID.randomUUID(), user.getUserId(), ActivityType.PROJECT);
        
        Map<String,String> details = new HashMap<>();
        details.put("projectId", project.getId().toString());
        details.put("projectName", project.getName());
        activity1.setDetails(new JSONObject(details));
        activity1.setCreatedDate(Timeline.currentDateTimeUTCString());
        activityDao.create(activity1);
        
        ProjectActivity activity2 = new ProjectActivity(project.getId(), 
                UUID.randomUUID(), user.getUserId(), ActivityType.TASK);
        
        details = new HashMap<>();
        details.put("taskId", UUID.randomUUID().toString());
        details.put("taskTitle", "UI Research task");
        activity2.setDetails(new JSONObject(details));
        activity2.setCreatedDate(Timeline.currentDateTimeUTCString());
        activityDao.create(activity2);
        
        PagingIterable<ProjectActivity> recentActivities = activityDao.findAll(project.getId());
        List<ProjectActivity> activityList = recentActivities.all();
        
        assertThat(activityList.isEmpty()).isFalse();
        assertThat(activityList.size() >= 2).isTrue();
        
    }
    
}
