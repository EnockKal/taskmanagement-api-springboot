# Task Management API (Spring Boot)

## Overview
A production-style RESTful API for managing projects and tasks, built with Spring Boot and PostgreSQL. This application supports full CRUD operations, validation, exception handling, pagination, sorting, and filtering.

This project demonstrates backend engineering best practices, including DTO-based architecture, layered design, and scalable API patterns.

---

## Tech Stack
- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- Lombok
- Jakarta Validation

---

## Features

### Project Management
- Create, update, delete, and retrieve projects
- One-to-many relationship with tasks

### Task Management
- Create, update, delete, and retrieve tasks
- Assign tasks to projects
- Task status management (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)

### API Capabilities
- DTO-based request/response design
- Global exception handling
- Input validation
- Partial updates (PATCH-style logic)
- Pagination
- Sorting
- Filtering by task status

---

## Architecture
The application follows a layered architecture:
- Controller → handles HTTP requests
- Service → contains business logic
- Repository → interacts with database
- DTO → controls API input/output
- Mapper → converts between Entity and DTO

---

## Key Concepts Implemented
- One-to-Many relationship (Project → Tasks)
- DTO pattern for clean API design
- Global exception handling
- Partial updates (update only provided fields)
- Pagination with PageRequest
- Sorting with Sort
- Filtering using query parameters

---

## Running the Application
1. Configure PostgreSQL in application.properties
2. (Optional) Change port:
   server.port=8081
3. Run:
   mvn spring-boot:run


## API Endpoints

### Projects
- `POST /api/projects`
- `GET /api/projects`
- `GET /api/projects/{id}`
- `PUT /api/projects/{id}`
- `DELETE /api/projects/{id}`

### Tasks
- `POST /api/tasks`
- `GET /api/tasks`
- `GET /api/tasks/{id}`
- `PUT /api/tasks/{id}`
- `DELETE /api/tasks/{id}`

---

## Pagination, Sorting, and Filtering

### Pagination
```http
GET /api/tasks?page=0&size=5
```

### Sorting
```http
GET /api/tasks?page=0&size=5&sortBy=dueDate&direction=asc
```

### Filtering by Status
```http
GET /api/tasks?taskStatus=IN_PROGRESS&page=0&size=5
```

### Combined
```http
GET /api/tasks?taskStatus=PENDING&page=0&size=5&sortBy=title&direction=desc
```

---


