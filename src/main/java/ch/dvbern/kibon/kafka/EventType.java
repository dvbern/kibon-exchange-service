package ch.dvbern.kibon.kafka;

import java.util.Arrays;

import javax.annotation.Nonnull;

/**
 * All known event types.
 */
public enum EventType {
	CLIENT_ADDED("ClientAdded"),
	CLIENT_REMOVED("ClientRemoved"),
	INSTITUTION_CHANGED("InstitutionChanged"),
	VERFUEGUNG_VERFUEGT("VerfuegungVerfuegt");

	private final String name;

	@Nonnull
	EventType(String name) {
		this.name = name;
	}

	@Nonnull
	public static EventType of(@Nonnull String name) {
		return Arrays.stream(values())
			.filter(value -> value.getName().equals(name))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException("No EventType found for name " + name));
	}

	@Nonnull
	public String getName() {
		return name;
	}
}
