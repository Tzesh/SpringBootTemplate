# Stage 1: Build
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /build

# Copy Maven wrapper and config first (dependency cache layer)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY spring-boot-template-core/pom.xml spring-boot-template-core/pom.xml
COPY spring-boot-template-api/pom.xml spring-boot-template-api/pom.xml

RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code
COPY spring-boot-template-core/src spring-boot-template-core/src
COPY spring-boot-template-api/src spring-boot-template-api/src

# Build the API module (and its dependency core module)
RUN ./mvnw package -pl spring-boot-template-api -am -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=builder /build/spring-boot-template-api/target/spring-boot-template-api-*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
