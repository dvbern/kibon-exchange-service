FROM docker-registry.dvbern.ch/devops/docker/jdk/adoptium/temurin-jdk17-alpine
WORKDIR /home/app

ARG BUILD_VERSION

ADD target/docker-extra/run-java/run-java.sh /home/app

# Configure the JAVA_OPTIONS, you can add -XshowSettings:vm to also display the heap size.
ENV JAVA_OPTIONS="-Dquarkus.log.sentry.release=$BUILD_VERSION -Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --chown=5555 target/quarkus-app/lib/ /home/app/lib/
COPY --chown=5555 target/quarkus-app/*.jar /home/app/
COPY --chown=5555 target/quarkus-app/app/ /home/app/app/
COPY --chown=5555 target/quarkus-app/quarkus/ /home/app/quarkus/

EXPOSE 8080
USER app

ENTRYPOINT [ "/home/app/run-java.sh" ]
