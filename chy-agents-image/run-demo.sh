#!/bin/bash
echo "CHY-Agents Image Module Demo"
echo "==========================="

STABILITY_API_KEY=$1
OPENAI_API_KEY=$2

if [ -z "$STABILITY_API_KEY" ]; then
    echo "STABILITY_API_KEY not provided! Please provide it as the first argument."
    echo "Example: ./run-demo.sh YOUR_STABILITY_API_KEY YOUR_OPENAI_API_KEY"
    exit 1
fi

if [ -z "$OPENAI_API_KEY" ]; then
    echo "OPENAI_API_KEY not provided! Please provide it as the second argument."
    echo "Example: ./run-demo.sh YOUR_STABILITY_API_KEY YOUR_OPENAI_API_KEY"
    exit 1
fi

echo "Starting demo application with the provided API keys..."
echo

JAVA_OPTS="-Dspring.ai.stability-ai.api-key=$STABILITY_API_KEY -Dspring.ai.openai.api-key=$OPENAI_API_KEY"

cd ..
mvn clean package -DskipTests -pl chy-agents-image -am
cd chy-agents-image

java $JAVA_OPTS -jar target/chy-agents-image-*.jar

echo
echo "Demo application stopped." 