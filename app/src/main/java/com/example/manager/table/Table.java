package com.example.manager.table;


import java.io.Serializable;

public class Table implements Serializable {
	
	private int tableId;
	private String tablePeople;
	private int ORD_ID;

	public Table(int tableId, String tablePeople) {
		super();
		this.tableId = tableId;
		this.tablePeople = tablePeople;
	}

	public Table(int tableId, String tablePeople, int ORD_ID) {
		super();
		this.tableId = tableId;
		this.tablePeople = tablePeople;
		this.ORD_ID = ORD_ID;

	}
	public void Table(String tablePeople, int tableId) {
		this.tableId = tableId;
		this.tablePeople = tablePeople;
	}
	
	@Override
	public String toString() {
		String text = "Table Id" + tableId + "\nTable People" + tablePeople ;
		return text;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.tableId == tableId;
	}
	
	@Override
	public int hashCode() {
		return tableId;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public String getTablePeople() {
		return tablePeople;
	}

	public void setTablePeople(String tablePeople) {
		this.tablePeople = tablePeople;
	}

	public int getORD_ID() {
		return ORD_ID;
	}

	public void setORD_ID(int ORD_ID) {
		this.ORD_ID = ORD_ID;
	}


}
