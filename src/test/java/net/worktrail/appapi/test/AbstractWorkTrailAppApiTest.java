package net.worktrail.appapi.test;

import net.worktrail.appapi.WorkTrailAppApi;
import net.worktrail.appapi.WorkTrailScope;

import org.junit.Before;

public abstract class AbstractWorkTrailAppApiTest {
	
	protected WorkTrailAppApi workTrail;

	@Before
	public void setUp() throws Exception {
		// app and authentication token for a "test app" on qa.worktrail.net - we will probably remove this
		// one day.
//		this.workTrail = new WorkTrailAuth("cNnWzkhBjQ", "TfH6CQGADdkuEvUwHECt9UrdHJ4k6GxZxnFS6GMZLnnkVZFLZ5", null);
//		workTrail.setServerUrl("http://qa.worktrail.net");
		this.workTrail = new WorkTrailAppApi("FJRU2eeDkT", "W6DbU7947hQgPmFMLhjdWv5AHWcawqmw3KZQyUBvzqtd2tafJs", null);
		workTrail.setServerUrl("http://127.0.0.1:8000");
		
		workTrail.generateTestUser(new WorkTrailScope[] { WorkTrailScope.READ_EMPLOYEES, WorkTrailScope.WRITE_TASKS, WorkTrailScope.READ_TASKS });
	}

}
