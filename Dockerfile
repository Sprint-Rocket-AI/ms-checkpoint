# ── Stage 1: Build ──────────────────────────────────────────
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw

COPY pom.xml .
RUN ./mvnw dependency:go-offline -q

COPY src ./src
RUN ./mvnw package -DskipTests -q

# ── Stage 2: Runtime ─────────────────────────────────────────
FROM eclipse-temurin:25-jre
WORKDIR /app

LABEL maintainer="SprintRocket IA"
LABEL service="ms-checkpoint"

RUN groupadd -r spring && useradd -r -g spring spring
USER spring

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]