#!/bin/bash

echo "Building Spring Boot + React + Camunda Workflow Application"

# Build frontend for production
echo "Building React Frontend..."
cd frontend
npm install
npm run build
cd ..

echo "Build completed successfully!"
echo "Run 'docker-compose up -d --build' to start all services"
echo "Note: Backend services will be built automatically by Docker"
