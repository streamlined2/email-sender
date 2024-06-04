package com.streamlined.emailsender.data;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.streamlined.emailsender.dto.MessageStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(indexName = "messages")
public class MessageData {

	@Id
	private String id;

	@Field(type = FieldType.Date)
	private Instant createdInstant;

	private ContactData sender;

	private List<ContactData> recipients;

	@Field(type = FieldType.Text)
	private String subject;

	@Field(type = FieldType.Text)
	private String content;

	@Field(type = FieldType.Keyword)
	private MessageStatus status;

	@Field(type = FieldType.Integer)
	private int attempt;
	
	@Field(type = FieldType.Date)
	private Instant lastAttemptInstant;

	@Field(type = FieldType.Text)
	private String errorMessage;

}
