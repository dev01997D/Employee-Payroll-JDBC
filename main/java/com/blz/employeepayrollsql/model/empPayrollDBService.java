package com.blz.employeepayrollsql.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class empPayrollDBService {

	private static Connection getConnection() throws CustomPayrollException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service";
		String userName = "root";
		String password = "Kumar@12345";
		Connection con;

		// 1 . Load driver class -> MYSQL JDBC
		try {
		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Driver Loaded");
		}
		catch(ClassNotFoundException e) {
			throw new CustomPayrollException("Error!!! Unable to load the driver");
		}
		
		// 2. create Connection object
		try {
		con = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("Connection Done..!!!" + con);
		}catch(SQLException e) {
			throw new CustomPayrollException("Error!!! Unable to establish the Connection with JDBC");
		}
		return con;
	}

	public static List<Contact> readData() throws CustomPayrollException {
		String sql = "SELECT * FROM employee_payroll";
		List<Contact> empPayrollList = new ArrayList<>();
		try (Connection con = getConnection();) 
		{
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				empPayrollList.add(new Contact(id, salary, name, startDate));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return empPayrollList;
	}

}
