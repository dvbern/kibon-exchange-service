#!/bin/bash

./mvnw clean package -Dhibernate.types.print.banner=false -Pdevelopment-mode
docker build -f docker/service/Dockerfile -t docker.dvbern.ch/kibon/exchange-service:latest-snapshot .
