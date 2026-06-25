CREATE TABLE IF NOT EXISTS lenders (
  id          UUID         PRIMARY KEY,
  slug        VARCHAR(64)  NOT NULL UNIQUE,
  name        VARCHAR(160) NOT NULL,
  brand_color VARCHAR(16),
  active      BOOLEAN      NOT NULL DEFAULT TRUE,
  created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS loan_applications (
  id             UUID          PRIMARY KEY,
  user_id        UUID          NOT NULL,
  lender_id      UUID          NOT NULL REFERENCES lenders(id),
  passport_id    UUID,
  purpose        VARCHAR(64)   NOT NULL,
  origin_country VARCHAR(8),
  dest_country   VARCHAR(8),
  amount         NUMERIC(15,2),
  currency       VARCHAR(8),
  term_months    INT,
  status         VARCHAR(32)   NOT NULL,
  submitted_at   TIMESTAMPTZ,
  created_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  updated_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_loan_apps_user   ON loan_applications(user_id);
CREATE INDEX IF NOT EXISTS idx_loan_apps_lender ON loan_applications(lender_id);

CREATE TABLE IF NOT EXISTS borrower_profiles (
  id               UUID          PRIMARY KEY,
  application_id   UUID          NOT NULL UNIQUE REFERENCES loan_applications(id) ON DELETE CASCADE,
  full_name        VARCHAR(160),
  dob              DATE,
  email            VARCHAR(200),
  phone            VARCHAR(32),
  nationality      VARCHAR(8),
  address_line     VARCHAR(255),
  city             VARCHAR(120),
  region           VARCHAR(120),
  postal_code      VARCHAR(32),
  country          VARCHAR(8),
  employment_type  VARCHAR(32),
  employer_name    VARCHAR(160),
  monthly_income   NUMERIC(15,2),
  income_currency  VARCHAR(8),
  business_name    VARCHAR(160),
  business_revenue NUMERIC(15,2),
  supporting_rent     BOOLEAN,
  supporting_utility  BOOLEAN,
  supporting_telecom  BOOLEAN,
  created_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  updated_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS application_documents (
  id                UUID          PRIMARY KEY,
  application_id    UUID          NOT NULL REFERENCES loan_applications(id) ON DELETE CASCADE,
  doc_type          VARCHAR(64)   NOT NULL,
  tag               VARCHAR(120),
  original_filename VARCHAR(255)  NOT NULL,
  stored_path       VARCHAR(512)  NOT NULL,
  content_type      VARCHAR(120),
  size_bytes        BIGINT,
  created_at        TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  updated_at        TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_app_docs_application ON application_documents(application_id);
