package net.worktrail.appapi.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.worktrail.appapi.EmployeeListResponse;
import net.worktrail.appapi.model.Company;
import net.worktrail.appapi.response.RequestErrorException;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WorkTrailAppApiTest extends AbstractWorkTrailAppApiTest {
	private static final Logger logger = Logger.getLogger(WorkTrailAppApiTest.class.getName());
	
	@Test
	public void testFetchEmployees() throws Exception {
		EmployeeListResponse employees = workTrail.fetchEmployees();
		Assert.assertEquals("We should receive one employee.", 1, employees.getEmployeeList().size());
		logger.info("We got the following employees: " + employees.getEmployeeList());
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
