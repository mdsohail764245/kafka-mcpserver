package com.example.kafkamcpserver;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class KafkaMcpserverApplication {


	public static void main(String[] args) {
		SpringApplication.run(KafkaMcpserverApplication.class, args);
	}

	@Bean
	public ToolCallbackProvider kafkaTools(KafkaConsumerService kafkaConsumerService) {
		return MethodToolCallbackProvider
				.builder()
				.toolObjects(kafkaConsumerService)
				.build();
	}


}
