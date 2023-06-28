
package com.akwabasystems.asakusa.dao;

import com.akwabasystems.asakusa.model.Task;
import com.akwabasystems.asakusa.utils.Timeline;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.DefaultNullSavingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Query;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import com.datastax.oss.driver.api.mapper.annotations.Update;
import static com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy.SET_TO_NULL;
import java.util.UUID;


@Dao
@DefaultNullSavingStrategy(SET_TO_NULL)
public interface TaskDao {

    /**
     * Creates a new task
     * 
     * @param task      the task to create
     */
    @Insert
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void create(Task task);
    
    
    /**
     * Updates a task
     * 
     * @param task      the task to update
     * @throws Exception if the task cannot be updated
     */
    @Update
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void save(Task task) throws Exception;
    
    
    /**
     * Finds a task by ID
     * 
     * @param projectId     the ID of the project for this task
     * @param id            the ID of the task to find
     * @return the task with the specified ID
     */
    @Select
    Task findById(UUID projectId, UUID id);

    
    /**
     * Returns the list of all the tasks for the specified project
     *
     * @param projectId        the ID of the project for which to return the tasks
     * @return the list of all the tasks for the specified project
     */
    @Select(customWhereClause = "project_id = :projectId")
    PagingIterable<Task> findTasksByProject(@CqlName("projectId") UUID projectId);
    
    
    /**
     * Assigns a task to the specified user
     * 
     * @param projectId     the ID of the project for the task
     * @param taskId        the ID of the task to assign
     * @param userId            the ID of the user to assign the task to
     */
    @Query("INSERT INTO user_tasks (assignee_id, project_id, task_id) VALUES (:userId, :projectId, :taskId)")
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void addTaskAssignee(@CqlName("projectId") UUID projectId, 
                         @CqlName("taskId") UUID taskId, 
                         @CqlName("userId") String userId);
    
    
    /**
     * Returns the list of all the tasks assigned to the specified user
     *
     * @param projectId     the ID of the project in which to look for the tasks
     * @param userId        the ID of the user for whom to find the tasks
     * @return the list of all the tasks assigned to the specified user
     */
    @Query("SELECT * FROM user_tasks WHERE project_id = :projectId AND assignee_id = :userId")
    ResultSet findTasksByAssignee(@CqlName("projectId") UUID projectId, 
                                  @CqlName("userId") String userId);
    
    
    /**
     * Unassigns a task from the specified user
     * 
     * @param projectId     the ID of the project for the task
     * @param taskId        the ID of the task to unassign
     * @param userId        the ID of the user from whom to unassign the task
     */
    @Query("DELETE FROM user_tasks WHERE assignee_id = :userId AND project_id = :projectId AND task_id = :taskId")
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void removeTaskAssignee(@CqlName("projectId") UUID projectId, 
                      @CqlName("taskId") UUID taskId, 
                      @CqlName("userId") String userId);
    
    /**
     * Assigns a task to the specified user. 
     * 
     * This is the default implementation of {@code TaskDao#addTaskAssignee}. It
     * updates the appropriate properties of the task before saving it and 
     * invoking {@code TaskDao@addTaskAssignee}.
     * 
     * @param task      the task to assign
     * @param userId    the ID of the new assignee
     * @throws an exception if the task cannot be assigned
     */
    default void assignTask(Task task, String userId) throws Exception {
        task.setAssigneeId(userId);
        task.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        save(task);
        addTaskAssignee(task.getProjectId(), task.getId(), userId);
    }
    
    
    /**
     * Unassigns a task from the specified user
     * 
     * This is the default implementation of {@code TaskDao#removeTaskAssignee}. 
     * It updates the appropriate properties of the task before saving it and 
     * invoking {@code TaskDao@removeTaskAssignee}.
     * 
     * @param task      the task to unassign
     * @param userId    the ID of current task assignee
     * @throws an exception if the task cannot be unassigned
     */
    default void unassignTask(Task task, String userId) throws Exception {
        task.setAssigneeId(null);
        task.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        save(task);
        removeTaskAssignee(task.getProjectId(), task.getId(), userId);
    }
    
    
    /**
     * Deletes the specified task
     * 
     * @param task      the task to delete
     * @throws an exception if the task cannot be deleted
     */
    @Delete
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void delete(Task task) throws Exception;
    
    
    /**
     * Deletes the specified task
     * 
     * This is the default implementation of {@code TaskDao#delete}. 
     * If the task is currently assigned to a user, it removes the assignment
     * before invoking {@code TaskDao@delete}.
     * 
     * @param task      the task to delete
     * @throws an exception if the task cannot be deleted
     */
    default void deleteTask(Task task) throws Exception {
        if (task.getAssigneeId() != null) {
            removeTaskAssignee(task.getProjectId(), task.getId(), task.getAssigneeId());
        }
        
        delete(task);
    }
    
}
