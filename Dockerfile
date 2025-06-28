FROM openjdk:17
COPY ./target/workflow-engine-0.0.1-SNAPSHOT.jar ./
WORKDIR ./
CMD ["java", "-jar", "workflow-engine-0.0.1-SNAPSHOT.jar"]