package com.hdekker.modbuserror;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;

import com.hdekker.modbuserror.ClientIntegration.MG;

@Configuration
public class ModbusClient {
	
	@Autowired
	ServerConfig config;
	
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
