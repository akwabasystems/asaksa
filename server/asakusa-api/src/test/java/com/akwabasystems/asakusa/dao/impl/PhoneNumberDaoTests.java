
package com.akwabasystems.asakusa.dao.impl;

import com.akwabasystems.asakusa.BaseTestSuite;
import com.akwabasystems.asakusa.dao.PhoneNumberDao;
import com.akwabasystems.asakusa.model.ItemStatus;
import com.akwabasystems.asakusa.model.PhoneNumber;
import com.akwabasystems.asakusa.model.PhoneNumberType;
import com.akwabasystems.asakusa.model.User;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.akwabasystems.asakusa.utils.TestUtils;
import com.akwabasystems.asakusa.utils.Timeline;
import com.datastax.oss.driver.api.core.CqlSession;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class PhoneNumberDaoTests extends BaseTestSuite {

    @Autowired
    private CqlSession cqlSession;
    
    private RepositoryMapper mapper;
    
    
    @BeforeAll
    public void setup() {
        mapper = RepositoryMapper.builder(cqlSession).build();
    }
    
    
    @Test
    public void testDaoInitialization() {
        PhoneNumberDao phoneNumberDao = mapper.phoneNumberDao();
        assertThat(phoneNumberDao).isNotNull();
    }
    
    
    @Test
    public void testAddPhoneNumber() throws Exception {
        PhoneNumberDao phoneNumberDao = mapper.phoneNumberDao();
        User user = TestUtils.defaultUser();
        
        PhoneNumber phoneNumber = new PhoneNumber(user.getUserId(), 
                UUID.randomUUID(), "+1", "8005551212", 
                PhoneNumberType.MOBILE);
        
        phoneNumber.setCountryId("US");
        phoneNumber.setFormattedNumber(phoneNumber.toInternationalFormat());
        phoneNumber.setCreatedDate(Timeline.currentDateTimeUTCString());
        phoneNumber.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        
        phoneNumberDao.add(phoneNumber);
        
        PhoneNumber userPhone = phoneNumberDao.findByUserId(user.getUserId());
        
        assertThat(userPhone).isNotNull();
        assertThat(userPhone.getCountryId()).isEqualTo("US");
        assertThat(userPhone.getCountryCode()).isEqualTo("+1");
        assertThat(userPhone.getNumber()).isEqualTo("8005551212");
        assertThat(userPhone.getFormattedNumber()).isEqualTo("+18005551212");
        assertThat(userPhone.getStatus()).isEqualTo(ItemStatus.UNVERIFIED);
        
        phoneNumberDao.delete(phoneNumber);
        
        userPhone = phoneNumberDao.findByUserId(user.getUserId());
        assertThat(userPhone).isNull();
    }
    
    
    @Test
    public void testUpdatePhoneNumber() throws Exception {
        PhoneNumberDao phoneNumberDao = mapper.phoneNumberDao();
        User user = TestUtils.defaultUser();
        
        PhoneNumber phoneNumber = new PhoneNumber(user.getUserId(), 
                UUID.randomUUID(), "+1", "8005551212", 
                PhoneNumberType.MOBILE);
        
        phoneNumber.setCountryId("US");
        phoneNumber.setFormattedNumber(phoneNumber.toInternationalFormat());
        phoneNumber.setCreatedDate(Timeline.currentDateTimeUTCString());
        phoneNumber.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        
        phoneNumberDao.add(phoneNumber);
        
        PhoneNumber userPhone = phoneNumberDao.findByUserId(user.getUserId());
        
        assertThat(userPhone.getFormattedNumber()).isEqualTo("+18005551212");
        assertThat(userPhone.getStatus()).isEqualTo(ItemStatus.UNVERIFIED);
        
        userPhone.setNumber("2123456789");
        userPhone.setFormattedNumber(userPhone.toInternationalFormat());
        userPhone.setType(PhoneNumberType.WORK);
        userPhone.setStatus(ItemStatus.VERIFIED);
        phoneNumberDao.save(userPhone);
        
        userPhone = phoneNumberDao.findByUserId(user.getUserId());
        
        assertThat(userPhone.toString()).isEqualTo("+12123456789");
        assertThat(userPhone.getFormattedNumber()).isEqualTo("+12123456789");
        assertThat(userPhone.getStatus()).isEqualTo(ItemStatus.VERIFIED);
        
        phoneNumberDao.delete(userPhone);
        
        userPhone = phoneNumberDao.findByUserId(user.getUserId());
        assertThat(userPhone).isNull();
        
    }
    
}
