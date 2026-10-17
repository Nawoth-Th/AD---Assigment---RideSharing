# RideLink Microservices Platform — Account & Authentication Service (:8081)
## API Execution & Cross-Service ID Placement Manual — Member 1 (IT24102280)

---

### Service Metadata & Topology
- **Ownership**: Member 1 — IT24102280
- **Service Name**: `account-service`
- **Port**: `8081` | Context Base: `http://localhost:8081`
- **Swagger UI**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **MongoDB Database**: `ridelink_account_db` (collection: `users`)
- **Security**: BCrypt Password Hashing (Strength 10) + Stateless JWT (HMAC-SHA256, JJWT 0.12.6)
- **Official PDF Manual**: [01_Account_Authentication_Service_Execution_Guide.pdf](file:///g:/AD%20-%20Ride%20Sharing%20Application%20with%20Microservices/docs/01_Account_Authentication_Service_Execution_Guide.pdf)

---

### 1. Master ID Placement & Cross-Service Flow Matrix

| Identifier | Created / Emitted By | Where It Must Be Put (Target Endpoint & Parameter Location) | Sample Realistic Value |
| :--- | :--- | :--- | :--- |
| **`userId`**<br/>(Passenger) | `POST /api/v1/auth/register`<br/>(role: `PASSENGER`) | • **Account Svc**: Path variable in `GET /api/v1/users/{id}` (Step 04)<br/>• **Account Svc**: Path variable in `PUT /api/v1/users/{id}` (Step 05)<br/>• **Account Svc**: Path variable in `PATCH /api/v1/users/{id}/status` (Step 07)<br/>• **Ride Svc**: Body field `passengerId` in `POST /api/v1/rides/request` (Step 01)<br/>• **Ride Svc**: Path variable in `GET /api/v1/rides/passenger/{passengerId}` (Step 08)<br/>• **Payment Svc**: Body field `passengerId` in `POST /api/v1/payments/process` (Step 02) | `670fd1a2b4e89c1234567890` |
| **`userId`**<br/>(Driver User) | `POST /api/v1/auth/register`<br/>(role: `DRIVER`) | • **Driver Svc**: Request Body field `userId` in `POST /api/v1/drivers` (Step 01)<br/>• **Driver Svc**: Path variable `{userId}` in `GET /api/v1/drivers/user/{userId}` (Step 06) | `670fd1a2b4e89c1234567891` |
| **`userId`**<br/>(Admin User) | `POST /api/v1/auth/register`<br/>(role: `ADMIN`) | • **Account Svc**: Path variable in `GET /api/v1/users/{id}` (Step 04) | `670fd1a2b4e89c1234567892` |
| **`authToken`**<br/>(Passenger / Driver JWT) | `POST /api/v1/auth/login` or `POST /auth/register` | • **Account Svc**: Request Body field `token` in `POST /api/v1/auth/validate` (Step 03)<br/>• **Swagger UI**: Top right green **Authorize** button<br/>• **HTTP Header**: `Authorization: Bearer <token>` | `eyJhbGciOiJIUzI1NiJ9...` |
| **`adminAuthToken`**<br/>(Admin JWT) | `POST /api/v1/auth/login`<br/>(Admin credentials) | • **Account Svc**: HTTP Header `Authorization: Bearer <token>` in `GET /api/v1/users` (Step 06)<br/>• **Account Svc**: HTTP Header `Authorization: Bearer <token>` in `PATCH /api/v1/users/{id}/status` (Step 07)<br/>• *Alternative*: Header `X-User-Role: ADMIN` | `eyJhbGciOiJIUzI1NiJ9...` |

---

### 2. Swagger UI Live Demo Step-by-Step Execution Sequence

Open **`http://localhost:8081/swagger-ui.html`** in your browser. All endpoints are clearly labeled from **Step 01** to **Step 07**:

| Demo Step # | Swagger UI Endpoint Name | HTTP Method & URL | Input Data & Parameter Location | Expected Output & Value to Copy |
| :--- | :--- | :--- | :--- | :--- |
| **Step 01** | `Step 01 - Register new passenger or driver` | `POST /api/v1/auth/register` | Request Body (role: `PASSENGER`) | 201 Created. **Copy 'userId' (passengerId) and 'token' (authToken)** |
| **Step 01b** | `Step 01 - Register new passenger or driver` | `POST /api/v1/auth/register` | Request Body (role: `DRIVER`) | 201 Created. **Copy 'userId' (driverUserId)** for Driver Service |
| **Step 01c** | `Step 01 - Register new passenger or driver` | `POST /api/v1/auth/register` | Request Body (role: `ADMIN`) | 201 Created. **Copy 'token' (adminAuthToken)** for Admin endpoints |
| **Step 02** | `Step 02 - User login` | `POST /api/v1/auth/login` | Request Body (email + password) | 200 OK. Demonstrates BCrypt password matching & fresh JWT |
| **Step 03** | `Step 03 - Validate token` | `POST /api/v1/auth/validate` | Body: `{"token": "<authToken>"}` | 200 OK. Validates stateless signature (`valid: true`) |
| **Step 04** | `Step 04 - Get user profile by ID` | `GET /api/v1/users/{id}` | Path variable `{id}` = **passengerId** | 200 OK. Returns full user profile & active status |
| **Step 05** | `Step 05 - Update user profile` | `PUT /api/v1/users/{id}` | Path: **passengerId**; Body: updated details | 200 OK. Verifies name and phone number persistence |
| **Step 06** | `Step 06 - List all users (Admin operation)` | `GET /api/v1/users` | Header: `Authorization: Bearer <adminToken>` | 200 OK. Returns complete user registry array |
| **Step 07** | `Step 07 - Update account status (Admin operation)` | `PATCH /api/v1/users/{id}/status` | Path: **passengerId**; Body: `{"status": "SUSPENDED"}` | 200 OK. Suspends account. Demonstrates security rejection |

---

### 3. Detailed Endpoint Specifications & Swagger UI Demo Guide

#### `POST /api/v1/auth/register` — Step 01 - Register new passenger or driver
- **Operation**: `Step 01 - Register new passenger or driver`
- **Method & Path**: `POST /api/v1/auth/register`
- **Headers**: `Content-Type: application/json`

> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **POST /api/v1/auth/register** (`Step 01`).
> 2. Click **Try it out**.
> 3. Paste the registration JSON payload with role `PASSENGER`.
> 4. Click **Execute**.
> 5. Confirm HTTP **201 Created** status code.
> 6. **CRITICAL**: Copy the returned `userId` (this is your `passengerId`) and `token` (this is your `authToken`).

```json
// Request Body (Passenger Registration)
{
  "name": "Nimal Perera",
  "email": "nimal.perera@example.com",
  "phone": "+94771234567",
  "password": "Password@123",
  "role": "PASSENGER"
}
```

```json
// Response Body (HTTP 201 Created)
{
  "userId": "670fd1a2b4e89c1234567890",
  "name": "Nimal Perera",
  "email": "nimal.perera@example.com",
  "phone": "+94771234567",
  "role": "PASSENGER",
  "status": "ACTIVE",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

#### `POST /api/v1/auth/login` — Step 02 - User login
- **Operation**: `Step 02 - User login`
- **Method & Path**: `POST /api/v1/auth/login`

> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **POST /api/v1/auth/login** (`Step 02`).
> 2. Click **Try it out**.
> 3. Enter the registered email and password.
> 4. Click **Execute**.
> 5. Verify HTTP **200 OK** and demonstrate BCrypt password authentication.

```json
{
  "email": "nimal.perera@example.com",
  "password": "Password@123"
}
```

---

#### `POST /api/v1/auth/validate` — Step 03 - Validate token
- **Operation**: `Step 03 - Validate token`
- **Method & Path**: `POST /api/v1/auth/validate`

> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **POST /api/v1/auth/validate** (`Step 03`).
> 2. Click **Try it out**.
> 3. In the Request body, set `"token": "<paste authToken here>"`.
> 4. Click **Execute**.
> 5. Confirm HTTP **200 OK** and show `valid: true`.

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

#### `GET /api/v1/users/{id}` — Step 04 - Get user profile by ID
- **Operation**: `Step 04 - Get user profile by ID`
- **Method & Path**: `GET /api/v1/users/{id}`
- **Path Parameter**: `id` = `670fd1a2b4e89c1234567890` (passengerId)

> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **GET /api/v1/users/{id}** (`Step 04`).
> 2. Click **Try it out**.
> 3. In the `id` field, paste your **passengerId**.
> 4. Click **Execute**.
> 5. Confirm HTTP **200 OK** and profile attributes.

---

#### `PUT /api/v1/users/{id}` — Step 05 - Update user profile
- **Operation**: `Step 05 - Update user profile`
- **Method & Path**: `PUT /api/v1/users/{id}`
- **Path Parameter**: `id` = `670fd1a2b4e89c1234567890`

> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **PUT /api/v1/users/{id}** (`Step 05`).
> 2. Click **Try it out**.
> 3. In `id`, paste **passengerId**.
> 4. In Request body, modify `name` and `phone`.
> 5. Click **Execute**.
> 6. Verify HTTP **200 OK** and updated profile.

---

#### `GET /api/v1/users` — Step 06 - List all users (Admin operation)
- **Operation**: `Step 06 - List all users (Admin operation)`
- **Method & Path**: `GET /api/v1/users`

> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Click top right green **Authorize** button in Swagger UI, paste `adminAuthToken`, or use Header `X-User-Role: ADMIN`.
> 2. Expand **GET /api/v1/users** (`Step 06`).
> 3. Click **Try it out** and **Execute**.
> 4. Confirm HTTP **200 OK** and demonstrate user registry list.

---

#### `PATCH /api/v1/users/{id}/status` — Step 07 - Update account status (Admin operation)
- **Operation**: `Step 07 - Update account status (Admin operation)`
- **Method & Path**: `PATCH /api/v1/users/{id}/status`
- **Path Parameter**: `id` = `670fd1a2b4e89c1234567890`

> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **PATCH /api/v1/users/{id}/status** (`Step 07`).
> 2. Click **Try it out**.
> 3. In `id`, paste **passengerId**.
> 4. In Request body, supply `{"status": "SUSPENDED"}`.
> 5. Click **Execute**.
> 6. Verify HTTP **200 OK** and status changed to `SUSPENDED`.
