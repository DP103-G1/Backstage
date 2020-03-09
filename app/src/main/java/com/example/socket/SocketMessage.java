package com.example.socket;

import com.example.main.MenuDetail;

import java.io.Serializable;
import java.util.Optional;


public class SocketMessage implements Serializable {
	private String type;//{"menuDetail", "seat", "service"}
	private String receiver;
	private MenuDetail menuDetail;

	public SocketMessage(String type, String receiver, MenuDetail menuDetail) {
		this.type = type;
		this.receiver = receiver;
		this.menuDetail = menuDetail;
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

	public Optional<MenuDetail> getMenuDetail() {
		return Optional.ofNullable(menuDetail);
	}
	
	public void setMenuDetail(MenuDetail menuDetail) {
		this.menuDetail = menuDetail;
	}
}
