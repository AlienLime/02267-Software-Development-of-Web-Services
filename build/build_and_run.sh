#!/usr/bin/env bash
set -e

# Install local maven dependencies
mvn clean install -f ../BankStub/pom.xml
mvn clean install -f ../MessagingUtilities/pom.xml

# Build microservices
services=(
    "AccountManager" 
    "TokenManager" 
    "TransactionManager"
    "ReportingManager"
    "DTUPayFacade"
)

for service in "${services[@]}"; do
  pushd ../"$service"
  mvn clean package
  popd
done

# Deploy
docker compose build
docker compose up -d
docker image prune -f
docker system prune -f

docker exec dtu-pay-rabbitmq rabbitmqctl await_startup

# Test
pushd ../SimpleDTUPayClient
mvn test
popd
