/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.blz.employeepayrollsql.controller;

import java.time.LocalDate;
import java.util.List;
import com.blz.employeepayrollsql.model.Contact;
import com.blz.employeepayrollsql.model.CustomPayrollException;
import com.blz.employeepayrollsql.model.EmpPayrollDBService;

public class Emp_Payroll_JDBC_Main {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO;
	}

	private List<Contact> employeePayrollList;
	private static EmpPayrollDBService employeePayrollDBServicebj;

	// Creating Singleton object of EmpPayrollDBService
	public Emp_Payroll_JDBC_Main() {
		employeePayrollDBServicebj = EmpPayrollDBService.getInstance();
	}

	public Emp_Payroll_JDBC_Main(List<Contact> empPayrollList) {
		this();
		this.employeePayrollList = empPayrollList;
	}

	// Reading all the employee data from table employee_payroll present in DB
	public List<Contact> readEmployeePayrollDatabase(IOService ioService) throws CustomPayrollException {
		if (ioService.equals(IOService.DB_IO)) {
			this.employeePayrollList = employeePayrollDBServicebj.readData();
		}
		return this.employeePayrollList;
	}

	// Reading all the employee from employee_payroll DB for start in given date
	// range
	public List<Contact> readEmployeePayrollForGivenDateRange(IOService ioService, LocalDate startDate,
			LocalDate endDate) throws CustomPayrollException {
		if (ioService.equals(IOService.DB_IO)) {
			this.employeePayrollList = employeePayrollDBServicebj.getEmployeeForDateRange(startDate, endDate);
		}
		return employeePayrollList;
	}

	// updating salary for employee if salary got modified in database then in
	// memory
	public void updateEmployeeSalaryInDBThenInList(String name, double salary) throws CustomPayrollException {
		int result = employeePayrollDBServicebj.updateEmployeeData(name, salary);
		if (result == 0) {
			return;
		}
		Contact contact = this.getEmployeePayrollData(name);
		if (contact != null)
			contact.salary = salary;
	}

	// Filter the employee with given name from the employee list
	private Contact getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream().filter(contactItem -> contactItem.name.equals(name)).findFirst()
				.orElse(null);
	}

	// Checking if two employee are equal or not
	public boolean checkEmployeePayrollListSyncWithDB(String name) throws CustomPayrollException {
		List<Contact> empPayrollDataList = employeePayrollDBServicebj.getEmployeePayrolldata(name);
		return empPayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}
}
