# kiBon Exchange Service

## Development Environement Setup

### Prepare the Environment
There are several services which must before the Quarkus application is started:
Postgres, Kafka and eventually Keycloak.

Postgres and Kafka can be started with through
 `docker-compose -f kafka/docker-compose.yml up -d`

Make sure that three services start: `kibon-exchange_zookeeper_1`, `kibon-exchange_kafka_1`, and `kibon-exchange_db_1`. Sometimes Kafka fails at startup.

### Start the Quarkus Application
Quarkus runs best from a terminal:

`./mvnw compile quarkus:dev -Dquarkus.profile=dev-with-data -Dhibernate.types.print.banner=false`

This will start Quarkus in Hot Replace mode, migrate the FlyWay schema and then execut the statements in `src/main/resources/import-dev.sql`.

### Port Configuration
The PostgreSQL database runs on 15432 to avoid conflicts with native installations.
Kafka and Zookeeper run on the standard ports 9092, respectively 2181.
All these prots can be changed in docker-compose.yml

When changing the ports, don't forget to update `src/main/resources/application.properties`.

| Property | Default Value | Comment |
| --- | --- | --- |
| quarkus.keycloak.auth-server-url | http://localhost:8180/auth | Keycloak Server |
| quarkus.datasource.url | jdbc:postgresql://localhost:15432/kibon-exchange | Postgres Database |
| kafka.bootstrap.servers | localhost:9092 | Kafka Server |
