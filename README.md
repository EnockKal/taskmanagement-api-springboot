# Task Management API | Spring Boot & AWS

> 🚧 **Actively evolving project.** New features, tests, and cloud integrations are being added as development continues.

A RESTful backend for managing projects, tasks, users, and task attachments. Built with Spring Boot and PostgreSQL, with AWS services for private file storage, asynchronous events, serverless file validation, and operational logging.

## Project Highlights

- Built REST APIs for projects, tasks, users, and attachments with Spring Boot and Spring Data JPA.
- Used DTOs, Jakarta Validation, centralized exception handling, and clean entity-to-DTO mapping.
- Implemented task filtering with JPA Specifications, pagination, and sorting.
- Integrated private Amazon S3 storage, temporary presigned download URLs, and attachment deletion.
- Added an S3-triggered AWS Lambda function for file metadata inspection and validation.
- Published attachment-upload events to Amazon SQS and implemented a Spring Boot SQS consumer.
- Implemented message retries, visibility timeout handling, and a dead-letter queue (DLQ).
- Added idempotent event processing and transactional attachment status updates in PostgreSQL.
- Added structured application logging and CloudWatch monitoring integrations.
- Wrote unit tests with JUnit 5 and Mockito, plus database integration tests with H2.

## Tech Stack

| Area | Technologies |
| --- | --- |
| Language | Java 21 |
| Backend | Spring Boot, Spring Data JPA, Hibernate |
| Database | PostgreSQL (application), H2 (integration tests) |
| Cloud | AWS S3, SQS, Lambda, CloudWatch, IAM |
| Testing | JUnit 5, Mockito, Spring Boot Test, H2 |
| API documentation | Swagger / OpenAPI (springdoc) |
| Build and tooling | Maven, Lombok, Jakarta Validation |

## Features

### Project Management
- Create, update, retrieve, and delete projects.
- Associate tasks with projects using a one-to-many relationship.

### Task Management
- Create, update, retrieve, and delete tasks.
- Assign tasks to projects and users.
- Manage task statuses: `PENDING`, `IN_PROGRESS`, `COMPLETED`, and `CANCELLED`.
- Filter tasks by status, project ID, and partial case-insensitive title.
- Paginate and sort task results.

### User Management
- Create, update, retrieve, and delete users.
- Look up users by username or email.
- Prevent duplicate email registration and validate email changes.
- Assign tasks to users.
- Handle deletion of users with assigned tasks according to the configured business rules.

### Task Attachments (Amazon S3)
- Upload files and associate them with tasks.
- Store attachment metadata in PostgreSQL and file contents in a private S3 bucket.
- Generate time-limited presigned URLs for accessing files.
- Delete attachments from S3 and the application database.
- Track asynchronous processing with attachment statuses including `PENDING` and `PROCESSED`.

### API Capabilities
- DTO-based request and response models.
- Request validation and centralized exception handling.
- Partial-update logic for supported operations.
- Pagination, sorting, and dynamic filtering.
- Interactive API documentation with Swagger UI.

## Architecture

The application follows a layered design:

```text
HTTP Request
    |
Controller  ->  DTO / validation
    |
Service     ->  business logic / transactions
    |
Repository  ->  Spring Data JPA
    |
PostgreSQL
```

**Entity relationships:**
- Project → Tasks: one-to-many.
- User → Tasks: one-to-many.
- Task → Attachments: one-to-many.

### AWS Attachment Processing

The application uses two related asynchronous workflows:

**S3-triggered validation and logging**

```text
File upload -> Private S3 bucket -> S3 event -> AWS Lambda
                                              |
                                   File metadata / validation
                                              |
                                      CloudWatch logs
```

**Application event processing with SQS**

```text
Spring Boot attachment upload
           |
   Attachment saved as PENDING
           |
  AttachmentUploadedEvent published
           |
      Amazon SQS queue
           |
  Spring Boot SQS consumer
           |
 AttachmentEventProcessingService
           |
   Transactional database update
       /                   \
Attachment -> PROCESSED   ProcessedEvent -> saved
       \                   /
        Commit transaction
                |
       Delete SQS message
```

The S3/Lambda and SQS flows are distinct. S3 events trigger the Lambda function, while the Spring Boot application publishes and consumes attachment-upload messages through SQS.

### SQS Reliability and Idempotency

- The application publishes an `AttachmentUploadedEvent` containing the attachment ID, task ID, file name, S3 object key, event type, and unique event ID.
- The consumer uses SQS long polling to receive messages.
- Successfully processed messages are deleted from the queue.
- Failed messages are left undeleted so they can become visible again after the visibility timeout.
- A configured dead-letter queue receives messages that exceed the retry threshold (currently **3 receives**).
- The `processed_events` table stores successfully handled event IDs. Previously processed events are skipped to avoid repeating database work.
- `AttachmentEventProcessingService.processEvent(...)` runs within a Spring-managed `@Transactional` boundary. Updating the attachment to `PROCESSED` and recording the processed event succeed or roll back together.
- SQS message deletion happens only after processing returns successfully.

> **Scope:** The current idempotency approach handles sequential duplicate deliveries. Additional protection for simultaneous processing of the same event across multiple consumers remains a potential improvement.

## Testing

The project includes both isolated unit tests and Spring Boot database integration tests.

### Unit Tests: JUnit 5 + Mockito
- Mock external collaborators and repositories to test service behavior independently.
- Verify SQS message deletion after successful processing.
- Verify messages are not deleted when processing fails.
- Cover duplicate-event handling and invalid event IDs.

### Integration Tests: Spring Boot + H2
- Use `@SpringBootTest` and `@ActiveProfiles("test")` to run real Spring services and JPA repositories against an in-memory H2 database.
- Replace external AWS-facing dependencies with test mocks so integration tests do not call live AWS services.
- Disable scheduled polling in the test profile.
- Verify that successful event processing changes an attachment from `PENDING` to `PROCESSED` and records the event ID.
- Verify transaction rollback when persisting an invalid, overlong event ID fails: the attachment remains `PENDING` and no processed-event record is committed.

**Run tests:**

```bash
mvn test
```

**Test database configuration:** `src/test/resources/application-test.properties` uses H2 with schema creation and cleanup for the test environment. The integration tests exercise H2, not production PostgreSQL; PostgreSQL-based Testcontainers tests are planned.

## API Endpoints

### Projects

| Method | Endpoint |
| --- | --- |
| POST | `/api/projects` |
| GET | `/api/projects` |
| GET | `/api/projects/{id}` |
| PUT | `/api/projects/{id}` |
| DELETE | `/api/projects/{id}` |

### Tasks

| Method | Endpoint |
| --- | --- |
| POST | `/api/tasks` |
| GET | `/api/tasks` |
| GET | `/api/tasks/{id}` |
| PUT | `/api/tasks/{id}` |
| DELETE | `/api/tasks/{id}` |

### Users

| Method | Endpoint |
| --- | --- |
| POST | `/api/users` |
| GET | `/api/users` |
| GET | `/api/users/{id}` |
| PUT | `/api/users/{id}` |
| DELETE | `/api/users/{id}` |
| GET | `/api/users/username/{username}` |
| GET | `/api/users/email/{email}` |

### Task Attachments

| Method | Endpoint |
| --- | --- |
| POST | `/api/tasks/{taskId}/attachments` |
| GET | `/api/tasks/{taskId}/attachments` |
| GET | `/api/tasks/{taskId}/attachments/{attachmentId}/url` |
| DELETE | `/api/tasks/{taskId}/attachments/{attachmentId}` |

## Task Filtering, Search, Pagination, and Sorting

Task retrieval supports optional filters:

| Parameter | Description |
| --- | --- |
| `taskStatus` | Exact task-status match |
| `projectId` | Match a related project |
| `title` | Partial, case-insensitive title match |
| `page` | Page number |
| `size` | Number of results per page |
| `sortBy` | Sort field |
| `direction` | Sort direction |

**Example requests:**

```http
GET /api/tasks
GET /api/tasks?taskStatus=COMPLETED
GET /api/tasks?projectId=1
GET /api/tasks?title=result
GET /api/tasks?taskStatus=COMPLETED&projectId=1&title=result
GET /api/tasks?page=0&size=5&sortBy=id&direction=asc&taskStatus=IN_PROGRESS&title=report
```

## Running the Application

**Prerequisites:** Java 21, Maven, PostgreSQL, and AWS resources/permissions for enabled cloud features.

1. Configure the PostgreSQL connection in your application configuration.
2. Supply the AWS region and credentials through your configured environment variables or AWS authentication setup. Do **not** commit secrets to the repository.
3. Configure the S3 bucket, SQS queue URL, and relevant CloudWatch settings.
4. Ensure the SQS queue has the appropriate dead-letter queue and redrive policy.
5. Start the application:

```bash
mvn spring-boot:run
```

The application can be configured to use port `8081` (as in the examples below). Check your active `server.port` setting.

### Swagger / OpenAPI

When running on port `8081`:

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

Swagger UI supports exploring and manually testing endpoints. Postman can also be used for upload and repeated-request scenarios.

## Monitoring and Operational Behavior

- Structured application logs record attachment processing and SQS failures.
- CloudWatch integrations support logging and monitoring of AWS-related activity.
- Failed SQS messages can be inspected in the DLQ after configured retries.
- The application uses IAM permissions scoped to the AWS operations required by its services.

## Planned Enhancements

- Authentication and authorization with Spring Security and JWT.
- Role-based access control.
- PostgreSQL integration tests using Testcontainers.
- Containerization and AWS deployment (Docker with EC2 or ECS).
- Stronger safeguards for concurrent duplicate-event processing.

## Notes

This project is a hands-on backend and AWS learning project. It is actively maintained, with an emphasis on API design, database consistency, cloud integration, reliable asynchronous processing, and automated testing.
