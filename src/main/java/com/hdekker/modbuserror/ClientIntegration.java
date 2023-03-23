package com.hdekker.modbuserror;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.dsl.TcpInboundGatewaySpec;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.messaging.Message;

@Configuration
public class ClientIntegration {
	
	@Bean
	DirectChannel inputGW() {
		return MessageChannels.direct("inputGW")
		.get();
	}
	
	@MessagingGateway(defaultRequestChannel = "inputGW")
	public interface MG{
		
		byte[] send(byte[] arr);
	}
	
	@Autowired
	ServerConfig sc;
	
	@Bean
	public TcpNetServerConnectionFactory connFactory() {
		ModbusServerSerializer serverSerialiser = new ModbusServerSerializer();
		
		TcpNetServerConnectionFactory svr = Tcp.netServer(sc.getPort())
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
				.get();
		
		return f;
		
	}
	
}
