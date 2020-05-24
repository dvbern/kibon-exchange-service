####
# This Dockerfile is used in order to build a container that runs the Quarkus application in JVM mode
#
# Before building the docker image run:
#
# mvn package
#
# Then, build the image with:
#
# docker build -f src/main/docker/Dockerfile.jvm -t kibon-exchange/service .
#
# Then run the container using:
#
# docker run -i --rm -p 8080:8080 kibon-exchange/service
#
###
FROM fabric8/java-alpine-openjdk11-jre
ARG BUILD_VERSION
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Dquarkus.log.sentry.release=$BUILD_VERSION -Djava.util.logging.manager=org.jboss.logmanager.LogManager  -Dhibernate.types.print.banner=false"
ENV AB_ENABLED=jmx_exporter

RUN apk update \
    && apk add openssl ca-certificates tzdata nss

COPY target/lib/* /deployments/lib/
COPY target/*-runner.jar /deployments/app.jar
ENTRYPOINT [ "/deployments/run-java.sh" ]
