package com.streamlined.emailsender;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.streamlined.emailsender.dao.MessageRepository;
import com.streamlined.emailsender.data.ContactData;
import com.streamlined.emailsender.data.MessageData;
import com.streamlined.emailsender.dto.Contact;
import com.streamlined.emailsender.dto.ContactDto;
import com.streamlined.emailsender.dto.Message;
import com.streamlined.emailsender.dto.MessageDto;
import com.streamlined.emailsender.dto.MessageStatus;
import com.streamlined.emailsender.service.emailsender.Sender;
import com.streamlined.emailsender.service.messagestore.MessageStoreService;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@EmbeddedKafka(kraft = true, count = 1, topics = { "${spring.kafka.topic}" }, partitions = 1, brokerProperties = {
		"listeners=PLAINTEXT://localhost:9094", "port=9094" })
@ContextConfiguration(classes = { EmailSenderApplication.class, TestKafkaConfiguration.class })
class EmailSenderTest {

	private static final long MESSAGE_RECEIVE_DELAY = 5_000L;
	private static final long MESSAGE_DELIVERY_TIME = MESSAGE_RECEIVE_DELAY + 3 * Sender.MESSAGE_DELIVERY_DELAY;

	@Value("${spring.kafka.topic}")
	private String notificationTopic;

	@MockBean
	private MailSender mailSender;
	@MockBean
	private MessageRepository messageRepository;

	@SpyBean
	private Sender emailSender;
	@SpyBean
	private MessageStoreService messageStoreService;

	@Autowired
	private KafkaOperations<String, Message> kafkaOperations;

	@Test
	void testMessageStoredAndSentSuccessfully() {

		Instant instant = Instant.now();
		Contact senderContact = Contact.builder().name("Administrator").email("admin@company.com").build();
		List<Contact> recipientContacts = List.of(Contact.builder().name("John").email("john@company.com").build(),
				Contact.builder().name("Jack").email("jack@company.com").build(),
				Contact.builder().name("Robert").email("robert@company.com").build());
		String subject = "Very important message";
		String content = "Some troublesome event happened somewhere";
		Message message = Message.builder().instant(instant).sender(senderContact).recipients(recipientContacts)
				.subject(subject).content(content).build();

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(senderContact.getName());
		mailMessage.setReplyTo(senderContact.getEmail());
		mailMessage.setSubject(subject);
		mailMessage.setText(content);
		mailMessage.setSentDate(Date.from(instant));
		mailMessage.setTo(recipientContacts.stream().map(Contact::getEmail).toArray(size -> new String[size]));

		final String messageId = "1";

		ContactDto senderContactDto = ContactDto.builder().name("Administrator").email("admin@company.com").build();
		List<ContactDto> recipientContactDtos = List.of(
				ContactDto.builder().name("John").email("john@company.com").build(),
				ContactDto.builder().name("Jack").email("jack@company.com").build(),
				ContactDto.builder().name("Robert").email("robert@company.com").build());
		MessageDto messageDto = MessageDto.builder().id(messageId).createdInstant(instant).sender(senderContactDto)
				.recipients(recipientContactDtos).subject(subject).content(content).build();

		ContactData senderContactData = ContactData.builder().name(senderContact.getName())
				.email(senderContact.getEmail()).build();
		List<ContactData> recipientContactData = recipientContacts.stream()
				.map(contact -> ContactData.builder().name(contact.getName()).email(contact.getEmail()).build())
				.toList();
		MessageData messageData = MessageData.builder().id(messageId).createdInstant(instant).sender(senderContactData)
				.recipients(recipientContactData).subject(subject).content(content).status(MessageStatus.FAIL)
				.attempt(0).lastAttemptInstant(null).errorMessage(null).build();

		when(messageRepository.save(any())).thenReturn(messageData);
		when(messageRepository.findById(messageId)).thenReturn(Optional.of(messageData));
		when(messageRepository.findByStatusIn(any())).thenReturn(List.of());

		CompletableFuture<SendResult<String, Message>> future = kafkaOperations.send(notificationTopic, message);
		kafkaOperations.flush();

		future.thenAccept(sendResult -> {
			verify(messageStoreService, timeout(MESSAGE_RECEIVE_DELAY)).save(messageDto);
			verify(emailSender, timeout(MESSAGE_RECEIVE_DELAY)).enqueue(messageDto);

			verify(mailSender, timeout(MESSAGE_DELIVERY_TIME)).send(mailMessage);
			verify(messageStoreService, timeout(MESSAGE_DELIVERY_TIME)).updateStatusSuccess(messageDto.id());
			verify(messageStoreService, never()).updateStatusFail(anyString(), anyString());
		}).exceptionally(exc -> {
			fail("Sending message to Kafka topic failed");
			return null;
		});
	}

	@Test
	void testMessageNeverStoredNorSent() {

		Instant instant = Instant.now();
		Contact senderContact = Contact.builder().name("Administrator").email("admin@company.com").build();
		List<Contact> recipientContacts = List.of(Contact.builder().name("John").email("john@company.com").build(),
				Contact.builder().name("Jack").email("jack@company.com").build(),
				Contact.builder().name("Robert").email("robert@company.com").build());
		String subject = "Very important message";
		String content = "Some troublesome event happened somewhere";
		Message message = Message.builder().instant(instant).sender(senderContact).recipients(recipientContacts)
				.subject(subject).content(content).build();

		final String messageId = "1";

		ContactDto senderContactDto = ContactDto.builder().name("Administrator").email("admin@company.com").build();
		List<ContactDto> recipientContactDtos = List.of(
				ContactDto.builder().name("John").email("john@company.com").build(),
				ContactDto.builder().name("Jack").email("jack@company.com").build(),
				ContactDto.builder().name("Robert").email("robert@company.com").build());
		MessageDto messageDto = MessageDto.builder().id(messageId).createdInstant(instant).sender(senderContactDto)
				.recipients(recipientContactDtos).subject(subject).content(content).build();

		when(messageRepository.save(any())).thenThrow(new RuntimeException());
		when(messageRepository.findById(anyString())).thenReturn(Optional.empty());
		when(messageRepository.findByStatusIn(any())).thenReturn(List.of());

		CompletableFuture<SendResult<String, Message>> future = kafkaOperations.send(notificationTopic, message);
		kafkaOperations.flush();

		future.thenAccept(sendResult -> {
			verify(messageStoreService, timeout(MESSAGE_RECEIVE_DELAY)).save(messageDto);
			verify(emailSender, never()).enqueue(any());

			verify(mailSender, never()).send(any(SimpleMailMessage.class));
			verify(messageStoreService, never()).updateStatusSuccess(messageDto.id());
			verify(messageStoreService, never()).updateStatusFail(anyString(), anyString());
		}).exceptionally(exc -> {
			fail("Sending message to Kafka topic failed");
			return null;
		});

	}

}
