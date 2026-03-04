# Contributing to Spring Boot Template

Thank you for your interest in contributing! This guide will help you get started.

## Prerequisites

- **Java 25** (Eclipse Temurin recommended)
- **Maven 3.8.7+** (or use the included `./mvnw` wrapper)
- **Docker & Docker Compose** (for running PostgreSQL, Redis, and Kafka locally)

## Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Tzesh/SpringBootTemplate.git
   cd SpringBootTemplate
   ```

2. **Start infrastructure services:**
   ```bash
   docker compose up -d db redis zookeeper kafka
   ```

3. **Build the project:**
   ```bash
   ./mvnw clean install
   ```

4. **Run the application:**
   ```bash
   ./mvnw -pl spring-boot-template-api spring-boot:run
   ```

## Code Style

- **Indentation:** 4 spaces (no tabs)
- **Line endings:** LF (Unix-style)
- **Charset:** UTF-8
- Use Lombok annotations where appropriate (`@RequiredArgsConstructor`, `@Getter`, etc.)
- Follow existing patterns in `BaseService`, `BaseEntity`, and `BaseDTO`
- Keep controllers thin — business logic belongs in services

## Branching

- `master` — stable, release-ready code
- `feature/<name>` — new features
- `fix/<name>` — bug fixes
- `release-<version>` — release branches

## Pull Request Process

1. Create a feature or fix branch from `master`
2. Make your changes with clear, focused commits
3. Ensure all tests pass: `./mvnw clean verify`
4. Open a PR against `master`
5. Provide a clear description of your changes
6. Wait for review and address feedback

## Testing

- Write unit tests for new services and controllers
- Place tests in the corresponding `src/test/java` directory
- Run tests: `./mvnw test`
- Check code coverage reports in `target/site/jacoco/`

## Questions?

Open an issue on GitHub if you have questions or need help.
