# Spring Boot Template

## Description
A template for Spring Boot projects. This template includes the following features:
- Spring Boot 3.0.6
- Spring Security with JWT authentication and authorization also with refresh token
- Swagger UI for API documentation and testing (http://localhost:8080/api/v1/swagger-ui/index.html) with authentication and authorization support
- Spring Data JPA with PostgreSQL database integration
- Exception handling mechanism with custom exceptions and exception handlers using `@ControllerAdvice`
- Base entity and base DTO for common fields
- Base service for common business logic
- Custom `@PreAuthorize` annotation for authorization
- Lombok for reducing boilerplate code
- MapStruct for mapping DTOs to entities and vice versa

## How to run
1. Clone this repository
2. Create a PostgreSQL database
3. Change the database configuration in `application.properties`
4. Run the application with `mvn spring-boot:run`
5. Open http://localhost:8080/api/v1/swagger-ui/index.html in your browser
6. Register a new user
7. Login with the registered user
8. Copy the JWT token from the response
9. Click the `Authorize` button in the Swagger UI
10. Paste the JWT token in the `Value` field without the `Bearer` prefix
11. Click the `Authorize` button
12. Now you can test the API endpoints
13. To test the refresh token endpoint, click the `Authorize` button again and paste the refresh token in the `Value` field without the `Bearer` prefix

![Maven Run](https://imgur.com/lGSrRLL.png)
![Swagger UI](https://imgur.com/7Vug2XR.png)

## Contribute to this project
If you want to contribute to this project, please create a pull request.
