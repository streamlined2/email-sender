package com.streamlined.emailsender.service.kafkaconsumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.streamlined.emailsender.dto.Message;
import com.streamlined.emailsender.dto.MessageDto;
import com.streamlined.emailsender.dto.MessageMapper;
import com.streamlined.emailsender.exception.CannotSaveMessageException;
import com.streamlined.emailsender.service.emailsender.Sender;
import com.streamlined.emailsender.service.messagestore.MessageStoreService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

	private final Sender sender;
	private final MessageStoreService messageStoreService;
	private final MessageMapper messageMapper;

	@KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.group-id}")
	public void listen(@Payload Message message, @Header(KafkaHeaders.KEY) Object messageKey,
			@Header(KafkaHeaders.RECORD_METADATA) Object metaData) {
		try {
			MessageDto messageDto = messageMapper.toDto(message);
			MessageDto savedMessageDto = messageStoreService.save(messageDto);
			sender.enqueue(savedMessageDto);
		} catch (Exception e) {
			log.error("Cannot save recieved message {}, message key {}, meta data {}, exception {}", message,
					messageKey, metaData, e);
			throw new CannotSaveMessageException("Cannot save recieved message", e);
		}
	}

}
