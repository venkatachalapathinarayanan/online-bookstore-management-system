# ---- Build Stage ----
FROM eclipse-temurin:17-jdk-alpine as build

RUN apk add --no-cache curl wget

WORKDIR /app
COPY . .
ARG MODULE=user-management-service
ARG SERVICE_PORT=8083
WORKDIR /app/${MODULE}
RUN chmod +x ../gradlew

# Add diagnostic steps to debug build issues
RUN echo "=== Starting build for ${MODULE} ===" && \
    echo "=== Current directory: $(pwd) ===" && \
    echo "=== Listing files: ===" && \
    ls -la && \
    echo "=== Listing gradlew: ===" && \
    ls -la ../gradlew

# Build with retry logic and better error reporting
RUN for i in 1 2 3; do \
        echo "=== Build attempt $i ===" && \
        ../gradlew :${MODULE}:bootJar --no-daemon --stacktrace --refresh-dependencies --max-workers=1 --info && \
        echo "=== Build successful! ===" && \
        echo "=== Checking for JAR file ===" && \
        ls -la build/libs/ && \
        break || \
        (echo "=== Attempt $i failed, retrying in 15 seconds... ===" && sleep 15); \
    done

# Verify the JAR was created
RUN echo "=== Final verification ===" && \
    ls -la build/libs/ && \
    echo "=== Build directory contents ===" && \
    find build/ -name "*.jar" -type f

# ---- Run Stage ----
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
ARG MODULE=user-management-service
ARG SERVICE_PORT=8083
COPY --from=build /app/${MODULE}/build/libs/${MODULE}.jar app.jar
EXPOSE ${SERVICE_PORT}
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${SERVICE_PORT}"] 