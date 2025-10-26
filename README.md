# Spring Boot + React + Camunda Workflow Application

A comprehensive application demonstrating microservices architecture with Spring Boot, React frontend, and Camunda workflow engine integration.

## Architecture

- **Frontend**: React application with modern Material-UI interface
- **Backend Microservices**:
  - Auth Service: User authentication and JWT management
  - Workflow Service: Camunda workflow integration
  - Task Service: Task management and user task operations
- **Workflow Engine**: Camunda BPM Platform
- **Database**: PostgreSQL for user data and workflow persistence
- **Containerization**: Docker and Docker Compose for deployment

## Features

- User authentication with JWT tokens
- Workflow initiation and management
- Task assignment and completion
- Real-time task updates
- Modern React UI with Material-UI
- Docker deployment ready
- Microservices architecture
- Camunda workflow integration

## Quick Start

### Option 1: Full Docker Deployment (Recommended)

1. Ensure Docker and Docker Compose are installed
2. Run the start script:
   ```bash
   ./start.sh
   ```
   Or manually:
   ```bash
   docker-compose up -d --build
   ```

3. Wait for services to start (about 30 seconds)
4. Access the application at `http://localhost:3000`
5. Camunda Cockpit available at `http://localhost:8080/camunda`

### Option 2: Development Mode

1. Start only database and Camunda:
   ```bash
   docker-compose -f docker-compose.dev.yml up -d
   ```

2. Run backend services locally:
   ```bash
   # Terminal 1 - Auth Service
   cd backend/auth-service && mvn spring-boot:run
   
   # Terminal 2 - Workflow Service
   cd backend/workflow-service && mvn spring-boot:run
   
   # Terminal 3 - Task Service
   cd backend/task-service && mvn spring-boot:run
   ```

3. Run frontend locally:
   ```bash
   cd frontend && npm start
   ```

## Services

- Frontend: `http://localhost:3000`
- Auth Service: `http://localhost:8081`
- Workflow Service: `http://localhost:8082`
- Task Service: `http://localhost:8083`
- Camunda: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

## Default Credentials

- Application: `admin/admin123`
- Camunda: `demo/demo`

## Application Flow

1. **Login**: Use default credentials to access the system
2. **Dashboard**: View overview of tasks and processes
3. **Launch Workflow**: Start new approval processes
4. **Task Management**: Complete assigned tasks with approve/reject decisions
5. **Camunda Cockpit**: Monitor processes and tasks in Camunda

## API Endpoints

### Auth Service (Port 8081)
- `POST /api/auth/signin` - User login
- `POST /api/auth/init` - Initialize default user
- `GET /api/auth/validate` - Validate JWT token

### Workflow Service (Port 8082)
- `POST /api/workflow/start` - Start new workflow
- `GET /api/workflow/processes` - Get available processes
- `POST /api/workflow/init` - Initialize processes

### Task Service (Port 8083)
- `GET /api/tasks/user/{userId}` - Get user tasks
- `GET /api/tasks/all` - Get all tasks
- `GET /api/tasks/{taskId}` - Get specific task
- `POST /api/tasks/complete` - Complete task
- `POST /api/tasks/{taskId}/claim` - Claim task

## Development

### Prerequisites
- Java 17+
- Maven 3.6+
- Node.js 16+
- Docker & Docker Compose

### Building
```bash
./build.sh
```

### Stopping Services
```bash
docker-compose down
```

### Viewing Logs
```bash
docker-compose logs -f [service-name]
```

## Project Structure

```
├── backend/
│   ├── auth-service/          # Authentication microservice
│   ├── workflow-service/      # Workflow management service
│   └── task-service/          # Task management service
├── frontend/                  # React application
├── docker/                    # Docker configurations
├── docker-compose.yml         # Production deployment
├── docker-compose.dev.yml     # Development deployment
├── build.sh                   # Build script
├── start.sh                   # Start script
└── README.md                  # This file
```

## Troubleshooting

1. **Port conflicts**: Ensure ports 3000, 8080-8083, and 5432 are available
2. **Database connection**: Wait for PostgreSQL to fully start before services
3. **Camunda not accessible**: Check if Camunda container is running and healthy
4. **Build failures**: Ensure all prerequisites are installed

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request
