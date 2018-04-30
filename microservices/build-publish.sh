#!/bin/bash
set -e

cd ./books
mvn clean package
docker build . -t books-l
docker tag books-l luismoramedina/books-l
docker push luismoramedina/books-l

cd ../stars/
mvn clean package
docker build . -t stars-l
docker tag stars-l luismoramedina/stars-l
docker push luismoramedina/stars-l

cd ../..