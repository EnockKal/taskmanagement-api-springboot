# Task Management API (Spring Boot)
> 🚧 Actively evolving project — new features and improvements are continuously being added.

```md
## Project highlights/Key Features

- Built a Task Management REST API using Spring Boot, Spring Data JPA, and PostgreSQL
- Implemented CRUD operations for Projects and Tasks
- Added request/response DTOs, validation, and clean entity-to-DTO mapping
- Implemented pagination and sorting for task retrieval
- Added dynamic task filtering by status, project, and title using Spring Data JPA Specifications
- Refactored from derived query methods to `JpaSpecificationExecutor` for scalable filtering logic
- Integrated AWS CloudWatch Logs for centralized logging of S3 operations (upload, delete, presigned URL generation)
- Implemented structured logging for success and failure scenarios to improve observability and debugging
- Configured IAM permissions for least-privilege access to S3 and CloudWatch Logs
```

## Overview
A production-style RESTful API for managing projects, tasks, users, and file attachments.

This project demonstrates backend engineering best practices including:
- Layered architecture (Controller → Service → Repository)
- DTO-based design
- Validation and global exception handling
- Scalable query patterns
- Cloud integration with AWS S3 and CloudWatch

---

## Tech Stack
- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- AWS S3 (S3, CloudWatch)
- Maven
- Lombok
- Jakarta Validation
- Swagger / OpenAPI (springdoc)

---

## Features

### Project Management
- Create, update, delete, and retrieve projects
- One-to-many relationship with tasks

### Task Management
- Create, update, delete, and retrieve tasks
- Assign tasks to projects
- Task status management (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)

### User Management
- Create, update, delete, and retrieve users
- Retrieve users by username or email
- Prevent duplicate email registration
- Update user email with conflict validation
- Assign tasks to users
- Safe deletion:
   - Prevent deletion if tasks are assigned
   - OR automatically unassign tasks before deletion

### Task Attachments (AWS S3 Integration)
- Upload files and attach them to tasks
- Store file metadata in PostgreSQL and files in a private S3 bucket
- Generate pre-signed URLs for secure, temporary access
- Delete attachments from both S3 and the database
- Log S3 upload, download URL generation, and delete events to AWS CloudWatch

### API Capabilities
- DTO-based request/response design
- Global exception handling
- Input validation
- Partial updates (PATCH-style logic)
- Pagination and Sorting
- Filtering by task status

---

## Architecture
This application follows a layered architecture:
- Controller → handles HTTP requests
- Service → contains business logic
- Repository → interacts with the database
- DTO + Mapper → ensures clean data transfer
- Global Exception Handler → centralized error handling

### Relationships:
- Project ↔ Tasks (One-to-Many)
- User ↔ Tasks (One-to-Many)
- Task ↔ Attachments (One-to-Many)

---

## Running the Application
1. Configure PostgreSQL in application.properties
2. Configure AWS credentials (via environment variables or AWS CLI)
3. Configure AWS S3 bucket name and CloudWatch log group/stream
4. (Optional) Change port:
   `server.port=8081`
5. Run:
   `mvn spring-boot:run`

## Swagger / OpenAPI Documentation

This project includes Swagger/OpenAPI documentation using springdoc-openapi.
After starting the application, Swagger UI is available at:

```
http://localhost:8081/swagger-ui.html
```
The OpenAPI JSON documentation is available at:
```
http://localhost:8081/v3/api-docs
```
Swagger UI can be used to explore available endpoints, view request/response models, and manually test API operations directly from the browser.
For more complete API workflow testing, especially file uploads and repeated request scenarios, Postman is also used.
This allows developers and recruiters to quickly understand and interact with the API without needing external tools.


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

### Users
- `POST /api/users`
- `GET /api/users`
- `GET /api/users/{id}`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`
- `GET /api/users/username/{username}`
- `GET /api/users/email/{email}`

### Task Attachments
- `POST /api/tasks/{taskId}/attachments`
- `GET /api/tasks/{taskId}/attachments`
- `GET /api/tasks/{taskId}/attachments/{attachmentId}/url`
- `DELETE /api/tasks/{taskId}/attachments/{attachmentId}`

---

## Cloud Architecture

- Files are stored in a private AWS S3 bucket
- File metadata is stored in PostgreSQL
- Pre-signed URLs are generated for secure, temporary file access
- Application logs are sent to AWS CloudWatch for monitoring and debugging

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

## Upcoming Enhancements

This project is actively being extended to simulate a more production-ready system. Planned improvements include:
- Authentication and authorization (JWT-based security)
- Role-based access control (Admin/User roles)
- Unit and integration testing
- Deployment to AWS (Docker + EC2/ECS) with Dockerized application
- Environment configuration and secrets management for production readiness

## Notes

This project is actively being improved to reflect real-world backend systems, scalable API design, and cloud-native best practices.

---


