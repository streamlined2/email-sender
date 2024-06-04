package com.streamlined.emailsender.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Message implements Serializable {

	@EqualsAndHashCode.Include
	private final Instant instant;
	@EqualsAndHashCode.Include
	private final Contact sender;
	private final List<Contact> recipients;
	@EqualsAndHashCode.Include
	private final String subject;
	private final String content;

	@Override
	public String toString() {
		return "Sender=%s, recipients=%s, subject=%s, content=%s".formatted(sender, recipients, subject, content);
	}

}
