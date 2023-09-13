
package com.akwabasystems.asakusa.dao.impl;

import com.akwabasystems.asakusa.BaseTestSuite;
import com.akwabasystems.asakusa.dao.AccessTokenDao;
import com.akwabasystems.asakusa.model.AccessToken;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.akwabasystems.asakusa.utils.Timeline;
import com.datastax.oss.driver.api.core.CqlSession;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class AccessTokenDaoTests extends BaseTestSuite {

    @Autowired
    private CqlSession cqlSession;
    
    private RepositoryMapper mapper;
    
    
    @BeforeAll
    public void setup() {
        mapper = RepositoryMapper.builder(cqlSession).build();
    }
    
    
    @Test
    public void testDaoInitialization() {
        AccessTokenDao accessTokenDao = mapper.accessTokenDao();
        assertThat(accessTokenDao).isNotNull();
    }
    
    
    @Test
    public void testAddRemoveAccessToken() throws Exception {
        AccessTokenDao accessTokenDao = mapper.accessTokenDao();
        
        String deviceID = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        
        AccessToken accessToken = new AccessToken(deviceID, token);
        accessToken.setCreatedDate(Timeline.currentDateTimeUTCString());
        accessToken.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        
        accessTokenDao.create(accessToken);
        
        AccessToken accessTokenById = accessTokenDao.findById(deviceID);
        
        assertThat(accessTokenById).isNotNull();
        assertThat(accessTokenById.getTokenKey()).isEqualTo(token);
        
        accessTokenById.setTokenKey(UUID.randomUUID().toString());
        accessTokenDao.save(accessTokenById);
        
        accessTokenById = accessTokenDao.findById(deviceID);
        assertThat(accessTokenById.getTokenKey()).isNotEqualTo(token);
        
        accessTokenDao.delete(accessTokenById);
        
        accessTokenById = accessTokenDao.findById(deviceID);        
        assertThat(accessTokenById).isNull();
        
    }
    
}
