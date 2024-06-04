package com.streamlined.emailsender.service.messagestore;

import com.streamlined.emailsender.dto.MessageDto;

public interface MessageStoreService {

	String save(MessageDto message);

	void updateStatusSuccess(String messageId);

	void updateStatusFail(String messageId, String errorMessage);

}
