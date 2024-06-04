package com.streamlined.emailsender.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.apache.kafka.common.serialization.Deserializer;

import com.streamlined.emailsender.dto.Message;
import com.streamlined.emailsender.exception.KafkaMessageSerializationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageDeserializer implements Deserializer<Message> {

	@Override
	public Message deserialize(String topic, byte[] data) {
		try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
				ObjectInputStream dataStream = new ObjectInputStream(byteStream)) {
			return ((Message) dataStream.readObject());
		} catch (ClassNotFoundException | IOException e) {
			log.error("impossible to deserialize Kafka message");
			throw new KafkaMessageSerializationException("impossible to deserialize Kafka message", e);
		}
	}

}
