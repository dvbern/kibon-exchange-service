# Proxy for development

This proxy configuration assumes the URL `http://local-kibon.dvbern.ch` is configured in your `/etc/hosts` file to
redirect to localhost.

The purpose of the proxy is to launch keycloak and the quarkus development with a common URL. This simulates deployment
of productive systems and avoids CORS issues.
It is the only way to get a fully functioning swagger-ui setup on
 [localhost](http://local-kibon.dvbern.ch/api/v1/q/swagger-ui).

Build the image `./build.sh` and launch it with `./start-proxy.sh`.

## Windows usage
For windows use, you need to change the nginx.conf apiserver and keycloak upstream server name from localhost to your server IP Adresse.
<br>
Example:
<br>
upstream apiserver {
<br>
server 192.168.10.96:8380;
<br>
}

upstream keycloak {
<br>
server 192.168.10.96:8180;
<br>
}

Then build the image `build.cmd` and launch it with `start-proxy.cmd`.

# Windows with WSL2
For windows with Container started in WSL2, we recommand the use of a windows nginx proxy with this configuration.
You just need to think that keycloak will be started in the WSL2 environment, while Quarkus in windows, the ip adresses will then be different for each server.
