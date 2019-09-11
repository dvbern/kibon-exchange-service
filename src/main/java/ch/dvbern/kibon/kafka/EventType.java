package ch.dvbern.kibon.kafka;

import java.util.Arrays;

/**
 * All known event types.
 */
public enum EventType {
	CLIENT_ADDED("ClientAdded"),
	CLIENT_REMOVED("ClientRemoved"),
	INSTITUTION_CHANGED("InstitutionChanged"),
	VERFUEGUNG_VERFUEGT("VerfuegungVerfuegt");

	private final String name;

	EventType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static EventType of(String name) {
		return Arrays.stream(values())
			.filter(value -> value.getName().equals(name))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException("No EventType found for name " + name));
	}
}
