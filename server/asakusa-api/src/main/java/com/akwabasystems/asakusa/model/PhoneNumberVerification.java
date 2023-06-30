
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.utils.Timeline;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


@Entity
@CqlName("phone_number_verifications")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class PhoneNumberVerification {

    @PartitionKey
    private String phoneNumber;
    
    private UUID id;
    private String code;

    @CqlName("created_at")
    private String createdDate;
    
    @CqlName("expires_at")
    private String expirationDate;


    public PhoneNumberVerification() {}
    
    public PhoneNumberVerification(String phoneNumber, String code, UUID id) {
        this.phoneNumber = phoneNumber;
        this.code = code;
        this.id = id;
        
        /** Set the validity of the verification code to 10 minutes */
        ZonedDateTime currentTimeUTC = ZonedDateTime.now(Timeline.timezoneUTC());
        ZonedDateTime expirationTimeUTC = currentTimeUTC.plus(10, ChronoUnit.MINUTES);
        createdDate = Timeline.toUTCFormat(currentTimeUTC);
        expirationDate = Timeline.toUTCFormat(expirationTimeUTC);
    }
    
    
    @Override
    public String toString() {
       return String.format("PhoneNumberVerification { PhoneNumber: '%s', code: '%s', expires: '%s' }", 
               getPhoneNumber(), getCode(), getExpirationDate());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PhoneNumberVerification)) {
            return false;
        }

        if (object == this) {
            return true;
        }
        
        PhoneNumberVerification verification = (PhoneNumberVerification) object;
        return (verification.getId() != null && verification.getId().equals(getId())) &&
               (verification.getPhoneNumber() != null && verification.getPhoneNumber().equals(getPhoneNumber())) &&
               (verification.getCode() != null && verification.getCode().equals(getCode()));
    }

    @Override
    public int hashCode() {
        int result = 17 * ((getId() != null)? getId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getPhoneNumber() != null)? getPhoneNumber().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getCode() != null)? getCode().hashCode() : Integer.hashCode(1));
        
        return result;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

}
