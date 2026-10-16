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
- **Quality Assurance**: Unit tests with JUnit 5 & Mockito (**53 unit tests, 100% pass rate**)
- **Comprehensive Documentation**: Academic Technical Report ([`docs/IT3130_RideLink_Technical_Report.md`](file:///docs/IT3130_RideLink_Technical_Report.md)) & Architecture & Sequence Diagrams ([`docs/architecture_and_sequence_diagrams.md`](file:///docs/architecture_and_sequence_diagrams.md))
- **Postman API Suite**: 47 automated requests covering happy paths, edge cases, and explicit negative scenarios with variable chaining

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

The collection contains **47 requests** with automated test scripts that dynamically transfer generated IDs and JWT tokens between requests:

1. **Account Service (14 requests)**:
   - `Register Passenger` $\rightarrow$ extracts `passengerId` & `authToken`.
   - `Register Driver Account` $\rightarrow$ extracts `driverUserId`.
   - `Register Admin Account` $\rightarrow$ extracts `adminUserId` & `adminAuthToken`.
   - `Login Passenger` & `Validate Auth Token`.
   - `Get User Profile` by ID.
   - `Admin - Get All Users` (requires `ADMIN` role or Bearer token).
   - `Admin - Update User Status` (`SUSPENDED` / `ACTIVE`).
   - `[Negative] Duplicate Email Registration` $\rightarrow$ verifies `409 Conflict`.
   - `[Negative] Login with Invalid Password` $\rightarrow$ verifies `401 Unauthorized`.
   - `[Negative] Unauthorised Access - Non-Admin Accessing User List` $\rightarrow$ verifies `403 Forbidden`.
   - `[Negative] Invalid Input - Registration with Invalid Email` $\rightarrow$ verifies `400 Bad Request`.
   - `[Negative] Token Validation Failed - Invalid Token` $\rightarrow$ verifies `401 Unauthorized`.
2. **Driver & Vehicle Service (11 requests)**:
   - `Create Driver Operational Profile` $\rightarrow$ registers vehicle and sets service area to `Colombo`.
   - `Update Driver Location` $\rightarrow$ sets simulated coordinates (6.9271, 79.8612).
   - `Set Driver Availability to AVAILABLE`.
   - `Get Eligible Available Drivers` $\rightarrow$ returns active driver.
   - `Driver Starts Trip` $\rightarrow$ transitions status to `ON_TRIP`.
   - `Driver Released from Trip` $\rightarrow$ restores status to `AVAILABLE` without incrementing rides.
   - `Increment Driver Rides Completed` $\rightarrow$ increments `totalRidesCompleted` and sets `AVAILABLE`.
   - `Update Driver Vehicle Details` $\rightarrow$ updates vehicle specifications.
   - `Get Driver Profile by User ID`.
   - `[Negative] Duplicate Driver License Registration` $\rightarrow$ verifies `409 Conflict`.
   - `[Negative] Driver Profile Not Found` $\rightarrow$ verifies `404 Not Found`.
3. **Fare & Payment Service (9 requests)**:
   - `Calculate Fare Estimate (Normal)` $\rightarrow$ formula: $(200 + (6 \text{km} \times 80)) \times 1.0 = 680 \text{ LKR}$.
   - `Calculate Fare Estimate (Peak Hour Surge)` $\rightarrow$ formula: $(200 + (6 \times 80)) \times 1.5 = 1020 \text{ LKR}$.
   - `Process Simulated Payment (Direct)`.
   - `Get Official Digital Receipt`.
   - `Get Payment by Ride ID`.
   - `Get All Payments List`.
   - `[Negative] Simulated Payment Failure` $\rightarrow$ simulates card decline and verifies `402 Payment Required`.
   - `[Negative] Digital Receipt Not Found` $\rightarrow$ verifies `404 Not Found`.
   - `[Negative] Invalid Fare Calculation - Negative Distance` $\rightarrow$ verifies `400 Bad Request`.
4. **Ride Management Service (13 requests)**:
   - `1. Request Ride (E2E Auto Assignment)` $\rightarrow$ queries Fare & Driver services, auto-assigns driver, status becomes `ASSIGNED`.
   - `2. Get Ride Details`.
   - `3. Driver Accepts Ride` $\rightarrow$ status becomes `ACCEPTED`.
   - `4. Driver Starts Ride` $\rightarrow$ status becomes `IN_PROGRESS` (locks driver to `ON_TRIP`).
   - `5. Complete Ride` $\rightarrow$ status becomes `COMPLETED`, automatically triggers Payment Service and releases driver.
   - `6. Cancel Ride` $\rightarrow$ releases driver to `AVAILABLE` and sets status to `CANCELLED`.
   - `Get Ride History by Passenger`.
   - `Get Ride History by Driver`.
   - `[Negative] Invalid State Transition - Start Completed Ride` $\rightarrow$ returns `400 Bad Request`.
   - `[Negative] Cannot Cancel Completed Ride` $\rightarrow$ returns `400 Bad Request`.
   - `[Negative] Missing Pickup Location` $\rightarrow$ returns `400 Bad Request`.
   - `[Negative] Ride Not Found` $\rightarrow$ returns `404 Not Found`.
   - `[Negative] Driver Mismatch - Wrong Driver Accepts Ride` $\rightarrow$ returns `400 Bad Request`.

---

## 7. Negative Test Scenarios Checklist (Rubric Requirement)

The platform demonstrates robust defensive error handling across all services:

1. **Role-Based Authorization Gate (`403 Forbidden`)**: Non-admin users attempting administrative operations (`GET /api/v1/users` or status modification) are strictly denied.
2. **Suspended Account Gate (`403 Forbidden` / `400 Bad Request`)**: Suspended users cannot log in or request new rides.
3. **No Eligible Driver Available**: Requesting a ride when no driver is available gracefully falls back to `REQUESTED` with `driverId: null` rather than failing.
4. **Invalid State Transition (`400 Bad Request`)**: Attempting to complete a ride directly without starting, or re-starting a completed ride, is rejected.
5. **Cancellation Integrity (`400 Bad Request`)**: Completed rides cannot be cancelled.
6. **Driver Mismatch (`400 Bad Request`)**: Only the driver assigned to a ride can accept it.
7. **Input Validation Failures (`400 Bad Request`)**: Missing pickup coordinates, negative distance calculations, or invalid email formats return descriptive validation errors.
8. **Duplicate Entity Protection (`409 Conflict`)**: Duplicate email registration or duplicate driver license registration is prevented.
9. **Authentication Failures (`401 Unauthorized`)**: Invalid credentials or malformed/expired JWT tokens are rejected.
10. **Simulated Payment Failures (`402 Payment Required`)**: Cards simulated with failure numbers trigger immediate payment declined responses.
11. **Resource Not Found (`404 Not Found`)**: Non-existent rides, users, drivers, or receipts return clean 404 responses.

---

## 8. Git Version Control & Branching Strategy

To comply with **LO4 & Rubric I3 (Version Control Practice)**, the team adopted the following Git workflow:

- **`master` / `main`**: Production-ready, stable integrated branch.
- **`feature/account-service`**: Primary branch for Member 1 (`IT24102280`).
- **`feature/driver-service`**: Primary branch for Member 2 (`IT24104248`).
- **`feature/ride-service`**: Primary branch for Member 3 (`IT24104016`).
- **`feature/payment-service`**: Primary branch for Member 4 (`IT24610827`).
- Pull requests and code reviews conducted prior to merging into main.
