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

	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service";
		String userName = "root";
		String password = "Kumar@12345";
		Connection con;

		// 1 . Load driver class -> MYSQL JDBC
		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Driver Loaded");

		// 2. create Connection object
		con = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("Connection Done..!!!" + con);

		return con;
	}

	public static List<Contact> readData() {
		String sql = "SELECT * FROM employee_payroll";
		List<Contact> empPayrollList = new ArrayList<>();
		try {
			Connection con = getConnection();
			Statement stmt = con.createStatement();
			ResultSet result = stmt.executeQuery(sql);
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double salary = result.getDouble("salary");
				LocalDate startDate = result.getDate("start").toLocalDate();
				empPayrollList.add(new Contact(id, salary, name, startDate));
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return empPayrollList;
	}

}
