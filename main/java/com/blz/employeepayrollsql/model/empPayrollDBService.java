package com.blz.employeepayrollsql.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmpPayrollDBService {
	private static EmpPayrollDBService employeePayrollDBServiceObj;
	private PreparedStatement preparedStmt;

	public static EmpPayrollDBService getInstance() {
		if (employeePayrollDBServiceObj == null)
			employeePayrollDBServiceObj = new EmpPayrollDBService();
		return employeePayrollDBServiceObj;
	}

	// Loading Driver and getting connection object
	private static Connection getConnection() throws CustomPayrollException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service";
		String userName = "root";
		String password = "Kumar@12345";
		Connection con;

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new CustomPayrollException("Error!!! Unable to load the driver");
		}

		try {
			con = DriverManager.getConnection(jdbcURL, userName, password);
		} catch (SQLException e) {
			throw new CustomPayrollException("Error!!! Unable to establish the Connection with JDBC");
		}
		return con;
	}

	// Reading all the employees data from the DB
	public List<Contact> readData() throws CustomPayrollException {
		String sql = "SELECT * FROM employee_payroll";
		return this.executeSQLAndReturnEmployeeList(sql);
	}

	// getting employee who has joined for given date range
	public List<Contact> getEmployeeForDateRange(LocalDate startDate, LocalDate endDate) throws CustomPayrollException {
		String sql = String.format("SELECT * FROM EMPLOYEE_PAYROLL WHERE START BETWEEN  '%s'  and '%s';",
				Date.valueOf(startDate), Date.valueOf(endDate));
		return this.executeSQLAndReturnEmployeeList(sql);
	}

	// Get average salary of employee group by gender
	public Map<String, Double> getAverageSalaryByGender() throws CustomPayrollException {
		String sql = "SELECT Gender, Avg(salary) from employee_payroll group by Gender;";
		String operation = "Avg(Salary)";
		return this.executeSQLAndReturnMap(sql, operation);
	}

	// Get average salary of employee group by gender
	public Map<String, Double> getMaxSalaryByGender() throws CustomPayrollException {
		String sql = "SELECT Gender, Max(salary) from employee_payroll group by Gender;";
		String operation = "Max(Salary)";
		return this.executeSQLAndReturnMap(sql, operation);
	}

	// Execute SQL and return required map
	public Map<String, Double> executeSQLAndReturnMap(String sql, String operation) throws CustomPayrollException {
		Map<String, Double> employeePayrollMap = new HashMap<>();
		try (Connection con = getConnection()) {
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);
			while (resultSet.next()) {
				String gender = resultSet.getString("Gender");
				double salary = resultSet.getDouble(operation);
				employeePayrollMap.put(gender, salary);
			}
		} catch (SQLException e) {
			throw new CustomPayrollException("Unable to execute query of function on salary");
		}
		return employeePayrollMap;
	}

	// Updating employee data into database
	public int updateEmployeeData(String name, double salary) throws CustomPayrollException {
		return this.updateEmployeeDataUsingPreparedStatement(name, salary);
	}

	// Updating salary for given employee using Prepared statement
	private int updateEmployeeDataUsingPreparedStatement(String name, double salary) throws CustomPayrollException {
		String sql = "UPDATE employee_payroll set salary =? where name =?";
		if (preparedStmt == null)
			preparedStatementForEmployeeData(sql);
		int noOfRowsAffected = 0;
		try {
			preparedStmt.setDouble(1, salary);
			preparedStmt.setString(2, name);
			noOfRowsAffected = preparedStmt.executeUpdate();
		} catch (SQLException e) {
			throw new CustomPayrollException("Unable to fetch data from Database!!");
		}
		preparedStmt = null;
		return noOfRowsAffected;
	}

	// Getting employee data with given name for comparison with DB and memory using
	// prepared statement
	public List<Contact> getEmployeePayrolldata(String name) throws CustomPayrollException {
		String sql = "SELECT * FROM employee_payroll WHERE name=?";
		List<Contact> employeePayrollList = null;
		if (preparedStmt == null)
			preparedStatementForEmployeeData(sql);
		try {
			preparedStmt.setString(1, name);
			ResultSet resultSet = preparedStmt.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			throw new CustomPayrollException("Error!! during employee with given name");
		}
		preparedStmt = null;
		return employeePayrollList;
	}

	// Execute sql statement, operate on resultSet and return employee payroll list
	public List<Contact> executeSQLAndReturnEmployeeList(String sql) throws CustomPayrollException {
		List<Contact> empPayrollList = new ArrayList<>();
		try (Connection con = getConnection();) {
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);
			empPayrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			throw new CustomPayrollException("Unable to fetch data from Database!!");
		}
		return empPayrollList;
	}

	// Method to get ResultSet of query performed and storing into memory as list
	private List<Contact> getEmployeePayrollData(ResultSet resultSet) {
		List<Contact> employeePayrollList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new Contact(id, salary, name, startDate));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	// Use of prepared statement to get employee data from DB
	private void preparedStatementForEmployeeData(String sql) throws CustomPayrollException {
		try {
			Connection con = getConnection();
			preparedStmt = con.prepareStatement(sql);
		} catch (SQLException e) {
			throw new CustomPayrollException("Error!! during prepared statemennt");
		}
	}
}
