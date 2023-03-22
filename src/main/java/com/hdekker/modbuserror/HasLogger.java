package com.hdekker.modbuserror;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

interface HasLogger {

	public default Logger logger(Class<?> clazz) {
		return LoggerFactory.getLogger(clazz);
	}
	
}
