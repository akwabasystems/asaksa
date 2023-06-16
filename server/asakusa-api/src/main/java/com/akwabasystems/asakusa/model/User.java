
package com.akwabasystems.asakusa.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.naming.NamingConvention;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;


@Entity
@CqlName("users")
@NamingStrategy(convention = NamingConvention.SNAKE_CASE_INSENSITIVE)
public class User {

    @PartitionKey
    private String userId;
    
    private String name;
    private String givenName;
    private String familyName;
    private String middleName;
    private String nickname;
    private String preferredUsername;
    private String profile;
    private String picture;
    private String website;
    private String email;
    private boolean emailVerified;
    private Gender gender;
    private LocalDate birthDate;
    private String zoneInfo;
    private Locale locale;
    private String phoneNumber;
    private boolean phoneNumberVerified;
    private Address address;
    
    @CqlName("updated_at")
    private Instant lastModifiedDate;
    
    public User() {}
    
    public User(String userId, String givenName, String familyName, String email) {
        this.userId = userId;
        this.givenName = givenName;
        this.familyName = familyName;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public void setPreferredUsername(String preferredUsername) {
        this.preferredUsername = preferredUsername;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getZoneInfo() {
        return zoneInfo;
    }

    public void setZoneInfo(String zoneInfo) {
        this.zoneInfo = zoneInfo;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isPhoneNumberVerified() {
        return phoneNumberVerified;
    }

    public void setPhoneNumberVerified(boolean phoneNumberVerified) {
        this.phoneNumberVerified = phoneNumberVerified;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return String.format("User { userId: %s, givenName: %s, familyName: %s, email: %s",
                getUserId(), getGivenName(), getFamilyName(), getEmail());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof User)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        User user = (User) object;
        return (user.getUserId()!= null && user.getUserId().equals(getUserId())) &&
               (user.getFamilyName()!= null && user.getFamilyName().equals(getFamilyName())) &&
               (user.getEmail() != null && user.getEmail().equals(getEmail()));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result * ((getUserId()!= null) ? getUserId().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getFamilyName()!= null) ? getFamilyName().hashCode() : Integer.hashCode(1));
        result = 31 * result * ((getEmail() != null) ? getEmail().hashCode() : Integer.hashCode(1));

        return result;
    }
}
