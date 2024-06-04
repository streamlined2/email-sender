package com.streamlined.emailsender.dto;

import org.springframework.stereotype.Component;

import com.streamlined.emailsender.data.MessageData;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageMapper {

	private final ContactMapper contactMapper;

	public MessageData toEntity(MessageDto dto) {
		return MessageData.builder().id(dto.id()).createdInstant(dto.createdInstant())
				.sender(contactMapper.toEntity(dto.sender())).recipients(contactMapper.toEntityList(dto.recipients()))
				.subject(dto.subject()).content(dto.content()).status(dto.status()).attempt(dto.attempt())
				.lastAttemptInstant(dto.lastAttemptInstant()).errorMessage(dto.errorMessage()).build();
	}

	public MessageDto toDto(MessageData entity) {
		return MessageDto.builder().id(entity.getId()).createdInstant(entity.getCreatedInstant())
				.sender(contactMapper.toDto(entity.getSender()))
				.recipients(contactMapper.toDtoList(entity.getRecipients())).subject(entity.getSubject())
				.content(entity.getContent()).status(entity.getStatus()).attempt(entity.getAttempt())
				.lastAttemptInstant(entity.getLastAttemptInstant()).errorMessage(entity.getErrorMessage()).build();
	}

}
