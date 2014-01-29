package net.worktrail.hub.sync.response;

public class Employee {

	private String firstName;
	private String lastName;
	private String displayName;
	private String primaryEmail;
	private long employeeId;

	public Employee(long employeeId, String firstName, String lastName, String displayName, String primaryEmail) {
		this.employeeId = employeeId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.displayName = displayName;
		this.primaryEmail = primaryEmail;
	}
	
	public long getEmployeeId() {
		return employeeId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getPrimaryEmail() {
		return primaryEmail;
	}

}
