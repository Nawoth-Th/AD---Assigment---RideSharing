# RideLink Microservices Platform — Ride Management & Dispatch Service (:8083)
## API Execution & Cross-Service ID Placement Manual — Member 3 (IT24104016)

---

### Service Metadata & Topology
- **Ownership**: Member 3 — IT24104016
- **Service Name**: `ride-service`
- **Port**: `8083` | Context Base: `http://localhost:8083`
- **Swagger UI**: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)
- **MongoDB Database**: `ridelink_ride_db` (collection: `rides`)
- **Lifecycle FSM**: `REQUESTED` &rarr; `ASSIGNED` &rarr; `ACCEPTED` &rarr; `IN_PROGRESS` &rarr; `COMPLETED` / `CANCELLED`
- **Official PDF Manual**: [03_Ride_Management_Service_Execution_Guide.pdf](file:///g:/AD%20-%20Ride%20Sharing%20Application%20with%20Microservices/docs/03_Ride_Management_Service_Execution_Guide.pdf)

---

### 1. Master ID Placement & Cross-Service Flow Matrix

| Identifier | Created / Emitted By | Where It Must Be Put (Target Endpoint & Parameter Location) | Sample Realistic Value |
| :--- | :--- | :--- | :--- |
| **`passengerId`**<br/>(User ID) | Account Service (:8081):<br/>`POST /api/v1/auth/register` (role: `PASSENGER`) | • **Ride Svc**: Request Body field `passengerId` in `POST /api/v1/rides/request` (Step 01)<br/>• **Ride Svc**: Path variable `{passengerId}` in `GET /api/v1/rides/passenger/{passengerId}` (Step 08) | `670fd1a2b4e89c1234567890` |
| **`driverId`**<br/>(Operational Profile) | Driver Service (:8082):<br/>`POST /api/v1/drivers` | • **Ride Svc**: Body field `driverId` in `POST /api/v1/rides/{id}/assign` (Step 02)<br/>• **Ride Svc**: **Query Param** `?driverId=` in `POST /api/v1/rides/{id}/accept` (Step 03)<br/>• **Ride Svc**: Path variable `{driverId}` in `GET /api/v1/rides/driver/{driverId}` (Step 09) | `670fd2e1b4e89c1234567891` |
| **`rideId`**<br/>(Trip Entity ID) | Ride Service (:8083):<br/>`POST /api/v1/rides/request` (returned as `"id"`) | • **Ride Svc**: Path var `{id}` in Steps 02, 03, 04, 05, 06, 07<br/>• **Payment Svc**: Path var `{rideId}` in `GET /api/v1/payments/ride/{rideId}` (Step 05)<br/>• **Payment Svc**: Body field `rideId` in `POST /api/v1/payments/process` (Step 02) | `670fd3f0b4e89c1234567892` |
| **`paymentId`** | Payment Service (:8084):<br/>Interservice completion hook | • Automatically stored in Ride document upon completion<br/>• **Payment Svc**: Path variable in `GET /api/v1/payments/{id}` (Step 04) | `670ff4a1b4e89c1234567899` |

---

### 2. Swagger UI Live Demo Step-by-Step Execution Sequence

Open **`http://localhost:8083/swagger-ui.html`** in your browser. All endpoints are numbered from **Step 01** to **Step 10**:

| Demo Step # | Swagger UI Endpoint Name | HTTP Method & URL | Input Data & Parameter Location | Expected Output & Value to Copy |
| :--- | :--- | :--- | :--- | :--- |
| **Step 01** | `Step 01 - Request a new ride` | `POST /api/v1/rides/request` | Body: `passengerId`, locations, serviceArea | 201 Created. **Copy 'id' (rideId)**. Fare calculated & driver auto-assigned. |
| **Step 02** | `Step 02 - Assign a driver to a requested ride` | `POST /api/v1/rides/{id}/assign` | Path: **rideId**; Body: `{"driverId": "<driverId>"}` | 200 OK. State transitions to `ASSIGNED`. |
| **Step 03** | `Step 03 - Driver accepts assigned ride` | `POST /api/v1/rides/{id}/accept` | Path: **rideId**; **Query Param:** `?driverId=<driverId>` | 200 OK. State transitions to `ACCEPTED`. |
| **Step 04** | `Step 04 - Driver starts ride trip` | `POST /api/v1/rides/{id}/start` | Path: **rideId** (No body) | 200 OK. State transitions to `IN_PROGRESS`. Driver is `ON_TRIP`. |
| **Step 05** | `Step 05 - Driver completes ride` | `POST /api/v1/rides/{id}/complete` | Path: **rideId** (No body) | 200 OK. State is `COMPLETED`. **paymentId generated!** |
| **Step 06** | `Step 06 - Cancel ride` | `POST /api/v1/rides/{id}/cancel` | Path: **rideId**; Body: `{"reason": "..."}` | 200 OK. State is `CANCELLED`. Driver released to `AVAILABLE`. |
| **Step 07** | `Step 07 - Get ride by ID` | `GET /api/v1/rides/{id}` | Path: **rideId** | 200 OK. Full trip record, timestamps, fare, payment reference. |
| **Step 08** | `Step 08 - Get rides by passenger ID` | `GET /api/v1/rides/passenger/{passengerId}` | Path: **passengerId** | 200 OK. List of all rides booked by passenger. |
| **Step 09** | `Step 09 - Get rides by driver ID` | `GET /api/v1/rides/driver/{driverId}` | Path: **driverId** | 200 OK. List of all trips fulfilled by driver. |
| **Step 10** | `Step 10 - List all rides` | `GET /api/v1/rides` | None | 200 OK. Administrative platform trip audit. |

---

### 3. Detailed Endpoint Specifications & Swagger UI Demo Guide

#### `POST /api/v1/rides/request` — Step 01 - Request a new ride
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **POST /api/v1/rides/request** (`Step 01`).
> 2. Click **Try it out**.
> 3. In Request body, set `passengerId` to the **passengerId** from Account Service Step 01.
> 4. Click **Execute**.
> 5. Verify response code **201 Created**.
> 6. **CRITICAL: Copy returned 'id'** (`670fd3f0b4e89c1234567892`) — this is your **rideId**!
> 7. Point out that Ride Service called Payment Service (:8084) interservice to calculate fare (872.50 LKR), and if a driver was available in Colombo, auto-assigned them!

```json
// Request Body
{
  "passengerId": "670fd1a2b4e89c1234567890",
  "pickupLocation": {
    "latitude": 6.9271,
    "longitude": 79.8612,
    "address": "Colombo Fort Railway Station, Colombo"
  },
  "dropoffLocation": {
    "latitude": 6.9147,
    "longitude": 79.9733,
    "address": "SLIIT Malabe Campus, New Kandy Rd, Malabe"
  },
  "serviceArea": "Colombo"
}
```

---

#### `POST /api/v1/rides/{id}/assign` — Step 02 - Assign a driver to a requested ride
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **POST /api/v1/rides/{id}/assign** (`Step 02`).
> 2. Click **Try it out**.
> 3. In `id` path variable field, paste **rideId**.
> 4. In Request body, enter `{"driverId": "<driverId>"}`.
> 5. Click **Execute**. Verify response **200 OK** and status `ASSIGNED`.

---

#### `POST /api/v1/rides/{id}/accept` — Step 03 - Driver accepts assigned ride
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **POST /api/v1/rides/{id}/accept** (`Step 03`).
> 2. Click **Try it out**.
> 3. In `id` path variable field, paste **rideId**.
> 4. **CRITICAL**: Put **driverId** into the `driverId` **Query Parameter field** (do NOT put it in the body!).
> 5. Click **Execute**. Verify response **200 OK** and status `ACCEPTED`.

---

#### `POST /api/v1/rides/{id}/start` — Step 04 - Driver starts ride trip
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **POST /api/v1/rides/{id}/start** (`Step 04`).
> 2. Click **Try it out**, paste **rideId** in `id`, and click **Execute**.
> 3. Verify response **200 OK** and status `IN_PROGRESS`. Explain background call to Driver Service (:8082) locking vehicle in `ON_TRIP`.

---

#### `POST /api/v1/rides/{id}/complete` — Step 05 - Driver completes ride
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **POST /api/v1/rides/{id}/complete** (`Step 05`).
> 2. Click **Try it out**, paste **rideId** in `id`, and click **Execute**.
> 3. Verify response **200 OK** and status `COMPLETED`.
> 4. **CRITICAL HIGHLIGHT**: Point out that `paymentId` is now populated (e.g. `670ff4a1b4e89c1234567899`)! Ride Service called Payment Service (:8084) interservice to charge fare and issue receipt!

---

#### `POST /api/v1/rides/{id}/cancel` — Step 06 - Cancel ride
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **POST /api/v1/rides/{id}/cancel** (`Step 06`).
> 2. Click **Try it out**, paste **rideId** in `id`.
> 3. In Request body, provide `{"reason": "Passenger cancelled due to delay"}` and click **Execute**.
> 4. Verify response **200 OK** and status `CANCELLED`. Show that Driver Service released the driver back to `AVAILABLE`.

---

#### `GET /api/v1/rides/{id}` — Step 07 - Get ride by ID
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **GET /api/v1/rides/{id}** (`Step 07`).
> 2. Click **Try it out**, paste **rideId** in `id`, and click **Execute**.
> 3. Verify response **200 OK**. Shows complete trip metadata and payment reference.

---

#### `GET /api/v1/rides/passenger/{passengerId}` — Step 08 - Get rides by passenger ID
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **GET /api/v1/rides/passenger/{passengerId}** (`Step 08`).
> 2. Click **Try it out**, paste **passengerId**, and click **Execute**.
> 3. Verify response **200 OK**. Shows passenger trip history.

---

#### `GET /api/v1/rides/driver/{driverId}` — Step 09 - Get rides by driver ID
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **GET /api/v1/rides/driver/{driverId}** (`Step 09`).
> 2. Click **Try it out**, paste **driverId**, and click **Execute**.
> 3. Verify response **200 OK**. Shows driver trips and earnings history.

---

#### `GET /api/v1/rides` — Step 10 - List all rides
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **GET /api/v1/rides** (`Step 10`).
> 2. Click **Try it out** and click **Execute**.
> 3. Verify response **200 OK**. Shows all rides recorded across the platform.
