# --- ETAPA 1: EL COCINERO (Construcción) ---
# 1. Descargamos una imagen de Java con herramientas para "cocinar" (compilar)
FROM eclipse-temurin:17-jdk-alpine as build

# 2. Creamos una carpeta de trabajo dentro de Render
WORKDIR /workspace/app

# 3. Copiamos tus archivos de configuración de Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# 4. Le damos permisos al ejecutable de Maven
RUN chmod +x mvnw

# 5. ¡COCINAMOS! Este comando descarga las librerías y crea el archivo .jar final
# (El -DskipTests es para que no pierda tiempo ejecutando pruebas unitarias)
RUN ./mvnw install -DskipTests

# --- ETAPA 2: EL PLATO SERVIDO (Ejecución) ---
# 6. Ahora usamos una imagen más ligera solo para "servir" (ejecutar)
FROM eclipse-temurin:17-jre-alpine

# 7. Preparamos el lugar
VOLUME /tmp

# 8. Copiamos SOLO el archivo .jar que cocinamos en la etapa 1
# (Esto hace que tu aplicación pese menos y arranque más rápido)
COPY --from=build /workspace/app/target/*.jar app.jar

# 9. Le decimos a Render: "Cuando inicies, ejecuta este comando"
ENTRYPOINT ["java","-jar","/app.jar"]