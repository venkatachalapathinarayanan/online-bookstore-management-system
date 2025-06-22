# ---- Build Stage ----
FROM eclipse-temurin:17-jdk-alpine as build

RUN apk add --no-cache curl wget

WORKDIR /app
COPY . .
ARG MODULE=book-inventory-service
ARG SERVICE_PORT=8081
WORKDIR /app/${MODULE}
RUN chmod +x ../gradlew

RUN for i in 1 2 3; do \
        ../gradlew :${MODULE}:bootJar --no-daemon --stacktrace --refresh-dependencies --max-workers=1 && break || \
        (echo "Attempt $i failed, retrying..." && sleep 15); \
    done

# ---- Run Stage ----
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
ARG MODULE=book-inventory-service
ARG SERVICE_PORT=8081
COPY --from=build /app/${MODULE}/build/libs/${MODULE}.jar app.jar
EXPOSE ${SERVICE_PORT}
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${SERVICE_PORT}"] 