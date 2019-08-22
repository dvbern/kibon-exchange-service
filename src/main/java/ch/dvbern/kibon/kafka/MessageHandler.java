package ch.dvbern.kibon.kafka;

import java.util.UUID;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface MessageHandler<T> {
	void handle(@Nonnull String key, @Nonnull UUID eventId, @Nonnull String eventType, T payload);
}
