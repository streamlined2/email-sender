package com.streamlined.emailsender.service.kafkaconsumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.streamlined.emailsender.dto.Message;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaConsumer {

	@KafkaListener(topics = "notification", groupId = "consumer-group")
	public void listen(@Payload Message message, @Header(KafkaHeaders.KEY) Object messageKey,
			@Header(KafkaHeaders.RECORD_METADATA) Object metaData) {
		log.info("Received message: {} with key {}, record metadata: {} ", message, messageKey, metaData);
	}

}
