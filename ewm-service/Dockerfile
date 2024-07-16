FROM amazoncorretto:11
COPY service/target/*.jar ewm-server.jar
ENTRYPOINT ["java","-jar","/ewm-server.jar"]