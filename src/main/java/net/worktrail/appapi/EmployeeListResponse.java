package net.worktrail.appapi;

import java.util.Collection;

import net.worktrail.appapi.model.EmployeeImpl;
import net.worktrail.appapi.response.WorkTrailResponse;

public class EmployeeListResponse extends WorkTrailResponse {

	private Collection<EmployeeImpl> employeeList;

	public EmployeeListResponse(Collection<EmployeeImpl> employeeList) {
		this.employeeList = employeeList;
	}
	
	public Collection<EmployeeImpl> getEmployeeList() {
		return employeeList;
	}

}
