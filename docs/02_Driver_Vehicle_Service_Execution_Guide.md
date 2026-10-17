# RideLink Microservices Platform — Driver & Vehicle Service (:8082)
## API Execution & Cross-Service ID Placement Manual — Member 2 (IT24104248)

---

### Service Metadata & Topology
- **Ownership**: Member 2 — IT24104248
- **Service Name**: `driver-service`
- **Port**: `8082` | Context Base: `http://localhost:8082`
- **Swagger UI**: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- **MongoDB Database**: `ridelink_driver_db` (collection: `drivers`)
- **Fleet States**: `AVAILABLE`, `ON_TRIP`, `OFFLINE`
- **Official PDF Manual**: [02_Driver_Vehicle_Service_Execution_Guide.pdf](file:///g:/AD%20-%20Ride%20Sharing%20Application%20with%20Microservices/docs/02_Driver_Vehicle_Service_Execution_Guide.pdf)

---

### 1. Master ID Placement & Cross-Service Flow Matrix

| Identifier | Created / Emitted By | Where It Must Be Put (Target Endpoint & Parameter Location) | Sample Realistic Value |
| :--- | :--- | :--- | :--- |
| **`driverUserId`**<br/>(User Account ID) | Account Service (:8081):<br/>`POST /api/v1/auth/register` (role: `DRIVER`) | • **Driver Svc**: Request Body field `userId` in `POST /api/v1/drivers` (Step 01)<br/>• **Driver Svc**: Path variable `{userId}` in `GET /api/v1/drivers/user/{userId}` (Step 06) | `670fd1a2b4e89c1234567891` |
| **`driverId`**<br/>(Profile ID) | Driver Service (:8082):<br/>`POST /api/v1/drivers` (returned as `"id"`) | • **Driver Svc**: Path var `{id}` in Steps 02, 03, 05, 07, 08, 09, 10<br/>• **Ride Svc**: Body field `driverId` in `POST /api/v1/rides/{id}/assign` (Step 02)<br/>• **Ride Svc**: Query param `?driverId=` in `POST /api/v1/rides/{id}/accept` (Step 03)<br/>• **Ride Svc**: Path var in `GET /api/v1/rides/driver/{driverId}` (Step 09)<br/>• **Payment Svc**: Body field `driverId` in `POST /api/v1/payments/process` (Step 02) | `670fd2e1b4e89c1234567891` |
| **`licenseNumber`** | Provided by testing agent | • Body field in `POST /api/v1/drivers` (must be unique) | `B-8492019` |
| **`serviceArea`** | Configured zone | • Body field in `POST /api/v1/drivers` (Step 01)<br/>• Query parameter in `GET /api/v1/drivers/eligible?serviceArea=Colombo` (Step 04) | `Colombo` |

---

### 2. Swagger UI Live Demo Step-by-Step Execution Sequence

Open **`http://localhost:8082/swagger-ui.html`** in your browser. All endpoints are numbered from **Step 01** to **Step 11**:

| Demo Step # | Swagger UI Endpoint Name | HTTP Method & URL | Input Data & Parameter Location | Expected Output & Value to Copy |
| :--- | :--- | :--- | :--- | :--- |
| **Prereq** | Register Driver Account | `POST :8081/api/v1/auth/register` | Body (role: `DRIVER`) | 201 Created. Produces **driverUserId**. |
| **Step 01** | `Step 01 - Create driver operational profile` | `POST /api/v1/drivers` | Body: `userId` = **driverUserId**, vehicle specs | 201 Created. **Copy 'id' (driverId)**. Status: `OFFLINE`. |
| **Step 02** | `Step 02 - Update simulated GPS location` | `PATCH /api/v1/drivers/{id}/location` | Path: **driverId**; Body: lat, lng, address | 200 OK. Persists GPS coordinates. |
| **Step 03** | `Step 03 - Update availability status` | `PATCH /api/v1/drivers/{id}/availability` | Path: **driverId**; Body: `{"availabilityStatus": "AVAILABLE"}` | 200 OK. Driver is ONLINE for dispatch matching. |
| **Step 04** | `Step 04 - Retrieve eligible available drivers` | `GET /api/v1/drivers/eligible` | Query: `serviceArea=Colombo` | 200 OK. Show driver in available query list. |
| **Step 05** | `Step 05 - Get driver profile by ID` | `GET /api/v1/drivers/{id}` | Path: **driverId** | 200 OK. Displays full profile, rating, total rides. |
| **Step 06** | `Step 06 - Get driver profile by User ID` | `GET /api/v1/drivers/user/{userId}` | Path: **driverUserId** | 200 OK. Shows cross-service linking by User ID. |
| **Step 07** | `Step 07 - Update vehicle details` | `PUT /api/v1/drivers/{id}/vehicle` | Path: **driverId**; Body: wrapped `vehicle` object | 200 OK. Updates vehicle make/model/plate. |
| **Step 08** | `Step 08 - Notify driver trip started` | `POST /api/v1/drivers/{id}/start-trip` | Path: **driverId** (No body) | 204 No Content. Status becomes `ON_TRIP`. |
| **Step 09** | `Step 09 - Release driver availability` | `POST /api/v1/drivers/{id}/release` | Path: **driverId** (No body) | 204 No Content. Status restored to `AVAILABLE`. |
| **Step 10** | `Step 10 - Notify driver ride completion` | `POST /api/v1/drivers/{id}/complete-ride` | Path: **driverId** (No body) | 204 No Content. Status `AVAILABLE`, `totalRides` +1. |
| **Step 11** | `Step 11 - List all drivers` | `GET /api/v1/drivers` | None | 200 OK. Complete fleet registry audit. |

---

### 3. Detailed Endpoint Specifications & Swagger UI Demo Guide

#### `POST /api/v1/drivers` — Step 01 - Create driver operational profile
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **POST /api/v1/drivers** (`Step 01`).
> 2. Click **Try it out**.
> 3. Replace `userId` with the **driverUserId** from Account Service Step 01b.
> 4. Click **Execute**.
> 5. Verify response code **201 Created**.
> 6. **CRITICAL: Copy returned 'id'** (`670fd2e1b4e89c1234567891`) — this is your operational **driverId**!

```json
// Request Body
{
  "userId": "670fd1a2b4e89c1234567891",
  "licenseNumber": "B-8492019",
  "experienceYears": 6,
  "serviceArea": "Colombo",
  "vehicle": {
    "make": "Toyota",
    "model": "Aqua",
    "year": 2021,
    "licensePlate": "WP-CBA-4321",
    "color": "Silver",
    "vehicleType": "HATCHBACK",
    "passengerCapacity": 4
  }
}
```

---

#### `PATCH /api/v1/drivers/{id}/location` — Step 02 - Update simulated GPS location
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **PATCH /api/v1/drivers/{id}/location** (`Step 02`).
> 2. Click **Try it out**.
> 3. In `id` path variable field, paste **driverId**.
> 4. In Request body, enter latitude (`6.9271`), longitude (`79.8612`), and address.
> 5. Click **Execute**. Verify response **200 OK**.

---

#### `PATCH /api/v1/drivers/{id}/availability` — Step 03 - Update availability status
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **PATCH /api/v1/drivers/{id}/availability** (`Step 03`).
> 2. Click **Try it out**.
> 3. In `id` path field, paste **driverId**.
> 4. In Request body, set `"availabilityStatus": "AVAILABLE"`.
> 5. Click **Execute**. Verify response **200 OK**. Driver is now discoverable for ride matching.

---

#### `GET /api/v1/drivers/eligible` — Step 04 - Retrieve eligible available drivers
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **GET /api/v1/drivers/eligible** (`Step 04`).
> 2. Click **Try it out**.
> 3. In query parameter `serviceArea`, enter `Colombo`.
> 4. Click **Execute**. Verify response **200 OK**. Point out to examiners that your driver appears in the array!

---

#### `GET /api/v1/drivers/{id}` — Step 05 - Get driver profile by ID
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **GET /api/v1/drivers/{id}** (`Step 05`).
> 2. Click **Try it out**, paste **driverId** in `id`, and click **Execute**.
> 3. Verify response **200 OK** and show full driver details.

---

#### `GET /api/v1/drivers/user/{userId}` — Step 06 - Get driver profile by User ID
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **GET /api/v1/drivers/user/{userId}** (`Step 06`).
> 2. Click **Try it out**, paste **driverUserId** in `userId`, and click **Execute**.
> 3. Verify response **200 OK**. Demonstrates cross-service relational linking by User ID.

---

#### `PUT /api/v1/drivers/{id}/vehicle` — Step 07 - Update vehicle details
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **PUT /api/v1/drivers/{id}/vehicle** (`Step 07`).
> 2. Click **Try it out**, paste **driverId** in `id`.
> 3. In Request body, wrap vehicle in `{"vehicle": { ... }}` and click **Execute**.
> 4. Verify response **200 OK**.

---

#### `POST /api/v1/drivers/{id}/start-trip` — Step 08 - Notify driver trip started
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **POST /api/v1/drivers/{id}/start-trip** (`Step 08`).
> 2. Click **Try it out**, paste **driverId**, and click **Execute**.
> 3. Verify HTTP **204 No Content**. Show that driver availability transitioned to `ON_TRIP`.

---

#### `POST /api/v1/drivers/{id}/release` — Step 09 - Release driver availability
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **POST /api/v1/drivers/{id}/release** (`Step 09`).
> 2. Click **Try it out**, paste **driverId**, and click **Execute**.
> 3. Verify HTTP **204 No Content**. Driver is restored to `AVAILABLE` without incrementing rides.

---

#### `POST /api/v1/drivers/{id}/complete-ride` — Step 10 - Notify driver ride completion
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **POST /api/v1/drivers/{id}/complete-ride** (`Step 10`).
> 2. Click **Try it out**, paste **driverId**, and click **Execute**.
> 3. Verify HTTP **204 No Content**. Check Step 05 to show examiners `totalRides` incremented to 1!

---

#### `GET /api/v1/drivers` — Step 11 - List all drivers
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **GET /api/v1/drivers** (`Step 11`).
> 2. Click **Try it out** and click **Execute**.
> 3. Verify response **200 OK**. Displays entire platform fleet registry.
