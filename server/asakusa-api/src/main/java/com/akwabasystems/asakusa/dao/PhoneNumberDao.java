
package com.akwabasystems.asakusa.dao;

import com.akwabasystems.asakusa.model.PhoneNumber;
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
public interface PhoneNumberDao {

    /**
     * Adds a new phone number
     * 
     * @param phoneNumber       the phone number to add
     */
    @Insert
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void add(PhoneNumber phoneNumber);
    
    
    /**
     * Updates a phone number
     * 
     * @param phoneNumber      the phone number to update
     * @throws Exception if the phone number cannot be updated
     */
    @Update
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void save(PhoneNumber phoneNumber) throws Exception;
    
    
    /**
     * Finds the phone number associated with the specified user ID
     * 
     * @param userId    the user ID associated with the phone number
     * @return the phone number associated with the specified user ID
     */
    @Select
    PhoneNumber findByUserId(String userId);

    
    /**
     * Deletes the specified phone number
     * 
     * @param phoneNumber      the phone number to delete
     */
    @Delete
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void delete(PhoneNumber phoneNumber) throws Exception;
    
}
