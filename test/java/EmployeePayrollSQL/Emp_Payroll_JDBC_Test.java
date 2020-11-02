/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package EmployeePayrollSQL;

import org.junit.Before;
import org.junit.Test;

import com.blz.employeepayrollsql.controller.Emp_Payroll_JDBC_Main;
import com.blz.employeepayrollsql.controller.Emp_Payroll_JDBC_Main.IOService;
import com.blz.employeepayrollsql.model.Contact;
import com.blz.employeepayrollsql.model.CustomPayrollException;

import junit.framework.Assert;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class Emp_Payroll_JDBC_Test {
	Emp_Payroll_JDBC_Main empPayrollService = null;

	@Before
	public void setup() {
		empPayrollService = new Emp_Payroll_JDBC_Main();
	}

	@Test
	public void givenEmployeePayrollServiceInDB_ShouldMatchEmployeeCount() {
		List<Contact> empPayrollData;
		try {
			empPayrollData = empPayrollService.readEmployeePayrollDatabase(IOService.DB_IO);
			Assert.assertEquals(5, empPayrollData.size());
		} catch (CustomPayrollException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void givenNewSalaryForEmployee_WhenUpdatedByPreapredStmt_ShouldSyncWithDB() {
		try {
			empPayrollService.readEmployeePayrollDatabase(IOService.DB_IO);
			empPayrollService.updateEmployeeSalaryInDBThenInList("Dev", 1500000.00);
			boolean result = empPayrollService.checkEmployeePayrollListSyncWithDB("Dev");
			Assert.assertTrue(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void givenDateRange_WhenRetrieved_shouldMatchEmployeeCount() throws CustomPayrollException {
		empPayrollService.readEmployeePayrollDatabase(IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2018, 02, 15);
		LocalDate endDate = LocalDate.now();
		List<Contact> empPayrollList = empPayrollService.readEmployeePayrollForGivenDateRange(IOService.DB_IO,
				startDate, endDate);
		System.out.println(empPayrollList);
		Assert.assertEquals(1, empPayrollList.size());
	}

	@Test
	public void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnCorrectValue()
			throws CustomPayrollException {
		empPayrollService.readEmployeePayrollDatabase(IOService.DB_IO);
		Map<String, Double> averageSalaryByGender = empPayrollService.readAverageSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(
				averageSalaryByGender.get("M").equals(1750000.00) && averageSalaryByGender.get("F").equals(3000000.00));
	}

	@Test
	public void givenNewEmployee_whenAdded_shouldSyncWithDB() throws CustomPayrollException {
		empPayrollService.readEmployeePayrollDatabase(IOService.DB_IO);
		empPayrollService.addEmployeeToEmployeePayrollDB(2, "Mark", "HYD", "M", 53, 5000000.00, LocalDate.now());
		boolean result = empPayrollService.checkEmployeePayrollListSyncWithDB("Mark");
		Assert.assertTrue(result);
	}
}
