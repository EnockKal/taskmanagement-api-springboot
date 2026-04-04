# Task Management API (Spring Boot)
> 🚧 Actively evolving project — new features and improvements are continuously being added.

```md
Project highlights

- Built a Task Management REST API using Spring Boot, Spring Data JPA, and PostgreSQL
- Implemented CRUD operations for Projects and Tasks
- Added request/response DTOs, validation, and clean entity-to-DTO mapping
- Implemented pagination and sorting for task retrieval
- Added dynamic task filtering by status, project, and title using Spring Data JPA Specifications
- Refactored from derived query methods to `JpaSpecificationExecutor` for scalable filtering logic
```

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

## Task Filtering, Search, Pagination, and Sorting

The Task Management API supports dynamic task filtering using Spring Data JPA Specifications.

### Supported filters
Users can retrieve tasks with any combination of the following optional query parameters:

- `taskStatus` → exact match on task status
- `projectId` → exact match on the related project
- `title` → partial, case-insensitive search on task title

### Example requests

```http
GET /api/tasks
GET /api/tasks?taskStatus=COMPLETED
GET /api/tasks?projectId=1
GET /api/tasks?title=result
GET /api/tasks?taskStatus=COMPLETED&title=result
GET /api/tasks?projectId=1&title=result
GET /api/tasks?taskStatus=COMPLETED&projectId=1
GET /api/tasks?taskStatus=COMPLETED&projectId=1&title=result
```
### Implementation details
This feature was first implemented using derived query methods, then refactored to use JpaSpecificationExecutor and a dedicated TaskSpecifications class for cleaner and more scalable dynamic filtering.

This refactor improved the codebase by:

- removing repository method explosion
- supporting flexible optional filter combinations
- keeping the repository layer clean and maintainable

### Pagination and sorting

Task retrieval also supports pagination and sorting through request parameters such as:
- page
- size
- sortBy
- direction

### Example
```http
GET /api/tasks?page=0&size=5&sortBy=id&direction=asc&taskStatus=IN_PROGRESS&title=report
```

## Roadmap / Upcoming Enhancements

This project is actively being extended to simulate a more production-ready system. Planned improvements include:

- User entity and task assignment (User → Tasks relationship)
- Filtering tasks by assigned user
- Authentication and authorization (JWT-based security)
- Role-based access control (Admin/User roles)
- Improved validation and error handling
- API documentation with Swagger/OpenAPI enhancements
- Unit and integration testing
- Cloud integration with AWS (S3 for file storage, RDS for PostgreSQL hosting)
- Deployment to AWS (EC2/ECS) with Dockerized application
- Environment configuration and secrets management for production readiness

The goal is to continuously evolve this project into a more complete, real-world backend system.

---


