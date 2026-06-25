CREATE TABLE IF NOT EXISTS app_users (
  id            UUID         PRIMARY KEY,
  email         VARCHAR(200) NOT NULL UNIQUE,
  first_name    VARCHAR(100) NOT NULL,
  last_name     VARCHAR(100) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  dob           DATE,
  status        VARCHAR(32)  NOT NULL,
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_app_users_email ON app_users(email);

CREATE TABLE IF NOT EXISTS otp_verification (
  id          UUID        PRIMARY KEY,
  email       VARCHAR(200) NOT NULL,
  code_hash   VARCHAR(100) NOT NULL,
  expires_at  TIMESTAMPTZ  NOT NULL,
  attempts    INT          NOT NULL DEFAULT 0,
  verified    BOOLEAN      NOT NULL DEFAULT FALSE,
  consumed    BOOLEAN      NOT NULL DEFAULT FALSE,
  created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_otp_email ON otp_verification(email);
