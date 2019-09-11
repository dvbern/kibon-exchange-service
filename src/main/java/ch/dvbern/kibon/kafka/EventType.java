package ch.dvbern.kibon.kafka;

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
}
