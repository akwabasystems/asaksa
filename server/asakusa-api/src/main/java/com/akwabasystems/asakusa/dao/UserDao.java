
package com.akwabasystems.asakusa.dao;

import com.akwabasystems.asakusa.dao.impl.CreateUserQueryProvider;
import com.akwabasystems.asakusa.dao.impl.DeleteUserQueryProvider;
import com.akwabasystems.asakusa.model.Role;
import com.akwabasystems.asakusa.model.User;
import com.akwabasystems.asakusa.model.UserCredentials;
import com.akwabasystems.asakusa.model.UserPreferences;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import com.datastax.oss.driver.api.mapper.annotations.Update;
import java.util.Optional;
import java.util.Set;


@Dao
public interface UserDao {

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
    @QueryProvider(
        providerClass = CreateUserQueryProvider.class,
        entityHelpers = { User.class, UserCredentials.class }
    )
    User create(User user, char[] password, Set<Role> roles) throws Exception;
    
    
    /**
     * Finds a user by ID
     * 
     * @param userId    the ID of the user to find
     * @return a user with the specified ID
     */
    @Select
    User findById(String userId);
    
    
    /**
     * Finds a user by email address
     * 
     * @param email     the email address of the user to find
     * @return a user with the specified email
     */
    @Select(customWhereClause = "email = :email", allowFiltering = true)
    Optional<User> findByEmail(@CqlName("email") String email);
    
    
    /**
     * Returns the list of all the users
     *
     * @return the list of all the users
     */
    @Select
    PagingIterable<User> findAll();
    
    
    /**
     * Updates the specified user account
     * 
     * @param user      the user account to update
     * @throws Exception if the credentials cannot be updated
     */
    @Update
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void update(User user) throws Exception;
    
    
    /**
     * Deletes the specified user
     * 
     * @param user      the user to delete
     * @return true if the user is deleted successfully; otherwise, returns false
     */
    @QueryProvider(
        providerClass = DeleteUserQueryProvider.class,
        entityHelpers = { User.class, UserCredentials.class }
    )
    boolean delete(User user) throws Exception;
    
    
    /**
     * Returns the credentials of the user with the specified ID
     * 
     * @param userId    the ID of the user whose credentials to return
     * @return the credentials of the user with the specified ID
     */
    @Select
    UserCredentials getCredentials(String userId);
    
    
    /**
     * Updates the given credentials
     * 
     * @param credentials       the credentials to update
     * @throws Exception if the credentials cannot be updated
     */
    @Update
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void updateCredentials(UserCredentials credentials) throws Exception;
    
    
    /**
     * Returns the preferences for the user with the specified ID
     * 
     * @param userId    the ID of the user whose preferences to return
     * @return the preferences of the user with the specified ID
     */
    @Select
    UserPreferences getPreferences(String userId);
    
    
    /**
     * Saves the given preferences
     * 
     * @param preferences       the credentials to save
     */
    @Update
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void savePreferences(UserPreferences preferences);
    
}
