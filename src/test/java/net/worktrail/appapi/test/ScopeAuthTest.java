package net.worktrail.appapi.test;

import net.worktrail.appapi.WorkTrailAccessType;
import net.worktrail.appapi.WorkTrailScope;
import net.worktrail.appapi.response.RequestErrorException;

import org.junit.Assert;
import org.junit.Test;

/**
 * a few tests to validate that scopes can only certain scopes can be used for
 * employee/company accesstype.
 * 
 * @author herbert
 */
public class ScopeAuthTest extends AbstractWorkTrailAppApiTest {
	
	@Test
	public void invalidScopeTest() {
		try {
			workTrail.generateTestUser(WorkTrailAccessType.EMPLOYEE, new WorkTrailScope[] { WorkTrailScope.WRITE_TASKS });
			Assert.fail("request must fail.");
		} catch (RequestErrorException e) {
			Assert.assertEquals("Expected invalid scope response.", "invalid-scope", e.getResponseObject().optString("errortype"));
		}
	}
	
	@Test
	public void employeeAccessTypeTest() throws RequestErrorException {
		workTrail.generateTestUser(WorkTrailAccessType.EMPLOYEE, new WorkTrailScope[] { WorkTrailScope.READ_EMPLOYEES, WorkTrailScope.SYNC_HUB_DATA });
	}
}
