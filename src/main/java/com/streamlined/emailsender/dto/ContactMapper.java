package com.streamlined.emailsender.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.streamlined.emailsender.data.ContactData;

@Component
public class ContactMapper {

	public ContactData toEntity(ContactDto dto) {
		return ContactData.builder().name(dto.name()).email(dto.email()).build();
	}

	public ContactDto toDto(ContactData entity) {
		return ContactDto.builder().name(entity.getName()).email(entity.getEmail()).build();
	}

	public ContactDto toDto(Contact contact) {
		return ContactDto.builder().name(contact.getName()).email(contact.getEmail()).build();
	}

	public List<ContactData> toEntityList(List<ContactDto> dtos) {
		return dtos.stream().map(this::toEntity).toList();
	}

	public List<ContactDto> toDtoList(List<ContactData> entities) {
		return entities.stream().map(this::toDto).toList();
	}

	public List<ContactDto> toDtoFromContactList(List<Contact> contacts) {
		return contacts.stream().map(this::toDto).toList();
	}

}
