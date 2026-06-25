CREATE TABLE IF NOT EXISTS lender_members (
  id         UUID        PRIMARY KEY,
  user_id    UUID        NOT NULL UNIQUE,
  lender_id  UUID        NOT NULL,
  role       VARCHAR(32) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_lender_members_lender ON lender_members(lender_id);

CREATE TABLE IF NOT EXISTS application_reviews (
  id               UUID        PRIMARY KEY,
  application_id   UUID        NOT NULL UNIQUE,
  lender_id        UUID        NOT NULL,
  review_status    VARCHAR(32) NOT NULL,
  recommendation   TEXT,
  reviewer_user_id UUID,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_app_reviews_lender ON application_reviews(lender_id);

CREATE TABLE IF NOT EXISTS review_notes (
  id             UUID        PRIMARY KEY,
  application_id UUID        NOT NULL,
  author_user_id UUID        NOT NULL,
  body           TEXT        NOT NULL,
  created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_review_notes_app ON review_notes(application_id);

CREATE TABLE IF NOT EXISTS audit_log (
  id             UUID        PRIMARY KEY,
  application_id UUID,
  actor_user_id  UUID,
  action         VARCHAR(64) NOT NULL,
  detail         TEXT,
  created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_audit_app ON audit_log(application_id);
