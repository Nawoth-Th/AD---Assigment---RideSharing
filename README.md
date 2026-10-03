# RideLink — Backend Microservices for a Ride-Sharing Platform

**Module**: IT3130 – Application Development  
**Assessment**: Group Assignment (30%)  
**Faculty**: Faculty of Computing, Department of Information Technology  

---

## 1. Group Organisation & Ownership Matrix

Each team member is the primary owner of one core microservice as required by the assignment brief:

| Member | Student ID | Assigned Microservice | Port | Database Name | Primary Responsibilities |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Member 1** | **IT24102280** | **Account Service** | `8081` | `ridelink_account_db` | User registration (Passenger & Driver), login & JWT issuance, token validation, role-based authorization (`PASSENGER`, `DRIVER`, `ADMIN`), user profiles, account status management (`ACTIVE`, `SUSPENDED`). |
| **Member 2** | **IT24104248** | **Driver & Vehicle Service** | `8082` | `ridelink_driver_db` | Driver operational profiles, vehicle registration & specs, availability management (`AVAILABLE`, `ON_TRIP`, `OFFLINE`), service area, simulated GPS location, eligible driver queries. |
| **Member 3** | **IT24104016** | **Ride Management Service** | `8083` | `ridelink_ride_db` | Ride request creation, pickup/destination, driver assignment, ride state lifecycle state machine (`REQUESTED` $\rightarrow$ `ASSIGNED` $\rightarrow$ `ACCEPTED` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `COMPLETED` / `CANCELLED`), state validation rules, ride history. |
| **Member 4** | **IT24610827** | **Fare & Payment Service** | `8084` | `ridelink_payment_db` | Transparent fare calculation rule (Base fare + Rate/KM + Surge), fare estimation endpoint, simulated payment processing, payment status tracking (`PENDING`, `COMPLETED`, `FAILED`), digital receipt generation & retrieval. |

---

## 2. Technology Stack & Architectural Highlights

- **Programming Language**: Java 22
- **Backend Framework**: Spring Boot 3.3.4
- **Database Architecture**: **Database-per-Service** (4 completely isolated MongoDB databases on MongoDB Cloud / Atlas)
- **API Documentation**: SpringDoc OpenAPI 3 / Swagger UI (`/swagger-ui.html`) on every service
- **Authentication**: Stateless HMAC-SHA256 JWT tokens & BCrypt password hashing
- **Interservice Communication**: Synchronous REST via Spring `RestClient` with structured fallbacks
- **Continuous Integration**: GitHub Actions CI (`.github/workflows/ci.yml`) automatically building and testing all 4 microservices
- **Quality Assurance**: Unit tests with JUnit 5 & Mockito (28 unit tests, 100% pass rate)

---

## 3. Database Isolation (MongoDB Atlas / Cloud)

Each microservice communicates solely with its own dedicated MongoDB database namespace:

- `Account Service` $\rightarrow$ `ridelink_account_db`
- `Driver Service` $\rightarrow$ `ridelink_driver_db`
- `Ride Service` $\rightarrow$ `ridelink_ride_db`
- `Payment Service` $\rightarrow$ `ridelink_payment_db`

### Connecting to MongoDB Atlas

Configure your cloud connection string in `.env` (or pass as an environment variable `MONGODB_URI`):

```bash
MONGODB_URI=mongodb+srv://<username>:<password>@<cluster-name>.mongodb.net/?retryWrites=true&w=majority
```

Each microservice automatically appends its database name so no data is ever shared across microservice boundaries.

---

## 4. Quickstart: Building & Running

### Prerequisites

- JDK 22 installed (`java -version`)
- Git

### Build & Run Tests

Build all 4 microservices and execute the full test suite using the included Maven Wrapper:

```powershell
# Windows
.\mvnw.cmd clean test

# Linux / macOS
./mvnw clean test
```

### Running the Microservices (Recommended Start-up Order)

Open 4 separate terminal windows (or run directly in IntelliJ IDEA):

```powershell
# Terminal 1: Account Service (Port 8081)
.\mvnw.cmd spring-boot:run -pl account-service

# Terminal 2: Driver & Vehicle Service (Port 8082)
.\mvnw.cmd spring-boot:run -pl driver-service

# Terminal 3: Fare & Payment Service (Port 8084)
.\mvnw.cmd spring-boot:run -pl payment-service

# Terminal 4: Ride Management Service (Port 8083)
.\mvnw.cmd spring-boot:run -pl ride-service
```

---

## 5. Interactive Swagger UI / OpenAPI Endpoints

Once services are running, access their live interactive documentation and API testing interfaces:

| Microservice | Swagger UI Location | OpenAPI JSON Spec |
| :--- | :--- | :--- |
| **Account Service** | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs) |
| **Driver & Vehicle Service** | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) | [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs) |
| **Ride Management Service** | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) | [http://localhost:8083/v3/api-docs](http://localhost:8083/v3/api-docs) |
| **Fare & Payment Service** | [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html) | [http://localhost:8084/v3/api-docs](http://localhost:8084/v3/api-docs) |

---

## 6. Postman Test Suite & Demonstration Guide

The repository includes pre-configured Postman artifacts located in `postman/`:

- `postman/RideLink_API.postman_collection.json`
- `postman/RideLink_Environment.postman_environment.json`

### Importing into Postman

1. Open Postman $\rightarrow$ Click **Import**.
2. Select both JSON files from the `postman/` directory.
3. In the top-right environment selector, choose **RideLink Microservices Environment**.

### Test Execution Sequence

The collection includes test scripts that dynamically transfer generated IDs and JWT tokens between requests:

1. **Account Service**:
   - `Register Passenger` $\rightarrow$ extracts `passengerId` & `authToken`.
   - `Register Driver Account` $\rightarrow$ extracts `driverUserId`.
   - `Login Passenger` & `Validate Auth Token`.
   - `[Negative] Duplicate Email Registration` $\rightarrow$ verifies `409 Conflict`.
   - `[Negative] Login with Invalid Password` $\rightarrow$ verifies `401 Unauthorized`.
2. **Driver & Vehicle Service**:
   - `Create Driver Operational Profile` $\rightarrow$ registers vehicle and sets service area to `Colombo`.
   - `Update Driver Location` $\rightarrow$ sets simulated coordinates (6.9271, 79.8612).
   - `Set Driver Availability to AVAILABLE`.
   - `Get Eligible Available Drivers` $\rightarrow$ returns active driver.
3. **Fare & Payment Service**:
   - `Calculate Fare Estimate (Normal)` $\rightarrow$ formula: $(200 + (6 \text{km} \times 80)) \times 1.0 = 680 \text{ LKR}$.
   - `Calculate Fare Estimate (Peak Hour Surge)` $\rightarrow$ formula: $(200 + (6 \times 80)) \times 1.25 = 850 \text{ LKR}$.
   - `[Negative] Simulated Payment Failure` $\rightarrow$ simulates card decline and verifies `402 Payment Required`.
4. **Ride Management Service (E2E Integration)**:
   - `1. Request Ride` $\rightarrow$ queries Fare Service & Driver Service, auto-assigns driver, status becomes `ASSIGNED`.
   - `2. Driver Accepts Ride` $\rightarrow$ status becomes `ACCEPTED`.
   - `3. Driver Starts Ride` $\rightarrow$ status becomes `IN_PROGRESS`.
   - `4. Driver Completes Ride` $\rightarrow$ status becomes `COMPLETED`, automatically triggers Payment Service to bill 680 LKR and generates digital receipt `REC-YYYYMMDD-XXXX`.
   - `[Negative] Invalid State Transition` $\rightarrow$ starting completed ride returns `400 Bad Request`.
   - `[Negative] Cancel Completed Ride` $\rightarrow$ returns `400 Bad Request`.

---

## 7. Negative Test Scenarios Checklist (Rubric Requirement)

The platform demonstrates robust defensive error handling across all services:

1. **No Eligible Driver Available**: Requesting a ride in an unserved area or when all drivers are `OFFLINE` keeps the ride in `REQUESTED` status without failing ungracefully.
2. **Invalid State Transition**: Attempting to complete a ride directly from `REQUESTED` or `ACCEPTED` without transitioning through `IN_PROGRESS` is rejected with `400 Bad Request`.
3. **Cancellation of Completed Rides**: Completed rides cannot be cancelled, protecting transaction integrity.
4. **Duplicate Entity Protection**: Registering with an existing email or license plate returns `409 Conflict`.
5. **Unauthorized Access / Suspended Account**: Attempting to authenticate with a suspended account returns `403 Forbidden`.
6. **Simulated Payment Failure**: When card declines or wallet funds are insufficient, payment is recorded as `FAILED` and returns `402 Payment Required`.

---

## 8. Git Version Control & Branching Strategy

To comply with **LO4 & Rubric I3 (Version Control Practice)**, the team adopted the following Git workflow:

- **`master` / `main`**: Production-ready, stable integrated branch.
- **`feature/account-service`**: Primary branch for Member 1 (`IT24102280`).
- **`feature/driver-service`**: Primary branch for Member 2 (`IT24104248`).
- **`feature/ride-service`**: Primary branch for Member 3 (`IT24104016`).
- **`feature/payment-service`**: Primary branch for Member 4 (`IT24610827`).
- Pull requests and code reviews conducted prior to merging into main.
