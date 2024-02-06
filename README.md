# kiBon Exchange Service

## Development Environment Setup

### Prepare the Environment

There are several services which must be running before the Quarkus application is started:
Postgres, Kafka, Schema Registry, Zookeeper and Keycloak.

Copy the `.env-template` file to the proejct root with filename `.env`. Go through the environment variables in the
created file and set credentials (replacing CHANGE_ME).

Once the setup is complete, the development environment can be started from the project root with
`docker-compose up -d`

Make sure that all services start:

* `kibon-exchange_zookeeper_1`
* `kibon-exchange_kafka_1`
* `kibon-exchange_schema_registry_1`
* `kibon-exchange_keycloak_1`
* `kibon-exchange_db_1`

Sometimes Kafka fails at startup, in that case just try again.

### Start the Quarkus Application

Quarkus runs best from a terminal:

`./mvnw compile quarkus:dev -Dquarkus.profile=dev-with-data -Pdevelopment-mode`

This will start Quarkus in hot replace mode and apply the FlyWay migrations.

The table bellow lists other profiles. The default Quarkus `dev` profile is unfortuantely not well suited in combination with FlyWay migrations.
It's usefull for rapid development, because changes in entities automatically rebuild the schema and then execute the statements in `src/main/resources/import-dev.sql`.
But it looks like the execution order of the schema recreation and the FlyWay import is not consistent, causing random crashes due to different database states.

There are a couple of other profiles defined in application.propperties.
 All of them have their pros and cons:

| Profile | Pros | Cons |
| --- | --- | --- |
| dev | entity changes are applied directly | FlyWay migrations are not recreated: everything that is specified only in FlyWay scripts (e.g. triggers) must be manually applied (or integrated in import-dev.sql). Crashes sometimes |
| dev-with-data | updates schema and keeps data | does not import mock data from import-dev.sql |
| test | see dev | FlyWay migrations are disabled for stability. Everything must be added to import-test.sql |
| prod | only applies FlyWay migrations | no mock data or dynamic schema updates |

### Port Configuration

The PostgreSQL database runs on 15432 to avoid conflicts with native installations.
Kafka and Zookeeper run on the standard ports 9092, respectively 2181.
All these ports can be changed in `docker-compose.yml`.

When changing the ports, don't forget to update `src/main/resources/application.properties`.

| Property | Default Value | Comment |
| --- | --- | --- |
| quarkus.keycloak.auth-server-url | `http://localhost:8180/auth` | Keycloak server |
| quarkus.datasource.jdbc.url | jdbc:postgresql://localhost:15432/kibon-exchange | Postgres database |
| kafka.bootstrap.servers | localhost:9092 | Kafka server |
| quarkus.http.port |8380| Application Port, e.g. `http://localhost:8380/api/v1/verfuegungen` |

### Swagger-UI and Keycloak integration

Swagger-ui is integrated under /api/v1/swagger-ui. To interact with the REST endpoints,
authorization is required. Thus, a full development stack should be started (see above). Additionally,
read in ./dev-proxy/README.md how to create and start a proxy.

Swagger-ui is accessible locally und the next url => http://local.kibon.ch/api/v1/q/swagger-ui/#/
You need to add local.kibon.ch to your local host file:
127.0.0.1       local.kibon.ch

Or you can directly access it through the localhost URL if your docker compose was startet on your local network
=> http://localhost/api/v1/q/swagger-ui/#

You should never access swagger-ui with the quarkus port otherwise the authentication with keycloak will fail as
the proxy will keep the port of quarkus for the redirect to keycloak.

## Debugging the Quarkus Application

Quarkus allows remote debugging on default Port 5005. When another application like a WildFly application server is running in
debug mode, this port is typically already in use. In that case, Quarkus logs "Port 5005 in use, not starting in debug
mode".

The Port can be changed by adding the debug property, e.g.:
`./mvnw compile quarkus:dev -Ddebug=5006 -Dquarkus.profile=dev-with-data`

The debug with IntelliJ simply add a new `Remote` configuration and attach to the JVM with the debug port.

## Preparing for Production

The `docker-compose.yml` file is intended for local development,
where quarkus is directly started through maven.

In production, an nginx-based proxy is set in front of the quarkus application and keycloak.
`https://my-domain/auth/` is proxied to keycloak while `https://my-domain/api/v1/` is proxied to quarkus.

The `docker-compose.prod.yml` file can be used to test the dockerized quarkus application. It is mostly similar to the
development environment, but assumes that there is an external postgres database set up.
It includes the nginx-proxy and the exchange-service.

At the moment the quarkus application has only been tested in JVM mode.
To create the docker images for exchange-service and the nginx-proxy run `./docker/create-docker-image.sh`.

To start a production-like environment, create the certificates for nginx and the quarkus application using
`docker/setup.sh`.

In case some environment variables must be adjusted, either create a separate .env file and use the docker-compose
`--env-file` argument, or use a wrapper script for docker-compose, e.g.:

```bash
#!/usr/bin/env bash

# using a fixed group id for kafka consumers, to continue listenting from last offset
export KAFKA_GROUP_ID=v1
# accessing keycloak directly through the docker network on the HTTP port (bypass self-signed certificate issue)
export QUARKUS_OIDC_AUTH_SERVER_URL=http://keycloak:8080/auth/realms/kibon
# connection to database on localhost
export DB_PORT=5432
# gets the IP address for interface enp0s31f6
HOST_IP=$(ifconfig enp0s31f6 | grep "inet " | awk '{print $2}')
export QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://${HOST_IP}:${DB_PORT}/kibon-exchange
export QUARKUS_DATASOURCE_PASSWORD=CHANGE_ME

export KEYCLOAK_FRONTEND_URL=https://localhost/auth
export QUARKUS_OIDC_TOKEN_ISSUER=${KEYCLOAK_FRONTEND_URL}/realms/kibon

# applies environment variables from `.env` file, with overrides as exported above, and uses the `docker-compose.prod.yml` setup.
# All arguments are passed further to docker-compose.
docker-compose -f docker-compose.prod.yml -p kibon-exchange-prod "$@"
```

Usage:

| Command | Explanation |
| --- | --- |
| `./my-wrapper-script.sh up -d` | to start in background |
| `./my-wrapper-script.sh stop` | to stop |
| `./my-wrapper-script.sh down` | to stop & remove containers |
| `./my-wrapper-script.sh logs -f` | to watch logs in "follow" mode |
