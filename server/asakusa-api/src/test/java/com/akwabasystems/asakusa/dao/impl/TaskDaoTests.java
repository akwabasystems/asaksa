
package com.akwabasystems.asakusa.dao.impl;

import com.akwabasystems.asakusa.BaseTestSuite;
import com.akwabasystems.asakusa.dao.TaskDao;
import com.akwabasystems.asakusa.model.ItemPriority;
import com.akwabasystems.asakusa.model.ItemStatus;
import com.akwabasystems.asakusa.model.Project;
import com.akwabasystems.asakusa.model.Task;
import com.akwabasystems.asakusa.model.User;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.akwabasystems.asakusa.utils.TestUtils;
import com.akwabasystems.asakusa.utils.Timeline;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class TaskDaoTests extends BaseTestSuite {

    @Autowired
    private CqlSession cqlSession;
    
    private RepositoryMapper mapper;
    
    
    @BeforeAll
    public void setup() {
        mapper = RepositoryMapper.builder(cqlSession).build();
    }
    
    
    @Test
    public void testDaoInitialization() {
        TaskDao taskDao = mapper.taskDao();
        assertThat(taskDao).isNotNull();
    }
    
    
    @Test
    public void testCreateAndRetrieveTask() throws Exception {
        TaskDao taskDao = mapper.taskDao();
        
        Project project = TestUtils.defaultProject();
        Task task = new Task(project.getId(), UUID.randomUUID(), 
            "Task " + TestUtils.randomSuffix());
        task.setStartDate(Timeline.currentDateTimeUTCString());
        task.setCreatedDate(Timeline.currentDateTimeUTCString());
        task.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        
        taskDao.create(task);
        
        Task taskById = taskDao.findById(project.getId(), task.getId());
        
        assertThat(taskById).isNotNull();
        assertThat(taskById.getStatus()).isEqualTo(ItemStatus.TODO);
        assertThat(taskById.getPriority()).isEqualTo(ItemPriority.MEDIUM);
        assertThat(taskById.getEstimatedDuration()).isEqualTo(86400);
        
        taskDao.deleteTask(taskById);
        
        taskById = taskDao.findById(project.getId(), task.getId());
        assertThat(taskById).isNull();
        
    }
    
    
    @Test
    public void testSaveTask() throws Exception {
        TaskDao taskDao = mapper.taskDao();
        
        Project project = TestUtils.defaultProject();
        Task task = new Task(project.getId(), UUID.randomUUID(), 
            "Task " + TestUtils.randomSuffix());
        task.setStartDate(Timeline.currentDateTimeUTCString());
        task.setCreatedDate(Timeline.currentDateTimeUTCString());
        task.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        
        taskDao.create(task);
        
        Task taskById = taskDao.findById(project.getId(), task.getId());
        
        taskById.setTitle(taskById.getTitle() + " (Updated)");
        taskById.setStatus(ItemStatus.COMPLETED);
        taskById.setPriority(ItemPriority.HIGH);
        taskDao.save(taskById);
        
        taskById = taskDao.findById(project.getId(), task.getId());
        
        assertThat(taskById.getTitle()).contains("(Updated)");
        assertThat(taskById.getStatus()).isEqualTo(ItemStatus.COMPLETED);
        assertThat(taskById.getPriority()).isEqualTo(ItemPriority.HIGH);
        
        taskDao.deleteTask(taskById);
        
        taskById = taskDao.findById(project.getId(), task.getId());
        assertThat(taskById).isNull();
    }
    
    
    @Test
    public void testRetrieveTasksByProject() throws Exception {
        TaskDao taskDao = mapper.taskDao();
        
        Project project = TestUtils.defaultProject();
        Task task = new Task(project.getId(), UUID.randomUUID(), 
            "Task " + TestUtils.randomSuffix());
        task.setStartDate(Timeline.currentDateTimeUTCString());
        task.setCreatedDate(Timeline.currentDateTimeUTCString());
        task.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        
        taskDao.create(task);
                
        PagingIterable<Task> tasksForProject = taskDao.findTasksByProject(project.getId());
        List<Task> taskList = tasksForProject.all();
        
        assertThat(taskList.isEmpty()).isFalse();
        assertThat(taskList.get(0).getProjectId()).isEqualTo(project.getId());
        
        Task taskById = taskDao.findById(project.getId(), task.getId());
        taskDao.deleteTask(taskById);
        
        taskById = taskDao.findById(project.getId(), task.getId());
        assertThat(taskById).isNull();
        
    }
    
    
    @Test
    public void testAssignAndUnassignTask() throws Exception {
        TaskDao taskDao = mapper.taskDao();
        
        User user = TestUtils.defaultUser();
        Project project = TestUtils.defaultProject();
        Task task = new Task(project.getId(), UUID.randomUUID(), 
            "Task " + TestUtils.randomSuffix());
        task.setStartDate(Timeline.currentDateTimeUTCString());
        task.setCreatedDate(Timeline.currentDateTimeUTCString());
        task.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        
        taskDao.create(task);
        taskDao.assignTask(task, user.getUserId());
        
        Task assignedTask = taskDao.findById(project.getId(), task.getId());
        assertThat(assignedTask).isNotNull();
        assertThat(assignedTask.getAssigneeId()).isEqualTo(user.getUserId());
        
        ResultSet userTasksResult = taskDao.findTasksByAssignee(project.getId(), user.getUserId());
        List<Row> rows = userTasksResult.all();
        assertThat(rows.isEmpty()).isFalse();
        
        taskDao.unassignTask(assignedTask, user.getUserId());
        
        Task taskById = taskDao.findById(project.getId(), assignedTask.getId());
        assertThat(taskById.getAssigneeId()).isNull();
        
        userTasksResult = taskDao.findTasksByAssignee(project.getId(), user.getUserId());
        rows = userTasksResult.all();
        assertThat(rows.isEmpty()).isTrue();
        
        taskDao.deleteTask(taskById);
        
        taskById = taskDao.findById(project.getId(), task.getId());
        assertThat(taskById).isNull();
    }
    
    
    @Test
    public void testDeleteAssignedTask() throws Exception {
        TaskDao taskDao = mapper.taskDao();
        
        User user = TestUtils.defaultUser();
        Project project = TestUtils.defaultProject();
        Task task = new Task(project.getId(), UUID.randomUUID(), 
            "Task " + TestUtils.randomSuffix());
        task.setStartDate(Timeline.currentDateTimeUTCString());
        task.setCreatedDate(Timeline.currentDateTimeUTCString());
        task.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        
        taskDao.create(task);
        taskDao.assignTask(task, user.getUserId());
        
        Task assignedTask = taskDao.findById(project.getId(), task.getId());
        assertThat(assignedTask).isNotNull();
        assertThat(assignedTask.getAssigneeId()).isEqualTo(user.getUserId());
        
        ResultSet userTasksResult = taskDao.findTasksByAssignee(project.getId(), user.getUserId());
        List<Row> rows = userTasksResult.all();
        
        assertThat(rows.isEmpty()).isFalse();
        
        taskDao.deleteTask(assignedTask);
        
        Task taskById = taskDao.findById(project.getId(), assignedTask.getId());
        assertThat(taskById).isNull();
        
    }
}
