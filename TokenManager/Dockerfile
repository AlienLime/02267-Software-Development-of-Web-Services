FROM eclipse-temurin:21 AS jre-build
WORKDIR /usr/src
COPY target/TokenManager-1.0.0-SNAPSHOT.jar /usr/src/
CMD java -Xmx32m -jar TokenManager-1.0.0-SNAPSHOT.jar