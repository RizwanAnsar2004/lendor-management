ALTER TABLE app_users ADD COLUMN IF NOT EXISTS role VARCHAR(32) NOT NULL DEFAULT 'BORROWER';

INSERT INTO app_users (id, email, first_name, last_name, password_hash, status, role)
VALUES (
  '00000000-0000-0000-0000-000000000020',
  'reviewer@abc-fintech.com',
  'Demo',
  'Reviewer',
  '$2b$10$e/pWpn2mfXKFQMnLjk.4ae3uCxqSNTTzl.EM8SIhY/KHFMTDzJJx2',
  'ACTIVE',
  'LENDER'
)
ON CONFLICT (email) DO NOTHING;
