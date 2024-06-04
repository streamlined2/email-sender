package com.streamlined.emailsender.dto;

import java.time.Instant;
import java.util.List;

import lombok.Builder;

@Builder
public record MessageDto(String id, Instant createdInstant, ContactDto sender, List<ContactDto> recipients,
		String subject, String content, MessageStatus status, int attempt, Instant lastAttemptInstant,
		String errorMessage) {

}
