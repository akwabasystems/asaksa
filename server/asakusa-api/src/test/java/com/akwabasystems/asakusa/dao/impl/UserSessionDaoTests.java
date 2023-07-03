
package com.akwabasystems.asakusa.dao.impl;

import com.akwabasystems.asakusa.BaseTestSuite;
import com.akwabasystems.asakusa.dao.UserSessionDao;
import com.akwabasystems.asakusa.model.ItemStatus;
import com.akwabasystems.asakusa.model.User;
import com.akwabasystems.asakusa.model.UserSession;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.akwabasystems.asakusa.utils.TestUtils;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import java.time.temporal.ChronoUnit;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class UserSessionDaoTests extends BaseTestSuite {

    @Autowired
    private CqlSession cqlSession;
    
    private RepositoryMapper mapper;
    
    
    @BeforeAll
    public void setup() {
        mapper = RepositoryMapper.builder(cqlSession).build();
    }
    
    
    @Test
    public void testDaoInitialization() {
        UserSessionDao sessionDao = mapper.userSessionDao();
        assertThat(sessionDao).isNotNull();
    }
    
    
    @Test
    public void testAddRemoveUserSession() throws Exception {
        UserSessionDao sessionDao = mapper.userSessionDao();
        User user = TestUtils.defaultUser();
        
        UserSession session = new UserSession(user.getUserId(), Uuids.timeBased());
        session.setClient("iPhone 12 Mini");

        sessionDao.create(session);
        
        UserSession sessionById = sessionDao.findById(user.getUserId(), session.getId());
                
        assertThat(sessionById).isNotNull();
        assertThat(sessionById.getStartDate()).isNotNull();
        assertThat(sessionById.getStatus()).isEqualTo(ItemStatus.ACTIVE);
        
        PagingIterable<UserSession> recentSessions = sessionDao.findAll(user.getUserId());
        List<UserSession> sessionList = recentSessions.all();
        assertThat(sessionList.isEmpty()).isFalse();
        
        sessionById.setEndDate(sessionById.getStartDate().plus(20, ChronoUnit.MINUTES));
        sessionById.setStatus(ItemStatus.INACTIVE);
        sessionDao.save(sessionById);
        
        sessionById = sessionDao.findById(user.getUserId(), session.getId());
        
        assertThat(sessionById.getEndDate()).isNotNull();
        assertThat(sessionById.getStatus()).isEqualTo(ItemStatus.INACTIVE);
        
        sessionDao.delete(sessionById);
        
        sessionById = sessionDao.findById(user.getUserId(), session.getId());
        assertThat(sessionById).isNull();
        
    }
    
}
