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