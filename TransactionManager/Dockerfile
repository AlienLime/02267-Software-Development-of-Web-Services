FROM eclipse-temurin:21 AS jre-build
COPY target/TransactionManager-1.0.0-SNAPSHOT-jar-with-dependencies.jar /usr/src/
WORKDIR /usr/src
CMD java -Xmx32m -jar TransactionManager-1.0.0-SNAPSHOT-jar-with-dependencies.jar