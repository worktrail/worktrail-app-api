package net.worktrail.api.hub.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.worktrail.hub.sync.EmployeeListResponse;
import net.worktrail.hub.sync.WorkTrailAuth;
import net.worktrail.hub.sync.WorkTrailScope;
import net.worktrail.hub.sync.model.Company;
import net.worktrail.hub.sync.response.RequestErrorException;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WorkTrailAuthTest {
	private static final Logger logger = Logger.getLogger(WorkTrailAuthTest.class.getName());
	
	private WorkTrailAuth workTrail;

	@Before
	public void setUp() throws Exception {
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
	
	
	protected JSONObject requestPage(String path, Map<String, String> args) throws RequestErrorException {
		try {
			Method requestPageMethod = workTrail.getClass().getDeclaredMethod("requestPage", new Class[] { String.class, Map.class });
			requestPageMethod.setAccessible(true);
			JSONObject response = (JSONObject) requestPageMethod.invoke(workTrail, path, args);
			return response;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			if (e.getCause() instanceof RequestErrorException) {
				throw (RequestErrorException) e.getCause();
			}
			throw new RuntimeException("Error while calling requestPage.", e);
		}
	}
	
	@Test
	public void testFetchCompany() throws Exception {
		Company company = workTrail.fetchCompany();
		logger.info("got our company: " + company);
		Assert.assertNotNull(company);
	}
	
	@Test
	public void testCreateBrokenProject() throws Exception {
		JSONObject prj = new JSONObject();
		prj.put("name", "Example Project");
		prj.put("state", "blubber");
		Map<String, String> args = new HashMap<String, String>();
		args.put("project", prj.toString());
		try {
			requestPage("rest/projects/create/", args);
			Assert.fail("request was successful?!");
		} catch (RequestErrorException e) {
			JSONObject res = e.getResponseObject();
			logger.info("result: " + res.toString());
			Assert.assertEquals("invalid-model", res.optString("errortype"));
			JSONObject errorObj = res.getJSONObject("errorobj");
			@SuppressWarnings("unchecked")
			ArrayList<String> keys = Lists.newArrayList(errorObj.keys());
			Assert.assertEquals(Lists.newArrayList("state"), keys);
		}
	}
	
	@Test
	public void testCreateBrokenTask() throws Exception {
		Company company = workTrail.fetchCompany();
		JSONObject task = new JSONObject();
		task.put("project_id", company.getUnassignedProjectId());
		task.put("summary", "Example Summary");
//		task.put("description", "some sample description.");
		task.put("state", "blubber");
		try {
			requestPage("rest/tasks/create/", Maps.newHashMap(Collections.singletonMap("task", task.toString())));
			Assert.fail("task create request was successful?!");
		} catch (RequestErrorException e) {
			JSONObject res = e.getResponseObject();
			Assert.assertEquals("invalid-model", res.optString("errortype"));
			JSONObject errorObj = res.getJSONObject("errorobj");
			@SuppressWarnings("unchecked")
			ArrayList<String> keys = Lists.newArrayList(errorObj.keys());
			Assert.assertEquals(Lists.newArrayList("state"), keys);
		}
	}

}
