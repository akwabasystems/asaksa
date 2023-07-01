
package com.akwabasystems.asakusa.dao;

import com.akwabasystems.asakusa.model.Membership;
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
public interface MembershipDao {

    /**
     * Adds a new membership
     * 
     * @param membership       the membership to add
     */
    @Insert
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void add(Membership membership);
    
    
    /**
     * Updates a membership
     * 
     * @param membership      the membership to update
     * @throws Exception if the membership cannot be updated
     */
    @Update
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void save(Membership membership) throws Exception;
    
    
    /**
     * Finds the membership for the specified user ID
     * 
     * @param userId    the user ID for which to find the membership
     * @return the membership for the specified user ID
     */
    @Select
    Membership findByUserId(String userId);

    
    /**
     * Deletes the specified membership
     * 
     * @param membership      the membership to delete
     */
    @Delete
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void delete(Membership membership);
    
}
