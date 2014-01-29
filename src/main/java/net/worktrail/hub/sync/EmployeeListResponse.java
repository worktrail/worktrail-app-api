package net.worktrail.hub.sync;

import java.util.Collection;

import net.worktrail.hub.sync.response.Employee;
import net.worktrail.hub.sync.response.WorkTrailResponse;

public class EmployeeListResponse extends WorkTrailResponse {

	private Collection<Employee> employeeList;

	public EmployeeListResponse(Collection<Employee> employeeList) {
		this.employeeList = employeeList;
	}
	
	public Collection<Employee> getEmployeeList() {
		return employeeList;
	}

}
