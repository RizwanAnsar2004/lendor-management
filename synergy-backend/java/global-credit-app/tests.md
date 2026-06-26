# End-to-End Test Guide — Global Credit Platform

All services run locally. Every request goes through the API Gateway on port 8080 except audit-service (not gateway-exposed) which is called directly on 8091.

## Prerequisites

```bash
# Start all services
cd synergy-backend/java/global-credit-app
.\start-all.ps1          # Windows
# bash start-all.sh      # Linux/Mac (if available)

# Tools required: curl, jq
# Windows note: replace $(curl ... | jq -r '.field') with PowerShell subexpressions
```

## Variable Exports (set these as you go)

```bash
export BASE="http://localhost:8080"
export BASE_AUDIT="http://localhost:8091"

# Populated during the test run:
# TOKEN          — borrower JWT
# LENDER_TOKEN   — lender JWT
# USER_ID        — registered borrower's UUID
# APP_ID         — loan application UUID
# PASSPORT_ID    — passport UUID
# DOC_ID         — uploaded document UUID
# NOTE_ID        — lender note UUID
```

## Seed Data (available at startup, no setup needed)

| Item | Value |
|---|---|
| Lender slug | `abc-fintech` |
| Lender UUID | `00000000-0000-0000-0000-000000000010` |
| Demo lender email | `reviewer@abc-fintech.com` (role: LENDER) |
| Demo lender password | `Lender123!` |

**Lender reviewer setup** (run once after first start):

```bash
# Set the lender reviewer password via psql (bash — avoids $ escaping issues)
HASH=$(python3 -c "import bcrypt; h = bcrypt.hashpw(b'Lender123!', bcrypt.gensalt(10)); print(h.decode())")
docker exec ubl-postgres psql -U ubl_app -d gcp \
  -c "UPDATE app_users SET password_hash = '$HASH' WHERE email = 'reviewer@abc-fintech.com';"
```

---

## Phase 0 — Service Health

Confirm all 6 services are up before running any other phase.

```bash
# 1. Audit service
curl -s http://localhost:8091/actuator/health
# Expected: 200  {"status":"UP"}

# 2. Auth service
curl -s http://localhost:8088/actuator/health
# Expected: 200  {"status":"UP"}

# 3. Borrower service
curl -s http://localhost:8089/actuator/health
# Expected: 200  {"status":"UP"}

# 4. Passport service
curl -s http://localhost:8087/actuator/health
# Expected: 200  {"status":"UP"}

# 5. Lender service
curl -s http://localhost:8090/actuator/health
# Expected: 200  {"status":"UP"}

# 6. Gateway
curl -s http://localhost:8080/actuator/health
# Expected: 200  {"status":"UP"}
```

---

## Phase 1 — Auth Service

### Happy Path

```bash
# 1. Request OTP
curl -s -X POST $BASE/auth/otp/request \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@example.com"}'
# Expected: 200  {"message":"OTP sent ..."}

# 2. Verify OTP  (check service log for the dev code)
#    grep "DEV CODE" logs/auth-service.log | tail -1
curl -s -X POST $BASE/auth/otp/verify \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@example.com","code":"<6-digit-from-log>"}'
# Expected: 200  {"verified":true}

# 3. Register borrower
curl -s -X POST $BASE/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email":     "testuser@example.com",
    "firstName": "Jane",
    "lastName":  "Doe",
    "password":  "SecurePass1",
    "dob":       "1990-05-15"
  }'
# Expected: 200  {"userId":"<uuid>","status":"ACTIVE"}
export USER_ID=$(curl -s -X POST $BASE/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser2@example.com","firstName":"Jane","lastName":"Doe","password":"SecurePass1","dob":"1990-05-15"}' \
  | jq -r '.userId')

# 4. Login
export TOKEN=$(curl -s -X POST $BASE/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@example.com","password":"SecurePass1"}' \
  | jq -r '.accessToken')
echo "TOKEN=$TOKEN"
# Expected: 200  {"accessToken":"<jwt>","tokenType":"Bearer","expiresInSeconds":3600}

# 5. Get current user
curl -s $BASE/auth/me \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200  {"userId":"...","email":"testuser@example.com","firstName":"Jane","lastName":"Doe"}
```

### Edge Cases

```bash
# 6. OTP request — blank email
curl -s -X POST $BASE/auth/otp/request \
  -H "Content-Type: application/json" \
  -d '{"email":""}'
# Expected: 400  {"status":400,"error":"...","fields":{"email":"..."}}

# 7. OTP request — invalid email format
curl -s -X POST $BASE/auth/otp/request \
  -H "Content-Type: application/json" \
  -d '{"email":"notanemail"}'
# Expected: 400

# 8. OTP verify — code too short (5 chars)
curl -s -X POST $BASE/auth/otp/verify \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@example.com","code":"12345"}'
# Expected: 400  (code must be exactly 6 characters)

# 9. OTP verify — wrong code
curl -s -X POST $BASE/auth/otp/verify \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@example.com","code":"000000"}'
# Expected: 400

# 10. Register — password too short (7 chars)
curl -s -X POST $BASE/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"short@example.com","firstName":"A","lastName":"B","password":"1234567","dob":"1990-01-01"}'
# Expected: 400  (password must be at least 8 characters)

# 11. Register — future date of birth
curl -s -X POST $BASE/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"future@example.com","firstName":"A","lastName":"B","password":"SecurePass1","dob":"2099-01-01"}'
# Expected: 400  (@Past constraint on dob)

# 12. Register — duplicate email
curl -s -X POST $BASE/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@example.com","firstName":"A","lastName":"B","password":"SecurePass1","dob":"1990-01-01"}'
# Expected: 409  {"status":409,"error":"Account with this email already exists"}

# 13. Login — wrong password
curl -s -X POST $BASE/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@example.com","password":"WrongPass1"}'
# Expected: 401

# 14. Login — unknown email
curl -s -X POST $BASE/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"nobody@example.com","password":"SecurePass1"}'
# Expected: 401

# 15. GET /auth/me — no token
curl -s $BASE/auth/me
# Expected: 401

# 16. GET /auth/me — malformed token
curl -s $BASE/auth/me \
  -H "Authorization: Bearer not.a.real.token"
# Expected: 401
```

---

## Phase 2 — Passport Service

### Happy Path

```bash
# 1. Init passport
export PASSPORT_ID=$(curl -s -X POST $BASE/v1/passports/init \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "purpose":       "Education funding",
    "originCountry": "PK",
    "destCountry":   "GB",
    "fullName":      "Jane Doe",
    "dob":           "1990-05-15"
  }' | jq -r '.passportId')
echo "PASSPORT_ID=$PASSPORT_ID"
# Expected: 200  {"passportId":"<uuid>","status":"IN_PROGRESS"}

# 2. Connect data sources
curl -s -X POST $BASE/v1/passports/$PASSPORT_ID/sources \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"sources":["BANK","TAX","PAYROLL"]}'
# Expected: 204  (no body)

# 3. Generate passport
curl -s -X POST $BASE/v1/passports/$PASSPORT_ID/generate \
  -H "Authorization: Bearer $TOKEN"
# Expected: 204  (no body)
```

### Edge Cases

```bash
# 4. Init — blank purpose
curl -s -X POST $BASE/v1/passports/init \
  -H "Content-Type: application/json" \
  -d '{"purpose":"","originCountry":"PK","destCountry":"GB"}'
# Expected: 400

# 5. Init — blank originCountry
curl -s -X POST $BASE/v1/passports/init \
  -H "Content-Type: application/json" \
  -d '{"purpose":"Education","originCountry":"","destCountry":"GB"}'
# Expected: 400

# 6. Init — blank destCountry
curl -s -X POST $BASE/v1/passports/init \
  -H "Content-Type: application/json" \
  -d '{"purpose":"Education","originCountry":"PK","destCountry":""}'
# Expected: 400

# 7. Sources — null list
curl -s -X POST $BASE/v1/passports/$PASSPORT_ID/sources \
  -H "Content-Type: application/json" \
  -d '{"sources":null}'
# Expected: 400  (@NotNull on sources)

# 8. Sources — empty list (valid)
curl -s -X POST $BASE/v1/passports/$PASSPORT_ID/sources \
  -H "Content-Type: application/json" \
  -d '{"sources":[]}'
# Expected: 204

# 9. Generate on nonexistent passport
curl -s -X POST $BASE/v1/passports/00000000-0000-0000-0000-000000000000/generate
# Expected: 404
```

---

## Phase 3 — Borrower Application Lifecycle

### Public Lender Lookup

```bash
# 1. Get lender by slug (no auth required)
curl -s $BASE/v1/lenders/abc-fintech
# Expected: 200  {"id":"00000000-0000-0000-0000-000000000010","slug":"abc-fintech","name":"ABC Fintech","brandColor":"#3fb950"}

# 2. Nonexistent slug
curl -s $BASE/v1/lenders/does-not-exist
# Expected: 404
```

### Create Application

```bash
# 3. Create application (happy path)
export APP_ID=$(curl -s -X POST $BASE/v1/applications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "lenderSlug":  "abc-fintech",
    "purpose":     "Home purchase",
    "originCountry": "PK",
    "destCountry":   "GB",
    "amount":      50000,
    "currency":    "GBP",
    "termMonths":  24,
    "passportId":  "'$PASSPORT_ID'"
  }' | jq -r '.applicationId')
echo "APP_ID=$APP_ID"
# Expected: 200  {"applicationId":"<uuid>","status":"DRAFT"}
```

### Edge Cases — Creation

```bash
# 4. Blank lenderSlug
curl -s -X POST $BASE/v1/applications \
  -H "Content-Type: application/json" \
  -d '{"lenderSlug":"","purpose":"Home purchase","amount":50000,"termMonths":24}'
# Expected: 400

# 5. Blank purpose
curl -s -X POST $BASE/v1/applications \
  -H "Content-Type: application/json" \
  -d '{"lenderSlug":"abc-fintech","purpose":"","amount":50000,"termMonths":24}'
# Expected: 400

# 6. amount = 0
curl -s -X POST $BASE/v1/applications \
  -H "Content-Type: application/json" \
  -d '{"lenderSlug":"abc-fintech","purpose":"Home purchase","amount":0,"termMonths":24}'
# Expected: 400  (@Positive)

# 7. amount negative
curl -s -X POST $BASE/v1/applications \
  -H "Content-Type: application/json" \
  -d '{"lenderSlug":"abc-fintech","purpose":"Home purchase","amount":-100,"termMonths":24}'
# Expected: 400

# 8. termMonths = 0
curl -s -X POST $BASE/v1/applications \
  -H "Content-Type: application/json" \
  -d '{"lenderSlug":"abc-fintech","purpose":"Home purchase","amount":50000,"termMonths":0}'
# Expected: 400  (@Min(1))
```

### Read Application

```bash
# 9. Get application by ID
curl -s $BASE/v1/applications/$APP_ID \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200  full object with status "DRAFT"

# 10. Nonexistent ID
curl -s $BASE/v1/applications/00000000-0000-0000-0000-000000000000 \
  -H "Authorization: Bearer $TOKEN"
# Expected: 404
```

### Update Profile

```bash
# 11. Update borrower profile (happy path)
curl -s -X PUT $BASE/v1/applications/$APP_ID/profile \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "fullName":       "Jane Doe",
    "dob":            "1990-05-15",
    "email":          "jane@example.com",
    "phone":          "+447700900000",
    "nationality":    "Pakistani",
    "addressLine":    "123 Main St",
    "city":           "Lahore",
    "country":        "PK",
    "employmentType": "EMPLOYED",
    "employerName":   "Acme Corp",
    "monthlyIncome":  3500,
    "incomeCurrency": "GBP"
  }'
# Expected: 200  updated application object
```

### Edge Cases — Profile

```bash
# 12. Future date of birth
curl -s -X PUT $BASE/v1/applications/$APP_ID/profile \
  -H "Content-Type: application/json" \
  -d '{"dob":"2099-01-01"}'
# Expected: 400  (@Past)

# 13. Negative monthlyIncome
curl -s -X PUT $BASE/v1/applications/$APP_ID/profile \
  -H "Content-Type: application/json" \
  -d '{"monthlyIncome":-1}'
# Expected: 400  (@DecimalMin("0.0"))

# 14. Invalid email format in profile
curl -s -X PUT $BASE/v1/applications/$APP_ID/profile \
  -H "Content-Type: application/json" \
  -d '{"email":"notvalid"}'
# Expected: 400  (@Email)
```

### Upload Documents

```bash
# 15. Upload valid PDF
export DOC_ID=$(curl -s -X POST $BASE/v1/applications/$APP_ID/documents \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/sample.pdf;type=application/pdf" \
  -F "docType=INCOME_PROOF" \
  -F "tag=payslip" \
  | jq -r '.id')
echo "DOC_ID=$DOC_ID"
# Expected: 200  {"id":"<uuid>","docType":"INCOME_PROOF","originalFilename":"sample.pdf",...}

# 16. Upload PNG image
curl -s -X POST $BASE/v1/applications/$APP_ID/documents \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/photo.png;type=image/png" \
  -F "docType=ID_DOCUMENT"
# Expected: 201

# 17. Upload disallowed file type (.txt)
curl -s -X POST $BASE/v1/applications/$APP_ID/documents \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/notes.txt;type=text/plain" \
  -F "docType=OTHER"
# Expected: 400  (content type not in allowed list: application/pdf, image/png, image/jpeg)

# 18. Upload file > 10 MB
#     Create a 11MB test file first: dd if=/dev/zero of=big.pdf bs=1M count=11
curl -s -X POST $BASE/v1/applications/$APP_ID/documents \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@big.pdf;type=application/pdf" \
  -F "docType=OTHER"
# Expected: 400  (file exceeds 10 MB limit)

# 19. List documents
curl -s $BASE/v1/applications/$APP_ID/documents \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200  array with 2 document objects

# 20. Download document
curl -s -O -J $BASE/v1/applications/$APP_ID/documents/$DOC_ID \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200  file download with Content-Disposition header

# 21. Download nonexistent document
curl -s $BASE/v1/applications/$APP_ID/documents/00000000-0000-0000-0000-000000000000 \
  -H "Authorization: Bearer $TOKEN"
# Expected: 404
```

### Submit Application

```bash
# 22. Submit (happy path — requires at least 1 document)
curl -s -X POST $BASE/v1/applications/$APP_ID/submit \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200  {"applicationId":"...","status":"SUBMITTED","submittedAt":"..."}

# 23. Submit same application again
curl -s -X POST $BASE/v1/applications/$APP_ID/submit \
  -H "Authorization: Bearer $TOKEN"
# Expected: 409  (application already submitted)

# 24. Submit a fresh application with no documents
export NODOC_APP_ID=$(curl -s -X POST $BASE/v1/applications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"lenderSlug":"abc-fintech","purpose":"Test","amount":1000,"termMonths":6}' \
  | jq -r '.applicationId')
curl -s -X POST $BASE/v1/applications/$NODOC_APP_ID/submit \
  -H "Authorization: Bearer $TOKEN"
# Expected: 400  (at least one document required before submission)
```

---

## Phase 4 — Lender Review

### Setup: Set Reviewer Password

The seeded reviewer (`reviewer@abc-fintech.com`) has no password. Run this once (requires Python bcrypt: `pip install bcrypt`):

```bash
# Generate and set bcrypt hash for password 'Lender123!'
HASH=$(python3 -c "import bcrypt; print(bcrypt.hashpw(b'Lender123!', bcrypt.gensalt(10)).decode())")
docker exec ubl-postgres psql -U ubl_app -d gcp \
  -c "UPDATE app_users SET password_hash = '$HASH' WHERE email = 'reviewer@abc-fintech.com';"
```

Then get the lender JWT:

```bash
export LENDER_TOKEN=$(curl -s -X POST $BASE/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"reviewer@abc-fintech.com","password":"Lender123!"}' \
  | jq -r '.accessToken')
echo "LENDER_TOKEN=$LENDER_TOKEN"
# Expected: 200  {"accessToken":"<jwt>","tokenType":"Bearer",...}
```

### Happy Path

```bash
# 1. Get lender identity
curl -s $BASE/v1/lender/me \
  -H "Authorization: Bearer $LENDER_TOKEN"
# Expected: 200  {"userId":"...","role":"LENDER","lender":{"slug":"abc-fintech",...}}

# 2. List all submitted applications
curl -s $BASE/v1/lender/applications \
  -H "Authorization: Bearer $LENDER_TOKEN"
# Expected: 200  array including the application submitted in Phase 3

# 3. Get full application detail (triggers VIEW_DETAIL audit event)
curl -s $BASE/v1/lender/applications/$APP_ID \
  -H "Authorization: Bearer $LENDER_TOKEN"
# Expected: 200  full detail including profile, documents[], review, notes[]

# 4. Download applicant document
curl -s -O -J $BASE/v1/lender/applications/$APP_ID/documents/$DOC_ID \
  -H "Authorization: Bearer $LENDER_TOKEN"
# Expected: 200  file download
```

### Notes

```bash
# 5. Add a note
export NOTE_ID=$(curl -s -X POST $BASE/v1/lender/applications/$APP_ID/notes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $LENDER_TOKEN" \
  -d '{"body":"Application looks promising. Income verified."}' \
  | jq -r '.id')
echo "NOTE_ID=$NOTE_ID"
# Expected: 200  {"id":"...","body":"Application looks promising...","createdAt":"..."}

# 6. Add note — blank body
curl -s -X POST $BASE/v1/lender/applications/$APP_ID/notes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $LENDER_TOKEN" \
  -d '{"body":""}'
# Expected: 400  (@NotBlank on body)

# 7. List notes
curl -s $BASE/v1/lender/applications/$APP_ID/notes \
  -H "Authorization: Bearer $LENDER_TOKEN"
# Expected: 200  array with 1 note
```

### Review Status Transitions

```bash
# 8. Set UNDER_REVIEW
curl -s -X PUT $BASE/v1/lender/applications/$APP_ID/review \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $LENDER_TOKEN" \
  -d '{"reviewStatus":"UNDER_REVIEW"}'
# Expected: 200  {"reviewStatus":"UNDER_REVIEW",...}

# 9. Set INFO_REQUESTED
curl -s -X PUT $BASE/v1/lender/applications/$APP_ID/review \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $LENDER_TOKEN" \
  -d '{"reviewStatus":"INFO_REQUESTED","recommendation":"Need payslips for last 3 months"}'
# Expected: 200

# 10. Set REVIEWED (final decision)
curl -s -X PUT $BASE/v1/lender/applications/$APP_ID/review \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $LENDER_TOKEN" \
  -d '{"reviewStatus":"REVIEWED","recommendation":"Approve — low risk"}'
# Expected: 200  {"reviewStatus":"REVIEWED","recommendation":"Approve — low risk",...}

# 11. Invalid reviewStatus value
curl -s -X PUT $BASE/v1/lender/applications/$APP_ID/review \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $LENDER_TOKEN" \
  -d '{"reviewStatus":"APPROVED"}'
# Expected: 400  (must be one of: NEW, UNDER_REVIEW, INFO_REQUESTED, REVIEWED)

# 12. Blank reviewStatus
curl -s -X PUT $BASE/v1/lender/applications/$APP_ID/review \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $LENDER_TOKEN" \
  -d '{"reviewStatus":""}'
# Expected: 400  (@NotBlank)
```

### Audit Trail via Lender

```bash
# 13. Get audit trail for application (proxied from audit-service)
curl -s $BASE/v1/lender/applications/$APP_ID/audit \
  -H "Authorization: Bearer $LENDER_TOKEN"
# Expected: 200  array including VIEW_DETAIL and UPDATE_REVIEW events
```

### Role & Auth Guards

```bash
# 14. Borrower JWT accessing lender endpoint
curl -s $BASE/v1/lender/me \
  -H "Authorization: Bearer $TOKEN"
# Expected: 403  (LENDER role required)

# 15. No JWT at all
curl -s $BASE/v1/lender/me
# Expected: 403

# 16. Nonexistent application ID
curl -s $BASE/v1/lender/applications/00000000-0000-0000-0000-000000000099 \
  -H "Authorization: Bearer $LENDER_TOKEN"
# Expected: 404
```

---

## Phase 5 — Audit Service (Direct)

The audit service is not exposed via the gateway. Call it directly on port 8091.

### Ingest Events

```bash
# 1. Post a manual event (happy path)
curl -s -X POST $BASE_AUDIT/v1/audit/events \
  -H "Content-Type: application/json" \
  -d '{"service":"test","action":"PING"}'
# Expected: 201  {"id":"<uuid>","recordedAt":"..."}

# 2. Missing service field
curl -s -X POST $BASE_AUDIT/v1/audit/events \
  -H "Content-Type: application/json" \
  -d '{"action":"PING"}'
# Expected: 400

# 3. Missing action field
curl -s -X POST $BASE_AUDIT/v1/audit/events \
  -H "Content-Type: application/json" \
  -d '{"service":"test"}'
# Expected: 400

# 4. Blank service string
curl -s -X POST $BASE_AUDIT/v1/audit/events \
  -H "Content-Type: application/json" \
  -d '{"service":"","action":"PING"}'
# Expected: 400  (@NotBlank)

# 5. All optional fields populated
curl -s -X POST $BASE_AUDIT/v1/audit/events \
  -H "Content-Type: application/json" \
  -d '{
    "service":       "test",
    "action":        "FULL_EVENT",
    "applicationId": "'$APP_ID'",
    "actorUserId":   "'$USER_ID'",
    "actorRole":     "BORROWER",
    "detail":        "manual test event",
    "occurredAt":    "2026-06-26T00:00:00Z"
  }'
# Expected: 201
```

### Query Events

```bash
# 6. Filter by applicationId
curl -s "$BASE_AUDIT/v1/audit/events?applicationId=$APP_ID"
# Expected: 200  array with events from borrower-service and lender-service for this app

# 7. Filter by service
curl -s "$BASE_AUDIT/v1/audit/events?service=auth-service"
# Expected: 200  only events where service="auth-service"

# 8. Filter by action
curl -s "$BASE_AUDIT/v1/audit/events?action=LOGIN_SUCCESS"
# Expected: 200  only LOGIN_SUCCESS events

# 9. Limit results
curl -s "$BASE_AUDIT/v1/audit/events?limit=2"
# Expected: 200  array with at most 2 items

# 10. Filter that matches nothing
curl -s "$BASE_AUDIT/v1/audit/events?service=nonexistent-service"
# Expected: 200  []  (empty array, not 404)
```

---

## Phase 6 — Cross-Service Audit Trail Verification

After completing Phases 1–4, verify the full audit trail for one application.

```bash
# 1. Full application event trail
curl -s "$BASE_AUDIT/v1/audit/events?applicationId=$APP_ID" | jq '[.[] | {service, action}]'
# Expected events (newest first):
#   {"service":"lender-service",   "action":"UPDATE_REVIEW"}      × 3
#   {"service":"lender-service",   "action":"VIEW_DETAIL"}
#   {"service":"borrower-service", "action":"APPLICATION_SUBMITTED"}
#   {"service":"borrower-service", "action":"DOCUMENT_UPLOADED"}   × 2
#   {"service":"borrower-service", "action":"PROFILE_UPDATED"}
#   {"service":"borrower-service", "action":"APPLICATION_CREATED"}

# 2. Auth service events
curl -s "$BASE_AUDIT/v1/audit/events?service=auth-service" | jq '[.[] | .action]'
# Expected to include: USER_REGISTERED, LOGIN_SUCCESS

# 3. Passport service events
curl -s "$BASE_AUDIT/v1/audit/events?service=passport-service" | jq '[.[] | .action]'
# Expected to include: PASSPORT_INIT, SOURCES_CONNECTED, PASSPORT_GENERATED
```

---

## Phase 7 — CORS

```bash
# 1. Preflight from localhost:3000
curl -s -X OPTIONS $BASE/auth/login \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type,Authorization" \
  -v 2>&1 | grep -i "access-control"
# Expected headers in response:
#   Access-Control-Allow-Origin: http://localhost:3000
#   Access-Control-Allow-Methods: POST (or *)
#   Access-Control-Allow-Headers: Content-Type,Authorization (or *)

# 2. Preflight from arbitrary origin
curl -s -X OPTIONS $BASE/v1/applications \
  -H "Origin: https://app.example.com" \
  -H "Access-Control-Request-Method: POST" \
  -v 2>&1 | grep -i "access-control"
# Expected: Access-Control-Allow-Origin: https://app.example.com

# 3. Preflight — verify methods
curl -s -X OPTIONS $BASE/v1/applications \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: PUT" \
  -v 2>&1 | grep -i "Access-Control-Allow-Methods"
# Expected: includes PUT (or *)

# 4. Preflight — verify credentials allowed
curl -s -X OPTIONS $BASE/auth/me \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Headers: Authorization" \
  -v 2>&1 | grep -i "access-control-allow-credentials"
# Expected: Access-Control-Allow-Credentials: true
```

---

## Phase 8 — Swagger / OpenAPI

```bash
# 1. Swagger UI HTML (via gateway)
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/swagger-ui.html
# Expected: 200

# 2. Auth service API spec
curl -s http://localhost:8080/auth-docs/v3/api-docs | jq '.info.title'
# Expected: "Auth Service"

# 3. Borrower service API spec
curl -s http://localhost:8080/borrower-docs/v3/api-docs | jq '.info.title'
# Expected: "Borrower Service" (or similar)

# 4. Lender service API spec
curl -s http://localhost:8080/lender-docs/v3/api-docs | jq '.info.title'
# Expected: "Lender Service"

# 5. Passport service API spec
curl -s http://localhost:8080/passport-docs/v3/api-docs | jq '.info.title'
# Expected: "Passport Service"

# 6. Audit service API spec
curl -s http://localhost:8080/audit-docs/v3/api-docs | jq '.info.title'
# Expected: "Audit Service"
```

---

## Quick Reference: Expected Status Codes

| Scenario | Code |
|---|---|
| Success (GET, PUT) | 200 |
| Created | 201 |
| No content | 204 |
| Validation failure | 400 |
| Auth failure / bad token | 401 |
| Wrong role (non-LENDER) | 403 |
| Resource not found | 404 |
| Duplicate resource | 409 |
| Audit service unavailable (lender read) | 502 |

## Error Response Shape

All 4xx/5xx responses follow:

```json
{
  "status": 400,
  "error": "Human-readable message",
  "fields": {
    "fieldName": "constraint message"
  }
}
```

`fields` is only present on validation (400) errors.
