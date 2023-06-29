
package com.akwabasystems.asakusa.dao.impl;

import com.akwabasystems.asakusa.BaseTestSuite;
import com.akwabasystems.asakusa.dao.DeviceTokenDao;
import com.akwabasystems.asakusa.model.DeviceToken;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.akwabasystems.asakusa.utils.Timeline;
import com.datastax.oss.driver.api.core.CqlSession;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class DeviceTokenDaoTests extends BaseTestSuite {

    @Autowired
    private CqlSession cqlSession;
    
    private RepositoryMapper mapper;
    
    
    @BeforeAll
    public void setup() {
        mapper = RepositoryMapper.builder(cqlSession).build();
    }
    
    
    @Test
    public void testDaoInitialization() {
        DeviceTokenDao deviceTokenDao = mapper.deviceTokenDao();
        assertThat(deviceTokenDao).isNotNull();
    }
    
    
    @Test
    public void testAddRetrieveDeviceToken() throws Exception {
        DeviceTokenDao deviceTokenDao = mapper.deviceTokenDao();
        String deviceID = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        
        DeviceToken deviceToken = new DeviceToken(deviceID, token);
        deviceToken.setCreatedDate(Timeline.currentDateTimeUTCString());
        deviceToken.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        
        deviceTokenDao.add(deviceToken);
        
        DeviceToken deviceTokenById = deviceTokenDao.findById(deviceID);
        
        assertThat(deviceTokenById).isNotNull();
        assertThat(deviceTokenById.getToken()).isEqualTo(token);
        
        deviceTokenById.setToken(UUID.randomUUID().toString());
        deviceTokenDao.save(deviceTokenById);
        
        deviceTokenById = deviceTokenDao.findById(deviceID);
        assertThat(deviceTokenById.getToken()).isNotEqualTo(token);
        
        deviceTokenDao.delete(deviceTokenById);
        
        deviceTokenById = deviceTokenDao.findById(deviceID);        
        assertThat(deviceTokenById).isNull();
        
    }
    
    
}
