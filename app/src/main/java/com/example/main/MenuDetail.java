package com.example.main;

public class MenuDetail {

	private int ORD_ID;
	private int BK_ID;
	private String MENU_ID;
	private int TABLE_ID;
	private String FOOD_NAME;
	private int FOOD_AMOUNT;
	private boolean FOOD_ARRIVAL;
	private int TOTAL;
	private int ORD_TOTAL;
	private boolean FOOD_STATUS;
	private boolean ORD_BILL;
	private int memberId;

	public MenuDetail(int ORD_ID, String MENU_ID, int FOOD_AMOUNT, boolean FOOD_ARRIVAL, int TOTAL,
			boolean FOOD_STATUS) {
		this.ORD_ID = ORD_ID;
		this.MENU_ID = MENU_ID;
		this.FOOD_AMOUNT = FOOD_AMOUNT;
		this.FOOD_ARRIVAL = FOOD_ARRIVAL;
		this.TOTAL = TOTAL;
		this.FOOD_STATUS = FOOD_STATUS;
	}

	public MenuDetail(int ORD_ID, String MENU_ID, String FOOD_NAME, int FOOD_AMOUNT, boolean FOOD_ARRIVAL, int TOTAL, boolean FOOD_STATUS) {
		this.ORD_ID = ORD_ID;
		this.MENU_ID = MENU_ID;
		this.FOOD_NAME = FOOD_NAME;
		this.FOOD_AMOUNT = FOOD_AMOUNT;
		this.FOOD_ARRIVAL = FOOD_ARRIVAL;
		this.TOTAL = TOTAL;
		this.FOOD_STATUS = FOOD_STATUS;
	}

	public MenuDetail(int ORD_ID, String MENU_ID, int FOOD_AMOUNT, int TOTAL, boolean FOOD_STATUS) {
		this.ORD_ID = ORD_ID;
		this.MENU_ID = MENU_ID;
		this.FOOD_AMOUNT = FOOD_AMOUNT;
		this.TOTAL = TOTAL;
		this.FOOD_STATUS = FOOD_STATUS;
	}

	public MenuDetail(String MENU_ID, int FOOD_AMOUNT, int TOTAL) {
		this(0, MENU_ID, FOOD_AMOUNT, false, TOTAL, false);
	}

	public MenuDetail(int ORD_ID, String MENU_ID, int TABLE_ID, String FOOD_NAME, int FOOD_AMOUNT, boolean FOOD_ARRIVAL, int TOTAL, boolean FOOD_STATUS) {
		this.ORD_ID = ORD_ID;
		this.MENU_ID = MENU_ID;
		this.TABLE_ID = TABLE_ID;
		this.FOOD_NAME = FOOD_NAME;
		this.FOOD_AMOUNT = FOOD_AMOUNT;
		this.FOOD_ARRIVAL = FOOD_ARRIVAL;
		this.TOTAL = TOTAL;
		this.FOOD_STATUS = FOOD_STATUS;
	}

	public MenuDetail(int ORD_ID, String MENU_ID, int TABLE_ID, String FOOD_NAME, int FOOD_AMOUNT, boolean FOOD_ARRIVAL, int TOTAL, boolean FOOD_STATUS, int memberId) {
		this.ORD_ID = ORD_ID;
		this.MENU_ID = MENU_ID;
		this.TABLE_ID = TABLE_ID;
		this.FOOD_NAME = FOOD_NAME;
		this.FOOD_AMOUNT = FOOD_AMOUNT;
		this.FOOD_ARRIVAL = FOOD_ARRIVAL;
		this.TOTAL = TOTAL;
		this.FOOD_STATUS = FOOD_STATUS;
		this.memberId = memberId;
	}

	public MenuDetail(int ORD_ID, String MENU_ID, int TABLE_ID, String FOOD_NAME, int FOOD_AMOUNT, boolean FOOD_ARRIVAL, int TOTAL, int ORD_TOTAL, boolean FOOD_STATUS, boolean ORD_BILL) {
		this.ORD_ID = ORD_ID;
		this.MENU_ID = MENU_ID;
		this.TABLE_ID = TABLE_ID;
		this.FOOD_NAME = FOOD_NAME;
		this.FOOD_AMOUNT = FOOD_AMOUNT;
		this.FOOD_ARRIVAL = FOOD_ARRIVAL;
		this.TOTAL = TOTAL;
		this.ORD_TOTAL = ORD_TOTAL;
		this.FOOD_STATUS = FOOD_STATUS;
		this.setORD_BILL(ORD_BILL);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((MENU_ID == null) ? 0 : MENU_ID.hashCode());
		result = prime * result + ORD_ID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MenuDetail other = (MenuDetail) obj;
		if (MENU_ID == null) {
			if (other.MENU_ID != null)
				return false;
		} else if (!MENU_ID.equals(other.MENU_ID))
			return false;
		if (ORD_ID != other.ORD_ID)
			return false;
		return true;
	}


	public int getORD_ID() {
		return ORD_ID;
	}

	public void setORD_ID(int ord_id) {
		ORD_ID = ord_id;
	}

	public String getMENU_ID() {
		return MENU_ID;
	}

	public void setMENU_ID(String menu_id) {
		MENU_ID = menu_id;
	}

	public int getFOOD_AMOUNT() {
		return FOOD_AMOUNT;
	}

	public void setFOOD_AMOUNT(int food_amount) {
		FOOD_AMOUNT = food_amount;
	}

	public boolean isFOOD_ARRIVAL() {
		return FOOD_ARRIVAL;
	}

	public void setFOOD_ARRIVAL(boolean food_arrival) {
		FOOD_ARRIVAL = food_arrival;
	}

	public int getTOTAL() {
		return TOTAL;
	}

	public void setTOTAL(int total) {
		TOTAL = total;
	}

	public boolean isFOOD_STATUS() {
		return FOOD_STATUS;
	}

	public void setFOOD_STATUS(boolean food_status) {
		FOOD_STATUS = food_status;
	}

	public int getBK_ID() {
		return BK_ID;
	}

	public void setBK_ID(int bk_id) {
		BK_ID = bk_id;
	}

	public String getFOOD_NAME() {
		return FOOD_NAME;
	}

	public void setFOOD_NAME(String food_name) {
		FOOD_NAME = food_name;
	}

	public int getTABLE_ID() {
		return TABLE_ID;
	}

	public void setTABLE_ID(int table_id) {
		TABLE_ID = table_id;
	}

	public int getORD_TOTAL() {
		return ORD_TOTAL;
	}

	public void setORD_TOTAL(int oRD_TOTAL) {
		ORD_TOTAL = oRD_TOTAL;
	}

	public boolean isORD_BILL() {
		return ORD_BILL;
	}

	public void setORD_BILL(boolean oRD_BILL) {
		ORD_BILL = oRD_BILL;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}


}
