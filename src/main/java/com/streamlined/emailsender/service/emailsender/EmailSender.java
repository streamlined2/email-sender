package com.streamlined.emailsender.service.emailsender;

import java.util.stream.Collectors;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.streamlined.emailsender.kafka.Contact;
import com.streamlined.emailsender.kafka.Message;

@Component
public class EmailSender implements Sender {

	private final MailSender mailSender;

	private EmailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void send(Message message) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(message.getSender().getName());
		mailMessage.setReplyTo(message.getSender().getEmail());
		mailMessage.setSubject(message.getSubject());
		mailMessage.setText(message.getContent());
		mailMessage.setTo(message.getRecipients().stream().map(Contact::getEmail).collect(Collectors.joining(",")));
		mailSender.send(mailMessage);
	}

}
