#!/usr/bin/env bash
set -e

pushd BankStub
mvn jaxws:wsimport
mvn install
popd

pushd SimpleDTUPayServer
mvn package 
docker compose build
docker compose up -d
docker image prune -f
popd

sleep 2

pushd SimpleDTUPayClient
mvn test
popd