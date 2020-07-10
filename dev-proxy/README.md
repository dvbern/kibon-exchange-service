# Proxy for development

This proxy configuration assumes the URL `http://local-kibon.dvbern.ch` is configured in your `/etc/hosts` file to
redirect to localhost.

The purpose of the proxy is to launch keycloak and the quarkus development with a common URL. This simulates deployment
of productive systems and avoids CORS issues.
It is the only way to get a fully functioning swagger-ui setup on
 [localhost](http://local-kibon.dvbern.ch/api/v1/swagger-ui).

Build the image `./build.sh` and launch it with `./start-proxy.sh`.
