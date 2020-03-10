package com.example.manager.table;


import java.io.Serializable;

public class Table implements Serializable {
	
	private int tableId;
	private String tablePeople;
	private int ORD_ID;
	private boolean status;
	
	
	
	public Table(int tableId, String tablePeople) {
		super();
		this.tableId = tableId;
		this.tablePeople = tablePeople;
	}

	public Table(int tableId, String tablePeople, int ORD_ID, boolean status) {
		super();
		this.tableId = tableId;
		this.tablePeople = tablePeople;
		this.ORD_ID = ORD_ID;
		this.status = status;
	}

	public Table(int tableId, String tablePeople, int ORD_ID) {
		this(tableId, tablePeople, ORD_ID, false);
	}

	public Table(int tableId, int ORD_ID) {
		super();
		this.tableId = tableId;
		this.ORD_ID = ORD_ID;
	}
	
	@Override
	public String toString() {
		String text = "Table Id" + tableId + "\nTable People" + tablePeople ;
		return text;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Table)) {
			return false;
		}
		Table table = (Table) obj;
		return table.getTableId() == this.getTableId();
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

	public void setORD_ID(int ord_id) {
		ORD_ID = ord_id;
	}
	
	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
}
