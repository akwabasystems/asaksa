# Stage 1: Package the JAR file
FROM maven:3.9-eclipse-temurin-17 as build
WORKDIR /api
COPY pom.xml .
RUN mvn dependency:resolve
COPY src src
RUN mvn package

# Stage 2: Extract the layers of the Spring Boot app
FROM maven:3.9-eclipse-temurin-17 as layers
WORKDIR /api
COPY --from=build /api/target/asakusa-api.jar asakusa-api.jar
RUN java -Djarmode=layertools -jar asakusa-api.jar extract 

# Stage 3: Copy the layers and launch the app
FROM tomcat:jre17-temurin
WORKDIR /api
COPY --from=layers /api/dependencies/ ./
COPY --from=layers /api/snapshot-dependencies/ ./
COPY --from=layers /api/spring-boot-loader/ ./
COPY --from=layers /api/application/ ./
EXPOSE 9443
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
