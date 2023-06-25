
package com.akwabasystems.asakusa.dao.impl;

import com.akwabasystems.asakusa.dao.exception.DAOException;
import com.akwabasystems.asakusa.model.Role;
import com.akwabasystems.asakusa.model.User;
import com.akwabasystems.asakusa.model.UserCredentials;
import com.akwabasystems.asakusa.repository.SchemaNames;
import com.akwabasystems.asakusa.utils.PasswordUtils;
import com.akwabasystems.asakusa.utils.Timeline;
import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchStatementBuilder;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import java.util.Set;
import lombok.extern.java.Log;
import org.springframework.lang.NonNull;


/**
 * A class that provides the implementation of the {@code UserDao#create} method
 */
@Log
public class CreateUserQueryProvider {

    private CqlSession session;
    private EntityHelper<User> userHelper;
    private EntityHelper<UserCredentials> credentialsHelper;
    private PreparedStatement preparedInsertUser;
    private PreparedStatement preparedFindUserById;
    private PreparedStatement preparedFindUserByEmail;
    private PreparedStatement preparedInsertCredentials;
    
    
    public CreateUserQueryProvider() {}
    
    
    public CreateUserQueryProvider(MapperContext context,
                             EntityHelper<User> userHelper,
                             EntityHelper<UserCredentials> credentialsHelper) {
        this.session = context.getSession();
        this.userHelper = userHelper;
        this.credentialsHelper = credentialsHelper;
        
        this.prepareStatements();
    }
    
    
    private void prepareStatements() {
        preparedInsertUser = session.prepare(userHelper.insert().asCql());
        
        preparedFindUserById = session.prepare(userHelper.selectByPrimaryKey().asCql());
        
        preparedFindUserByEmail = session.prepare(
            userHelper.selectStart()
                    .whereColumn(SchemaNames.COLUMN_EMAIL).isEqualTo(QueryBuilder.bindMarker("email"))
                    .allowFiltering()
                    .build()
        );
        
        preparedInsertCredentials = session.prepare(credentialsHelper.insert().asCql());
        
    }
    
    
    /**
     * Creates a user account. This operation involves the following steps:
     * First, checking whether a user with the specified ID or email already exist;
     * second, encrypting the user's password; and finally creating the account.
     * 
     * @param user      the user account to create
     * @param password  the user's password
     * @param roles     the user's roles
     * @return the newly created user account
     * @throws Exception if the account cannot be created
     */
    public User create(@NonNull User user, char[] password, Set<Role> roles) throws Exception {

        boolean hasValidParameters = (
            user.getUserId() != null &&
            user.getEmail() != null &&
            password != null
        );

        if (!hasValidParameters) {
            throw new IllegalArgumentException(DAOException.INVALID_PARAMETERS);
        }
        
        if (userIdAlreadyExists(user)) {
            throw new IllegalArgumentException(DAOException.USER_ALREADY_EXISTS);
        }
        
        if (emailAlreadyExists(user)) {
            throw new IllegalArgumentException(DAOException.EMAIL_ALREADY_EXISTS);
        }
        
        user.setLastModifiedDate(Timeline.currentDateTimeUTCString());
       
        /** Create new user */
        BoundStatementBuilder insertUser = preparedInsertUser.boundStatementBuilder();
        userHelper.set(user, insertUser, NullSavingStrategy.DO_NOT_SET, false);
        
        /** Add user credentials */
        UserCredentials credentials = new UserCredentials(user.getUserId(), PasswordUtils.hash(password));
        credentials.setRoles(roles);
        
        BoundStatementBuilder insertCredentials = preparedInsertCredentials.boundStatementBuilder();
        credentialsHelper.set(credentials, insertCredentials, 
                NullSavingStrategy.DO_NOT_SET, false);
        
        BatchStatementBuilder batchStart = BatchStatement.builder(BatchType.UNLOGGED)
                                                .addStatement(insertUser.build())
                                                .addStatement(insertCredentials.build());
        
        BatchStatement batchStatement = batchStart.build().setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
        ResultSet resultSet = session.execute(batchStatement);
        
        if (!resultSet.wasApplied()) {
            log.severe(String.format("[UserDao#create]: Couldn't execute statement: ", batchStart.toString()));
            return null;
        }
        
        return user;

    }

    
    private boolean userIdAlreadyExists(User user) {
        BoundStatementBuilder findUserById = preparedFindUserById.boundStatementBuilder();
        findUserById.setString("user_id", user.getUserId());
        findUserById.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
        
        int result = session.execute(findUserById.build()).getAvailableWithoutFetching();
        
        return result > 0;
    }
    
    
    private boolean emailAlreadyExists(User user) {
        BoundStatementBuilder findUserByEmail = preparedFindUserByEmail.boundStatementBuilder();
        findUserByEmail.setString("email", user.getEmail());
        findUserByEmail.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
        
        int result = session.execute(findUserByEmail.build()).getAvailableWithoutFetching();
        
        return result > 0;
    }

}
