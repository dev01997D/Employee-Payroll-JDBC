package com.blz.employeepayrollsql.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
				String address = resultSet.getString("address");
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
		int rowsAffected = 0;
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
					+ "JOIN department d on ed.dept_id=d.dept_id " + "JOIN company c on c.company_id=e.company_id "
					+ "JOIN payroll p on p.emp_id=e.emp_id " + "WHERE e.name = ?";
			preparedStmt = connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new CustomPayrollException("Unable to do prepared statement execution");
		}
	}

	public List<Contact> getEmployeeForDateRange(LocalDate startDate, LocalDate endDate) throws CustomPayrollException {
		String sql = String.format(
				"SELECT e.emp_id, e.company_id, e.Name, e.address, e.gender, c.company_name, ed.dept_id, d.dept_name, p.basic_pay, p.start "
						+ "from employee e right join employee_department ed using(emp_id) "
						+ "JOIN department d on ed.dept_id=d.dept_id " + "JOIN company c on c.company_id=e.company_id "
						+ "JOIN payroll p on p.emp_id=e.emp_id "
						+ "WHERE p.start BETWEEN CAST('2019-01-01' AS DATE) AND DATE(NOW());");
		return getEmployeePayrollDataUsingSQLQuery(sql);
	}

	public Map<String, Double> getAverageSalaryByGender() throws CustomPayrollException {
		String sql = "SELECT e.gender, AVG(P.basic_pay) as avg_salary FROM employee e NATURAL JOIN payroll p GROUP BY gender;";
		Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
		try (Connection connection = connectionObj.getConnection();) {
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			ResultSet resultSet = prepareStatement.executeQuery(sql);
			while (resultSet.next()) {
				String gender = resultSet.getString("gender");
				double salary = resultSet.getDouble("avg_salary");
				genderToAverageSalaryMap.put(gender, salary);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return genderToAverageSalaryMap;
	}

	public Contact addEmployeeToDB(int company_id, String name, String address, String gender, int dept_id,
			double salary, LocalDate startDate) throws CustomPayrollException {
		int employeeId = -1;
		Connection connection = null;
		Contact contact = null;
		try {
			connection = connectionObj.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO employee (company_id, name, address, gender) VALUES ('%s','%s','%s','%s');",
					company_id, name, address, gender);
			int rowAffected = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
			contact = new Contact(employeeId, company_id, name, address, gender);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return contact;
		}

		try (Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			String sql = String.format(
					"INSERT INTO payroll (emp_id, basic_pay, deductions, taxable_income, income_tax ,net_pay, start) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')",
					employeeId, salary, deductions, taxablePay, tax, netPay, Date.valueOf(startDate));
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1) {
				contact = new Contact(employeeId, company_id, name, address, gender);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return contact;
		}

		try (Statement statement = connection.createStatement();) {
			String sql = String.format("INSERT INTO employee_department (emp_id, dept_id) VALUES (%s,%s)", employeeId,
					dept_id);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1) {
				contact = new Contact(employeeId, company_id, name, address, gender);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return contact;
		}

		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return contact;

	}

	public List<Contact> getActiveEmployees() throws CustomPayrollException {
		String sql = String.format("select * from employee where is_active='%s';","yes");
		return this.getEmployeePayrollDBUsingDBForActiveEmployee(sql);
	}
	
	private List<Contact> getEmployeePayrollDataNormalisedActive(ResultSet resultSet) {
		List<Contact> employeePayrollList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("emp_id");
				int companyId = resultSet.getInt("company_id");
				String name = resultSet.getString("Name");
				String address=resultSet.getString("address");
				String gender = resultSet.getString("gender");
				String is_active=resultSet.getString("is_active");
				employeePayrollList
						.add(new Contact(id, companyId, name,  address, gender, is_active));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(employeePayrollList);
		return employeePayrollList;
	}
	
	private List<Contact> getEmployeePayrollDBUsingDBForActiveEmployee(String sql) throws CustomPayrollException {
		ResultSet resultSet;
		List<Contact> employeePayrollList = null;
		try (Connection connection = connectionObj.getConnection();) {
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			resultSet = prepareStatement.executeQuery(sql);
			employeePayrollList = this.getEmployeePayrollDataNormalisedActive(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}
}
