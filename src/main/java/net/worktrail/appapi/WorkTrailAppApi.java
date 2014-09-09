package net.worktrail.appapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.worktrail.appapi.model.Company;
import net.worktrail.appapi.model.CompanyImpl;
import net.worktrail.appapi.model.EmployeeImpl;
import net.worktrail.appapi.model.HubEntry;
import net.worktrail.appapi.model.WorkEntry;
import net.worktrail.appapi.model.WorkEntryImpl;
import net.worktrail.appapi.response.CreateAuthResponse;
import net.worktrail.appapi.response.CreateHubEntriesResponse;
import net.worktrail.appapi.response.RequestErrorException;
import net.worktrail.appapi.response.WorkEntryListResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import at.tapo.worktrail.api.javaonly.WorkTrailConnectionUtils;

/**
 * Entry point for accessing the WorkTrail API - see https://worktrail.net/en/api/ for details.
 * 
 * @author herbert
 */
public class WorkTrailAppApi {
	private static Logger logger = Logger.getLogger(WorkTrailAppApi.class.getName());

	private String appKey;
	private String secretApiKey;
	private String authToken;
	public static final String WORKTRAIL_SERVER = "https://worktrail.net";
	private String workTrailServer = WORKTRAIL_SERVER;
	public Object setServerUrl;

	
	/**
	 * Creates a new api accessor instance.
	 * 
	 * @param appKey Application key, provided by WorkTrail - https://worktrail.net/en/api/apps/
	 * @param secretApiKey When registering your app, you will also receive a secret api key.
	 * @param authToken Auth token is required to access secure api endpoints - ie. everything except {@link #createAuthRequest(WorkTrailAccessType, WorkTrailScope[])} (see linked documentation for auth details)
	 */
	public WorkTrailAppApi(String appKey, String secretApiKey, String authToken) {
		this.appKey = appKey;
		this.secretApiKey = secretApiKey;
		this.authToken = authToken;
		
		workTrailServer = System.getProperty("net.worktrail.hub.server", WORKTRAIL_SERVER);
	}
	
	public String getAuthToken() {
		return authToken;
	}
	
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	
	/**
	 * Will request a new authentication request. The returned {@link CreateAuthResponse} will 
	 * contain a URL your users will have to open in their browser to authenticate this app.
	 * 
	 * See API documentation at https://worktrail.net/en/api/ for details about this parameters!
	 * 
	 * @param accessType Which access type is required - company or employee level access.
	 * @param scopes All required scopes this app requires.
	 * @return {@link CreateAuthResponse} with a URL where the user can grant access to the app.
	 * @throws RequestErrorException if an error happens during authentication.
	 */
	public CreateAuthResponse createAuthRequest(WorkTrailAccessType accessType, WorkTrailScope[] scopes) throws RequestErrorException {
		Map<String, String> args = createAuthArgs(accessType, scopes);
		JSONObject ret = requestPage("rest/token/request/", args);
		try {
			return new CreateAuthResponse(ret.getString("requestkey"),
					ret.getString("authtoken"), new URL(ret.getString("redirecturl")));
		} catch (MalformedURLException | JSONException e) {
			throw new RequestErrorException("Error while sending auth request.", e);
		}
	}


	private Map<String, String> createAuthArgs(WorkTrailAccessType accessType, WorkTrailScope[] scopes) {
		StringBuilder builder = new StringBuilder();
		for (WorkTrailScope scope : scopes) {
			if (builder.length() > 0) {
				builder.append(',');
			}
			builder.append(scope.getStringIdentifier());
		}
		Map<String, String> args = new HashMap<String, String>();
		args.put("scopes", builder.toString());
		args.put("accesstype", accessType.getStringIdentifier());
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
	
	public WorkEntryListResponse fetchWorkEntries(long lastModifyDate, long page) throws RequestErrorException {
		JSONObject ret = requestPage("rest/workentries/?after="+lastModifyDate+"&page="+page);
		try {
			JSONArray array = ret.getJSONArray("list");
			Collection<WorkEntry> workEntryList = new ArrayList<>();
			for (int i = 0 ; i < array.length() ; i++) {
				JSONObject workEntryObj = array.getJSONObject(i);
				try {
					WorkEntryImpl workEntry = new WorkEntryImpl(
							workEntryObj.getLong("id"),
							workEntryObj.getString("description"),
							new Date(workEntryObj.getJSONObject("start").getLong("time") * 1000),
							new Date(workEntryObj.getJSONObject("end").getLong("time") * 1000),
							workEntryObj.getLong("taskid"),
							workEntryObj.getLong("employee"),
							workEntryObj.getLong("modifydate"));
					workEntryList.add(workEntry);
				} catch (JSONException e) {
					logger.log(Level.SEVERE, "catched in invalid json response for work entry. " + workEntryObj.toString(4), e);
				}
			}
			return new WorkEntryListResponse(ret.getLong("num_pages"), ret.getLong("page"), workEntryList);
		} catch (JSONException e) {
			throw new RequestErrorException("Error while fetching work entry list.", e);
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
			throw new RequestErrorException("Error when requesting page {" + path + "}", e);
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
	public String generateTestUser(WorkTrailAccessType accessType, WorkTrailScope[] scopes) throws RequestErrorException {
		Map<String, String> args = createAuthArgs(accessType, scopes);
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
