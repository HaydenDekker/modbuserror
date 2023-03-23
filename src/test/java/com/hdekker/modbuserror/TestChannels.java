package com.hdekker.modbuserror;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.MessageChannels;

@Configuration
public class TestChannels {

	@Bean
	public DirectChannel testChan() {
		return MessageChannels.direct("test-chan")
				.get();
	}
	

	@Bean
	DirectChannel freply() {
		return MessageChannels.direct("freply")
		.get();
	}
	
	@Bean
	DirectChannel fixedResp() {
		return MessageChannels.direct("fixedResp")
		.get();
	}
	
}
