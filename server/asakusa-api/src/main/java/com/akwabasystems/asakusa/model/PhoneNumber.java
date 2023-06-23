
package com.akwabasystems.asakusa.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.UUID;


@Entity
@CqlName("phone_numbers")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public final class PhoneNumber {

    private String userId;
    private UUID id;
    private String number;
    private String formattedNumber;
    private String countryId;
    private String countryCode;
    private PhoneNumberType type = PhoneNumberType.MOBILE;
    private ItemStatus status = ItemStatus.UNVERIFIED;
    private String createdDate;
    private String lastModifiedDate;
    
    public PhoneNumber() { }
    
    public PhoneNumber(String userId,
                       UUID id,
                       String countryCode,
                       String number,
                       PhoneNumberType type) {
        this.userId = userId;
        this.id = id;
        this.countryCode = countryCode;
        this.number = number;
        this.type = type;
    }
    
    public PhoneNumber(String countryCode, String number) {
        this.countryCode = countryCode;
        this.number = number;
    }
    
    public void setNumber(String input) {
        String normalizedInput = input.replaceAll("^\\s+", "").replaceAll("\\s+$","");
        this.number = normalizedInput;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PhoneNumber)) {
            return false;
        }

        if (object == this) {
            return true;
        }
        
        PhoneNumber number = (PhoneNumber) object;
        return (number.getId() != null && number.getId().equals(getId())) &&
               (number.getCountryCode()!= null && number.getCountryCode().equals(getCountryCode())) &&
               (number.getNumber()!= null && number.getNumber().equals(getNumber()));
    }
    
    @Override
    public int hashCode() {
        int result = 17 * ((getId() != null)? getId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getCountryCode()!= null)? getCountryCode().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getNumber()!= null)? getNumber().hashCode() : Integer.hashCode(1));
        
        return result;
    }
    
    /**
     * Returns an instance of this PhoneNumber object wrapped in a PhoneNumber object of the libphonenumber library
     * 
     * @param phoneNumber   the phone number to wrap
     * @return an instance of this PhoneNumber object wrapped in a PhoneNumber object of the libphonenumber library
     */
    private Phonenumber.PhoneNumber wrap(PhoneNumber phoneNumber) {
        Phonenumber.PhoneNumber number = new Phonenumber.PhoneNumber();
        number.setCountryCode(Integer.parseInt(phoneNumber.getCountryCode()));
        number.setNationalNumber(Long.parseLong(phoneNumber.getNumber()));
        
        return number;
    }
    
    /**
     * Returns true if this phone number matches the specified one; otherwise, returns false
     * 
     * @param number    the number with which to compare this phone number
     * @return true if this phone number matches the specified one; otherwise, returns false
     */
    public boolean matchesNumber(PhoneNumber number) {
        PhoneNumberUtil.MatchType type = PhoneNumberUtil.getInstance().isNumberMatch(wrap(this), wrap(number));
        
        return ((type == PhoneNumberUtil.MatchType.EXACT_MATCH) || 
                (type == PhoneNumberUtil.MatchType.NSN_MATCH));
    }
    
    /**
     * Returns true if this phone number exactly matches the specified one; otherwise, returns false
     * 
     * @param number    the number with which to compare this phone number
     * @return true if this phone number exactly matches the specified one; otherwise, returns false
     */
    public boolean isExactMatchToNumber(PhoneNumber number) {
        PhoneNumberUtil.MatchType type = PhoneNumberUtil.getInstance().isNumberMatch(wrap(this), wrap(number));
        
        return (type == PhoneNumberUtil.MatchType.EXACT_MATCH);
    }
    
    @Override
    public String toString() {
        return toInternationalFormat();
    }

    /**
     * Returns the string representation of this phone number, in international format (i.e. +18005551212)
     * 
     * @return the string representation of this phone number, in international format
     */
    public String toInternationalFormat() {
        return PhoneNumberUtil.getInstance().format(wrap(this), PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public String getFormattedNumber() {
        return formattedNumber;
    }

    public void setFormattedNumber(String formattedNumber) {
        this.formattedNumber = formattedNumber;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public PhoneNumberType getType() {
        return type;
    }

    public void setType(PhoneNumberType type) {
        this.type = type;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}
