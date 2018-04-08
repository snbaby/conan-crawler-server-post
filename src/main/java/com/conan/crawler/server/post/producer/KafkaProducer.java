package com.conan.crawler.server.post.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaProducer {
	@Autowired
	private KafkaTemplate kafkaTemplate;
	
	public void keyWordProducer() {
		
	}
}
