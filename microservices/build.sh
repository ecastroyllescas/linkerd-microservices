#!/bin/bash
set -e

cd books
mvn clean package
docker build . -t books-l

cd ../stars/
mvn clean package
docker build . -t stars-l

cd ../..