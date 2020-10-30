package com.blz.employeepayrollsql.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

	// Updating employee data into database
	public int updateEmployeeData(String name, double salary) throws CustomPayrollException {
		return this.updateEmployeeDataUsingPreparedStatement(name, salary);
	}

	// Updating salary for given employee using Prepared statement
	private int updateEmployeeDataUsingPreparedStatement(String name, double salary) throws CustomPayrollException {
		String sql = "UPDATE employee_payroll set salary =? where name =?";
		int noOfRowsAffected = 0;
		try (Connection con = getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql);) {
			preparedStatement.setDouble(1, salary);
			preparedStatement.setString(2, name);
			noOfRowsAffected = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new CustomPayrollException("Unable to fetch data from Database!!");
		}
		return noOfRowsAffected;
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

	// Getting employee data with given name for comparison with DB and list
	public List<Contact> getEmployeePayrolldata(String name) throws CustomPayrollException {
		List<Contact> employeePayrollList = null;
		// System.out.println(preparedStmt == null);
		if (preparedStmt == null)
			preparedStatementForEmployeeData();
		try {
			preparedStmt.setString(1, name);
			ResultSet resultSet = preparedStmt.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			throw new CustomPayrollException("Error!! during employee with given name");
		}
		System.out.println(employeePayrollList);
		return employeePayrollList;
	}

	// Use of prepared statement to get employee data from DB
	private void preparedStatementForEmployeeData() throws CustomPayrollException {
		try {
			Connection con = getConnection();
			String sql = "SELECT * FROM employee_payroll WHERE name=?";
			preparedStmt = con.prepareStatement(sql);
		} catch (SQLException e) {
			throw new CustomPayrollException("Error!! during prepared statemennt");
		}
	}
}
