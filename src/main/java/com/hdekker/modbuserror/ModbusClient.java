package com.hdekker.modbuserror;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.dsl.TcpNetClientConnectionFactorySpec;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.hdekker.modbuserror.ClientIntegration.MG;

@Component
public class ModbusClient {
	
	@Autowired
	ServerConfig config;
	
	@Autowired
	private IntegrationFlowContext flowContext;
	
	public ModbusClient(ServerConfig config,
			IntegrationFlowContext flowContext) {
			
		this.config = config;
		this.flowContext = flowContext;
		
	}
	
	@Bean
	IntegrationFlow client() {
		
		ModbusClientSerializer cl = new ModbusClientSerializer();
		
		TcpNetClientConnectionFactory client = Tcp.netClient("localhost", config.getPort())
			.deserializer(cl)
			.serializer(cl)
			.get();
		
		TcpOutboundGateway og = Tcp.outboundGateway(
				client)
				.get();

		StandardIntegrationFlow flow = IntegrationFlow.
				from("inputGW")
				.handle(
					og
					)
				.get();
		
		return flow;
		
	}
	
	@Autowired
	MG mg;

	public byte[] request(byte[] req) {
		return mg.send(req);
		
		
	}
	
}
