package ch.dvbern.kibon.platzbestaetigung.facade;

import javax.enterprise.context.ApplicationScoped;

import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungEventDTO;

@ApplicationScoped
public class BetreuungKafkaEventProducer {


	public void process(BetreuungEventDTO betreuungEventDTO) {
		//TODO find out how to send the BetreuungEvent to kafka, Problem with @outgoing
	}
}
