@echo off
echo ========================================================
echo   RideLink Microservices Platform - Launch Services
echo ========================================================
echo.

echo [1/4] Starting Account Service (Port 8081)...
start "RideLink - Account Service (:8081)" cmd /k ".\mvnw.cmd spring-boot:run -pl account-service"
timeout /t 4 /nobreak >nul

echo [2/4] Starting Driver and Vehicle Service (Port 8082)...
start "RideLink - Driver Service (:8082)" cmd /k ".\mvnw.cmd spring-boot:run -pl driver-service"
timeout /t 4 /nobreak >nul

echo [3/4] Starting Fare and Payment Service (Port 8084)...
start "RideLink - Payment Service (:8084)" cmd /k ".\mvnw.cmd spring-boot:run -pl payment-service"
timeout /t 4 /nobreak >nul

echo [4/4] Starting Ride Management Service (Port 8083)...
start "RideLink - Ride Service (:8083)" cmd /k ".\mvnw.cmd spring-boot:run -pl ride-service"

echo.
echo ========================================================
echo   All 4 microservices have been launched in separate windows!
echo ========================================================
echo.
echo Swagger UI Interfaces:
echo   - Account Service:   http://localhost:8081/swagger-ui.html
echo   - Driver Service:    http://localhost:8082/swagger-ui.html
echo   - Ride Service:      http://localhost:8083/swagger-ui.html
echo   - Payment Service:   http://localhost:8084/swagger-ui.html
echo.
echo To stop all services later, run: .\stop-all.bat
echo ========================================================
