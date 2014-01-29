package net.worktrail.hub.sync;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.worktrail.hub.sync.response.CreateAuthResponse;
import net.worktrail.hub.sync.response.CreateHubEntriesResponse;
import net.worktrail.hub.sync.response.Employee;
import net.worktrail.hub.sync.response.HubEntry;
import net.worktrail.hub.sync.response.RequestErrorException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import at.tapo.worktrail.api.javaonly.WorkTrailConnectionUtils;

public class WorkTrailAuth {
	private String appKey;
	private String secretApiKey;
	private String authToken;
//	private static final String WORKTRAIL_SERVER = "https://worktrail.net";
	private static final String WORKTRAIL_SERVER = "http://tools.sphene.net:8888";
	private static Logger logger = Logger.getLogger(WorkTrailAuth.class.getName());

	
	public WorkTrailAuth(String appKey, String secretApiKey, String authToken) {
		this.appKey = appKey;
		this.secretApiKey = secretApiKey;
		this.authToken = authToken;
	}
	
	
	public CreateAuthResponse createAuthRequest(WorkTrailScope[] scopes) throws RequestErrorException {
		StringBuilder builder = new StringBuilder();
		for (WorkTrailScope scope : scopes) {
			if (builder.length() > 0) {
				builder.append(',');
			}
			builder.append(scope.getStringIdentifier());
		}
		Map<String, String> args = new HashMap<String, String>();
		args.put("scopes", builder.toString());
		args.put("accesstype", "company");
		JSONObject ret = requestPage("rest/token/request/", args);
		try {
			return new CreateAuthResponse(ret.getString("requestkey"),
					ret.getString("authtoken"), new URL(ret.getString("redirecturl")));
		} catch (MalformedURLException | JSONException e) {
			throw new RequestErrorException("Error while sending auth request.", e);
		}
	}
	
	public EmployeeListResponse fetchEmployees() throws RequestErrorException {
		JSONObject ret = requestPage("rest/employees/");
		try {
			if (logger.isLoggable(Level.FINEST)) {
				logger.finest("Retrieved employees: " + ret.toString(4));
			}
			JSONArray array = ret.getJSONArray("list");
			Collection<Employee> employeeList = new ArrayList<>();
			for (int i = 0 ; i < array.length() ; i++) {
				JSONObject employee = array.getJSONObject(i);
				Employee e = new Employee(
						employee.getLong("id"),
						employee.getString("first_name"),
						employee.getString("last_name"),
						employee.getString("name"),
						employee.getString("username"));
				employeeList.add(e);
			}
			return new EmployeeListResponse(employeeList);
		} catch (JSONException e) {
			throw new RequestErrorException("Error while fetching employee list.", e);
		}
	}
	
	public CreateHubEntriesResponse createHubEntries(Collection<HubEntry> hubEntries) throws RequestErrorException {
		try {
			JSONArray create = new JSONArray();
			for (HubEntry hubEntry : hubEntries) {
				create.put(hubEntry.toJSONObject());
			}
			JSONObject req = new JSONObject();
			req.put("create", create);
			JSONObject ret = requestPage("rest/hub/entries/create/", req);
			JSONArray created = ret.getJSONArray("created");
			List<Long> createdList = new ArrayList<>();
			for (int i = 0 ; i < created.length() ; i++) {
				createdList.add(created.getLong(i));
			}
			return new CreateHubEntriesResponse(createdList);
		} catch (JSONException e) {
			throw new RequestErrorException("Error while creating hub entries.", e);
		}
	}
	
	public void cleanHubEntries() {
		requestPage("rest/hub/entries/clean/");
	}
	
	/**
	 * Checks whether the auth token which is represented by the request key was already authorized.
	 * @param requestKey
	 * @return
	 */
	public boolean verifyAuthorization(String requestKey) {
		Map<String, String> args = new HashMap<>();
		args.put("requestkey", requestKey);
		JSONObject ret = requestPage("rest/token/confirm/", args);
		try {
			String status = ret.getString("status");
			if ("active".equals(status)) {
				return true;
			} else if ("rejected".equals(status)) {
				throw new RuntimeException("authorization was rejected.");
			}
			return false;
		} catch (JSONException e) {
			throw new RuntimeException("Error while retrieving status.", e);
		}
	}

	private JSONObject requestPage(String path) {
		return requestPage(path, (Map<String, String>)null);
	}

	private JSONObject requestPage(String path, JSONObject data) {
		Map<String, String> args = new HashMap<>();
		args.put("data", data.toString());
		return requestPage(path, args);
	}
	private JSONObject requestPage(String path, Map<String, String> args) {
		try {
			if (args == null) {
				args = new HashMap<>();
			}
			args.put("appkey", appKey);
			args.put("secretapikey", secretApiKey);
			if (authToken != null) {
				args.put("authtoken", authToken);
			}
			
			URL url = new URL(WORKTRAIL_SERVER + "/" + path);
			String query = WorkTrailConnectionUtils.getQuery(args.entrySet());
			StringBuilder ret = WorkTrailConnectionUtils.requestDataFromUrl(url, query.getBytes("UTF-8"), "WorkTrail Hub");
			return new JSONObject(ret.toString());
		} catch (IOException | JSONException e) {
			throw new RuntimeException("Error when requesting page {" + path + "}", e);
		}
	}
}
