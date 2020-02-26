package com.example.g1;

public class MenuDetail {

	private int ORD_ID;
	private int BK_ID;
	private int MEMBER_ID;
	private int TABLE_ID;
	private String MENU_ID;
	private String FOOD_NAME;
	private int FOOD_AMOUNT;
	private boolean FOOD_ARRIVAL;
	private int TOTAL;
	private boolean FOOD_STATUS;

	public MenuDetail(int ORD_ID, String MENU_ID, int FOOD_AMOUNT, boolean FOOD_ARRIVAL, int TOTAL,
                      boolean FOOD_STATUS) {
		this.ORD_ID = ORD_ID;
		this.MENU_ID = MENU_ID;
		this.FOOD_AMOUNT = FOOD_AMOUNT;
		this.FOOD_ARRIVAL = FOOD_ARRIVAL;
		this.TOTAL = TOTAL;
		this.FOOD_STATUS = FOOD_STATUS;
	}

	public MenuDetail(String MENU_ID, int FOOD_AMOUNT, int TOTAL) {
		this(0, MENU_ID, FOOD_AMOUNT, false, TOTAL, false);
	}

	public MenuDetail(int ORD_ID, int MEMBER_ID, String MENU_ID, int FOOD_AMOUNT, boolean FOOD_ARRIVAL, int TOTAL,
                      boolean FOOD_STATUS) {
		this.ORD_ID = ORD_ID;
		this.MEMBER_ID = MEMBER_ID;
		this.MENU_ID = MENU_ID;
		this.FOOD_AMOUNT = FOOD_AMOUNT;
		this.FOOD_ARRIVAL = FOOD_ARRIVAL;
		this.TOTAL = TOTAL;
		this.FOOD_STATUS = FOOD_STATUS;
	}

	public MenuDetail(int MEMBER_ID, String MENU_ID, int FOOD_AMOUNT, int TOTAL) {
		this(0,MEMBER_ID, MENU_ID, FOOD_AMOUNT, false, TOTAL, false);
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

	public String getFOOD_NAME() {
		return FOOD_NAME;
	}

	public void setFOOD_NAME(String FOOD_NAME) {
		this.FOOD_NAME = FOOD_NAME;
	}

	public int getTABLE_ID() {
		return TABLE_ID;
	}

	public void setTABLE_ID(int TABLE_ID) {
		this.TABLE_ID = TABLE_ID;
	}

	public int getMEMBER_ID() {
		return MEMBER_ID;
	}

	public void setMEMBER_ID(int MEMBER_ID) {
		this.MEMBER_ID = MEMBER_ID;
	}
}
