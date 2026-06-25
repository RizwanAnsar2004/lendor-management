CREATE TABLE IF NOT EXISTS audit_events (
  id             UUID         PRIMARY KEY,
  service        VARCHAR(64)  NOT NULL,
  application_id UUID,
  actor_user_id  UUID,
  actor_role     VARCHAR(32),
  action         VARCHAR(64)  NOT NULL,
  detail         TEXT,
  occurred_at    TIMESTAMPTZ  NOT NULL,
  recorded_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_audit_events_app     ON audit_events(application_id);
CREATE INDEX IF NOT EXISTS idx_audit_events_service ON audit_events(service);
