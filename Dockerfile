# ---- Build Stage ----
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app
COPY . .
ARG MODULE=user-management-service
ARG SERVICE_PORT=8083
WORKDIR /app/${MODULE}
RUN chmod +x ../gradlew
RUN ../gradlew :${MODULE}:bootJar

# ---- Run Stage ----
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
ARG MODULE=user-management-service
ARG SERVICE_PORT=8083
COPY --from=build /app/${MODULE}/build/libs/${MODULE}.jar app.jar
EXPOSE ${SERVICE_PORT}
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${SERVICE_PORT}"]
