# Etapa 1: Build do frontend
FROM node:18-alpine AS frontend-build
WORKDIR /app/frontend
COPY SGB---Frontend/sgb-web/sgb-web/Web/package*.json ./
RUN npm install
COPY SGB---Frontend/sgb-web/sgb-web/Web ./
RUN npm run build

# Etapa 2: Build do backend
FROM eclipse-temurin:17-jdk AS backend-build
WORKDIR /app/backend
COPY SGB---Backend/sgb-api/sgb-api/mvnw ./
COPY SGB---Backend/sgb-api/sgb-api/.mvn ./.mvn
COPY SGB---Backend/sgb-api/sgb-api/pom.xml ./
RUN ./mvnw dependency:go-offline
COPY SGB---Backend/sgb-api/sgb-api/src ./src
# Copiar build do frontend para resources do Spring Boot
COPY --from=frontend-build /app/frontend/dist/. ./src/main/resources/static/
RUN ./mvnw package -DskipTests

# Etapa 3: Imagem final
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend-build /app/backend/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
