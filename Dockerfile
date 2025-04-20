FROM openjdk:17
COPY build/libs/my-app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]