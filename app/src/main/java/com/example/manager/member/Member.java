package com.example.manager.member;

import java.io.Serializable;

public class Member implements Serializable {

	private int member_Id;
	private String account;
	private String password;
	private String name;
	private String phone;
	private int state;


	public Member(int member_Id, String account, String password, String name, String phone, int state) {
		this.member_Id = member_Id;
		this.account = account;
		this.password = password;
		this.name = name;
		this.phone = phone;
		this.state = state;
	}

	public Member(int member_Id, int state) {
		this.member_Id = member_Id;
		this.state = state;
	}
	
	public Member(String account, String password, String name, String phone, int state) {
		this(0, account, password, name, phone, state);
	}

	public int getmember_Id() {
		return member_Id;
	}

	public void setmember_Id(int member_Id) {
		this.member_Id = member_Id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getpassword() {
		return password;
	}

	public void setpassword(String password) {
		this.password = password;
	}

	public String getname() {
		return name;
	}

	public void setname(String name) {
		this.name = name;
	}
	
	public String getphone() {
		return phone;
	}

	public void setphone(String phone) {
		this.phone = phone;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
