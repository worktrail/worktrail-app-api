package net.worktrail.hub.sync;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import net.worktrail.hub.sync.response.CreateAuthResponse;
import net.worktrail.hub.sync.response.RequestErrorException;

import org.json.JSONException;
import org.json.JSONObject;

import at.tapo.worktrail.api.javaonly.WorkTrailConnectionUtils;

public class WorkTrailAuth {
	private String appKey;
	private String secretApiKey;
//	private static final String WORKTRAIL_SERVER = "https://worktrail.net";
	private static final String WORKTRAIL_SERVER = "http://tools.sphene.net:8888";

	
	public WorkTrailAuth(String appKey, String secretApiKey) {
		this.appKey = appKey;
		this.secretApiKey = secretApiKey;
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


	private JSONObject requestPage(String path, Map<String, String> args) {
		try {
			args.put("appkey", appKey);
			args.put("secretapikey", secretApiKey);
			
			URL url = new URL(WORKTRAIL_SERVER + "/" + path);
			String query = WorkTrailConnectionUtils.getQuery(args.entrySet());
			StringBuilder ret = WorkTrailConnectionUtils.requestDataFromUrl(url, query.getBytes("UTF-8"), "WorkTrail Hub");
			return new JSONObject(ret.toString());
		} catch (IOException | JSONException e) {
			throw new RuntimeException("Error when requesting page {" + path + "}", e);
		}
	}
}
