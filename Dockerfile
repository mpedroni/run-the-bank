FROM eclipse-temurin:21-jdk-alpine

WORKDIR /opt/app

COPY build/libs/*.jar /opt/app/application.jar

RUN addgroup -S spring && adduser -S spring -G spring
RUN chown -R spring:spring /opt/app

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/app/application.jar"]