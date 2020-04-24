FROM openjdk:11-jre-slim
COPY covidrescue.jar /covidrescue.jar
COPY application.properties /application.properties
EXPOSE 8080
ENTRYPOINT ["java","-jar","/covidrescue.jar"]