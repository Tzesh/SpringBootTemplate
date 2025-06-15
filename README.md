# Spring Boot Template

## Description
A robust template for Spring Boot projects with modern best practices and production-ready integrations.

![Docker Compose](https://imgur.com/qWLYDrg.png)
![Swagger UI](https://imgur.com/7Vug2XR.png)

### Key Features
- **Spring Boot 3.2.3** (Java 21 LTS)
- **JWT Authentication & Authorization** (with refresh tokens)
- **Swagger UI** for API documentation and testing ([http://localhost:8080/api/v1/swagger-ui/index.html](http://localhost:8080/api/v1/swagger-ui/index.html))
- **PostgreSQL** integration (via Docker Compose)
- **Redis** for caching and session management
- **Kafka & Zookeeper** for message brokering (via Docker Compose)
- **Role-based Security** with Spring Security
- **Rate Limiting**
- **Exception Handling** with `@ControllerAdvice`
- **Lombok** and **MapStruct** for reduced boilerplate
- **Environment Isolation**: Easily run multiple environments (dev, test, prod) in parallel using Docker Compose project names and override files
- **Configurable Logging** (including Kafka)
- **Unit & Integration Tests** for key services and controllers

> **Note:** Java 21 is required. Ensure your local environment supports Java 21 for development and builds.

---

## How to Run

### Using Docker Compose (Recommended)
1. Clone this repository
2. Ensure Docker and Docker Compose are installed
3. For a default environment, run:
   ```sh
   docker compose up -d
   ```
4. For multiple environments (e.g., test and prod) in parallel:
   ```sh
   docker-compose -f docker-compose.yml -f docker-compose.test.override.yml --env-file env.test -p myapp_test up -d
   docker-compose -f docker-compose.yml -f docker-compose.prod.override.yml --env-file env.prod -p myapp_prod up -d
   ```
   - This will create isolated containers, networks, and volumes for each environment.
   - Make sure to set different ports in override files to avoid conflicts.
5. The application will be available at [http://localhost:8080/api/v1/swagger-ui/index.html](http://localhost:8080/api/v1/swagger-ui/index.html) (or the port you set).

### Using Maven (Local Development)
1. Clone this repository
2. Set up PostgreSQL and Redis (or use Docker Compose)
3. Adjust `application.properties` as needed
4. Run:
   ```sh
   mvn spring-boot:run
   ```
5. Open [http://localhost:8080/api/v1/swagger-ui/index.html](http://localhost:8080/api/v1/swagger-ui/index.html)

---

## Kafka Messaging
- Kafka and Zookeeper are included in Docker Compose for local development.
- REST endpoint `/kafka/send` allows sending messages to Kafka, with optional scheduling for future delivery.
- Kafka consumer logs received messages.
- Kafka logging is configurable in `application.properties`.

---

## Environment Configuration
- Use `.env` files for environment-specific variables.
- Use Docker Compose override files for port and resource separation.
- Example for test environment:
  ```sh
  docker-compose -f docker-compose.yml -f docker-compose.test.override.yml --env-file env.test -p myapp_test up -d
  ```

---

## API Usage
1. Register a new user
2. Login and obtain a JWT token
3. Use the token in Swagger UI via the `Authorize` button
4. Access secured endpoints (e.g., Kafka messaging requires ADMIN role)

---

## Project Abilities Summary
- Modular REST API with security, rate limiting, and exception handling
- PostgreSQL, Redis, Kafka, and Zookeeper integration (all containerized)
- Role-based access control
- Swagger/OpenAPI documentation
- Environment isolation for dev, test, and prod
- Configurable logging
- Unit and integration tests

---

For more details, see the code and comments, or open an issue for questions!

