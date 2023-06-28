
package com.akwabasystems.asakusa.dao;

import com.akwabasystems.asakusa.model.ProjectActivity;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.DefaultNullSavingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import static com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy.SET_TO_NULL;
import java.util.UUID;


@Dao
@DefaultNullSavingStrategy(SET_TO_NULL)
public interface ProjectActivityDao {

    /**
     * Adds a new project activity
     * 
     * @param activity      the project activity to add
     */
    @Insert
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void create(ProjectActivity activity);
    
  
    /**
     * Finds a project activity by ID
     * 
     * @param projectId     the ID of the project for which to retrieve the activity
     * @param id            the ID of the activity to find
     * @return the project activity with the specified ID
     */
    @Select
    ProjectActivity findById(UUID projectId, UUID id);

    
    /**
     * Returns the activities for the specified project
     *
     * @param projectId     the ID of the project for which to return the activities
     * @return the activities for the specified project
     */
    @Select(customWhereClause = "project_id = :projectId")
    PagingIterable<ProjectActivity> findAll(@CqlName("projectId") UUID projectId);
    
}
