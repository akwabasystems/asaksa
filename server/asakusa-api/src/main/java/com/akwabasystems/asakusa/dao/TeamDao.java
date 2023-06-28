
package com.akwabasystems.asakusa.dao;

import com.akwabasystems.asakusa.dao.impl.CreateTeamQueryProvider;
import com.akwabasystems.asakusa.model.Team;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.DefaultNullSavingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Query;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import com.datastax.oss.driver.api.mapper.annotations.Update;
import static com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy.SET_TO_NULL;
import java.util.Optional;
import java.util.UUID;


@Dao
@DefaultNullSavingStrategy(SET_TO_NULL)
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
     */
    @Delete
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void delete(Team team) throws Exception;
    
    
    /**
     * Adds a member to the specified team
     * 
     * @param id        the ID of the team to which to add the member
     * @param userId    the ID of the user to add
     */
    @Query("INSERT INTO team_members (id, user_id) VALUES (:id, :userId)")
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void addTeamMember(@CqlName("id") UUID id, @CqlName("userId") String userId);
    
    
    /**
     * Removes a member from the specified team
     * 
     * @param id        the ID of the team from which to remove the member
     * @param userId    the ID of the user to remove
     */
    @Query("DELETE FROM team_members WHERE id = :id AND user_id = :userId")
    @StatementAttributes(consistencyLevel = "LOCAL_QUORUM")
    void removeTeamMember(@CqlName("id") UUID id, @CqlName("userId") String userId);

    
    /**
     * Returns the list of member IDs for the specified team
     * 
     * @param id        the ID of the team for which to retrieve the member IDs
     * @return the list of member IDs for the specified team
     */
    @Query("SELECT * from team_members WHERE id = :id")
    ResultSet teamMembers(@CqlName("id") UUID id);
    
}
