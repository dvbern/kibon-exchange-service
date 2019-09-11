package ch.dvbern.kibon.consumedmessage.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

	public ConsumedMessage() {
    	eventId = UUID.randomUUID();
    	timeOfReceiving = Instant.now();
    }

    public ConsumedMessage(@Nonnull UUID eventId, @Nonnull Instant timeOfReceiving) {
        this.eventId = eventId;
        this.timeOfReceiving = timeOfReceiving;
    }

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof ConsumedMessage)) {
			return false;
		}

		ConsumedMessage that = (ConsumedMessage) o;

		return getEventId().equals(that.getEventId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getEventId());
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
