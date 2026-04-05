# FortuneLife — Insurance Management Backend

A production-ready insurance management system built with **Spring Boot 3** and **Java 21**, deployed on Render with PostgreSQL.

> **Live API:** [https://fortunelife-backend.onrender.com/swagger-ui/index.html](https://fortunelife-backend.onrender.com/swagger-ui/index.html)
> *(Free tier — first request takes ~2 min to wake up, then it's fast)*

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.x, Java 21 |
| Security | Spring Security + JWT (stateless) |
| Database | PostgreSQL (Render) |
| ORM | Spring Data JPA / Hibernate |
| Payments | Razorpay (order + signature verification) |
| File Storage | Cloudinary (cloud-based) |
| Email | Gmail SMTP + App Passwords |
| Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Deployment | Docker + Render |
| Testing | JUnit 5 + Mockito (24 tests) |

---

## Features Implemented

### Core Requirements
- ✅ **User & Role Management** — 4 roles: `ADMIN`, `EMPLOYEE`, `CUSTOMER`, `AGENT` with auto-seeded roles on startup
- ✅ **Financial Records CRUD** — Payments, Policies, Claims, Commissions with full lifecycle
- ✅ **Record Filtering** — All list endpoints support pagination, sorting, and multi-field filtering (date, category, type, status)
- ✅ **Dashboard Summary APIs** — Single endpoint returning totals, trends, category-wise breakdown, recent activity
- ✅ **Role-Based Access Control** — `@Secured` + Spring Security filter chain with per-endpoint role restrictions
- ✅ **Input Validation & Error Handling** — Jakarta Bean Validation + Global Exception Handler with proper HTTP status codes
- ✅ **Data Persistence** — PostgreSQL with JPA, environment-based profiles (dev/prod)

### Optional Enhancements
- ✅ JWT Token Authentication (stateless, 7-day expiry)
- ✅ Paginated Listing on all endpoints
- ✅ Global Search across customers, agents, policies, schemes
- ✅ Soft Delete (activate/deactivate pattern)
- ✅ Rate Limiting (10 req/min per IP on auth endpoints)
- ✅ Unit Tests (24 passing)
- ✅ API Documentation (Swagger UI — interactive, try-it-out enabled)
- ✅ Cloudinary File Uploads with type validation
- ✅ Dockerized Deployment
- ✅ Postman Collection (117 requests)

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Client / Postman                  │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP
┌──────────────────────▼──────────────────────────────┐
│              RateLimitFilter (auth endpoints)        │
│              JwtAuthenticationFilter                 │
│              Spring Security Filter Chain            │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                   Controllers                        │
│  Auth │ Admin │ Employee │ Agent │ Customer │ Policy │
│  Claim │ Query │ Payment │ Dashboard │ Search │ ...  │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                   Services                           │
│         Business Logic + Validation                  │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                  Repositories                        │
│              Spring Data JPA                         │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                  PostgreSQL                          │
└─────────────────────────────────────────────────────┘
```

---

## API Flow — Step by Step

Below is the complete flow to set up and use the system from scratch.

### Step 1: Register & Login (Admin)
```
POST /fortuneLife/auth/register?tempRole=ADMIN
Body: { "firstName": "John", "lastName": "Admin", "username": "john@admin1",
        "password": "Admin@1234", "email": "admin@example.com",
        "mobileNumber": "9876543210", "gender": "MALE", "dateOfBirth": "1990-05-15" }

POST /fortuneLife/auth/login
Body: { "usernameOrEmail": "john@admin1", "password": "Admin@1234", "role": "ADMIN" }
→ Returns: { "accessToken": "eyJhbG..." }
```
Use the `accessToken` as `Bearer <token>` in the Authorization header for all subsequent requests.

### Step 2: Create Employee (Admin only)
```
POST /fortuneLife/employee?role=EMPLOYEE
Headers: Authorization: Bearer <adminToken>
Body: { "salary": 50000, "userDto": { "firstName": "Jane", "lastName": "Emp",
        "username": "jane@emp1", "password": "Employee@1234",
        "email": "jane@example.com", "mobileNumber": "9876543211",
        "gender": "FEMALE", "dateOfBirth": "1992-08-20" } }
```
> ⚠️ Employees CANNOT self-register. Only Admin can create them.

### Step 3: Setup Geography (States → Cities)
```
POST /fortuneLife/state         → { "name": "Maharashtra", "active": true }
POST /fortuneLife/city/1        → { "pincode": 400001, "name": "Mumbai", "active": true }
```

### Step 4: Create Insurance Plans & Schemes
```
POST /fortuneLife/plan          → { "planName": "Life Insurance", "active": true }
POST /fortuneLife/scheme/1      → { "schemeName": "Term Life", "schemeImage": "...",
                                     "description": "...", "minAmount": 50000,
                                     "maxAmount": 1000000, "minAge": 18, "maxAge": 60,
                                     "profitRatio": 10.0, ... }
```

### Step 5: Link Scheme to City
```
PUT /fortuneLife/city/400001/scheme/1
```

### Step 6: Set Tax Rate
```
POST /fortuneLife/tax/set-tax?taxRate=18&deductionRate=5
```

### Step 7: Register Agent & Customer
```
POST /fortuneLife/auth/register?tempRole=AGENT     → Agent self-registers
POST /fortuneLife/auth/register?tempRole=CUSTOMER   → Customer self-registers
```

### Step 8: Customer Buys a Policy
```
POST /fortuneLife/customer/{customerId}/Insurance-Scheme/{schemeId}/policy
Body: { "premiumType": "MONTHLY", "policyAmount": 100000, "time": 10,
        "premiumAmount": 1000, "nomineeName": "John Doe",
        "relationStatusWithNominee": "Father", "submittedDocumentsDto": [] }
```

### Step 9: Make Payment (Razorpay)
```
POST /fortuneLife/payments/create-order   → Returns Razorpay order_id
POST /fortuneLife/payments/verify         → Verify signature + save payment
```

### Step 10: Employee Verifies Policy Documents
```
PUT /fortuneLife/policy/{policyId}/verify-policy
Body: [{ "id": 1, "documentName": "Aadhaar", "documentStatus": "APPROVED", "documentImage": "..." }]
```

### Step 11: Customer Files a Claim
```
POST /fortuneLife/claim/customer/{customerId}/Insurance-policy/{policyId}
Body: { "claimAmount": 50000, "bankName": "SBI", "branchName": "Mumbai",
        "bankAccountNumber": "123456789", "ifscCode": "SBIN0001234" }
```

### Step 12: Admin Approves/Rejects Claim
```
PUT /fortuneLife/claim/approve/{claimId}?operation=APPROVE&message=Approved
```

### Step 13: View Dashboard
```
GET /fortuneLife/dashboard/summary
→ Returns: totalIncome, totalExpenses, netBalance, policyCount, customerCount,
           agentCount, schemeWiseIncome, monthlyTrends, recentActivity
```

---

## How to Test the API

### Option 1: Swagger UI (Interactive — Recommended)
1. Open [https://fortunelife-backend.onrender.com/swagger-ui/index.html](https://fortunelife-backend.onrender.com/swagger-ui/index.html)
2. Click **"Try it out"** on any endpoint
3. Fill in the request body and click **"Execute"**
4. For protected endpoints: Login first, copy the `accessToken`, click the 🔒 **Authorize** button, enter `Bearer <token>`

### Option 2: Postman Collection
1. Import `FortuneLife-Postman-Collection.json` from this repo into Postman
2. The collection has **117 pre-configured requests** with sample bodies
3. Login endpoints auto-save tokens to collection variables
4. Variables: `devUrl` = Render, `baseUrl` = localhost

---

## Local Setup

### Prerequisites
- Java 21
- PostgreSQL
- Maven

### Steps
```bash
# 1. Clone the repo
git clone <repo-url>
cd FortuneLife-backend

# 2. Create PostgreSQL database
psql -U postgres -c "CREATE DATABASE fortune_life;"

# 3. Create application.properties (not in git — contains secrets)
cp src/main/resources/application-example.properties src/main/resources/application.properties
# Edit with your DB credentials, JWT secret, Cloudinary keys, etc.

# 4. Run
./mvnw spring-boot:run

# 5. Open Swagger
# http://localhost:8082/swagger-ui/index.html
```

### Run Tests
```bash
./mvnw test
# 24 tests passing: DashboardService, FileService, RateLimitFilter, GlobalExceptionHandler
```

---

## Role Permissions Matrix

| Action | ADMIN | EMPLOYEE | AGENT | CUSTOMER |
|--------|:-----:|:--------:|:-----:|:--------:|
| Create Employee | ✅ | ❌ | ❌ | ❌ |
| Create Agent | ✅ | ✅ | ❌ | ❌ |
| Create Customer | ✅ | ✅ | ✅ | ❌ |
| Manage Plans/Schemes | ✅ | ❌ | ❌ | ❌ |
| Buy Policy | ❌ | ❌ | ✅ | ✅ |
| Verify Policy Docs | ❌ | ✅ | ❌ | ❌ |
| File Claim | ❌ | ❌ | ✅ | ✅ |
| Approve/Reject Claim | ✅ | ❌ | ❌ | ❌ |
| View Dashboard | ✅ | ✅ | ❌ | ❌ |
| Global Search | ✅ | ✅ | ✅ | ❌ |
| Download Reports | ✅ | ❌ | ❌ | ❌ |
| Withdrawal Request | ❌ | ❌ | ✅ | ❌ |
| Approve Withdrawal | ✅ | ❌ | ❌ | ❌ |

---

## Project Structure

```
src/main/java/com/techlabs/app/
├── config/          # Security, CORS, Swagger, Cloudinary, DataInitializer
├── controller/      # 18 REST controllers
├── dto/             # Request/Response DTOs (decoupled from entities)
├── entity/          # JPA entities
├── exception/       # Global exception handler + custom exceptions
├── repository/      # Spring Data JPA repositories
├── security/        # JWT provider, auth filter, rate limit filter
├── service/         # Business logic layer
└── util/            # PageResponse wrapper
```

---

## Technical Decisions & Trade-offs

| Decision | Reasoning |
|----------|-----------|
| **Monolithic over Microservices** | Appropriate for the scope; reduces deployment complexity |
| **JWT stateless auth** | Scalable, no server-side session storage needed |
| **ddl-auto=update over Flyway** | Faster iteration for development; would use Flyway in production |
| **Cloudinary over S3** | Simpler setup, generous free tier, no AWS config needed |
| **Razorpay over Stripe** | Better suited for Indian market, simpler integration |
| **Gmail SMTP over SendGrid** | Sufficient for OTP emails, zero cost |
| **Soft delete pattern** | Preserves data integrity, supports audit trails |
| **Single dashboard endpoint** | Reduces frontend API calls, returns all analytics in one response |
| **Rate limiting on auth only** | Protects against brute force without impacting normal usage |
| **Environment profiles** | Clean separation of dev/prod configs, secrets via env vars in prod |

---

## Deployment

- **Platform:** Render (Free Tier)
- **Database:** Render PostgreSQL (Singapore region)
- **Docker:** Multi-stage build with Eclipse Temurin 21
- **Profiles:** `prod` profile activated via Dockerfile, all secrets from environment variables
- **Cold Start:** ~2-3 minutes on free tier (normal behavior)
