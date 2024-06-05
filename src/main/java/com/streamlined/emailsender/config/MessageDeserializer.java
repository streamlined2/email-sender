package com.streamlined.emailsender.config;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.common.serialization.Deserializer;

import com.streamlined.emailsender.dto.Message;
import com.streamlined.emailsender.exception.KafkaMessageSerializationException;
import com.streamlined.emailsender.dto.Contact;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageDeserializer implements Deserializer<Message> {

	@Override
	public Message deserialize(String topic, byte[] data) {
		try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
				DataInputStream dataStream = new DataInputStream(byteStream)) {

			Message.MessageBuilder messageBuilder = Message.builder();
			final long timestampSeconds = dataStream.readLong();
			final Instant createdTime = Instant.ofEpochMilli(timestampSeconds * 1000);
			messageBuilder.instant(createdTime);

			messageBuilder.sender(Contact.builder().name(dataStream.readUTF()).email(dataStream.readUTF()).build());
			final int recipientCount = dataStream.readInt();
			List<Contact> recipients = new ArrayList<>(recipientCount);
			for (int k = 0; k < recipientCount; k++) {
				recipients.add(Contact.builder().name(dataStream.readUTF()).email(dataStream.readUTF()).build());
			}
			messageBuilder.recipients(recipients);
			messageBuilder.subject(dataStream.readUTF());
			messageBuilder.content(dataStream.readUTF());
			return messageBuilder.build();
		} catch (IOException e) {
			log.error("impossible to deserialize Kafka message {}", data);
			throw new KafkaMessageSerializationException("impossible to deserialize Kafka message", e);
		}
	}

}
