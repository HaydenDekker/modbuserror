package com.hdekker.modbuserror;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@ConfigurationProperties("modbus.server")
public class ServerConfig {
	
	Logger log = LoggerFactory.getLogger(ServerConfig.class);

	public Integer port;
	public String exception;
	
	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}	
	
	@PostConstruct
	public void log() {
		log.info("port " + port);
		log.info("exc " + exception);
	}
	
}
