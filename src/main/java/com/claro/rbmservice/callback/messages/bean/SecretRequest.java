package com.claro.rbmservice.callback.messages.bean;

public class SecretRequest {
	public String getClientToken() {
		return clientToken;
	}
	public void setClientToken(String clientToken) {
		this.clientToken = clientToken;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	private String clientToken;
	private String secret;
	
}
