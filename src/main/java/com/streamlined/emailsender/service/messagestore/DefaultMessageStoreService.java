package com.streamlined.emailsender.service.messagestore;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.streamlined.emailsender.dao.MessageRepository;
import com.streamlined.emailsender.data.MessageData;
import com.streamlined.emailsender.dto.MessageDto;
import com.streamlined.emailsender.dto.MessageMapper;
import com.streamlined.emailsender.dto.MessageStatus;
import com.streamlined.emailsender.exception.NoDocumentFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultMessageStoreService implements MessageStoreService {

	private final MessageRepository messageRepository;
	private final MessageMapper messageMapper;

	@Override
	public String save(MessageDto message) {
		MessageData entity = messageMapper.toEntity(message);
		return messageRepository.save(entity).getId();
	}

	@Override
	public void updateStatusSuccess(String messageId) {
		Optional<MessageData> messageData = messageRepository.findById(messageId);
		if (messageData.isEmpty()) {
			log.error("No document found with id {}", messageId);
			throw new NoDocumentFoundException("No document found with id %s".formatted(messageId));
		}
		MessageData entity = messageData.get();
		entity.setStatus(MessageStatus.SUCCESS);
		entity.setAttempt(0);
		entity.setLastAttemptInstant(null);
		entity.setErrorMessage(null);
		messageRepository.save(entity);
	}

	@Override
	public void updateStatusFail(String messageId, String errorMessage) {
		Optional<MessageData> messageData = messageRepository.findById(messageId);
		if (messageData.isEmpty()) {
			log.error("No document found with id {}", messageId);
			throw new NoDocumentFoundException("No document found with id %s".formatted(messageId));
		}
		MessageData entity = messageData.get();
		entity.setStatus(MessageStatus.FAIL);
		entity.setAttempt(entity.getAttempt() + 1);
		entity.setLastAttemptInstant(Instant.now());
		entity.setErrorMessage(errorMessage);
		messageRepository.save(entity);
	}

}