
package com.akwabasystems.asakusa.repository;

import com.akwabasystems.asakusa.dao.ProjectActivityDao;
import com.akwabasystems.asakusa.dao.ProjectDao;
import com.akwabasystems.asakusa.dao.TaskDao;
import com.akwabasystems.asakusa.dao.TeamDao;
import com.akwabasystems.asakusa.dao.UserDao;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.mapper.MapperBuilder;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;


/**
 * An interface that generates the boilerplate to execute queries and convert
 * the results into application objects. It allows access to DAO instances.
 */
@Mapper
public interface RepositoryMapper {

    @DaoFactory
    UserDao userDao();
    
    @DaoFactory
    TeamDao teamDao();
    
    @DaoFactory
    ProjectDao projectDao();
    
    @DaoFactory
    ProjectActivityDao projectActivityDao();
    
    @DaoFactory
    TaskDao taskDao();
    
    static MapperBuilder<RepositoryMapper> builder(CqlSession cqlSession) {
        return new RepositoryMapperBuilder(cqlSession);
    }
    
}
