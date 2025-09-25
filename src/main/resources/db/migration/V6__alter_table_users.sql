ALTER TABLE auth.users
    DROP COLUMN IF EXISTS oauth_secret;

ALTER TABLE auth.users
    RENAME COLUMN oauth_provider TO provider;

ALTER TABLE auth.users
    RENAME COLUMN oauth_id TO provider_id;

ALTER TABLE auth.users
    ALTER COLUMN password_hash DROP NOT NULL;