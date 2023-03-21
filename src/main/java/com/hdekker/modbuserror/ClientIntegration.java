package com.hdekker.modbuserror;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.MessageProcessorSpec;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.dsl.TcpInboundGatewaySpec;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.messaging.Message;

@EnableIntegration
@Configuration
public class ClientIntegration {


	@Bean
	DirectChannel outputGW() {
		return MessageChannels.direct("outputGW")
		.get();
	}
	
	@Bean
	DirectChannel inputGW() {
		return MessageChannels.direct("inputGW")
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
	
	@MessagingGateway(defaultRequestChannel = "inputGW",
			defaultReplyChannel = "outputGW")
	public interface MG{
		
		byte[] send(byte[] arr);
	}
	
	@Autowired
	ServerConfig serverConfig;
	private TcpInboundGatewaySpec gws;
	
	@Bean
	public TcpNetServerConnectionFactory connFactory() {
		ModbusServerSerializer serverSerialiser = new ModbusServerSerializer();
		
		TcpNetServerConnectionFactory svr = Tcp.netServer(serverConfig.getPort())
			.deserializer(serverSerialiser)
			.serializer(serverSerialiser)
			.backlog(30)
			.get();
		
		return svr;
	
	}
	
	@Autowired
	ModbusErrorServer er;
	
	@Bean
	IntegrationFlow server() {
		
		StandardIntegrationFlow f = IntegrationFlow.from(
					Tcp.inboundGateway(
							connFactory())
					)
				.transform(Message.class, m->er.fixedResponse(m))
				.handle(Tcp.outboundAdapter(connFactory()))
				.get();
		
		return f;
		
	}
	
//	@Bean
//	IntegrationFlow serverReply() {
//		
//		return IntegrationFlow.from(Tcp.outboundGateway(connFactory())
//				
//		
//	}
	
}
