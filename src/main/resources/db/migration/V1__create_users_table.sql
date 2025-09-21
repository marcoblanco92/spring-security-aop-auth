-- Create schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS auth;

-- Grant usage on schema to marbl_u
GRANT USAGE ON SCHEMA auth TO marbl_u;
GRANT CREATE ON SCHEMA auth TO marbl_u;

-- Users table
CREATE TABLE auth.users
(
    id              BIGSERIAL PRIMARY KEY,                 -- Primary key
    username        VARCHAR(100) UNIQUE NOT NULL,          -- Unique username, required
    email           VARCHAR(255) UNIQUE,                   -- Unique email, optional
    password_hash   VARCHAR(255) NOT NULL,                -- Password hash, required
    salt            VARCHAR(255) NOT NULL,                -- Salt for password hashing, required
    enabled         BOOLEAN DEFAULT TRUE NOT NULL,        -- Account enabled flag, default true
    failed_attempts INT DEFAULT 0 NOT NULL,               -- Failed login attempts, default 0
    locked_until    TIMESTAMP NULL,                        -- Timestamp until account is locked
    oauth_provider  VARCHAR(50),                           -- OAuth provider name (optional)
    oauth_id        VARCHAR(255),                          -- OAuth id (optional)
    oauth_secret    VARCHAR(255),                          -- OAuth secret (optional)
    created_at      TIMESTAMP DEFAULT NOW() NOT NULL,     -- Record creation timestamp
    updated_at      TIMESTAMP DEFAULT NOW() NOT NULL      -- Record last update timestamp
);

-- Roles table
CREATE TABLE auth.roles
(
    id   BIGSERIAL PRIMARY KEY,                            -- Primary key
    name VARCHAR(50) UNIQUE NOT NULL                       -- Role name, unique
);

-- Join table for Many-to-Many relationship between users and roles
CREATE TABLE auth.user_roles
(
    user_id BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,   -- FK to users
    role_id BIGINT NOT NULL REFERENCES auth.roles(id) ON DELETE CASCADE,   -- FK to roles
    PRIMARY KEY (user_id, role_id)                                          -- Composite primary key
);

-- Optional indexes to optimize join queries
CREATE INDEX IF NOT EXISTS idx_user_roles_user ON auth.user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role ON auth.user_roles(role_id);

-- Grant privileges to marbl_u
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE auth.users TO marbl_u;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE auth.roles TO marbl_u;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE auth.user_roles TO marbl_u;

-- Grant sequence privileges (for BIGSERIAL primary keys)
GRANT USAGE, SELECT, UPDATE ON SEQUENCE auth.users_id_seq TO marbl_u;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE auth.roles_id_seq TO marbl_u;