CREATE KEYSPACE asakusa WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 2};


CREATE TABLE IF NOT EXISTS asakusa.users (
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
    birth_date text,
    zone_info text,
    locale text,
    phone_number text,
    phone_number_verified boolean,
    address map<text, text>,
    updated_at text
);


CREATE TABLE IF NOT EXISTS asakusa.user_credentials (
    user_id text PRIMARY KEY,
    password text,
    roles set<text>
);


CREATE TABLE IF NOT EXISTS asakusa.user_preferences (
    user_id text PRIMARY KEY,
    settings text,
    last_modified_date text
);


CREATE TABLE IF NOT EXISTS asakusa.teams (
    id uuid PRIMARY KEY,
    name text,
    description text,
    created_date text,
    last_modified_date text
);


CREATE TABLE IF NOT EXISTS asakusa.team_members (
    id uuid,
    user_id text,
    PRIMARY KEY (("id"), "user_id")
) WITH COMMENT = 'Retrieve the members of a team';


CREATE TABLE IF NOT EXISTS asakusa.projects (
    id uuid PRIMARY KEY,
    name text,
    description text,
    team_id uuid,
    owner_id text,
    start_date text,
    deadline text,
    end_date text,
    capacity smallint,
    status text,
    priority text,
    tags set<text>,
    created_date text,
    last_modified_date text
);


CREATE TABLE IF NOT EXISTS asakusa.user_projects (
    user_id text PRIMARY KEY,
    project_id uuid
) WITH COMMENT = 'Retrieve the projects for a user';


CREATE TABLE IF NOT EXISTS asakusa.team_projects (
    team_id uuid PRIMARY KEY,
    project_id uuid
) WITH COMMENT = 'Retrieve the projects for a team';


CREATE TABLE IF NOT EXISTS asakusa.tasks (
    project_id uuid,
    id uuid,
    title text,
    description text,
    assignee_id text,
    depends_on uuid,
    start_date text,
    estimated_duration smallint,
    end_date text,
    status text,
    priority text,
    tags set<text>,
    created_date text,
    last_modified_date text,
    PRIMARY KEY (("project_id"), "id")
);


CREATE TABLE IF NOT EXISTS asakusa.user_tasks (
    assignee_id text PRIMARY KEY,
    project_id uuid,
    task_id uuid
) WITH COMMENT = 'Retrieve the tasks assigned to a user';


CREATE TABLE IF NOT EXISTS asakusa.project_discussions (
    project_id uuid,
    id timeuuid,
    author_id text,
    title text,
    created_date text,
    PRIMARY KEY (("project_id"), "id")
);


CREATE TABLE IF NOT EXISTS asakusa.user_discussions (
    author_id text PRIMARY KEY,
    project_id uuid,
    discussion_id timeuuid
) WITH COMMENT = 'Retrieve the discussions for a user';


CREATE TABLE IF NOT EXISTS asakusa.discussion_messages (
    project_id uuid,
    discussion_id timeuuid,
    id timeuuid,
    author_id text,
    body text,
    created_date text,
    PRIMARY KEY (("project_id", "discussion_id"), "id")
) WITH CLUSTERING ORDER BY ("id" ASC);


CREATE TABLE IF NOT EXISTS asakusa.media (
    id uuid PRIMARY KEY,
    title text,
    thumbnail text,
    url text,
    type text,
    created_date text,
    last_modified_date text
);


CREATE TABLE IF NOT EXISTS asakusa.project_media (
    project_id uuid PRIMARY KEY,
    media_id uuid
) WITH COMMENT = 'Retrieve the media for a project';


CREATE TABLE IF NOT EXISTS asakusa.task_media (
    task_id uuid PRIMARY KEY,
    media_id uuid
) WITH COMMENT = 'Retrieve the media for a task';


CREATE TABLE IF NOT EXISTS asakusa.tags (
    id uuid PRIMARY KEY,
    name text,
    color_code text
);


CREATE TABLE IF NOT EXISTS asakusa.project_activity (
    project_id uuid,
    id timeuuid,
    type text,
    actor text,
    details text,
    created_date text,
    PRIMARY KEY (("project_id"), "id")
) WITH CLUSTERING ORDER BY ("id" DESC);


CREATE TABLE IF NOT EXISTS asakusa.phone_numbers (
    user_id text PRIMARY KEY,
    id timeuuid,
    country_id text,
    country_code text,
    number text,
    formatted_number text,
    type text,
    status text,
    created_date text,
    last_modified_date text
);

CREATE TABLE IF NOT EXISTS asakusa.phone_number_verifications (
    phone_number text PRIMARY KEY,
    id uuid,
    code text,
    created_at text,
    expires_at text
);


CREATE TABLE IF NOT EXISTS asakusa.device_tokens (
    user_id text PRIMARY KEY,
    id uuid,
    device_token text,
    created_date text,
    last_modified_date text
);
