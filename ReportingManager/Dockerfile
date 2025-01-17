FROM eclipse-temurin:21 AS jre-build
WORKDIR /usr/src
COPY target/ReportingManager-1.0.0-SNAPSHOT.jar /usr/src/
CMD java -Xmx32m -jar ReportingManager-1.0.0-SNAPSHOT.jar