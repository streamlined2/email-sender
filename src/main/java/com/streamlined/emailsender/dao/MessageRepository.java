package com.streamlined.emailsender.dao;

import org.springframework.stereotype.Repository;

import java.util.Collection;

import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.streamlined.emailsender.data.MessageData;
import com.streamlined.emailsender.dto.MessageStatus;

@Profile("!test")
@Repository
public interface MessageRepository extends ElasticsearchRepository<MessageData, String> {
	
	Iterable<MessageData> findByStatusIn(Collection<MessageStatus> status); 
	
}
