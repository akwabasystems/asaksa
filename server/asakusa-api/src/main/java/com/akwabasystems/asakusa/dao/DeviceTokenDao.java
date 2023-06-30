
package com.akwabasystems.asakusa.dao;

import com.akwabasystems.asakusa.model.DeviceToken;
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
public interface DeviceTokenDao {

    /**
     * Adds a new device token
     * 
     * @param deviceToken       the device token to add
     */
    @Insert
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void add(DeviceToken deviceToken);
    
    
    /**
     * Updates a device token
     * 
     * @param deviceToken      the device token to update
     * @throws Exception if the device token cannot be updated
     */
    @Update
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void save(DeviceToken deviceToken) throws Exception;
    
    
    /**
     * Finds the token associated with the specified device ID
     * 
     * @param deviceId  the ID of the device for which to retrieve the token
     * @return the token associated with the specified device ID
     */
    @Select
    DeviceToken findById(String deviceId);

    
    /**
     * Deletes the specified device token
     * 
     * @param deviceToken      the device token to delete
     */
    @Delete
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void delete(DeviceToken deviceToken) throws Exception;
    
}
