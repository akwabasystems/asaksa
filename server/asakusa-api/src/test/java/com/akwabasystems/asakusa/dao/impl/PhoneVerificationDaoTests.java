
package com.akwabasystems.asakusa.dao.impl;

import com.akwabasystems.asakusa.BaseTestSuite;
import com.akwabasystems.asakusa.dao.PhoneVerificationDao;
import com.akwabasystems.asakusa.model.PhoneNumberVerification;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.datastax.oss.driver.api.core.CqlSession;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class PhoneVerificationDaoTests extends BaseTestSuite {

    @Autowired
    private CqlSession cqlSession;
    
    private RepositoryMapper mapper;
    
    
    @BeforeAll
    public void setup() {
        mapper = RepositoryMapper.builder(cqlSession).build();
    }
    
    
    @Test
    public void testTeamDaoInitialization() {
        PhoneVerificationDao phoneVerificationDao = mapper.phoneVerificationDao();
        assertThat(phoneVerificationDao).isNotNull();
    }
    
    
    @Test
    public void testPhoneNumberVerification() throws Exception {
        PhoneVerificationDao phoneVerificationDao = mapper.phoneVerificationDao();
        String phoneNumber = "+18005551212";
        
        String verificationCode = String.valueOf((int) Math.floor(Math.random() * 10000));
        PhoneNumberVerification phoneVerification = new PhoneNumberVerification(
                phoneNumber, verificationCode, UUID.randomUUID());
        
        phoneVerificationDao.addVerificationCode(phoneVerification);
        
        PhoneNumberVerification verification = phoneVerificationDao.findByPhoneNumber(phoneNumber);
        
        assertThat(verification).isNotNull();
        assertThat(verification.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(verification.getCode()).isEqualTo(verificationCode);
        assertThat(verification.getCreatedDate()).isNotNull();
        assertThat(verification.getExpirationDate()).isNotNull();
        
        phoneVerificationDao.delete(verification);
        
        verification = phoneVerificationDao.findByPhoneNumber(phoneNumber);        
        assertThat(verification).isNull();
        
    }
    
    
}
