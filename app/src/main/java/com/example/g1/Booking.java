package com.example.g1;

import com.example.manager.member.Member;

import java.io.Serializable;
import java.util.Date;

public class Booking implements Serializable {
	private int bkId;
	private Member member;
	private int tableId;
	private String bkTime;
	private Date bkDate;
	private String bkChild;
	private String bkAdult;
	private String bkPhone;
	private int status;
	private int state;

	public Booking(int bkId, Member member, int tableId,
                   String bkTime, Date bkDate, String bkChild, String bkAdult,
                   String bkPhone, int status) {
		super();
		this.bkId = bkId;
		this.member = member;
		this.tableId = tableId;
		this.bkTime = bkTime;
		this.bkDate = bkDate;
		this.bkChild = bkChild;
		this.bkAdult = bkAdult;
		this.bkPhone = bkPhone;
		this.status = status;
	}




//
//	@Override
//	public String toString() {
//		return "Booking [bkId=" + bkId + ", memberId=" + memberId + ", tableId=" + tableId + ", bkTime=" + bkTime
//				+ ", bkDate=" + bkDate + ", bkChild=" + bkChild + ", bkAdult=" + bkAdult + ", bkPhone=" + bkPhone + "]";
//	}



	public int getBkId() {
		return bkId;
	}

	public void setBkId(int bkId) {
		this.bkId = bkId;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public String getBkTime() {
		return bkTime;
	}

	public void setBkTime(String bkTime) {
		this.bkTime = bkTime;
	}

	public Date getBkDate() {
		return bkDate;
	}

	public void setBkDate(Date bkDate) {
		this.bkDate = bkDate;
	}

	public String getBkChild() {
		return bkChild;
	}

	public void setBkChild(String bkChild) {
		this.bkChild = bkChild;
	}

	public String getBkAdult() {
		return bkAdult;
	}

	public void setBkAdult(String bkAdult) {
		this.bkAdult = bkAdult;
	}

	public String getBkPhone() {
		return bkPhone;
	}

	public void setBkPhone(String bkPhone) {
		this.bkPhone = bkPhone;
	}


	public int getstatus() {
		return status;
	}

	public void setstatus(int status) {
		status = status;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
}
