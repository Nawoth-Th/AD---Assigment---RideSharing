@REM ----------------------------------------------------------------------------
@REM RideLink Maven Wrapper Launcher
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set "TOOL_MVN=C:\tools\apache-maven-3.9.9\bin\mvn.cmd"
if exist "%TOOL_MVN%" (
    call "%TOOL_MVN%" %*
    exit /b %ERRORLEVEL%
)

where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    call mvn %*
    exit /b %ERRORLEVEL%
)

echo [ERROR] Maven not found. Please install Maven or run via C:\tools\apache-maven-3.9.9\bin\mvn.cmd.
exit /b 1
