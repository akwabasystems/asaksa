
package com.akwabasystems.asakusa.dao;

import com.akwabasystems.asakusa.model.UserSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.DefaultNullSavingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import com.datastax.oss.driver.api.mapper.annotations.Update;
import static com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy.SET_TO_NULL;
import java.util.UUID;


@Dao
@DefaultNullSavingStrategy(SET_TO_NULL)
public interface UserSessionDao {

    /**
     * Creates a new user session
     * 
     * @param session       the user session to create
     */
    @Insert
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void create(UserSession session);
    
    
    /**
     * Updates a user session
     * 
     * @param session      the user session to update
     * @throws Exception if the session cannot be updated
     */
    @Update
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void save(UserSession session) throws Exception;
    
    
    /**
     * Finds a user session by ID
     * 
     * @param userId    the user ID for which to find the session
     * @param id        the ID of the session to find
     * @return user session with the specified ID
     */
    @Select
    UserSession findById(String userId, UUID id);

    
    /**
     * Returns all the sessions for the specified user
     *
     * @param userId    the ID of the user for whom to return the sessions
     * @return all the sessions for the specified user
     */
    @Select
    PagingIterable<UserSession> findAll(String userId);
    
    
    /**
     * Deletes the specified user session
     * 
     * @param session   the user session to delete
     */
    @Delete
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void delete(UserSession session);
    
}
