FROM eclipse-temurin:21 AS jre-build
COPY target/quarkus-app /usr/src/quarkus-app
WORKDIR /usr/src
CMD java -Xmx64m -jar quarkus-app/quarkus-run.jar