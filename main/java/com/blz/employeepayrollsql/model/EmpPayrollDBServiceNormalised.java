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

public class EmpPayrollDBServiceNormalised {
	private static EmpPayrollDBServiceNormalised normalisedDBServiceObj;
	private PreparedStatement preparedStmt;
	private JdbcConnection connectionObj = new JdbcConnection();

	public static EmpPayrollDBServiceNormalised getInstance() {
		if (normalisedDBServiceObj == null)
			normalisedDBServiceObj = new EmpPayrollDBServiceNormalised();
		return normalisedDBServiceObj;
	}

	public List<Contact> readData() throws CustomPayrollException {
		String sql = "SELECT e.emp_id, e.company_id, e.Name, e.address, e.gender, c.company_name, ed.dept_id, d.dept_name, p.basic_pay, p.start "
				+ "from employee e right join employee_department ed using(emp_id) "
				+ "JOIN department d on ed.dept_id=d.dept_id " + "JOIN company c on c.company_id=e.company_id "
				+ "JOIN payroll p on p.emp_id=e.emp_id;";
		return this.getEmployeePayrollDataUsingSQLQuery(sql);
	}

	private List<Contact> getEmployeePayrollDataUsingSQLQuery(String sql) throws CustomPayrollException {
		List<Contact> employeePayrollList = null;
		try (Connection connection = connectionObj.getConnection();) {
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			ResultSet resultSet = prepareStatement.executeQuery(sql);
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			throw new CustomPayrollException("Unable to execute SQL query!!");
		}
		return employeePayrollList;
	}

	private List<Contact> getEmployeePayrollData(ResultSet resultSet) throws CustomPayrollException {
		List<Contact> employeePayrollList = new ArrayList<>();
		List<String> department = new ArrayList<String>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("emp_id");
				String name = resultSet.getString("Name");
				String address=resultSet.getString("address");
				String gender = resultSet.getString("gender");
				int companyId = resultSet.getInt("company_id");
				String companyName = resultSet.getString("company_name");
				int deptId = resultSet.getInt("dept_id");
				String dept = resultSet.getString("dept_name");
				double salary = resultSet.getDouble("basic_pay");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				department.add(dept);
				String[] departmentArray = new String[department.size()];
				employeePayrollList.add(new Contact(id, companyId, name, address, gender, companyName, deptId, dept,
						salary, startDate, department.toArray(departmentArray)));
			}
		} catch (SQLException e) {
			throw new CustomPayrollException("Unable to read from resultSet!!");
		}
		return employeePayrollList;
	}

	public int updateEmployeeData(String name, Double salary) throws CustomPayrollException {
		return this.updateEmployeeDataUsingPreparedStatement(name, salary);
	}

	private int updateEmployeeDataUsingPreparedStatement(String name, double salary) throws CustomPayrollException {
		int rowsAffected=0;
		String sql = String.format("UPDATE payroll SET basic_pay = %.2f WHERE emp_id = "
				+ "(SELECT emp_id from employee WHERE name = '%s');", salary, name);
		try (Connection connection = connectionObj.getConnection();) {
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			rowsAffected = prepareStatement.executeUpdate(sql);
		} catch (SQLException e) {
			throw new CustomPayrollException("Unable to update the salary field");
		}
		return rowsAffected;
	}

	public List<Contact> getEmployeePayrollData(String name) throws CustomPayrollException {
		List<Contact> employeePayrollList = null;
		if (this.preparedStmt == null)
			this.preparedStatementForEmployeeData();
		try {
			preparedStmt.setString(1, name);
			ResultSet resultSet = preparedStmt.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private void preparedStatementForEmployeeData() throws CustomPayrollException {
		try {
			Connection connection = connectionObj.getConnection();
			String sql = "SELECT e.emp_id, e.company_id, e.Name, e.address, e.gender, c.company_name, ed.dept_id, d.dept_name, p.basic_pay, p.start "
					+ "from employee e right join employee_department ed using(emp_id) "
					+ "JOIN department d on ed.dept_id=d.dept_id "
					+ "JOIN company c on c.company_id=e.company_id "
					+ "JOIN payroll p on p.emp_id=e.emp_id "
					+ "WHERE e.name = ?";
			preparedStmt = connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new CustomPayrollException("Unable to do prepared statement execution");
		}
	}
}
