
package com.akwabasystems.asakusa.dao;

import com.akwabasystems.asakusa.model.AccessToken;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.DefaultNullSavingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import com.datastax.oss.driver.api.mapper.annotations.Update;
import static com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy.SET_TO_NULL;


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
     * Finds the access token for the specified device ID
     * 
     * @param deviceId  the ID of the device for which to retrieve the access token
     * @return the access token for the specified device ID
     */
    @Select
    AccessToken findById(String deviceId);

   
    /**
     * Deletes the specified access token
     * 
     * @param accessToken   the access token to delete
     */
    @Delete
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void delete(AccessToken accessToken);
    
}
