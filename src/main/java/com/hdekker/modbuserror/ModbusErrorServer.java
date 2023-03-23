package com.hdekker.modbuserror;

import java.util.HexFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class ModbusErrorServer {

	Logger log = LoggerFactory.getLogger(ModbusErrorServer.class);
	
	@Autowired
	ServerConfig serverConfig;
	
	static byte[] resp = {0x00, 0x00, 0x00, 0x00, 0x00, 0x03, 0x01, (byte) 0x84, 0x0c};
	
	public Message<?> fixedResponse(Message<?> msg) {
		
		Object id = msg.getHeaders()
			.get("ip_connectionId");
		
		log.info((String) id);
		
		byte[] request = (byte[]) msg.getPayload();
		
		 StringBuilder msgStr = new StringBuilder();
         for (byte b: request ) {
             msgStr.append(String.format("%02x ", b));
         }
        log.info("Serialized request message: " + msgStr.toString());
        
		String hexException = serverConfig.getException();
		byte[] hex = HexFormat.of().parseHex(hexException);
		
		log.info("Exception code is " + hex[0]);
		
		// mbas
		for (int i = 0; i < 7; i++) {
			resp[i] = request[i];
		}
		
		resp[5] = 0x03; // constant error
		
		Message<byte[]> res = MessageBuilder.withPayload(resp)
				.copyHeaders(msg.getHeaders())
				.build();
		
		id = res.getHeaders()
				.get("ip_connectionId");
			
		log.info((String) id);
		
		return res;
	}
	
}
