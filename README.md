# Spring Boot Template

## Description
A template for Spring Boot projects. This template includes the following features:
- Spring Boot 3.2.3 (latest 3.x LTS, updated for security and new features)
- Java 21 (latest LTS, required for Spring Boot 3.2+)
- Spring Security with JWT authentication and authorization also with refresh token
- Swagger UI for API documentation and testing (http://localhost:8080/api/v1/swagger-ui/index.html) with authentication and authorization support
- Spring Data JPA with PostgreSQL database integration
- Exception handling mechanism with custom exceptions and exception handlers using `@ControllerAdvice`
- Base entity and base DTO for common fields
- Base service for common business logic
- Custom `@PreAuthorize` annotation for authorization
- Lombok for reducing boilerplate code
- MapStruct for mapping DTOs to entities and vice versa
- **Docker Compose support for easy containerized development**
- Redis integration for caching and session management (configurable via environment variables)

> **Note:** This template is up-to-date with Spring Boot 3.2.3 and Java 21. Ensure your local environment supports Java 21 for development and builds.

## How to run

### Using Docker Compose (Recommended)
1. Clone this repository
2. Make sure Docker and Docker Compose are installed on your system
3. Run the following command in the project root:
   ```sh
   docker compose up -d
   ```
4. The application will be available at [http://localhost:8080/api/v1/swagger-ui/index.html](http://localhost:8080/api/v1/swagger-ui/index.html)
5. PostgreSQL will be available at port 5432 (default credentials are set in `docker-compose.yml` and can be overridden with environment variables)

### Using Maven (Local Development)
1. Clone this repository
2. Create a PostgreSQL database or use the provided Docker Compose setup
3. Change the database configuration in `application.properties` if needed
4. Make sure you have redis and postgresql running locally or use the Docker Compose setup
4. Run the application with:
   ```sh
   mvn spring-boot:run
   ```
5. Open [http://localhost:8080/api/v1/swagger-ui/index.html](http://localhost:8080/api/v1/swagger-ui/index.html) in your browser

### API Usage
1. Register a new user
2. Login with the registered user
3. Copy the JWT token from the response
4. Click the `Authorize` button in the Swagger UI
5. Paste the JWT token in the `Value` field without the `Bearer` prefix
6. Click the `Authorize` button
7. Now you can test the API endpoints
8. To test the refresh token endpoint, click the `Authorize` button again and paste the refresh token in the `Value` field without the `Bearer` prefix

![Maven Run](https://imgur.com/lGSrRLL.png)
![Swagger UI](https://imgur.com/7Vug2XR.png)

## Environment Variables
- You can override default database credentials and other settings using environment variables in `docker-compose.yml` or by creating a `.env` file in the project root.

### Environment Variables for Redis

You can configure Redis connection using environment variables (recommended for Docker Compose):

- `SPRING_DATA_REDIS_HOST` (default: `redis`)
- `SPRING_DATA_REDIS_PORT` (default: `6379`)

Example in `docker-compose.yml`:

```yaml
app:
  # ...existing code...
  environment:
    SPRING_DATA_REDIS_HOST: redis
    SPRING_DATA_REDIS_PORT: 6379
    # ...other env vars...
```

Redis will be available at port 6379 by default.

## Database Configuration

- To connect to your PostgreSQL database:
  - Host: `db`
  - Port: `5432`
  - Username: as set in `docker-compose.yml` (default: `development`)
  - Password: as set in `docker-compose.yml` (default: `T3st!ng`)

## Contribute to this project
If you want to contribute to this project, please create a pull request.
