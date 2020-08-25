package ch.dvbern.kibon.platzbestaetigung.facade;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;

import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungEventDTO;
import ch.dvbern.kibon.exchange.commons.util.AvroConverter;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;


import static ch.dvbern.kibon.exchange.commons.util.EventUtil.MESSAGE_HEADER_EVENT_TYPE;
import static com.google.common.base.Preconditions.checkNotNull;

@ApplicationScoped
public class BetreuungKafkaEventProducer {

	@Inject
	@Channel("BetreuungEvents")
	private Emitter<ProducerRecord<String, GenericRecord>> betreuungEvents;

	public void process(BetreuungEventDTO betreuungEventDTO) {
		String key = betreuungEventDTO.getRefnr();
		String topic = "BetreuungEvent";
		String eventType = "Betreuung";
		byte[] payload = AvroConverter.toAvroBinary(betreuungEventDTO);
		GenericRecord specificRecordBase = AvroConverter.fromAvroBinaryGeneric(betreuungEventDTO.getSchema(), payload);

		Iterable<Header> headers = Arrays.asList(
			//We dont have any event ID
			// new RecordHeader(MESSAGE_HEADER_EVENT_ID, eventId.getBytes(StandardCharsets.UTF_8)),
			new RecordHeader(MESSAGE_HEADER_EVENT_TYPE, eventType.getBytes(StandardCharsets.UTF_8))
		);
		long timestamp = checkNotNull(LocalDateTime.now())
			.atZone(ZoneId.systemDefault())
			.toInstant()
			.toEpochMilli();
		ProducerRecord<String, GenericRecord> producerRecord = new ProducerRecord<>(
			topic,	null,	timestamp, key, specificRecordBase, headers);
		//TODO find out how to send the BetreuungEvent to kafka => exception because ProducerRecord is an unsupported
		// Avro Type, but from kiBon we send ProducerRecord to kafka...
		betreuungEvents.send(producerRecord);
	}
}
