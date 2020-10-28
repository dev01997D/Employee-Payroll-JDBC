package com.blz.employeepayrollsql.model;

import java.time.LocalDate;

public class Contact {
	public int id;
	public double salary;
	public String name;
	public LocalDate startDate;

	// connstructor
	public Contact(int id, double salary, String name) {
		this.id = id;
		this.salary = salary;
		this.name = name;
	}

//	 connstructor
	public Contact(int id, double salary, String name, LocalDate startDate) {
		this(id, salary, name);
		this.startDate = startDate;
	}

	public Contact() {
	}

	@Override
	public String toString() {
		return "EmployeePayrollData [id=" + id + ", salary=" + salary + ", name=" + name + "]";
	}
}
