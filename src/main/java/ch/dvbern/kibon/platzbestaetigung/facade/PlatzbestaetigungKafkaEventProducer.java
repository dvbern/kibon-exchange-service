package ch.dvbern.kibon.platzbestaetigung.facade;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;

import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungEventDTO;
import ch.dvbern.kibon.exchange.commons.util.AvroConverter;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;

import static ch.dvbern.kibon.exchange.commons.util.EventUtil.MESSAGE_HEADER_EVENT_TYPE;

@ApplicationScoped
public class PlatzbestaetigungKafkaEventProducer {

	@Inject
	@Channel("PlatzbestaetigungBetreuungEvents")
	private Emitter<GenericRecord> platzbestaetigungBetreuungEvents;

	public void process(BetreuungEventDTO betreuungEventDTO) {
		String key = betreuungEventDTO.getRefnr();
		//String topic = "PlatzbestaetigungBetreuungEvents";   //.withTopic(topic)
		String eventType = "PlatzbestaetigungBetreuung";
		byte[] payload = AvroConverter.toAvroBinary(betreuungEventDTO);
		GenericRecord specificRecordBase = AvroConverter.fromAvroBinaryGeneric(betreuungEventDTO.getSchema(), payload);

		OutgoingKafkaRecordMetadata<String> metadata = OutgoingKafkaRecordMetadata.<String>builder()
			.withKey(key)
			.withTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
			.withHeaders(new RecordHeaders().add(MESSAGE_HEADER_EVENT_TYPE,
				eventType.getBytes(StandardCharsets.UTF_8)))
			.build();
		platzbestaetigungBetreuungEvents.send(KafkaRecord.of(key,
			specificRecordBase).addMetadata(metadata));
	}
}
