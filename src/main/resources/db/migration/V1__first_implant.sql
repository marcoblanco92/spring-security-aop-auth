-- Create schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS auth;

-- Grant usage on schema to marbl_u
GRANT USAGE ON SCHEMA auth TO marbl_u;
GRANT CREATE ON SCHEMA auth TO marbl_u;

-- ============================
-- Users table (account principale)
-- ============================
CREATE TABLE auth.users
(
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) UNIQUE NOT NULL,          -- Email univoca, base identity
    username        VARCHAR(100) UNIQUE,                   -- Username opzionale
    password_hash   VARCHAR(255),                          -- Password hash, nullable (se solo OAuth)
    enabled         BOOLEAN DEFAULT TRUE NOT NULL,
    failed_attempts INT DEFAULT 0 NOT NULL,
    locked_until    TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT NOW() NOT NULL,
    updated_at      TIMESTAMP DEFAULT NOW() NOT NULL
);

-- ============================
-- Roles table
-- ============================
CREATE TABLE auth.roles
(
    id        BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL                  -- Enum-like (USER, ADMIN, ecc.)
);

-- ============================
-- Join table User-Roles (many-to-many)
-- ============================
CREATE TABLE auth.user_roles
(
    user_id BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES auth.roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- ============================
-- UserAuthProviders (1:N con Users)
-- ============================
CREATE TABLE auth.user_auth_providers
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    provider    VARCHAR(50) NOT NULL,                      -- GOOGLE, FACEBOOK, APPLE, ecc.
    provider_id VARCHAR(255) NOT NULL,                     -- ID utente dal provider
    created_at  TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT uq_provider UNIQUE (provider, provider_id)   -- Evita duplicati
);

-- ============================
-- Password reset tokens
-- ============================
CREATE TABLE auth.password_reset_tokens
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP    NOT NULL,
    used       BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_token_hash UNIQUE (token_hash)
);

-- ============================
-- Indexes
-- ============================
CREATE INDEX IF NOT EXISTS idx_user_roles_user ON auth.user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role ON auth.user_roles(role_id);
CREATE INDEX IF NOT EXISTS idx_auth_providers_user ON auth.user_auth_providers(user_id);

-- ============================
-- Grant privileges
-- ============================
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE auth.users TO marbl_u;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE auth.roles TO marbl_u;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE auth.user_roles TO marbl_u;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE auth.user_auth_providers TO marbl_u;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE auth.password_reset_tokens TO marbl_u;

-- Grant sequence privileges
GRANT USAGE, SELECT, UPDATE ON SEQUENCE auth.users_id_seq TO marbl_u;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE auth.roles_id_seq TO marbl_u;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE auth.user_auth_providers_id_seq TO marbl_u;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE auth.password_reset_tokens_id_seq TO marbl_u;

-- ============================
-- Audit schema
-- ============================
CREATE SCHEMA IF NOT EXISTS audit;
GRANT CREATE ON SCHEMA audit TO marbl_u;

CREATE TABLE IF NOT EXISTS audit.audit_log
(
    id             BIGSERIAL PRIMARY KEY,
    correlation_id VARCHAR(255) NOT NULL,
    username       VARCHAR(255),
    method         VARCHAR(255) NOT NULL,
    timestamp      TIMESTAMP NOT NULL,
    before_state   TEXT,
    after_state    TEXT,
    success        BOOLEAN NOT NULL DEFAULT TRUE
);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE audit.audit_log TO marbl_u;
CREATE INDEX idx_audit_correlation_id ON audit.audit_log(correlation_id);

-- ============================
-- Default roles
-- ============================
INSERT INTO auth.roles (role_name) VALUES ('ROLE_ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO auth.roles (role_name) VALUES ('ROLE_USER')  ON CONFLICT DO NOTHING;