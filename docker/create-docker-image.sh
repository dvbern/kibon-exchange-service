#!/usr/bin/env bash

VERSION=latest-snapshot

cd ..
./mvnw clean package -Dhibernate.types.print.banner=false -Pdevelopment-mode
docker build -f docker/service/Dockerfile -t docker.dvbern.ch/kibon/exchange-service:${VERSION} .

docker build -f docker/nginx/Dockerfile -t docker.dvbern.ch/kibon/exchange-nginx:${VERSION} docker/nginx/
