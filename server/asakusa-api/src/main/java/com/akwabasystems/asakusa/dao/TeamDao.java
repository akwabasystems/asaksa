
package com.akwabasystems.asakusa.dao;

import com.akwabasystems.asakusa.dao.impl.CreateTeamQueryProvider;
import com.akwabasystems.asakusa.model.Team;
import com.akwabasystems.asakusa.model.User;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import com.datastax.oss.driver.api.mapper.annotations.Update;
import java.util.Optional;
import java.util.UUID;


@Dao
public interface TeamDao {

    /**
     * Creates a new team
     * 
     * @param team      the team to create
     * @return the newly created team
     * @throws Exception if the team cannot be created
     */
    @QueryProvider(
        providerClass = CreateTeamQueryProvider.class,
        entityHelpers = { Team.class }
    )
    Team create(Team team) throws Exception;
    
    
    /**
     * Updates a team
     * 
     * @param team      the team to update
     * @throws Exception if the team cannot be updated
     */
    @Update
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void save(Team team) throws Exception;
    
    
    /**
     * Finds a team by ID
     * 
     * @param id    the ID of the team to find
     * @return the team with the specified ID
     */
    @Select
    Team findById(UUID id);
    
    
    /**
     * Finds a team by name
     * 
     * @param name     the name of the team to find
     * @return the team with the specified name, if it exists
     */
    @Select(customWhereClause = "name = :name", allowFiltering = true)
    Optional<Team> findByName(@CqlName("name") String name);
    
    
    /**
     * Returns the list of all the teams
     *
     * @return the list of all the teams
     */
    @Select
    PagingIterable<Team> findAll();
    
    
    /**
     * Deletes the specified team
     * 
     * @param team      the team to delete
     * @return true if the team is deleted successfully; otherwise, returns false
     */
    @Delete
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    boolean delete(Team team) throws Exception;
    
}
