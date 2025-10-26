#!/bin/bash

echo "Starting Spring Boot + React + Camunda Workflow Application"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker first."
    exit 1
fi

# Build and start all services
echo "Building and starting all services..."
docker-compose up -d --build

echo "Waiting for services to start..."
sleep 30

echo "Application is starting up!"
echo ""
echo "Services will be available at:"
echo "- Frontend: http://localhost:3000"
echo "- Auth Service: http://localhost:8081"
echo "- Workflow Service: http://localhost:8082"
echo "- Task Service: http://localhost:8083"
echo "- Camunda Cockpit: http://localhost:8080/camunda"
echo ""
echo "Default credentials:"
echo "- Application: admin / admin123"
echo "- Camunda: demo / demo"
echo ""
echo "To view logs: docker-compose logs -f"
echo "To stop: docker-compose down"
