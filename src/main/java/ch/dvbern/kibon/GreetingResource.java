package ch.dvbern.kibon;

import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

@Path("/hello")
public class GreetingResource {

	@Inject
	GreetingService service;

	@Inject
	KeycloakSecurityContext keycloakSecurityContext;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/greeting/{name}")
	public String greeting(@PathParam("name") String name) {
		return service.greeting(name);
	}

	@GET
	@PermitAll // PermitAll geht nicht: ohne access token wird 403 zurück gegeben
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		Set<String> roles = keycloakSecurityContext.getToken().getRealmAccess().getRoles();

		return "hello\n" + roles;
	}

	// funktioniert mit einem Direct Access Grant
	@GET
	@Path("/me")
	@RolesAllowed("user")
	@Produces(MediaType.APPLICATION_JSON)
	@NoCache
	public User me() {
		return new User(keycloakSecurityContext);
	}

	public class User {

		private final String userName;

		private final String clientId;

		User(KeycloakSecurityContext securityContext) {
			AccessToken token = securityContext.getToken();
			this.userName = token.getPreferredUsername();
			this.clientId = token.getIssuedFor();
		}

		public String getUserName() {
			return userName;
		}

		public String getClientId() {
			return clientId;
		}
	}
}
