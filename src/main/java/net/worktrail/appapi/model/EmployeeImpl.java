package net.worktrail.appapi.model;

import com.google.common.base.Objects;

public class EmployeeImpl implements Employee {

	private String firstName;
	private String lastName;
	private String displayName;
	private String primaryEmail;
	private long employeeId;

	public EmployeeImpl(long employeeId, String firstName, String lastName, String displayName, String primaryEmail) {
		this.employeeId = employeeId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.displayName = displayName;
		this.primaryEmail = primaryEmail;
	}
	
	/* (non-Javadoc)
	 * @see net.worktrail.appapi.model.Employee#getEmployeeId()
	 */
	@Override
	public long getEmployeeId() {
		return employeeId;
	}
	
	/* (non-Javadoc)
	 * @see net.worktrail.appapi.model.Employee#getFirstName()
	 */
	@Override
	public String getFirstName() {
		return firstName;
	}
	
	/* (non-Javadoc)
	 * @see net.worktrail.appapi.model.Employee#getLastName()
	 */
	@Override
	public String getLastName() {
		return lastName;
	}
	
	/* (non-Javadoc)
	 * @see net.worktrail.appapi.model.Employee#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	/* (non-Javadoc)
	 * @see net.worktrail.appapi.model.Employee#getPrimaryEmail()
	 */
	@Override
	public String getPrimaryEmail() {
		return primaryEmail;
	}
	
	/* (non-Javadoc)
	 * @see net.worktrail.appapi.model.Employee#toString()
	 */
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("employeeId", employeeId)
			.add("firstName", firstName)
			.add("lastName", lastName)
			.add("displayName", displayName)
			.add("primaryEmail", primaryEmail)
			.toString();
	}

}
