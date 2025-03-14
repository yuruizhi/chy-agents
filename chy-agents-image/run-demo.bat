@echo off
echo CHY-Agents Image Module Demo
echo ===========================

set STABILITY_API_KEY=%1
set OPENAI_API_KEY=%2

if "%STABILITY_API_KEY%"=="" (
    echo STABILITY_API_KEY not provided! Please provide it as the first argument.
    echo Example: run-demo.bat YOUR_STABILITY_API_KEY YOUR_OPENAI_API_KEY
    exit /b 1
)

if "%OPENAI_API_KEY%"=="" (
    echo OPENAI_API_KEY not provided! Please provide it as the second argument.
    echo Example: run-demo.bat YOUR_STABILITY_API_KEY YOUR_OPENAI_API_KEY
    exit /b 1
)

echo Starting demo application with the provided API keys...
echo.

set JAVA_OPTS=-Dspring.ai.stability-ai.api-key=%STABILITY_API_KEY% -Dspring.ai.openai.api-key=%OPENAI_API_KEY%

cd ..
call mvn clean package -DskipTests -pl chy-agents-image -am
cd chy-agents-image

java %JAVA_OPTS% -jar target/chy-agents-image-*.jar

echo.
echo Demo application stopped. 