package com.streamlined.emailsender.service.emailsender;

import java.util.stream.Collectors;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.streamlined.emailsender.dto.ContactDto;
import com.streamlined.emailsender.dto.MessageDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailSender implements Sender {

	private final MailSender mailSender;

	@Override
	public void send(MessageDto message) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(message.sender().name());
		mailMessage.setReplyTo(message.sender().email());
		mailMessage.setSubject(message.subject());
		mailMessage.setText(message.content());
		mailMessage.setTo(message.recipients().stream().map(ContactDto::email).collect(Collectors.joining(",")));
		mailSender.send(mailMessage);
	}

}
