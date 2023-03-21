package com.hdekker.modbuserror;

import java.util.HexFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.dsl.TcpInboundGatewaySpec;
import org.springframework.integration.ip.dsl.TcpNetServerConnectionFactorySpec;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.TcpCodecs;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class ModbusErrorServer {

	Logger log = LoggerFactory.getLogger(ModbusErrorServer.class);
	
	@Autowired
	ServerConfig serverConfig;
	
	public ModbusErrorServer(ServerConfig serverConfig,
			IntegrationFlowContext fc) {

		this.serverConfig = serverConfig;
		log.info("Creating server.");
		
		
	}
	
	static byte[] resp = {0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, (byte) 0x84, 0x0c};
	
	public Message<?> fixedResponse(Message<?> msg) {
		
		Object id = msg.getHeaders()
			.get("ip_connectionId");
		
		log.info((String) id);
		
		 StringBuilder msgStr = new StringBuilder();
         for (byte b: (byte[]) msg.getPayload()) {
             msgStr.append(String.format("%02x ", b));
         }
        log.info("Serialized request message: " + msgStr.toString());

         
		String hexException = serverConfig.getException();
		byte[] hex = HexFormat.of().parseHex(hexException);
		
		log.info("Exception code is " + hex[0]);
		
		Message<byte[]> res = MessageBuilder.withPayload(resp)
				.copyHeaders(msg.getHeaders())
				.build();
		
		id = res.getHeaders()
				.get("ip_connectionId");
			
		log.info((String) id);
		
		return res;
	}
	
}