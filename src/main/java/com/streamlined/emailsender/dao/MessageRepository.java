package com.streamlined.emailsender.dao;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.streamlined.emailsender.data.MessageData;

public interface MessageRepository extends ElasticsearchRepository<MessageData, String> {
}
