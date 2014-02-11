package net.worktrail.api.hub.test;

import java.util.logging.Logger;

import net.worktrail.hub.sync.EmployeeListResponse;
import net.worktrail.hub.sync.WorkTrailAuth;
import net.worktrail.hub.sync.WorkTrailScope;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WorkTrailAuthTest {
	private static final Logger logger = Logger.getLogger(WorkTrailAuthTest.class.getName());
	
	private WorkTrailAuth workTrail;

	@Before
	public void setUp() {
		// app and authentication token for a "test app" on qa.worktrail.net - we will probably remove this
		// one day.
//		this.workTrail = new WorkTrailAuth("cNnWzkhBjQ", "TfH6CQGADdkuEvUwHECt9UrdHJ4k6GxZxnFS6GMZLnnkVZFLZ5", null);
//		workTrail.setServerUrl("http://qa.worktrail.net");
		this.workTrail = new WorkTrailAuth("FJRU2eeDkT", "W6DbU7947hQgPmFMLhjdWv5AHWcawqmw3KZQyUBvzqtd2tafJs", null);
		workTrail.setServerUrl("http://127.0.0.1:8000");
		
		workTrail.generateTestUser(new WorkTrailScope[] { WorkTrailScope.READ_EMPLOYEES, WorkTrailScope.WRITE_TASKS, WorkTrailScope.READ_TASKS });
	}
	
	@Test
	public void testFetchEmployees() throws Exception {
		EmployeeListResponse employees = workTrail.fetchEmployees();
		Assert.assertEquals("We should receive one employee.", 1, employees.getEmployeeList().size());
		logger.info("We got the following employees: " + employees.getEmployeeList());
	}

}
