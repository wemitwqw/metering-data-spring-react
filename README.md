# Metering Data Assessment
A full-stack web application built with Spring Boot backend and React frontend. Does not provide any meaningful functionalities.

## Architecture
- Backend (Spring Boot)

  Framework: Spring Boot 3.4.5 with Java 21

  Database: PostgreSQL 16 with Liquibase migrations

  Caching: Redis 7

  Security: JWT-based authentication

  Documentation: OpenAPI/Swagger UI

  Build Tool: Gradle 8.13

- Frontend (React)

  Framework: React with TypeScript/JavaScript

  Build Tool: Node.js 22 with npm

## Important

For simplicity of the development, electricity prices are being fetched from the Elering's API for every month of a certain year (as stated in the assignment guidelines).

Customer can login and logout.

* user1@test.ee:password
* user2@test.ee:password

## Prerequisites

Docker and Docker Compose

## Quick Start

* Clone the repository
* Run:
  ```docker compose up -d```

### Access the application:

Frontend: http://localhost:3001

Backend API: http://localhost:3000

API Documentation: http://localhost:3000/swagger-ui.html

## JWT Configuration
The application uses JWT tokens for authentication with configurable expiration times:

Access token: 60 seconds (set for development so token refreshing could be tested in frontend, change in application.properties file)

Refresh token: 7 days

## Features
Authentication & Authorization: JWT-based security

Database Management: Liquibase migrations with PostgreSQL

Caching: Redis integration for performance optimization

API Documentation: Auto-generated Swagger/OpenAPI docs

## Testing

### Backend tests
```cd backend```

```./gradlew test```

### Frontend tests (install node_modules with ```npm install```)
```cd frontend```

```npm test```


## Automatic schema management
Migration scripts in src/main/resources/db/changelog/

Drop-first enabled for development (This will drop all data on re-run)

## Architecture Diagram
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│                 │    │                 │    │                 │
│   React         │◄──►│  Spring Boot    │◄──►│   PostgreSQL    │
│   Frontend      │    │   Backend       │    │   Database      │
│   (Port 3001)   │    │   (Port 3000)   │    │   (Port 5432)   │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │                 │
                       │     Redis       │
                       │     Cache       │
                       │   (Port 6379)   │
                       │                 │
                       └─────────────────┘
```
