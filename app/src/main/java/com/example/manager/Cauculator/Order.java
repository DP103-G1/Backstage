package com.example.manager.Cauculator;


import com.example.main.MenuDetail;

import java.util.Date;
import java.util.List;

public class Order {

	private int ORD_ID;
	private int MEMBER_ID;
	private int BK_ID;
	private int ORD_TOTAL;
	private boolean ORD_STATUS;
	private boolean ORD_BILL;
	private Date ORD_TIME;
	private List<MenuDetail> menuDetails;
	
	public Order(int ORD_ID, int MEMBER_ID, int BK_ID,  int ORD_TOTAL,
			boolean ORD_STATUS, boolean ORD_BILL, Date ORD_TIME) {
		this.ORD_ID = ORD_ID;
		this.MEMBER_ID = MEMBER_ID;
		this.BK_ID = BK_ID;
		this.ORD_TOTAL = ORD_TOTAL;
		this.ORD_STATUS = ORD_STATUS;
		this.ORD_BILL = ORD_BILL;
		this.ORD_TIME = ORD_TIME;
	}
	
	public Order(int ORD_ID, int BK_ID, int ORD_TOTAL,
			boolean ORD_STATUS, boolean ORD_BILL, Date ORD_TIME) {
		this.ORD_ID = ORD_ID;
		this.BK_ID = BK_ID;
		this.ORD_TOTAL = ORD_TOTAL;
		this.ORD_STATUS = ORD_STATUS;
		this.ORD_BILL = ORD_BILL;
		this.ORD_TIME = ORD_TIME;
	}

	public Order(int ORD_ID, int MEMBER_ID, int BK_ID, int ORD_TOTAL,
			boolean ORD_STATUS, boolean ORD_BILL,Date ORD_TIME, List<MenuDetail> menuDetails) {
		this.ORD_ID = ORD_ID;
		this.MEMBER_ID = MEMBER_ID;
		this.BK_ID = BK_ID;
		this.ORD_TOTAL = ORD_TOTAL;
		this.ORD_STATUS = ORD_STATUS;
		this.ORD_BILL = ORD_BILL;
		this.ORD_TIME = ORD_TIME;
		this.menuDetails = menuDetails;
	}
	
	public int getORD_ID() {
		return ORD_ID;
	}

	public void setORD_ID(int ord_id) {
		ORD_ID = ord_id;
	}

	public int getMEMBER_ID() {
		return MEMBER_ID;
	}

	public void setMEMBER_ID(int member_id) {
		MEMBER_ID = member_id;
	}

	public int getORD_TOTAL() {
		return ORD_TOTAL;
	}

	public void setORD_TOTAL(int ord_total) {
		ORD_TOTAL = ord_total;
	}

	public boolean isORD_STATUS() {
		return ORD_STATUS;
	}

	public void setORD_STATUS(boolean ord_status) {
		ORD_STATUS = ord_status;
	}

	public boolean isORD_BILL() {
		return ORD_BILL;
	}

	public void setORD_BILL(boolean ord_bill) {
		ORD_BILL = ord_bill;
	}
	
	public List<MenuDetail> getMenuDetails() {
		return menuDetails;
	}

	public void setMenuDetails(List<MenuDetail> menuDetails) {
		this.menuDetails = menuDetails;
	}

	public int getBK_ID() {
		return BK_ID;
	}

	public void setBK_ID(int bK_ID) {
		BK_ID = bK_ID;
	}

	public Date getORD_TIME() {return ORD_TIME;}

	public void setORD_TIME(Date ord_time) {ORD_TIME = ord_time; }
}
