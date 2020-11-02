/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.blz.employeepayrollsql.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.blz.employeepayrollsql.model.Contact;
import com.blz.employeepayrollsql.model.CustomPayrollException;
import com.blz.employeepayrollsql.model.EmpPayrollDBService;
import com.blz.employeepayrollsql.model.EmpPayrollDBServiceNormalised;

public class Emp_Payroll_JDBC_Main {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO;
	}

	private List<Contact> employeePayrollList;
	private Map<String, Double> employeePayrollMap;
	private EmpPayrollDBService employeePayrollDBServicebj;
	private EmpPayrollDBServiceNormalised normalisedDBServiceObj;

	// Creating Singleton object of EmpPayrollDBService
	public Emp_Payroll_JDBC_Main() {
		employeePayrollDBServicebj = EmpPayrollDBService.getInstance();
		normalisedDBServiceObj = EmpPayrollDBServiceNormalised.getInstance();
	}

	public Emp_Payroll_JDBC_Main(List<Contact> empPayrollList) {
		this();
		this.employeePayrollList = empPayrollList;
	}

	public Emp_Payroll_JDBC_Main(Map<String, Double> employeePayrollMap) {
		this();
		this.employeePayrollMap = employeePayrollMap;
	}

	// Reading all the employee data from table employee_payroll present in DB
	public List<Contact> readEmployeePayrollDatabase(IOService ioService) throws CustomPayrollException {
		if (ioService.equals(IOService.DB_IO)) {
			this.employeePayrollList = normalisedDBServiceObj.readData();
		}
		return this.employeePayrollList;
	}

	// updating salary for employee in DB and if salary got modified in database
	// then in
	// memory
	public void updateEmployeeSalaryInDBThenInList(String name, double salary) throws CustomPayrollException {
		int result = normalisedDBServiceObj.updateEmployeeData(name, salary);
		if (result == 0) {
			return;
		}
		Contact contact = this.getEmployeePayrollData(name);
		if (contact != null)
			contact.salary = salary;
	}

	// Reading employee from employee_payroll DB for start in given date range
	public List<Contact> readEmployeePayrollForGivenDateRange(IOService ioService, LocalDate startDate,
			LocalDate endDate) throws CustomPayrollException {
		if (ioService.equals(IOService.DB_IO)) {
			this.employeePayrollList = normalisedDBServiceObj.getEmployeeForDateRange(startDate, endDate);
		}
		return employeePayrollList;
	}

	// Reading employee from employee_payroll DB with average salary group by gender
	public Map<String, Double> readAverageSalaryByGender(IOService ioService) throws CustomPayrollException {
		return normalisedDBServiceObj.getAverageSalaryByGender();
	}

	// Filter the employee with given name from the employee list
	private Contact getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream().filter(contactItem -> contactItem.name.equals(name)).findFirst()
				.orElse(null);
	}

	// Checking if two employee are equal or not
	public boolean checkEmployeePayrollListSyncWithDB(String name) throws CustomPayrollException {
		List<Contact> empPayrollDataList = normalisedDBServiceObj.getEmployeePayrollData(name);
		return empPayrollDataList.get(0).name.equalsIgnoreCase(getEmployeePayrollData(name).name);
	}

	public void addEmployeeToEmployeePayrollDB(int company_id, String name, String address, String gender, int dept_id,
			double salary, LocalDate startDate) throws CustomPayrollException {
		employeePayrollList.add(normalisedDBServiceObj.addEmployeeToDB(company_id, name, address, gender, dept_id, salary, startDate));
	}
}
