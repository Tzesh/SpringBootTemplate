# Spring Boot Template

A production-ready template for Spring Boot projects with modern best practices and built-in infrastructure.

![Docker Compose](https://imgur.com/qWLYDrg.png)
![Swagger UI](https://imgur.com/5MixfZe.png)

## Features

- **Spring Boot 4.0.0** (Java 25)
- **UUID Primary Keys** via Hibernate `@UuidGenerator`
- **JWT Authentication & Authorization** with refresh tokens
- **Role-based Security** with Spring Security
- **Spring Boot Actuator** for health, info, and metrics endpoints
- **Request Correlation ID** — automatic `X-Correlation-ID` header propagation and MDC logging
- **Structured JSON Logging** — human-readable console + JSON file output (Logstash format)
- **Pagination** — built into `BaseService` and controllers via Spring Data `Pageable`
- **Rate Limiting** — annotation-based (`@RateLimit`, `@RateLimitCategory`) with configurable categories
- **Idempotency** — `@Idempotent` annotation with Redis-backed request deduplication
- **Swagger UI** for API documentation ([http://localhost:8080/api/v1/swagger-ui/index.html](http://localhost:8080/api/v1/swagger-ui/index.html))
- **PostgreSQL**, **Redis**, **Kafka & Zookeeper** — all containerized via Docker Compose
- **Multi-stage Dockerfile** with dependency caching and JRE runtime
- **JaCoCo Code Coverage** reports
- **Maven Enforcer** — ensures Java 25+ and Maven 3.8.7+
- **Lombok** and **MapStruct** for reduced boilerplate
- **Environment Isolation** — run dev, test, and prod in parallel via Docker Compose
- **Unit & Integration Tests** for services, controllers, and infrastructure

> **Note:** Java 25 and Maven 3.8.7+ are required.

---

## Quick Start

### 1. Use This Template

Click **"Use this template"** on GitHub, then clone your new repository:

```sh
git clone https://github.com/<your-username>/<your-repo>.git
cd <your-repo>
```

### 2. Initialize Your Project

Run the initialization script to rename packages, modules, and containers to match your project:

```sh
./init-project.sh --group-id com.acme --artifact my-app --name "My App" --author "Your Name"
```

| Option | Description | Required |
|--------|-------------|----------|
| `--group-id` | Maven groupId / Java package root (e.g. `com.acme`) | Yes |
| `--artifact` | Artifact name in kebab-case (e.g. `my-app`) | Yes |
| `--name` | Human-readable project name (default: derived from artifact) | No |
| `--author` | Author name for `@author` tags (default: `tzesh`) | No |

You can also run it interactively — omit the flags and you'll be prompted:

```sh
./init-project.sh
```

The script will:
- Rename Java packages (`com.tzesh.springtemplate` -> your package)
- Rename module directories (`spring-boot-template-api` -> `<artifact>-api`, etc.)
- Update Docker container names, Kafka group IDs, and all references
- Replace `@author` tags if `--author` is provided
- Self-delete after completion

### 3. Verify the setup

```sh
./mvnw clean install
```

### 4. Start infrastructure and run

```sh
# Start PostgreSQL, Redis, Kafka
docker compose up -d db redis zookeeper kafka

# Run the application
./mvnw -pl spring-boot-template-api spring-boot:run
```

Or run everything with Docker:

```sh
docker compose up -d
```

The application will be available at [http://localhost:8080/api/v1/swagger-ui/index.html](http://localhost:8080/api/v1/swagger-ui/index.html).

---

## Project Structure

```
SpringBootTemplate/
├── pom.xml                          # Parent POM (enforcer, JaCoCo, shared deps)
├── spring-boot-template-core/       # Core module (base classes, annotations, exceptions)
│   └── src/main/java/.../base/
│       ├── annotation/              # @RateLimit, @Idempotent, etc.
│       ├── dto/                     # BaseDTO
│       ├── entity/                  # BaseEntity
│       ├── exception/               # NotFoundException, RateLimitExceededException, etc.
│       ├── handler/                 # @ControllerAdvice exception handlers
│       ├── mapper/                  # BaseMapper (MapStruct)
│       ├── response/                # BaseResponse wrapper
│       └── service/                 # BaseService (CRUD + pagination)
├── spring-boot-template-api/        # API module (controllers, services, config)
│   ├── Dockerfile                   # Multi-stage build
│   └── src/main/java/.../
│       ├── config/
│       │   ├── correlation/         # CorrelationIdFilter
│       │   ├── idempotency/         # IdempotencyAspect
│       │   ├── ratelimit/           # RateLimitAspect, RateLimitingFilter
│       │   └── security/            # SecurityChain, JWT filters
│       ├── controller/              # REST controllers
│       └── service/                 # Business services
├── docker-compose.yml               # PostgreSQL, Redis, Kafka, app
├── init-project.sh                  # Project initialization script
├── .editorconfig                    # Editor formatting rules
├── .gitattributes                   # Git line ending config
└── .dockerignore                    # Docker build exclusions
```

---

## API Usage

1. **Register** a new user via `POST /auth/register`
2. **Login** via `POST /auth/login` to obtain a JWT token
3. **Authorize** in Swagger UI using the `Authorize` button
4. **Access** secured endpoints (e.g. user management requires `ADMIN` role)

---

## Actuator Endpoints

Health, info, and metrics are exposed at:

| Endpoint | Description |
|----------|-------------|
| `GET /api/v1/actuator/health` | Application health status |
| `GET /api/v1/actuator/info` | Application info |
| `GET /api/v1/actuator/metrics` | Application metrics |

Health details are shown when authenticated with an authorized role. Kubernetes liveness and readiness probes are enabled.

---

## Correlation ID

Every request is assigned a correlation ID for distributed tracing:

- If the client sends an `X-Correlation-ID` header, it is reused
- Otherwise, a UUID is generated automatically
- The correlation ID is added to the MDC for all log output and returned in the response header

---

## Rate Limiting

### Annotation-based (per-endpoint)

```java
@RateLimit(limit = 20, duration = 1, key = RateLimitKeyStrategy.USER)
@RateLimitCategory(RateLimitCategoryType.STANDARD)
```

### Configurable categories in `application.properties`

| Category | Default Limit | Duration |
|----------|--------------|----------|
| `strict` | 10 req | 1 min |
| `standard` | 60 req | 1 min |
| `relaxed` | 200 req | 1 min |
| `authentication` | 5 req | 1 min |

---

## Idempotency

Annotate mutating endpoints with `@Idempotent` to prevent duplicate processing. Clients must send an `Idempotency-Key` header. Backed by Redis.

```java
@PostMapping("/")
@Idempotent
public ResponseEntity<BaseResponse<UserDTO>> createUser(...) { ... }
```

---

## Kafka Messaging

- Kafka and Zookeeper are included in Docker Compose
- `POST /kafka/send` sends messages to Kafka (supports optional scheduled delivery)
- Consumer logs received messages
- Logging level configurable in `application.properties`

---

## Environment Configuration

Use `.env` files for environment-specific variables and Docker Compose override files for port/resource separation:

```sh
# Run test environment
docker compose -f docker-compose.yml -f docker-compose.test.override.yml --env-file .env.test -p myapp_test up -d

# Run prod environment
docker compose -f docker-compose.yml -f docker-compose.prod.override.yml --env-file .env.prod -p myapp_prod up -d
```

---

## Code Coverage

JaCoCo reports are generated during `mvn test` and available at:

```
spring-boot-template-api/target/site/jacoco/index.html
spring-boot-template-core/target/site/jacoco/index.html
```

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for setup instructions, code style, and PR process.

---

## License

See [LICENSE](LICENSE) for details.
