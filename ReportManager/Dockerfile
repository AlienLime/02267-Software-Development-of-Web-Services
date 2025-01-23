FROM eclipse-temurin:21 AS jre-build
COPY target/ReportingManager-1.0.0-SNAPSHOT-jar-with-dependencies.jar /usr/src/
WORKDIR /usr/src
CMD java -Xmx32m -jar ReportingManager-1.0.0-SNAPSHOT-jar-with-dependencies.jar