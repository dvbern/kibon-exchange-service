# kiBon Exchange Service

## Development Environment Setup

### Prepare the Environment
There are several services which must be running before the Quarkus application is started:
Postgres, Kafka and eventually Keycloak.

Postgres and Kafka can be started with
 `docker-compose -f docker/docker-compose.yml up -d`

Make sure that three services start: `kibon-exchange_zookeeper_1`, `kibon-exchange_kafka_1`, 
and `kibon-exchange_db_1`. Sometimes Kafka fails at startup.

### Start the Quarkus Application
Quarkus runs best from a terminal:

`./mvnw compile quarkus:dev`

This will start Quarkus in Hot Replace mode, migrate the FlyWay schema and then execut the 
statements in `src/main/resources/import-dev.sql`. Unfortunately, it looks like the execution order is not consistent
(maybe both started asynchronously?). Combining both, import scritps and FlyWay migrations is not properly supported
and may result in random crashes.

There are a couple of other profiles defined in application.propperties.
 All of them have their pros and cons:

| Profile | Pros | Cons |
| --- | --- | --- |
| dev | entity changes are applied directly | FlyWay migrations are not recreated: everything that is specified only in FlyWay scripts (e.g. triggers) must be manually applied (or integrated in import-dev.sql). Crashes sometimes |
| dev-with-data | updates schema and keeps data | does not import mock data from import-dev.sql | 
| test | see dev | FlyWay migrations are disabled for stability. Everything must be added to import-test.sql | 
| prod | only applies FlyWay migrations | no mock data or dynamic schema updates | 

It is suggested to start with the `dev` profile and switch to `dev-with-data` when the database should not be
wiped.

To start with a specific profile run with the `quarkus.profile` parameter, e.g.:

`./mvnw compile quarkus:dev -Dquarkus.profile=dev-update`

### Port Configuration
The PostgreSQL database runs on 15432 to avoid conflicts with native installations.
Kafka and Zookeeper run on the standard ports 9092, respectively 2181.
All these ports can be changed in docker-compose.yml

When changing the ports, don't forget to update `src/main/resources/application.properties`.

| Property | Default Value | Comment |
| --- | --- | --- |
| quarkus.keycloak.auth-server-url | http://localhost:8180/auth | Keycloak Server |
| quarkus.datasource.url | jdbc:postgresql://localhost:15432/kibon-exchange | Postgres Database |
| kafka.bootstrap.servers | localhost:9092 | Kafka Server |
| quarkus.http.port |8380| Application Port, e.g. http://localhost:8380/api/v1/verfuegungen |

### Swagger-UI and Keycloak integration
In development mode, swagger-ui is integrated under /api/v1/swagger-ui. To interact with the REST endpoints, 
authorization is required. Thus a full development stack should be started (see above). Additionally,
read in ./dev-proxy/README.md how to create and start a proxy. 

# Debugging the Quarkus Application
Quarkus allows remote debugging on default Port 5005. When another application like a WildFly is already running in
debug mode, this port is typically already in use. In that case, Quarkus logs "Port 5005 in use, not starting in debug 
mode".

The Port can be changed by adding the debug property, e.g.:
`./mvnw compile quarkus:dev -Ddebug=5006 -Dquarkus.profile=dev-with-data`

The debug with IntelliJ simply add a new `Remote` configuration an attach to the JVM with the debug port.

# Preparing for Production

The docker/docker-compose.yml file is intended for local development, 
where quarkus is directly started through maven.

In production, an nginx-based proxy is set in front of the quarkus application and keycloak. 
`https://my-domain/auth` is proxied to keycloak while `https://my-domain/api/` is proxied to quarkus.

At the moment the quarkus application has only been tested in JVM mode. 
To create the docker images for exchange-service and the nginx-proxy run `./mvnw install docker:build`.

To start a production-like environment, create the certificates for nginx and the quarkus application using 
`docker/setup.sh`. Update `docker/.env` with host IP address and credentials.
Finally, run `docker-compose -f docker/docker-compose.prod.yml --project-directory docker up`
