package ch.dvbern.kibon.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import ch.dvbern.kibon.exchange.commons.util.ObjectMapperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class CustomObjectMapperConfig {

	@Singleton
	@Produces
	public ObjectMapper objectMapper() {
		return ObjectMapperUtil.MAPPER;
	}
}
