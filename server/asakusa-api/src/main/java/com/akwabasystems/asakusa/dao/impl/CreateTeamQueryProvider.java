
package com.akwabasystems.asakusa.dao.impl;

import com.akwabasystems.asakusa.dao.exception.DAOException;
import com.akwabasystems.asakusa.model.Team;
import com.akwabasystems.asakusa.repository.SchemaNames;
import com.akwabasystems.asakusa.utils.Timeline;
import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import lombok.extern.java.Log;
import org.springframework.lang.NonNull;


/**
 * A class that provides the implementation of the {@code TeamDao#create} method
 */
@Log
public class CreateTeamQueryProvider {

    private CqlSession session;
    private EntityHelper<Team> teamHelper;
    private PreparedStatement preparedInsertTeam;
    private PreparedStatement preparedFindTeamByName;    
    
    
    public CreateTeamQueryProvider() {}
    
    
    public CreateTeamQueryProvider(MapperContext context,
                                   EntityHelper<Team> teamHelper) {
        this.session = context.getSession();
        this.teamHelper = teamHelper;
        
        this.prepareStatements();
    }
    
    
    private void prepareStatements() {
        preparedInsertTeam = session.prepare(teamHelper.insert().asCql());
        
        preparedFindTeamByName = session.prepare(
            teamHelper.selectStart()
                    .whereColumn(SchemaNames.COLUMN_NAME).isEqualTo(QueryBuilder.bindMarker("name"))
                    .allowFiltering()
                    .build()
        );        
    }
    
    
    /**
     * Creates a new team
     * 
     * @param team      the team to create
     * @return the newly created team
     * @throws Exception if the team cannot be created
     */
    public Team create(@NonNull Team team) throws Exception {

        boolean hasValidParameters = (
            team.getId() != null &&
            team.getName() != null
        );

        if (!hasValidParameters) {
            throw new IllegalArgumentException(DAOException.INVALID_PARAMETERS);
        }
        
        if (teamAlreadyExists(team)) {
            throw new IllegalArgumentException(DAOException.TEAM_ALREADY_EXISTS);
        }

        String currentTimeUTC = Timeline.currentDateTimeUTCString();
        
        if (team.getCreatedDate() == null) {
            team.setCreatedDate(currentTimeUTC);
        }
        
        team.setLastModifiedDate(currentTimeUTC);
       
        /** Create new user */
        BoundStatementBuilder insertTeam = preparedInsertTeam.boundStatementBuilder();
        insertTeam.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
        teamHelper.set(team, insertTeam, NullSavingStrategy.DO_NOT_SET, false);
        
        ResultSet resultSet = session.execute(insertTeam.build());
        
        if (!resultSet.wasApplied()) {
            log.severe(String.format("[TeamDao#create]: Couldn't execute statement: ", 
                    insertTeam.toString()));
            return null;
        }
        
        return team;

    }

    
    private boolean teamAlreadyExists(Team team) {
        BoundStatementBuilder findTeamByName = preparedFindTeamByName.boundStatementBuilder();
        findTeamByName.setString("name", team.getName());
        findTeamByName.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
        
        int result = session.execute(findTeamByName.build()).getAvailableWithoutFetching();
        
        return result > 0;
    }

}
