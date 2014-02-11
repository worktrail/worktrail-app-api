package net.worktrail.appapi.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import net.worktrail.appapi.WorkTrailAccessType;
import net.worktrail.appapi.WorkTrailAppApi;
import net.worktrail.appapi.WorkTrailScope;
import net.worktrail.appapi.response.RequestErrorException;

import org.json.JSONObject;
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
		
		workTrail.generateTestUser(WorkTrailAccessType.COMPANY,
				new WorkTrailScope[] { WorkTrailScope.READ_EMPLOYEES, WorkTrailScope.WRITE_TASKS, WorkTrailScope.READ_TASKS });
	}

	/**
	 * can be used by tests to directly access the requestPage method and handle json parameters.
	 */
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
	

}
