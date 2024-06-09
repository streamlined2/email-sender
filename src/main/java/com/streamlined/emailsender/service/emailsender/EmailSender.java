package com.streamlined.emailsender.service.emailsender;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.streamlined.emailsender.dto.ContactDto;
import com.streamlined.emailsender.dto.MessageDto;
import com.streamlined.emailsender.service.messagestore.MessageStoreService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailSender implements Sender {

	private final MessageStoreService messageStoreService;
	private final MailSender mailSender;
	private final BlockingQueue<MessageDto> messageQueue = new LinkedBlockingQueue<>();

	@Override
	public void enqueue(MessageDto message) {
		messageQueue.add(message);
	}

	@Scheduled(fixedDelay = MESSAGE_DELIVERY_DELAY)
	public void dispatchMessages() {
		for (MessageDto message; (message = messageQueue.poll()) != null;) {
			dispatchMessage(message);
		}
	}

	private void dispatchMessage(MessageDto message) {
		try {
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setFrom(message.sender().name());
			mailMessage.setReplyTo(message.sender().email());
			mailMessage.setSubject(message.subject());
			mailMessage.setText(message.content());
			mailMessage.setSentDate(Date.from(message.createdInstant()));
			mailMessage.setTo(message.recipients().stream().map(ContactDto::email).toArray(size -> new String[size]));
			mailSender.send(mailMessage);
			messageStoreService.updateStatusSuccess(message.id());
		} catch (Exception e) {
			messageStoreService.updateStatusFail(message.id(),
					"exception class name: %s, message: %s".formatted(e.getClass().getName(), e.getMessage()));
			log.error("Message can't be dispatched: {}, exception {}", message, e);
		}
	}

	@Async
	@Scheduled(fixedRate = MESSAGE_RETRY_LAPSE, initialDelay = MESSAGE_DELIVERY_DELAY)
	public void retryMessageDispatch() {
		messageStoreService.queryForFailedMessages().forEach(this::dispatchMessage);
	}

}
