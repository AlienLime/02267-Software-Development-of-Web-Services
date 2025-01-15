#!/usr/bin/env bash
set -e

pushd BankStub
mvn jaxws:wsimport
mvn install
popd

pushd AccountManager
mvn package
popd

pushd ReportingManager
mvn package
popd

pushd DTUPayFacade
mvn package
popd

pushd TokenManager
mvn package
popd

pushd TransactionManager
mvn package
popd

pushd build
docker compose build
docker compose up -d
docker image prune -f
popd

sleep 2

pushd SimpleDTUPayClient
mvn test
popd