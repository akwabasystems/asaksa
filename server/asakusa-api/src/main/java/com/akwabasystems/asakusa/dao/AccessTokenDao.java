
package com.akwabasystems.asakusa.dao;

import com.akwabasystems.asakusa.model.AccessToken;
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
public interface AccessTokenDao {

    /**
     * Creates a new access token
     * 
     * @param accessToken   the access token to create
     */
    @Insert
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void create(AccessToken accessToken);
    
    
    /**
     * Updates an access token
     * 
     * @param accessToken   the access token to update
     * @throws Exception if the session cannot be updated
     */
    @Update
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void save(AccessToken accessToken) throws Exception;
    
    
    /**
     * Finds an access token by ID
     * 
     * @param userId    the user ID for which to find the token
     * @param id        the ID of the access token to find
     * @return the access token with the specified ID
     */
    @Select
    AccessToken findById(String userId, UUID id);

    
    /**
     * Returns all the access tokens for the specified user
     *
     * @param userId    the ID of the user for whom to return the tokens
     * @return all the access tokens for the specified user
     */
    @Select
    PagingIterable<AccessToken> findAll(String userId);
    
    
    /**
     * Deletes the specified access token
     * 
     * @param accessToken   the access token to delete
     */
    @Delete
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void delete(AccessToken accessToken);
    
}
