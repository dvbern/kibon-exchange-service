package ch.dvbern.kibon.messagelog.model;

import java.time.Instant;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class ConsumedMessage {

	@Nonnull
    @Id
    @Column(nullable = false, unique = true)
    private @NotNull UUID eventId;

    @Nonnull
    @Column(nullable = false)
    private @NotNull Instant timeOfReceiving;

    @SuppressWarnings("unused")
	ConsumedMessage() {
    	eventId = UUID.randomUUID();
    	timeOfReceiving = Instant.now();
    }

    public ConsumedMessage(@Nonnull UUID eventId, @Nonnull Instant timeOfReceiving) {
        this.eventId = eventId;
        this.timeOfReceiving = timeOfReceiving;
    }

	@Nonnull
	public UUID getEventId() {
		return eventId;
	}

	public void setEventId(@Nonnull UUID eventId) {
		this.eventId = eventId;
	}

	@Nonnull
	public Instant getTimeOfReceiving() {
		return timeOfReceiving;
	}

	public void setTimeOfReceiving(@Nonnull Instant timeOfReceiving) {
		this.timeOfReceiving = timeOfReceiving;
	}
}
