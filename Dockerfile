# Etapa de Build (Usando Maven)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src

# Copiar el POM
COPY pom.xml .

# --- INICIO DE LA CORRECCIÓN ---
# Copiar el settings.xml que tiene el mirror HTTP
COPY ci/settings.xml .

# Usar el settings.xml con el flag -s para descargar dependencias
RUN mvn -s settings.xml dependency:go-offline
# --- FIN DE LA CORRECCIÓN ---

COPY src ./src

# --- INICIO DE LA CORRECCIÓN ---
# Usar el settings.xml también para el empaquetado
RUN mvn -s settings.xml package -DskipTests
# --- FIN DE LA CORRECCIÓN ---

# Etapa Final (Usando JRE)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /src/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]