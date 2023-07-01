
package com.akwabasystems.asakusa.dao.impl;

import com.akwabasystems.asakusa.BaseTestSuite;
import com.akwabasystems.asakusa.dao.MembershipDao;
import com.akwabasystems.asakusa.model.ItemStatus;
import com.akwabasystems.asakusa.model.Membership;
import com.akwabasystems.asakusa.model.MembershipType;
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


public class MembershipDaoTests extends BaseTestSuite {

    @Autowired
    private CqlSession cqlSession;
    
    private RepositoryMapper mapper;
    
    
    @BeforeAll
    public void setup() {
        mapper = RepositoryMapper.builder(cqlSession).build();
    }
    
    
    @Test
    public void testDaoInitialization() {
        MembershipDao membershipDao = mapper.membershipDao();
        assertThat(membershipDao).isNotNull();
    }
    
    
    @Test
    public void testAddPhoneNumber() throws Exception {
        MembershipDao membershipDao = mapper.membershipDao();
        User user = TestUtils.defaultUser();
        
        
        Membership membership = new Membership(user.getUserId(), UUID.randomUUID(), MembershipType.FREE);
        membership.setCreatedDate(Timeline.currentDateTimeUTCString());
        membership.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        membershipDao.add(membership);
        
        Membership membershipById = membershipDao.findByUserId(user.getUserId());
        
        assertThat(membershipById).isNotNull();
        assertThat(membershipById.getType()).isEqualTo(MembershipType.FREE);
        assertThat(membershipById.getStatus()).isEqualTo(ItemStatus.ACTIVE);
        
        membershipById.setType(MembershipType.PREMIUM);
        membershipById.setLastModifiedDate(Timeline.currentDateTimeUTCString());
        membershipDao.save(membershipById);
        
        membershipById = membershipDao.findByUserId(user.getUserId());
        
        assertThat(membershipById.getType()).isEqualTo(MembershipType.PREMIUM);
        
        membershipDao.delete(membershipById);
        
        membershipById = membershipDao.findByUserId(user.getUserId());
        assertThat(membershipById).isNull();
        
    }
    
}
