@echo off
REM ============================================================
REM  Contact Manager Pro – Build & Run Script (Windows)
REM  Requirements: JDK 17+, mysql-connector-j-8.x.x.jar
REM ============================================================

SET DRIVER_JAR=lib\mysql-connector-j-8.3.0.jar
SET SRC_DIR=src
SET OUT_DIR=out
SET MAIN_CLASS=contactmanager.App

IF NOT EXIST "%DRIVER_JAR%" (
    echo [ERROR] MySQL JDBC driver not found at %DRIVER_JAR%
    echo   Download from: https://dev.mysql.com/downloads/connector/j/
    echo   Place the JAR in the lib\ folder.
    pause
    exit /b 1
)

IF NOT EXIST "%OUT_DIR%" mkdir "%OUT_DIR%"

echo [1/3] Compiling sources...
javac -cp "%DRIVER_JAR%" -d "%OUT_DIR%" "%SRC_DIR%\contactmanager\*.java"
IF ERRORLEVEL 1 (
    echo [ERROR] Compilation failed.
    pause
    exit /b 1
)

echo [2/3] Compilation successful.
echo [3/3] Launching Contact Manager Pro...
java -cp "%OUT_DIR%;%DRIVER_JAR%" %MAIN_CLASS%
pause
