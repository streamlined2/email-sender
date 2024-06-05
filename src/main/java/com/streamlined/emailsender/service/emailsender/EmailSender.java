package com.streamlined.emailsender.service.emailsender;

import java.sql.Date;
import java.time.LocalDate;
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

	private static final long MESSAGE_DELIVERY_DELAY = 5_000L;
	private static final long MESSAGE_RETRY_LAPSE = 5 * 60 * 1000L;

	private final MessageStoreService messageStoreService;
	private final MailSender mailSender;
	private final BlockingQueue<MessageDto> messageQueue = new LinkedBlockingQueue<>();

	@Override
	public void enqueue(MessageDto message) {
		messageQueue.add(message);
		log.info("Message is queued for dispatch: {}", message);
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
			mailMessage.setSentDate(Date.valueOf(LocalDate.now()));
			mailMessage.setTo(message.recipients().stream().map(ContactDto::email).toArray(size -> new String[size]));
			mailSender.send(mailMessage);
			messageStoreService.updateStatusSuccess(message.id());
			log.info("Message dispatched: {}", message);
		} catch (Exception e) {
			messageStoreService.updateStatusFail(message.id(), e.getMessage());
			log.error("Message can't be dispatched: {}", message);
		}
	}

	@Async
	@Scheduled(fixedRate = MESSAGE_RETRY_LAPSE)
	public void retryMessageDispatch() {
		messageStoreService.queryForFailedMessages().forEach(this::dispatchMessage);
	}

}
