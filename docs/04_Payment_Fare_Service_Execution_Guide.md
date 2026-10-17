# RideLink Microservices Platform — Payment & Fare Estimation Service (:8084)
## API Execution & Cross-Service ID Placement Manual — Member 4 (IT24610827)

---

### Service Metadata & Topology
- **Ownership**: Member 4 — IT24610827
- **Service Name**: `payment-service`
- **Port**: `8084` | Context Base: `http://localhost:8084`
- **Swagger UI**: [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html)
- **MongoDB Database**: `ridelink_payment_db` (collection: `payments`)
- **Operations**: Algorithmic fare calculation, simulated payment settlement, digital receipt generation
- **Official PDF Manual**: [04_Payment_Fare_Service_Execution_Guide.pdf](file:///g:/AD%20-%20Ride%20Sharing%20Application%20with%20Microservices/docs/04_Payment_Fare_Service_Execution_Guide.pdf)

---

### 1. Master ID Placement & Cross-Service Flow Matrix

| Identifier | Created / Emitted By | Where It Must Be Put (Target Endpoint & Parameter Location) | Sample Realistic Value |
| :--- | :--- | :--- | :--- |
| **`paymentId`**<br/>(Transaction Record) | Payment Service (:8084):<br/>`POST /api/v1/payments/process` (field `"id"`) | • **Payment Svc**: Path variable `{id}` in `GET /api/v1/payments/{id}` (Step 04)<br/>• **Ride Svc**: Automatically saved in ride document upon completion | `670ff4a1b4e89c1234567899` |
| **`receiptNumber`**<br/>(Digital Invoice ID) | Payment Service (:8084):<br/>`POST /api/v1/payments/process` | • **Payment Svc**: Path variable `{receiptNumber}` in `GET /api/v1/payments/receipt/{receiptNumber}` (Step 03) | `REC-20261017-8492` |
| **`rideId`**<br/>(Trip Reference) | Ride Service (:8083):<br/>`POST /api/v1/rides/request` | • **Payment Svc**: Request Body field `rideId` in `POST /api/v1/payments/process` (Step 02)<br/>• **Payment Svc**: Path variable `{rideId}` in `GET /api/v1/payments/ride/{rideId}` (Step 05) | `670fd3f0b4e89c1234567892` |
| **`passengerId`** | Account Service (:8081):<br/>`POST /api/v1/auth/register` | • **Payment Svc**: Request Body field `passengerId` in `POST /api/v1/payments/process` (Step 02) | `670fd1a2b4e89c1234567890` |
| **`driverId`** | Driver Service (:8082):<br/>`POST /api/v1/drivers` | • **Payment Svc**: Request Body field `driverId` in `POST /api/v1/payments/process` (Step 02) | `670fd2e1b4e89c1234567891` |

---

### 2. Swagger UI Live Demo Step-by-Step Execution Sequence

Open **`http://localhost:8084/swagger-ui.html`** in your browser. All endpoints are numbered from **Step 01** to **Step 06** across Fare and Payment sections:

| Demo Step # | Swagger UI Endpoint Name | HTTP Method & URL | Input Data & Parameter Location | Expected Output & Value to Copy |
| :--- | :--- | :--- | :--- | :--- |
| **Step 01** | `Step 01 - Calculate ride fare estimate` | `POST /api/v1/fares/estimate` | Body: distanceKm, baseFare, perKmRate, surge | 200 OK. Returns itemized fare breakdown (872.50 LKR). |
| **Step 02** | `Step 02 - Process simulated payment` | `POST /api/v1/payments/process` | Body: `rideId`, `passengerId`, `driverId`, amount | 201 Created. **Copy 'paymentId' and 'receiptNumber'**. |
| **Step 03** | `Step 03 - Get official digital receipt` | `GET /api/v1/payments/receipt/{receiptNumber}` | Path: **receiptNumber** from Step 02 | 200 OK. Displays formatted customer invoice & timestamps. |
| **Step 04** | `Step 04 - Get payment by ID` | `GET /api/v1/payments/{id}` | Path: **paymentId** from Step 02 | 200 OK. Confirms ledger record and status `COMPLETED`. |
| **Step 05** | `Step 05 - Get payment for a ride` | `GET /api/v1/payments/ride/{rideId}` | Path: **rideId** from Ride Service | 200 OK. Enables Ride Service to audit billing by trip ID. |
| **Step 06** | `Step 06 - List all payment records` | `GET /api/v1/payments` | None | 200 OK. Platform-wide financial ledger audit. |

---

### 3. Detailed Endpoint Specifications & Swagger UI Demo Guide

#### `POST /api/v1/fares/estimate` — Step 01 - Calculate ride fare estimate
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **POST /api/v1/fares/estimate** (`Step 01`).
> 2. Click **Try it out**.
> 3. Adjust or keep body (e.g. `distanceKm: 8.5`, `baseFare: 150.0`, `perKmRate: 85.0`, `surgeMultiplier: 1.0`).
> 4. Click **Execute**.
> 5. Verify response code **200 OK**. Highlight itemized breakdown (Total: 872.50 LKR).

```json
// Request Body
{
  "distanceKm": 8.5,
  "baseFare": 150.0,
  "perKmRate": 85.0,
  "surgeMultiplier": 1.0
}
```

```json
// Response Body (HTTP 200 OK)
{
  "estimatedFare": 872.50,
  "distanceKm": 8.5,
  "baseFare": 150.00,
  "distanceFare": 722.50,
  "surgeMultiplier": 1.0,
  "surgeAmount": 0.00,
  "currency": "LKR"
}
```

---

#### `POST /api/v1/payments/process` — Step 02 - Process simulated payment
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **POST /api/v1/payments/process** (`Step 02`).
> 2. Click **Try it out**.
> 3. In Request body, paste **rideId**, **passengerId**, and **driverId**.
> 4. Click **Execute**.
> 5. Verify response code **201 Created**.
> 6. **CRITICAL: Copy 'id' (paymentId) and 'receiptNumber'** (e.g. `REC-20261017-8492`).

```json
// Request Body
{
  "rideId": "670fd3f0b4e89c1234567892",
  "passengerId": "670fd1a2b4e89c1234567890",
  "driverId": "670fd2e1b4e89c1234567891",
  "amount": 872.50,
  "paymentMethod": "CREDIT_CARD"
}
```

---

#### `GET /api/v1/payments/receipt/{receiptNumber}` — Step 03 - Get official digital receipt
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **GET /api/v1/payments/receipt/{receiptNumber}** (`Step 03`).
> 2. Click **Try it out**.
> 3. In `receiptNumber` path variable field, paste **receiptNumber** from Step 02.
> 4. Click **Execute**. Verify response **200 OK** and show official invoice breakdown.

---

#### `GET /api/v1/payments/{id}` — Step 04 - Get payment by ID
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **GET /api/v1/payments/{id}** (`Step 04`).
> 2. Click **Try it out**, paste **paymentId** in `id`, and click **Execute**.
> 3. Verify response **200 OK** and status `COMPLETED`.

---

#### `GET /api/v1/payments/ride/{rideId}` — Step 05 - Get payment for a ride
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **GET /api/v1/payments/ride/{rideId}** (`Step 05`).
> 2. Click **Try it out**, paste **rideId** in `rideId`, and click **Execute**.
> 3. Verify response **200 OK**. Demonstrates interservice payment lookup by trip reference.

---

#### `GET /api/v1/payments` — Step 06 - List all payment records
> **SWAGGER UI LIVE DEMO EXECUTION**:
> 1. Expand **GET /api/v1/payments** (`Step 06`).
> 2. Click **Try it out** and click **Execute**.
> 3. Verify response **200 OK**. Shows all financial ledger records across the system.
