package com.blz.employeepayrollsql.model;

import java.time.LocalDate;

public class Contact {

	public int id;
	public double salary;
	public String name;
	public LocalDate startDate;

	// Constructor
	public Contact(int id, double salary, String name) {
		this.id = id;
		this.salary = salary;
		this.name = name;
	}

	//	 Constructor
	public Contact(int id, double salary, String name, LocalDate startDate) {
		this(id, salary, name);
		this.startDate = startDate;
	}

	public Contact() {
	}

	//Override Equals method
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

	@Override
	public String toString() {
		return "EmployeePayrollData [id=" + id + ", salary=" + salary + ", name=" + name + "]";
	}
}
