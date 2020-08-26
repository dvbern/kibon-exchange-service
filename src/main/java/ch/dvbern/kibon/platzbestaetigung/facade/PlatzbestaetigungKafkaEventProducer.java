package ch.dvbern.kibon.platzbestaetigung.facade;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.enterprise.context.ApplicationScoped;

import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungEventDTO;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;

import static ch.dvbern.kibon.exchange.commons.util.EventUtil.MESSAGE_HEADER_EVENT_TYPE;

@ApplicationScoped
public class PlatzbestaetigungKafkaEventProducer{

	@Inject
	@Channel("PlatzbestaetigungBetreuungEvents")
	private Emitter<BetreuungEventDTO> platzbestaetigungBetreuungEvents;

	public void process(BetreuungEventDTO betreuungEventDTO) throws IllegalStateException{
		String key = betreuungEventDTO.getRefnr();
		String eventType = "PlatzbestaetigungBetreuung";

		OutgoingKafkaRecordMetadata<String> metadata = OutgoingKafkaRecordMetadata.<String>builder()
			.withKey(key)
			.withTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
			.withHeaders(new RecordHeaders().add(MESSAGE_HEADER_EVENT_TYPE,
				eventType.getBytes(StandardCharsets.UTF_8)))
			.build();

		KafkaRecord<String, BetreuungEventDTO> record = KafkaRecord.of(key, betreuungEventDTO);
		Message<BetreuungEventDTO> message = record.addMetadata(metadata);
		platzbestaetigungBetreuungEvents.send(message);
	}
}
