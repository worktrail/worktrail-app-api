package net.worktrail.appapi;

import java.util.Collection;

import net.worktrail.appapi.response.Employee;
import net.worktrail.appapi.response.WorkTrailResponse;

public class EmployeeListResponse extends WorkTrailResponse {

	private Collection<Employee> employeeList;

	public EmployeeListResponse(Collection<Employee> employeeList) {
		this.employeeList = employeeList;
	}
	
	public Collection<Employee> getEmployeeList() {
		return employeeList;
	}

}
