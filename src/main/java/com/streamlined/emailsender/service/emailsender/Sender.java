package com.streamlined.emailsender.service.emailsender;

import com.streamlined.emailsender.dto.MessageDto;

public interface Sender {

	void enqueue(MessageDto event);

	public void dispatchMessages();

	public void retryMessageDispatch();

}
