# Use Eclipse Temurin JRE 25 image
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy the built jar file using a wildcard
COPY target/spring-boot-template-api-*.jar app.jar

EXPOSE 8080

# Optional: Java options
ENV JAVA_OPTS=""

# Healthcheck (optional, for Docker monitoring)
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
