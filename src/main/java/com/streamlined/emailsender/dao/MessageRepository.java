package com.streamlined.emailsender.dao;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.stream.Stream;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.streamlined.emailsender.data.MessageData;
import com.streamlined.emailsender.dto.MessageStatus;

@Repository
public interface MessageRepository extends ElasticsearchRepository<MessageData, String> {
	
	Stream<MessageData> findByStatusIn(Collection<MessageStatus> status); 
	
}
