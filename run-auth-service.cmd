@echo off
cd /d E:\Projects\BookNPlay
echo Building auth-service...
call mvnw.cmd clean package -DskipTests -pl auth-service
if errorlevel 1 (
    echo Build failed
    exit /b 1
)
echo.
echo Starting auth-service...
cd auth-service
java -jar target/auth-service-0.0.1-SNAPSHOT.jar
