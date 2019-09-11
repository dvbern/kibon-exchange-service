#!/bin/bash

./mvnw clean package -Dhibernate.types.print.banner=false -Pdevelopment-mode
docker build -f src/main/docker/Dockerfile.jvm -t kibon-exchange/service .
