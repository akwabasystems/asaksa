
package com.akwabasystems.asakusa.dao.impl;

import com.akwabasystems.asakusa.dao.exception.DAOException;
import com.akwabasystems.asakusa.model.User;
import com.akwabasystems.asakusa.model.UserCredentials;
import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchStatementBuilder;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import lombok.extern.java.Log;
import org.springframework.lang.NonNull;


/**
 * A class that provides the implementation of the {@code UserDao#delete} method
 */
@Log
public class DeleteUserQueryProvider {

    private CqlSession session;
    private EntityHelper<User> userHelper;
    private EntityHelper<UserCredentials> credentialsHelper;
    private PreparedStatement preparedFindUserById;
    private PreparedStatement preparedDeleteUser;
    private PreparedStatement preparedDeleteCredentials;
    
    
    public DeleteUserQueryProvider() {}
    
    
    public DeleteUserQueryProvider(MapperContext context,
                             EntityHelper<User> userHelper,
                             EntityHelper<UserCredentials> credentialsHelper) {
        this.session = context.getSession();
        this.userHelper = userHelper;
        this.credentialsHelper = credentialsHelper;
        
        this.prepareStatements();
    }
    
    
    private void prepareStatements() {
        preparedFindUserById = session.prepare(userHelper.selectByPrimaryKey().asCql());
        preparedDeleteUser = session.prepare(userHelper.deleteByPrimaryKey().asCql());
        preparedDeleteCredentials = session.prepare(credentialsHelper.deleteByPrimaryKey().asCql());
    }
    

    /**
     * Deletes the specified user
     * 
     * @param user      the user to delete
     * @return true if the user is deleted successfully; otherwise, returns false
     * @throws Exception if the user cannot be deleted
     */
    public boolean delete(@NonNull User user) throws Exception {

        if (!userExists(user)) {
            throw new IllegalArgumentException(DAOException.INVALID_USER_ID);
        }
        
        /** Delete the user and related credentials*/
        BoundStatement deleteUser = preparedDeleteUser.bind(user.getUserId());
        BoundStatement deleteCredentials = preparedDeleteCredentials.bind(user.getUserId());
        
        BatchStatementBuilder batchStart = BatchStatement.builder(BatchType.UNLOGGED)
                                                .addStatement(deleteUser)
                                                .addStatement(deleteCredentials);
        BatchStatement batchStatement = batchStart.build().setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
        ResultSet resultSet = session.execute(batchStatement);
        
        return resultSet.wasApplied();
    }
   
    
    /**
     * Returns true if the specified user account already exists; otherwise, returns false
     * 
     * @param user      the user account to check
     * @return true if the specific user account already exists; otherwise, returns false
     */
    private boolean userExists(User user) {
        BoundStatementBuilder findUserById = preparedFindUserById.boundStatementBuilder();
        findUserById.setString("user_id", user.getUserId());
        findUserById.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
        
        int result = session.execute(findUserById.build()).getAvailableWithoutFetching();
        
        return result > 0;
    }

}
