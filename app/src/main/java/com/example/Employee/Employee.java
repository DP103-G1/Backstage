package com.example.Employee;

public class Employee {
	private int employee_Id;
	private String account;
	private	String password;

	
	public Employee(int employee_Id, String account, String password) {
		super();
		this.employee_Id = employee_Id;
		this.account = account;
		this.password = password;
		
	}

	public int getEmployee_Id() {
		return employee_Id;
	}

	public void setEmployee_Id(int employee_Id) {
		this.employee_Id = employee_Id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	
	
}
