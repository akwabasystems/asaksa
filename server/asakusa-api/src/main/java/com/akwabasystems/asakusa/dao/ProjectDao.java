
package com.akwabasystems.asakusa.dao;

import com.akwabasystems.asakusa.model.Project;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import com.datastax.oss.driver.api.mapper.annotations.Update;
import java.util.UUID;


@Dao
public interface ProjectDao {

    /**
     * Creates a new Project
     * 
     * @param project      the project to create
     */
    @Insert
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void create(Project project);
    
    
    /**
     * Updates a project
     * 
     * @param project      the project to update
     * @throws Exception if the project cannot be updated
     */
    @Update
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void save(Project project) throws Exception;
    
    
    /**
     * Finds a project by ID
     * 
     * @param id    the ID of the team to find
     * @return the team with the specified ID
     */
    @Select
    Project findById(UUID teamId, UUID id);
    
    
    /**
     * Deletes the specified project
     * 
     * @param project      the project to delete
     * @return true if the project is deleted successfully; otherwise, returns false
     * /
    @Delete
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    boolean delete(Project project) throws Exception;*/
    
    /**
     * Returns the list of all the projects for the specified team
     *
     * @param teamId        the ID of the team for which to return the projects
     * @return the list of all the projects for the specified team
     */
    @Select(customWhereClause = "team_id = :teamId")
    PagingIterable<Project> findProjectsByTeam(@CqlName("teamId") UUID teamId);
    
    
    /**
     * Returns the list of all the projects owned by the specified user
     *
     * @param userId        the ID of the project owner
     * @return the list of all the projects owned by the specified user
     */
    @Select(customWhereClause = "owner_id = :userId", allowFiltering = true)
    PagingIterable<Project> findProjectsByOwner(@CqlName("userId") String userId);
    
}
