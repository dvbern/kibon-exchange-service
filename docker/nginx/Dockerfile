FROM docker-registry.dvbern.ch/dockerhub/library/nginx:stable-alpine

MAINTAINER Fabio Heer <fabio.heer@dvbern.ch>
LABEL product="kibon"

HEALTHCHECK CMD wget \
    -q \
    -U DockerHealthCheck \
    --no-check-certificate \
    --spider \
    https://$(hostname -i || echo '127.0.0.1')/ || exit 1

COPY /copy /

RUN apk update \
    && apk add openssl ca-certificates tzdata

EXPOSE 80 443
