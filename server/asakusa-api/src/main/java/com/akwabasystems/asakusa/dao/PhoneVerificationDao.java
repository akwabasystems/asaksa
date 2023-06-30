
package com.akwabasystems.asakusa.dao;

import com.akwabasystems.asakusa.model.PhoneNumberVerification;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.DefaultNullSavingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import static com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy.SET_TO_NULL;


@Dao
@DefaultNullSavingStrategy(SET_TO_NULL)
public interface PhoneVerificationDao {

    /**
     * Adds a new phone number verification
     * 
     * @param verification       the phone number verification to add
     */
    @Insert
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void addVerificationCode(PhoneNumberVerification verification);
    
    
    /**
     * Finds the verification associated with the specified phone number
     * 
     * @param phoneNumber    the phone number for which to return the verification
     * @return the verification associated with the specified phone number
     */
    @Select(customWhereClause = "phone_number = :phoneNumber")
    PhoneNumberVerification findByPhoneNumber(@CqlName("phoneNumber") String phoneNumber);

    
    /**
     * Deletes the specified phone number verification
     * 
     * @param verification      the phone number verification to delete
     */
    @Delete
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void delete(PhoneNumberVerification verification) throws Exception;
    
}
