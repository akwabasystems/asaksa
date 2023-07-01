
package com.akwabasystems.asakusa.repository;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;


@Repository
public class AsakusaRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(AsakusaRepository.class);
    
    private final CqlSession cqlSession;
    private final CqlIdentifier keyspaceName;
    
    public AsakusaRepository(
        @NonNull CqlSession cqlSession,
        @NonNull CqlIdentifier keyspaceName) {
        this.cqlSession = cqlSession;
        this.keyspaceName = keyspaceName;
        
        logger.info(String.format("Asakusa repository initialized - Session: %s - Keyspace: %s%n",
                cqlSession.getName(), keyspaceName));
    }
    
    
    @PreDestroy
    public void cleanup() {
        if (cqlSession != null) {
            cqlSession.close();
            logger.info("[AsakusaRepository]: CqlSession has been successfully closed");
        }
    }
    
    /**
     * Creates the keyspace tables as defined in the <code>schema.cql</code> file
     */
    public void createTables() {
        
        /**
         * CREATE TABLE IF NOT EXISTS users (
         *   user_id text PRIMARY KEY,
         *   name text,
         *   given_name text,
         *   family_name text,
         *   middle_name text,
         *   nickname text,
         *   preferred_username text,
         *   profile text,
         *   picture text,
         *   website text,
         *   email text,
         *   email_verified boolean,
         *   gender text,
         *   birth_date text,
         *   zone_info text,
         *   locale text,
         *   phone_number text,
         *   phone_number_verified boolean,
         *   address map<text, text>,
         *   updated_at text
         * );
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_USERS)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_USER_ID, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_NAME, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_GIVEN_NAME, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_FAMILY_NAME, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_MIDDLE_NAME, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_NICKNAME, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_PREFERRED_USERNAME, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_PROFILE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_PICTURE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_WEBSITE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_EMAIL, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_EMAIL_VERIFIED, DataTypes.BOOLEAN)
                    .withColumn(SchemaNames.COLUMN_GENDER, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_BIRTH_DATE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_ZONE_INFO, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_LOCALE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_PHONE_NUMBER, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_PHONE_NUMBER_VERIFIED, DataTypes.BOOLEAN)
                    .withColumn(SchemaNames.COLUMN_ADDRESS, DataTypes.mapOf(DataTypes.TEXT, DataTypes.TEXT))
                    .withColumn(SchemaNames.COLUMN_UPDATED_AT, DataTypes.TEXT)
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_USERS.asInternal()));
        
        /**
         * CREATE TABLE IF NOT EXISTS user_credentials (
         *   user_id text PRIMARY KEY,
         *   password text,
         *   roles set<text>
         * );
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_USER_CREDENTIALS)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_USER_ID, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_PASSWORD, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_ROLES, DataTypes.setOf(DataTypes.TEXT))
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_USER_CREDENTIALS.asInternal()));
        
        /**
         * CREATE TABLE IF NOT EXISTS user_preferences (
         *   user_id text PRIMARY KEY,
         *   settings text,
         *   last_modified_date text
         * );
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_USER_PREFERENCES)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_USER_ID, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_SETTINGS, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_LAST_MODIFIED_DATE, DataTypes.TEXT)
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_USER_PREFERENCES.asInternal()));
        
        /**
         * CREATE TABLE IF NOT EXISTS teams (
         *   id uuid PRIMARY KEY,
         *   name text,
         *   description text,
         *   created_date text,
         *   last_modified_date text
         * );
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_TEAMS)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_ID, DataTypes.UUID)
                    .withColumn(SchemaNames.COLUMN_NAME, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_DESCRIPTION, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_CREATED_DATE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_LAST_MODIFIED_DATE, DataTypes.TEXT)
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_TEAMS.asInternal()));

        /**
         * CREATE TABLE IF NOT EXISTS team_members (
         *   id uuid,
         *   user_id text,
         *   PRIMARY KEY (("id"), "user_id")
         * ) WITH COMMENT = 'Retrieve the members of a team';
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_TEAM_MEMBERS)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_ID, DataTypes.UUID)
                    .withClusteringColumn(SchemaNames.COLUMN_USER_ID, DataTypes.TEXT)
                    .withComment("Retrieve the members of a team")
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_TEAM_MEMBERS.asInternal()));
        
        /**
         * CREATE TABLE IF NOT EXISTS projects (
         *   team_id uuid,
         *   id uuid,
         *   name text,
         *   description text,
         *   owner_id text,
         *   start_date text,
         *   deadline text,
         *   end_date text,
         *   capacity int,
         *   status text,
         *   priority text,
         *   tags set<text>,
         *   created_date text,
         *   last_modified_date text,
         *   PRIMARY KEY (("team_id"), "id")
         * );
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_PROJECTS)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_TEAM_ID, DataTypes.UUID)
                    .withClusteringColumn(SchemaNames.COLUMN_ID, DataTypes.UUID)
                    .withColumn(SchemaNames.COLUMN_NAME, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_DESCRIPTION, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_OWNER_ID, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_START_DATE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_DEADLINE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_END_DATE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_CAPACITY, DataTypes.INT)
                    .withColumn(SchemaNames.COLUMN_STATUS, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_PRIORITY, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_TAGS, DataTypes.setOf(DataTypes.TEXT))
                    .withColumn(SchemaNames.COLUMN_CREATED_DATE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_LAST_MODIFIED_DATE, DataTypes.TEXT)
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_PROJECTS.asInternal()));
        
        /**
         * CREATE TABLE IF NOT EXISTS tasks (
         *   project_id uuid,
         *   id uuid,
         *   title text,
         *   description text,
         *   assignee_id text,
         *   depends_on uuid,
         *   start_date text,
         *   estimated_duration int,
         *   end_date text,
         *   status text,
         *   priority text,
         *   tags set<text>,
         *   created_date text,
         *   last_modified_date text,
         *   PRIMARY KEY (("project_id"), "id")
         * );
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_TASKS)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_PROJECT_ID, DataTypes.UUID)
                    .withClusteringColumn(SchemaNames.COLUMN_ID, DataTypes.UUID)
                    .withColumn(SchemaNames.COLUMN_TITLE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_DESCRIPTION, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_ASSIGNEE_ID, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_DEPENDS_ON, DataTypes.UUID)
                    .withColumn(SchemaNames.COLUMN_START_DATE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_ESTIMATED_DURATION, DataTypes.INT)
                    .withColumn(SchemaNames.COLUMN_END_DATE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_STATUS, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_PRIORITY, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_TAGS, DataTypes.setOf(DataTypes.TEXT))
                    .withColumn(SchemaNames.COLUMN_CREATED_DATE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_LAST_MODIFIED_DATE, DataTypes.TEXT)
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_TASKS.asInternal()));

        /**
         * CREATE TABLE IF NOT EXISTS user_tasks (
         *   assignee_id text,
         *   project_id uuid,
         *   task_id uuid,
         *   PRIMARY KEY(("assignee_id", "project_id"), "task_id")
         * ) WITH COMMENT = 'Retrieve the tasks assigned to a user';
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_USER_TASKS)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_ASSIGNEE_ID, DataTypes.TEXT)
                    .withPartitionKey(SchemaNames.COLUMN_PROJECT_ID, DataTypes.UUID)
                    .withClusteringColumn(SchemaNames.COLUMN_TASK_ID, DataTypes.UUID)
                    .withComment("Retrieve the tasks assigned to a user")
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_USER_TASKS.asInternal()));

        /**
         * CREATE TABLE IF NOT EXISTS project_discussions (
         *   project_id uuid,
         *   id uuid,
         *   author_id text,
         *   title text,
         *   created_date text,
         *   PRIMARY KEY (("project_id"), "id")
         * );
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_PROJECT_DISCUSSIONS)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_PROJECT_ID, DataTypes.UUID)
                    .withClusteringColumn(SchemaNames.COLUMN_ID, DataTypes.UUID)
                    .withColumn(SchemaNames.COLUMN_AUTHOR_ID, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_TITLE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_CREATED_DATE, DataTypes.TEXT)
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_PROJECT_DISCUSSIONS.asInternal()));
        
        /**
         * CREATE TABLE IF NOT EXISTS user_discussions (
         *   author_id text PRIMARY KEY,
         *   project_id uuid,
         *   discussion_id uuid
         * ) WITH COMMENT = 'Retrieve the discussions for a user';
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_USER_DISCUSSIONS)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_AUTHOR_ID, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_PROJECT_ID, DataTypes.UUID)
                    .withColumn(SchemaNames.COLUMN_DISCUSSION_ID, DataTypes.UUID)
                    .withComment("Retrieve the discussions for a user")
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_USER_DISCUSSIONS.asInternal()));
        
        /**
         * CREATE TABLE IF NOT EXISTS discussion_messages (
         *   project_id uuid,
         *   discussion_id uuid,
         *   id uuid,
         *   author_id text,
         *   body text,
         *   created_date text,
         *   PRIMARY KEY (("project_id", "discussion_id"), "id")
         * ) WITH CLUSTERING ORDER BY ("id" ASC);
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_DISCUSSION_MESSAGES)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_PROJECT_ID, DataTypes.UUID)
                    .withPartitionKey(SchemaNames.COLUMN_DISCUSSION_ID, DataTypes.UUID)
                    .withClusteringColumn(SchemaNames.COLUMN_ID, DataTypes.UUID)
                    .withColumn(SchemaNames.COLUMN_AUTHOR_ID, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_BODY, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_CREATED_DATE, DataTypes.TEXT)
                    .withClusteringOrder(SchemaNames.COLUMN_ID, ClusteringOrder.ASC)
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_DISCUSSION_MESSAGES.asInternal()));
        
        /**
         * CREATE TABLE IF NOT EXISTS media (
         *   id uuid PRIMARY KEY,
         *   title text,
         *   thumbnail text,
         *   url text,
         *   type text,
         *   created_date text,
         *   last_modified_date text
         * );
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_MEDIA)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_ID, DataTypes.UUID)
                    .withColumn(SchemaNames.COLUMN_TITLE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_THUMBNAIL, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_URL, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_TYPE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_CREATED_DATE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_LAST_MODIFIED_DATE, DataTypes.TEXT)
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_MEDIA.asInternal()));
        
        /**
         * CREATE TABLE IF NOT EXISTS project_media (
         *   project_id uuid PRIMARY KEY,
         *   media_id uuid
         * ) WITH COMMENT = 'Retrieve the media for a project';
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_PROJECT_MEDIA)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_PROJECT_ID, DataTypes.UUID)
                    .withColumn(SchemaNames.COLUMN_MEDIA_ID, DataTypes.UUID)
                    .withComment("Retrieve the media for a project")
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_PROJECT_MEDIA.asInternal()));
        
        /**
         * CREATE TABLE IF NOT EXISTS task_media (
         *   task_id uuid PRIMARY KEY,
         *   media_id uuid
         * ) WITH COMMENT = 'Retrieve the media for a task';
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_TASK_MEDIA)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_TASK_ID, DataTypes.UUID)
                    .withColumn(SchemaNames.COLUMN_MEDIA_ID, DataTypes.UUID)
                    .withComment("Retrieve the media for a task")
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_TASK_MEDIA.asInternal()));
        
        /**
         * CREATE TABLE IF NOT EXISTS tags (
         *   id uuid PRIMARY KEY,
         *   name text,
         *   color_code text
         * );
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_TAGS)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_ID, DataTypes.UUID)
                    .withColumn(SchemaNames.COLUMN_NAME, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_COLOR_CODE, DataTypes.TEXT)
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_TAGS.asInternal()));
        
        /**
         * CREATE TABLE IF NOT EXISTS project_activity (
         *   project_id uuid,
         *   id uuid,
         *   type text,
         *   actor text,
         *   details text,
         *   created_date text,
         *   PRIMARY KEY (("project_id"), "id")
         * ) WITH CLUSTERING ORDER BY ("id" DESC);
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_PROJECT_ACTIVITY)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_PROJECT_ID, DataTypes.UUID)
                    .withClusteringColumn(SchemaNames.COLUMN_ID, DataTypes.UUID)
                    .withColumn(SchemaNames.COLUMN_TYPE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_ACTOR, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_DETAILS, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_CREATED_DATE, DataTypes.TEXT)
                    .withClusteringOrder(SchemaNames.COLUMN_ID, ClusteringOrder.DESC)
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_PROJECT_ACTIVITY.asInternal()));
        
        /**
         * CREATE TABLE IF NOT EXISTS phone_numbers (
         *   user_id text PRIMARY KEY,
         *   id uuid,
         *   country_id text,
         *   country_code text,
         *   number text,
         *   formatted_number text,
         *   type text,
         *   status text,
         *   created_date text,
         *   last_modified_date text
         * );
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_PHONE_NUMBERS)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_USER_ID, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_ID, DataTypes.UUID)
                    .withColumn(SchemaNames.COLUMN_COUNTRY_ID, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_COUNTRY_CODE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_NUMBER, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_FORMATTED_NUMBER, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_TYPE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_STATUS, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_CREATED_DATE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_LAST_MODIFIED_DATE, DataTypes.TEXT)
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_PHONE_NUMBERS.asInternal()));
        
        /**
         * CREATE TABLE IF NOT EXISTS phone_number_verifications (
         *   phone_number text PRIMARY KEY,
         *   id uuid,
         *   code text,
         *   created_at text,
         *   expires_at text
         * );
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_PHONE_NUMBER_VERIFICATIONS)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_PHONE_NUMBER, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_ID, DataTypes.UUID)
                    .withColumn(SchemaNames.COLUMN_CODE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_CREATED_AT, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_EXPIRES_AT, DataTypes.TEXT)
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_PHONE_NUMBER_VERIFICATIONS.asInternal()));
        
        /**
         * CREATE TABLE IF NOT EXISTS device_tokens (
         *   device_id text PRIMARY KEY,
         *   device_token text,
         *   created_date text,
         *   last_modified_date text
         * );
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_DEVICE_TOKENS)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_DEVICE_ID, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_DEVICE_TOKEN, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_CREATED_DATE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_LAST_MODIFIED_DATE, DataTypes.TEXT)
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_DEVICE_TOKENS.asInternal()));
        
        
        /**
         * CREATE TABLE IF NOT EXISTS membership (
         *   user_id text PRIMARY KEY,
         *   id uuid,
         *   status text,
         *   type text,
         *   created_date text,
         *   last_modified_date text
         * );
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_MEMBERSHIP)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_USER_ID, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_ID, DataTypes.UUID)
                    .withColumn(SchemaNames.COLUMN_STATUS, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_TYPE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_LICENSE_KEY, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_SOURCE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_CREATED_DATE, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_LAST_MODIFIED_DATE, DataTypes.TEXT)
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_MEMBERSHIP.asInternal()));
        

        /**
         * CREATE TABLE IF NOT EXISTS user_sessions (
         *   user_id text,
         *   id timeuuid,
         *   user_agent text,
         *   status text,
         *   start_date timestamp,
         *   end_date timestamp,
         *   PRIMARY KEY (("user_id"), "id")
         * ) WITH CLUSTERING ORDER BY ("id" DESC);
         */
        cqlSession.execute(
            createTable(keyspaceName, SchemaNames.TABLE_USER_SESSIONS)
                    .ifNotExists()
                    .withPartitionKey(SchemaNames.COLUMN_USER_ID, DataTypes.TEXT)
                    .withClusteringColumn(SchemaNames.COLUMN_ID, DataTypes.TIMEUUID)
                    .withColumn(SchemaNames.COLUMN_USER_AGENT, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_STATUS, DataTypes.TEXT)
                    .withColumn(SchemaNames.COLUMN_START_DATE, DataTypes.TIMESTAMP)
                    .withColumn(SchemaNames.COLUMN_END_DATE, DataTypes.TIMESTAMP)
                    .withClusteringOrder(SchemaNames.COLUMN_ID, ClusteringOrder.DESC)
                    .build());
        logger.info(String.format("Table '%s' has been created (if needed)", SchemaNames.TABLE_USER_SESSIONS.asInternal()));

    }
}
