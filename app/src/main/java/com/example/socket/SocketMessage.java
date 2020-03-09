package com.example.socket;

import java.io.Serializable;

public class SocketMessage implements Serializable {
	private String type;//{"menuDetail", "seat", "service"}
	private String receiver;
	private String message;

	public SocketMessage(String type, String receiver, String message) {
		this.type = type;
		this.receiver = receiver;
		this.message = message;
	}

	public SocketMessage(String type, String receiver) {
		this(type, receiver, null);
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
