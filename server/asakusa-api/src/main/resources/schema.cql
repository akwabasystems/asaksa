CREATE KEYSPACE asakusa WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 2};

use asakusa;


CREATE TABLE IF NOT EXISTS users (
    user_id text PRIMARY KEY,
    name text,
    given_name text,
    family_name text,
    middle_name text,
    nickname text,
    preferred_username text,
    profile text,
    picture text,
    website text,
    email text,
    email_verified boolean,
    gender text,
    birth_date date,
    zone_info text,
    locale text,
    phone_number text,
    phone_number_verified boolean,
    address map<text, text>,
    updated_at timestamp
);


CREATE TABLE IF NOT EXISTS user_credentials (
    user_id text PRIMARY KEY,
    password text,
    roles set<text>
);


CREATE TABLE IF NOT EXISTS user_preferences (
    user_id text PRIMARY KEY,
    id uuid,
    settings text,
    last_modified_date timestamp
);


CREATE TABLE IF NOT EXISTS teams (
    id uuid PRIMARY KEY,
    name text,
    description text,
    created_date timestamp,
    last_modified_date timestamp
);


CREATE TABLE IF NOT EXISTS team_members (
    team_id uuid PRIMARY KEY,
    user_id text
) WITH COMMENT = 'Retrieve the members of a team';


CREATE TABLE IF NOT EXISTS projects (
    id uuid PRIMARY KEY,
    name text,
    description text,
    team_id uuid,
    owner_id text,
    start_date timestamp,
    deadline timestamp,
    end_date timestamp,
    capacity smallint,
    status text,
    priority text,
    tags set<text>,
    created_date timestamp,
    last_modified_date timestamp
);


CREATE TABLE IF NOT EXISTS user_projects (
    user_id text PRIMARY KEY,
    project_id uuid
) WITH COMMENT = 'Retrieve the projects for a user';


CREATE TABLE IF NOT EXISTS team_projects (
    team_id uuid PRIMARY KEY,
    project_id uuid
) WITH COMMENT = 'Retrieve the projects for a team';


CREATE TABLE IF NOT EXISTS tasks (
    project_id uuid,
    id uuid,
    title text,
    description text,
    assignee_id text,
    depends_on uuid,
    start_date timestamp,
    estimated_duration smallint,
    end_date timestamp,
    status text,
    priority text,
    tags set<text>,
    created_date timestamp,
    last_modified_date timestamp,
    PRIMARY KEY (("project_id"), "id")
);


CREATE TABLE IF NOT EXISTS user_tasks (
    assignee_id text PRIMARY KEY,
    project_id uuid,
    task_id uuid
) WITH COMMENT = 'Retrieve the tasks assigned to a user';


CREATE TABLE IF NOT EXISTS project_discussions (
    project_id uuid,
    id timeuuid,
    author_id text,
    title text,
    created_date timestamp,
    PRIMARY KEY (("project_id"), "id")
);


CREATE TABLE IF NOT EXISTS user_discussions (
    author_id text PRIMARY KEY,
    project_id uuid,
    discussion_id timeuuid
) WITH COMMENT = 'Retrieve the discussions for a user';


CREATE TABLE IF NOT EXISTS discussion_messages (
    project_id uuid,
    discussion_id timeuuid,
    id timeuuid,
    author_id text,
    body text,
    created_date timestamp,
    PRIMARY KEY (("project_id", "discussion_id"), "id")
) WITH CLUSTERING ORDER BY ("id" ASC);


CREATE TABLE IF NOT EXISTS media (
    id uuid PRIMARY KEY,
    title text,
    thumbnail text,
    url text,
    type text,
    created_date timestamp,
    last_modified_date timestamp
);


CREATE TABLE IF NOT EXISTS project_media (
    project_id uuid PRIMARY KEY,
    media_id uuid
) WITH COMMENT = 'Retrieve the media for a project';


CREATE TABLE IF NOT EXISTS tasks_media (
    task_id uuid PRIMARY KEY,
    media_id uuid
) WITH COMMENT = 'Retrieve the media for a task';


CREATE TABLE IF NOT EXISTS tags (
    id uuid PRIMARY KEY,
    name text,
    color_code text
);


CREATE TABLE IF NOT EXISTS project_activity (
    project_id uuid,
    id timeuuid,
    type text,
    actor text,
    details text,
    created_date timestamp,
    PRIMARY KEY (("project_id"), "id")
) WITH CLUSTERING ORDER BY ("id" DESC);


CREATE TABLE IF NOT EXISTS phone_numbers (
    user_id text PRIMARY KEY,
    id timeuuid,
    country_id text,
    country_code text,
    number text,
    formatted_number text,
    type text,
    status text,
    create_date timestamp,
    last_modified_date timestamp
);

CREATE TABLE IF NOT EXISTS phone_number_verifications (
    phone_number text PRIMARY KEY,
    id uuid,
    code text,
    created_at timestamp,
    expires_at timestamp
);


CREATE TABLE IF NOT EXISTS device_tokens (
    user_id text PRIMARY KEY,
    id uuid,
    device_token text,
    created_date timestamp,
    last_modified_date timestamp
);
