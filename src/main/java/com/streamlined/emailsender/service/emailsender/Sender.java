package com.streamlined.emailsender.service.emailsender;

import com.streamlined.emailsender.dto.MessageDto;

public interface Sender {

	void send(MessageDto event);

}
