package com.streamlined.emailsender.dao;

import org.springframework.stereotype.Repository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.streamlined.emailsender.data.MessageData;

@Repository
public interface MessageRepository extends ElasticsearchRepository<MessageData, String> {
}
