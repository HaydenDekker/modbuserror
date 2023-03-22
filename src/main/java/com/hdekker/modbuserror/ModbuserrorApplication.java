package com.hdekker.modbuserror;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ModbuserrorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModbuserrorApplication.class, args);
	}

}
