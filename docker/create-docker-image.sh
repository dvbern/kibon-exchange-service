#!/usr/bin/env bash

BASE=`dirname $0`
ROOT=${BASE}/..

VERSION=latest-snapshot

${ROOT}/mvnw -f ${ROOT}/pom.xml clean package -Dhibernate.types.print.banner=false -Pdevelopment-mode
docker build -f ${BASE}/service/Dockerfile -t docker.dvbern.ch/kibon/exchange-service:${VERSION} ${ROOT}

docker build -f ${BASE}/nginx/Dockerfile -t docker.dvbern.ch/kibon/exchange-nginx:${VERSION} ${BASE}/nginx/
