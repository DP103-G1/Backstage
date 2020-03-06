package com.example.g1;

import java.io.Serializable;
import java.util.Date;

public class Booking implements Serializable{

	private int memberId;
	private int tableId;
	private String bkTime;
	private Date bkDate;
	private String bkChild;
	private String bkAdult;
	private String bkPhone;
	private int bkId;
	private int bkStatus;



	public Booking(int memberId, int tableId, String bkTime, Date bkDate, String bkChild, String bkAdult,String bkPhone,int bkStatus) {
		super();
		this.memberId = memberId;
		this.tableId = tableId;
		this.bkTime = bkTime;
		this.bkDate = bkDate;
		this.bkChild = bkChild;
		this.bkAdult = bkAdult;
		this.bkPhone = bkPhone;
		this.bkStatus = bkStatus;
	}

	public Booking(int memberId, int tableId, String bkTime, Date bkDate, String bkChild, String bkAdult, String bkPhone,int bkId,int bkStatus) {
		super();
		this.memberId = memberId;
		this.tableId = tableId;
		this.bkTime = bkTime;
		this.bkDate = bkDate;
		this.bkChild = bkChild;
		this.bkAdult = bkAdult;
		this.bkPhone = bkPhone;
		this.bkId = bkId;
		this.bkStatus = bkStatus;

	}

	public Booking( int tableId, String bkTime, Date bkDate, String bkChild, String bkAdult,String bkPhone,int bkId,int bkStatus) {
		super();
		this.tableId = tableId;
		this.bkTime = bkTime;
		this.bkDate = bkDate;
		this.bkChild = bkChild;
		this.bkAdult = bkAdult;
		this.bkPhone = bkPhone;
		this.bkId=bkId;
		this.bkStatus = bkStatus;
	}



	@Override
	public String toString() {
		return "Booking [bkId=" + bkId + ", memberId=" + memberId + ", tableId=" + tableId + ", bkTime=" + bkTime
				+ ", bkDate=" + bkDate + ", bkChild=" + bkChild + ", bkAdult=" + bkAdult + ", bkPhone=" + bkPhone + "]";
	}

	public int getStatus() {
		return bkStatus;
	}

	public void setStatus(int status) {
		this.bkStatus = status;
	}

	public int getBkId() {
		return bkId;
	}

	public void setBkId(int bkId) {
		this.bkId = bkId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
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


}
