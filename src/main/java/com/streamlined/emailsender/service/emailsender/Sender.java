package com.streamlined.emailsender.service.emailsender;

import com.streamlined.emailsender.dto.MessageDto;

public interface Sender {

	long MESSAGE_DELIVERY_DELAY = 5_000L;
	long MESSAGE_RETRY_LAPSE = 5 * 60 * 1000L;

	void enqueue(MessageDto event);

	void dispatchMessages();

	void retryMessageDispatch();

}
