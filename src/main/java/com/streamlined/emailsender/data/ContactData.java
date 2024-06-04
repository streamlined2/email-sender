package com.streamlined.emailsender.data;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactData {

	@Field(type = FieldType.Keyword)
	private String name;

	@Field(type = FieldType.Keyword)
	private String email;

}
