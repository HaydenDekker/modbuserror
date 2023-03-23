package com.hdekker.modbuserror;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.Lifecycle;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.dsl.context.IntegrationFlowContext.IntegrationFlowRegistration;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.MessageHandler;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class ClientGatewayTest {
	
	Logger log = LoggerFactory.getLogger(ClientGatewayTest.class);
	
	@Autowired
	ServerConfig serverConfig;
	
	@Autowired
	IntegrationFlowContext flowContex;
	
	Message<String> message;
	
	@Test
	public void whenTwoChannelsAreConnected_ExpectMessageSentToTheFirstReceivedBySecond() throws InterruptedException {
		
		log.info("Starting");
		
		// This does not get registered in this manner. so won't be used
		DirectChannel channel = MessageChannels.direct("outBound")
				.get();
		
		// will create new channel named "outBound"
		StandardIntegrationFlow flow1 = IntegrationFlow.from("outBound")
				.channel("test-chan") // test channel is defined as a Bean.
				.get();
		
		MessageHandler mh = (m) -> {
			log.info("Message received.");
			message = (Message<String>) m;
		};
		
		StandardIntegrationFlow flow2 = IntegrationFlow.from("test-chan")
				.handle(mh)
				.get();
		
		IntegrationFlowRegistration flowReg1 = flowContex.registration(flow1)
			.register();
		
		flowReg1.start();
			
		if(flow1.isRunning()) log.info("Flow 1 running.");
		
		flowContex.registration(flow2)
			.register()
			.start();
		
		log.info("Sending message");
		
		flow1.getInputChannel().send(MessageBuilder.withPayload("Great.").build());
		
		Thread.sleep(1000);
		
		assertThat(message, notNullValue());
	}
	
	@Test
	public void whenAMessageChannelIsCreatedAtRunTimeButNotRegistered_ExpectExceptionWhenCallingSendItCanBeUsedInAFLow() throws InterruptedException {
		
			DirectChannel channel = MessageChannels.direct("outBound")
					.get();
			
			MessageHandler mh = (m) -> {
				log.info("Message received.");
				message = (Message<String>) m;
			};
			
			StandardIntegrationFlow flow1 = IntegrationFlow.from("outBound")
					.handle(mh)
					.get();
			
			IntegrationFlowRegistration flowReg1 = flowContex.registration(flow1)
				.register();
			
			flowReg1.start();
				
			if(flow1.isRunning()) log.info("Flow 1 running.");
			
			log.info("Sending message");
			
			assertThrows(MessageDeliveryException.class, ()->{
				channel.send(MessageBuilder.withPayload("Great.").build());
			});

		
	}
	
	@Test
	public void whenAMessageChannelIsCreatedAtRunTimeAndRegistered_ExpectSendIsAllowed() throws InterruptedException {
		
			// name "outbound" already taken by preceeding tests.
			DirectChannel channel = MessageChannels.direct("outBound2")
					.get();
			
			StandardIntegrationFlow cf = IntegrationFlow.from(channel)
				.bridge() // bridge needed to meet int flow rules.
				.get();
			
			flowContex.registration(cf)
				.register()
				.start();
			
			
			MessageHandler mh = (m) -> {
				log.info("Message received.");
				message = (Message<String>) m;
			};
			
			StandardIntegrationFlow flow1 = IntegrationFlow.from("outBound2")
					.handle(mh)
					.get();
			
			IntegrationFlowRegistration flowReg1 = flowContex.registration(flow1)
				.register();
			
			flowReg1.start();
				
			if(flow1.isRunning()) log.info("Flow 1 running.");
			
			log.info("Sending message");
			
			channel.send(MessageBuilder.withPayload("Great.").build());

			Thread.sleep(1000);
			
			assertThat(message, notNullValue());
		
	}
	
	
}
