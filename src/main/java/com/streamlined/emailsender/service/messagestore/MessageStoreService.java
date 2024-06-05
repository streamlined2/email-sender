package com.streamlined.emailsender.service.messagestore;

import java.util.stream.Stream;

import com.streamlined.emailsender.dto.MessageDto;

public interface MessageStoreService {

	MessageDto save(MessageDto message);

	void updateStatusSuccess(String messageId);

	void updateStatusFail(String messageId, String errorMessage);
	
	Stream<MessageDto> queryForFailedMessages();

}
