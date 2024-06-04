package com.streamlined.emailsender.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ESConfiguration extends ElasticsearchConfiguration {

  @Value("${elasticsearch.address}")
  private String esAddress;

  @Override
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder()
        .connectedTo(esAddress)
        .build();
  }

}
