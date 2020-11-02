package com.blz.employeepayrollsql.model;

import java.time.LocalDate;
import java.util.Arrays;

public class Contact {

	public int id;
	public double salary;
	public String name;
	public String address;
	public LocalDate startDate;
	public String gender;
	public String companyName;
	public int companyId;
	public String department[];
	public int deptId;
	public String deptName;
	public String is_active;

	// Constructor
	public Contact(int id, double salary, String name) {
		this.id = id;
		this.salary = salary;
		this.name = name;
	}

	// Constructor
	public Contact(int id, double salary, String name, LocalDate startDate) {
		this(id, salary, name);
		this.startDate = startDate;
	}

	public Contact(int id, String name, String gender, double salary, LocalDate startDate) {
		this(id, salary, name, startDate);
		this.gender = gender;
	}

	public Contact(int id, int companyId, String name, String address, String gender, String companyName, int deptId,
			String deptName, double salary, LocalDate startDate, String[] department) {
		this(id, name, gender, salary, startDate);
		this.companyId = companyId;
		this.address = address;
		this.companyName = companyName;
		this.deptId = deptId;
		this.deptName = deptName;
		this.department = department;
	}

	public Contact(int employeeId, int company_id, String name, String address, String gender) {
		this.id=employeeId;
		this.companyId=company_id;
		this.name=name;
		this.address=address;
		this.gender=gender;
	}

	public Contact(int emp_id, int companyId, String name, String address, String gender, String is_active) {
		this(emp_id, companyId, name, address, gender);
		this.is_active=is_active;
	}

	// Override Equals method
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contact other = (Contact) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

	public void printDepartments() {
		String departments[] = this.getDepartment();
		for (String s : departments) {
			System.out.println("id: " + this.getId() + ":" + s);
		}
	}

	private int getId() {
		return id;
	}

	private String[] getDepartment() {
		return department;
	}

	@Override
	public String toString() {
		return "Contact [id=" + id + ", salary=" + salary + ", name=" + name + ", address=" + address + ", startDate="
				+ startDate + ", gender=" + gender + ", companyName=" + companyName + ", companyId=" + companyId
				+ ", department=" + Arrays.toString(department) + ", deptId=" + deptId + ", deptName=" + deptName
				+ ", is_active=" + is_active + "]";
	}
	
}
