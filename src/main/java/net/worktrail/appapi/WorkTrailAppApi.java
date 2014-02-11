package net.worktrail.appapi;

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

import net.worktrail.appapi.model.Company;
import net.worktrail.appapi.model.CompanyImpl;
import net.worktrail.appapi.model.EmployeeImpl;
import net.worktrail.appapi.model.HubEntry;
import net.worktrail.appapi.response.CreateAuthResponse;
import net.worktrail.appapi.response.CreateHubEntriesResponse;
import net.worktrail.appapi.response.RequestErrorException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import at.tapo.worktrail.api.javaonly.WorkTrailConnectionUtils;

public class WorkTrailAppApi {
	private String appKey;
	private String secretApiKey;
	private String authToken;
	public static final String WORKTRAIL_SERVER = "https://worktrail.net";
//	private static final String WORKTRAIL_SERVER = "http://tools.sphene.net:8888";
	private static Logger logger = Logger.getLogger(WorkTrailAppApi.class.getName());
	private String workTrailServer = WORKTRAIL_SERVER;
	public Object setServerUrl;

	
	public WorkTrailAppApi(String appKey, String secretApiKey, String authToken) {
		this.appKey = appKey;
		this.secretApiKey = secretApiKey;
		this.authToken = authToken;
		
		workTrailServer = System.getProperty("net.worktrail.hub.server", WORKTRAIL_SERVER);
	}
	
	
	public CreateAuthResponse createAuthRequest(WorkTrailScope[] scopes) throws RequestErrorException {
		Map<String, String> args = createAuthArgs(scopes);
		JSONObject ret = requestPage("rest/token/request/", args);
		try {
			return new CreateAuthResponse(ret.getString("requestkey"),
					ret.getString("authtoken"), new URL(ret.getString("redirecturl")));
		} catch (MalformedURLException | JSONException e) {
			throw new RequestErrorException("Error while sending auth request.", e);
		}
	}


	private Map<String, String> createAuthArgs(WorkTrailScope[] scopes) {
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
		return args;
	}
	
	public EmployeeListResponse fetchEmployees() throws RequestErrorException {
		JSONObject ret = requestPage("rest/employees/");
		try {
			if (logger.isLoggable(Level.FINEST)) {
				logger.finest("Retrieved employees: " + ret.toString(4));
			}
			JSONArray array = ret.getJSONArray("list");
			Collection<EmployeeImpl> employeeList = new ArrayList<>();
			for (int i = 0 ; i < array.length() ; i++) {
				JSONObject employee = array.getJSONObject(i);
				EmployeeImpl e = new EmployeeImpl(
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
	
	public void cleanHubEntries() throws RequestErrorException {
		requestPage("rest/hub/entries/clean/");
	}
	
	/**
	 * Checks whether the auth token which is represented by the request key was already authorized.
	 * @param requestKey
	 * @return
	 * @throws RequestErrorException 
	 */
	public boolean verifyAuthorization(String requestKey) throws RequestErrorException {
		Map<String, String> args = new HashMap<>();
		args.put("requestkey", requestKey);
		JSONObject ret = requestPage("rest/token/confirm/", args);
		try {
			String status = ret.getString("status");
			if ("active".equals(status)) {
				return true;
			} else if ("rejected".equals(status)) {
				throw new RequestErrorException("authorization was rejected.", null);
			}
			return false;
		} catch (JSONException e) {
			throw new RequestErrorException("Error while retrieving status.", e);
		}
	}

	private JSONObject requestPage(String path) throws RequestErrorException {
		return requestPage(path, (Map<String, String>)null);
	}

	private JSONObject requestPage(String path, JSONObject data) throws RequestErrorException {
		Map<String, String> args = new HashMap<>();
		args.put("data", data.toString());
		return requestPage(path, args);
	}
	private JSONObject requestPage(String path, Map<String, String> args) throws RequestErrorException {
		try {
			if (args == null) {
				args = new HashMap<>();
			}
			args.put("appkey", appKey);
			args.put("secretapikey", secretApiKey);
			if (authToken != null) {
				args.put("authtoken", authToken);
			}
			
			URL url = new URL(workTrailServer + "/" + path);
			String query = WorkTrailConnectionUtils.getQuery(args.entrySet());
			StringBuilder ret = WorkTrailConnectionUtils.requestDataFromUrl(url, query.getBytes("UTF-8"), "WorkTrail Hub");
			JSONObject response = new JSONObject(ret.toString());
			if (response.has("error")) {
				logger.severe("Server returned error from request." + response.toString(4));
				throw new RequestErrorException("Server responded with an error message: " + response.getString("error"), null, response);
			}
			return response;
		} catch (IOException | JSONException e) {
			throw new RuntimeException("Error when requesting page {" + path + "}", e);
		}
	}


	public void setServerUrl(String serverUrl) {
		this.workTrailServer = serverUrl;
	}


	/**
	 * generates a test user - will NOT work on https://worktrail.net !
	 * @return auth token.
	 * @throws RequestErrorException 
	 */
	public String generateTestUser(WorkTrailScope[] scopes) throws RequestErrorException {
		Map<String, String> args = createAuthArgs(scopes);
		JSONObject ret = requestPage("rest/token/generatetestuser/", args);
		try {
			String authToken = ret.getString("authtoken");
			this.authToken = authToken;
			return authToken;
		} catch (JSONException e) {
			try {
				logger.log(Level.SEVERE, "Error while generating test user: " + ret.toString(4));
			} catch (JSONException e1) {
				// never mind..
			}
			throw new RequestErrorException("Error while generating test user.", e);
		}
	}


	public Company fetchCompany() throws RequestErrorException {
		JSONObject ret = requestPage("rest/company/");
		try {
			Company company = new CompanyImpl(ret.getLong("id"),
					ret.getString("name"), ret.getString("slug"),
					ret.getLong("break_task_id"),
					ret.getLong("org_project_id"),
					ret.getLong("unassigned_project_id"));
			return company;
		} catch (JSONException e) {
			throw new RequestErrorException("Error while parsing response.", e);
		}
	}
}
