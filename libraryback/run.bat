@echo off
echo Starting Library Management System...
echo.

REM Set Maven home
set MAVEN_HOME=%TEMP%\maven\apache-maven-3.8.6
set PATH=%MAVEN_HOME%\bin;%PATH%

REM Check if Maven exists
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Maven not found. Downloading...
    powershell -Command "Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.zip' -OutFile '%TEMP%\apache-maven-3.8.6-bin.zip'"
    powershell -Command "Expand-Archive -Path '%TEMP%\apache-maven-3.8.6-bin.zip' -DestinationPath '%TEMP%\maven' -Force"
)

REM Run Spring Boot
cd /d "%~dp0"
call "%MAVEN_HOME%\bin\mvn.cmd" spring-boot:run

pause
