@echo off
echo ========================================================
echo   Stopping RideLink Services (Ports 8081, 8082, 8083, 8084)
echo ========================================================

for %%p in (8081 8082 8083 8084) do (
    for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":%%p" ^| findstr "LISTENING"') do (
        echo Stopping process %%a listening on port %%p...
        taskkill /F /PID %%a >nul 2>&1
    )
)

echo.
echo All RideLink microservice processes stopped.
