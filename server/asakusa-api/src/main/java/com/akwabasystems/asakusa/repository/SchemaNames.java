
package com.akwabasystems.asakusa.repository;

import com.datastax.oss.driver.api.core.CqlIdentifier;


public class SchemaNames {

    public static final CqlIdentifier TABLE_USERS = CqlIdentifier.fromCql("users");
    public static final CqlIdentifier TABLE_USER_CREDENTIALS = CqlIdentifier.fromCql("user_credentials");
    public static final CqlIdentifier TABLE_USER_PREFERENCES = CqlIdentifier.fromCql("user_preferences");
    public static final CqlIdentifier TABLE_TEAMS = CqlIdentifier.fromCql("teams");
    public static final CqlIdentifier TABLE_TEAM_MEMBERS = CqlIdentifier.fromCql("team_members");
    public static final CqlIdentifier TABLE_PROJECTS = CqlIdentifier.fromCql("projects");
    public static final CqlIdentifier TABLE_USER_PROJECTS = CqlIdentifier.fromCql("user_projects");
    public static final CqlIdentifier TABLE_TEAM_PROJECTS = CqlIdentifier.fromCql("team_projects");
    public static final CqlIdentifier TABLE_TASKS = CqlIdentifier.fromCql("tasks");
    public static final CqlIdentifier TABLE_USER_TASKS = CqlIdentifier.fromCql("user_tasks");
    public static final CqlIdentifier TABLE_PROJECT_DISCUSSIONS = CqlIdentifier.fromCql("project_discussions");
    public static final CqlIdentifier TABLE_USER_DISCUSSIONS = CqlIdentifier.fromCql("user_discussions");
    public static final CqlIdentifier TABLE_DISCUSSION_MESSAGES = CqlIdentifier.fromCql("discussion_messages");
    public static final CqlIdentifier TABLE_MEDIA = CqlIdentifier.fromCql("media");
    public static final CqlIdentifier TABLE_PROJECT_MEDIA = CqlIdentifier.fromCql("project_media");
    public static final CqlIdentifier TABLE_TASK_MEDIA = CqlIdentifier.fromCql("task_media");
    public static final CqlIdentifier TABLE_TAGS = CqlIdentifier.fromCql("tags");
    public static final CqlIdentifier TABLE_PROJECT_ACTIVITY = CqlIdentifier.fromCql("project_activity");
    public static final CqlIdentifier TABLE_PHONE_NUMBERS = CqlIdentifier.fromCql("phone_numbers");
    public static final CqlIdentifier TABLE_PHONE_NUMBER_VERIFICATIONS = CqlIdentifier.fromCql("phone_number_verifications");
    public static final CqlIdentifier TABLE_DEVICE_TOKENS = CqlIdentifier.fromCql("device_tokens");
    
    public static final CqlIdentifier COLUMN_ID = CqlIdentifier.fromCql("id");
    public static final CqlIdentifier COLUMN_USER_ID = CqlIdentifier.fromCql("user_id");
    public static final CqlIdentifier COLUMN_NAME = CqlIdentifier.fromCql("name");
    public static final CqlIdentifier COLUMN_GIVEN_NAME = CqlIdentifier.fromCql("given_name");
    public static final CqlIdentifier COLUMN_FAMILY_NAME = CqlIdentifier.fromCql("family_name");
    public static final CqlIdentifier COLUMN_MIDDLE_NAME = CqlIdentifier.fromCql("middle_name");
    public static final CqlIdentifier COLUMN_NICKNAME = CqlIdentifier.fromCql("nickname");
    public static final CqlIdentifier COLUMN_PREFERRED_USERNAME = CqlIdentifier.fromCql("preferred_username");
    public static final CqlIdentifier COLUMN_PROFILE = CqlIdentifier.fromCql("profile");
    public static final CqlIdentifier COLUMN_PICTURE = CqlIdentifier.fromCql("picture");
    public static final CqlIdentifier COLUMN_WEBSITE = CqlIdentifier.fromCql("website");
    public static final CqlIdentifier COLUMN_EMAIL = CqlIdentifier.fromCql("email");
    public static final CqlIdentifier COLUMN_EMAIL_VERIFIED = CqlIdentifier.fromCql("email_verified");
    public static final CqlIdentifier COLUMN_GENDER = CqlIdentifier.fromCql("gender");
    public static final CqlIdentifier COLUMN_BIRTH_DATE = CqlIdentifier.fromCql("birth_date");
    public static final CqlIdentifier COLUMN_ZONE_INFO = CqlIdentifier.fromCql("zone_info");
    public static final CqlIdentifier COLUMN_LOCALE = CqlIdentifier.fromCql("locale");
    public static final CqlIdentifier COLUMN_PASSWORD = CqlIdentifier.fromCql("password");
    public static final CqlIdentifier COLUMN_ROLES = CqlIdentifier.fromCql("roles");
    
    public static final CqlIdentifier COLUMN_TITLE = CqlIdentifier.fromCql("title");
    public static final CqlIdentifier COLUMN_DESCRIPTION = CqlIdentifier.fromCql("description");
    public static final CqlIdentifier COLUMN_CREATED_DATE = CqlIdentifier.fromCql("created_date");
    public static final CqlIdentifier COLUMN_CREATED_AT = CqlIdentifier.fromCql("created_at");
    public static final CqlIdentifier COLUMN_UPDATED_AT = CqlIdentifier.fromCql("updated_at");
    public static final CqlIdentifier COLUMN_EXPIRES_AT = CqlIdentifier.fromCql("expires_at");
    public static final CqlIdentifier COLUMN_LAST_MODIFIED_DATE = CqlIdentifier.fromCql("last_modified_date");
    public static final CqlIdentifier COLUMN_START_DATE = CqlIdentifier.fromCql("start_date");
    public static final CqlIdentifier COLUMN_END_DATE = CqlIdentifier.fromCql("end_date");
    public static final CqlIdentifier COLUMN_DEADLINE = CqlIdentifier.fromCql("deadline");
    public static final CqlIdentifier COLUMN_ESTIMATED_DURATION = CqlIdentifier.fromCql("estimated_duration");
    
    public static final CqlIdentifier COLUMN_TEAM_ID = CqlIdentifier.fromCql("team_id");
    public static final CqlIdentifier COLUMN_OWNER_ID = CqlIdentifier.fromCql("owner_id");
    public static final CqlIdentifier COLUMN_PROJECT_ID = CqlIdentifier.fromCql("project_id");
    public static final CqlIdentifier COLUMN_ASSIGNEE_ID = CqlIdentifier.fromCql("assignee_id");
    public static final CqlIdentifier COLUMN_TASK_ID = CqlIdentifier.fromCql("task_id");
    public static final CqlIdentifier COLUMN_AUTHOR_ID = CqlIdentifier.fromCql("author_id");
    public static final CqlIdentifier COLUMN_DISCUSSION_ID = CqlIdentifier.fromCql("discussion_id");
    public static final CqlIdentifier COLUMN_MEDIA_ID = CqlIdentifier.fromCql("media_id");
    public static final CqlIdentifier COLUMN_DEVICE_ID = CqlIdentifier.fromCql("device_id");
    
    public static final CqlIdentifier COLUMN_CAPACITY = CqlIdentifier.fromCql("capacity");
    public static final CqlIdentifier COLUMN_STATUS = CqlIdentifier.fromCql("status");
    public static final CqlIdentifier COLUMN_PRIORITY = CqlIdentifier.fromCql("priority");
    public static final CqlIdentifier COLUMN_TAGS = CqlIdentifier.fromCql("tags");
    public static final CqlIdentifier COLUMN_BODY = CqlIdentifier.fromCql("body");
    public static final CqlIdentifier COLUMN_THUMBNAIL = CqlIdentifier.fromCql("thumbnail");
    public static final CqlIdentifier COLUMN_URL = CqlIdentifier.fromCql("url");
    public static final CqlIdentifier COLUMN_TYPE = CqlIdentifier.fromCql("type");
    public static final CqlIdentifier COLUMN_COLOR_CODE = CqlIdentifier.fromCql("color_code");
    public static final CqlIdentifier COLUMN_ACTOR = CqlIdentifier.fromCql("actor");
    public static final CqlIdentifier COLUMN_DETAILS = CqlIdentifier.fromCql("details");
    public static final CqlIdentifier COLUMN_DEPENDS_ON = CqlIdentifier.fromCql("depends_on");
    
    public static final CqlIdentifier COLUMN_ADDRESS = CqlIdentifier.fromCql("address");
    public static final CqlIdentifier COLUMN_COUNTRY_ID = CqlIdentifier.fromCql("country_id");
    public static final CqlIdentifier COLUMN_COUNTRY_CODE = CqlIdentifier.fromCql("country_code");
    public static final CqlIdentifier COLUMN_NUMBER = CqlIdentifier.fromCql("number");
    public static final CqlIdentifier COLUMN_PHONE_NUMBER = CqlIdentifier.fromCql("phone_number");
    public static final CqlIdentifier COLUMN_PHONE_NUMBER_VERIFIED = CqlIdentifier.fromCql("phone_number_verified");
    public static final CqlIdentifier COLUMN_FORMATTED_NUMBER = CqlIdentifier.fromCql("formatted_number");
    public static final CqlIdentifier COLUMN_CODE = CqlIdentifier.fromCql("code");
    public static final CqlIdentifier COLUMN_DEVICE_TOKEN = CqlIdentifier.fromCql("device_token");
    public static final CqlIdentifier COLUMN_SETTINGS = CqlIdentifier.fromCql("settings");
    
}
