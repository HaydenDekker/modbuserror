package com.hdekker.modbuserror;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.modbuserror.client.ModbusClient;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;



@SpringBootTest
public class ErrorResponseTest {

	Logger log = LoggerFactory.getLogger(ErrorResponseTest.class);
	
	@Autowired
	ServerConfig serverConfig;
	
	public static byte[] eastRequestPg1 = {0x04, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04, 0x75, 0x31, 0x00, 0x79};

	@Autowired
	ModbusClient client;
	
	@Test
	public void whenServerReceivesRequest_ExpectErrorWithExceptionCalled() {
		
		log.info("Connecting to server.");
		
		byte[] resp = client.request(eastRequestPg1);
		
		StringBuilder msgStr = new StringBuilder();
        for (byte b: resp) {
            msgStr.append(String.format("%02x ", b));
        }
        log.info("Serialized request message: " + msgStr.toString());

        byte function = resp[7];
        byte exception = resp[8];
        
		assertThat(exception, equalTo((byte) 0x0c));
		
		
	}
	
}
