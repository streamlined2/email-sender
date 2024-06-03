package com.streamlined.emailsender.service.emailsender;

import com.streamlined.emailsender.kafka.Message;

public interface Sender {

	void send(Message event);

}
