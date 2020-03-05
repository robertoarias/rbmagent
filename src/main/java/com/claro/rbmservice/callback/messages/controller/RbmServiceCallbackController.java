package com.claro.rbmservice.callback.messages.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.claro.rbmservice.callback.messages.bean.SecretRequest;



@RestController
public class RbmServiceCallbackController {

	private static final Logger logger = LoggerFactory.getLogger(RbmServiceCallbackController.class);
	
	private final String MESSAGE_READ = "READ";
	private final String MESSAGE_DELIVERED = "DELIVERED";
	
	
	@CrossOrigin
	@RequestMapping(value = "isAlive", method = RequestMethod.GET)
	public ResponseEntity<String> isAlive() {
		logger.trace("isAlive==>Begin");
		String resp = "OK";
		logger.trace("isAlive==>End");
		return new ResponseEntity<String>(resp, HttpStatus.OK);
	}
	
	
	
	
	@CrossOrigin
	@RequestMapping(value = "agentEvents", method = RequestMethod.POST)
	public ResponseEntity<SecretRequest> agentEvents(@RequestBody SecretRequest input) {
	//public ResponseEntity<String> agentEvents(@RequestBody String input) {
		logger.info("agentEvents==>Begin");

		logger.info("[agentEvents] Reques entrante: " + input);			
		
		//Modificar termino OK
		logger.info("agentEvents==>End");
		return ResponseEntity.ok(input);			
	}
	
	
}
