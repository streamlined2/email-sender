package com.streamlined.emailsender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestEmailSenderApplication {

	@Bean
	@ServiceConnection
	ElasticsearchContainer elasticsearchContainer() {
		return new ElasticsearchContainer(DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:7.17.10"));
	}

	@Bean
	@ServiceConnection
	KafkaContainer kafkaContainer() {
		return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
	}

	public static void main(String[] args) {
		SpringApplication.from(EmailSenderApplication::main).with(TestEmailSenderApplication.class).run(args);
	}

}
