@echo off

echo Checking if port 8081 is in use...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8081') do (
    if not "%%a"=="" (
        echo Port 8081 is already in use.
        exit /b 1
    )
)

echo Running: java -jar C:\Users\Sahil\Desktop\pratik_excel\airline-0.0.1-SNAPSHOT.jar --server.port=8081
java -jar C:\Users\Sahil\Desktop\pratik_excel\airline-0.0.1-SNAPSHOT.jar --server.port=8081
