# ============================================================
# 后端 Spring Boot Dockerfile (多阶段构建)
# ============================================================
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
RUN mkdir -p /app/upload
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
