
package com.akwabasystems.asakusa.dao.impl;

import com.akwabasystems.asakusa.BaseTestSuite;
import com.akwabasystems.asakusa.dao.AccessTokenDao;
import com.akwabasystems.asakusa.model.AccessToken;
import com.akwabasystems.asakusa.model.ItemStatus;
import com.akwabasystems.asakusa.model.User;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.akwabasystems.asakusa.utils.TestUtils;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import java.util.List;
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
        User user = TestUtils.defaultUser();
        
        AccessToken accessToken = new AccessToken(user.getUserId(), Uuids.timeBased());
        accessToken.setTokenKey(UUID.randomUUID().toString());

        accessTokenDao.create(accessToken);
        
        AccessToken accessTokenById = accessTokenDao.findById(user.getUserId(), accessToken.getId());
        
        assertThat(accessTokenById).isNotNull();
        assertThat(accessTokenById.getCreatedDate()).isNotNull();
        assertThat(accessTokenById.getStatus()).isEqualTo(ItemStatus.ACTIVE);
        
        PagingIterable<AccessToken> accessTokens = accessTokenDao.findAll(user.getUserId());
        List<AccessToken> accessTokenList = accessTokens.all();
        assertThat(accessTokenList.isEmpty()).isFalse();
        
        accessTokenById.setStatus(ItemStatus.EXPIRED);
        accessTokenDao.save(accessTokenById);
        
        accessTokenById = accessTokenDao.findById(user.getUserId(), accessTokenById.getId());
        
        assertThat(accessTokenById.getStatus()).isEqualTo(ItemStatus.EXPIRED);
        
        accessTokenDao.delete(accessTokenById);
        
        accessTokenById = accessTokenDao.findById(user.getUserId(), accessToken.getId());
        assertThat(accessTokenById).isNull();
        
    }
    
}
