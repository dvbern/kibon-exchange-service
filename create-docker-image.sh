#!/bin/bash

./mvnw clean package -Dhibernate.types.print.banner=false -Pdevelopment-mode
docker build -f src/docker/Dockerfile -t kibon-exchange/service .
