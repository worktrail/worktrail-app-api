package net.worktrail.hub.sync;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import at.tapo.worktrail.api.javaonly.WorkTrailConnectionUtils;

public class WorkTrailAuth {
	private String appKey;
	private String secretApiKey;
//	private static final String WORKTRAIL_SERVER = "https://worktrail.net";
	private static final String WORKTRAIL_SERVER = "http://tools.sphene.net";

	
	public WorkTrailAuth(String appKey, String secretApiKey) {
		this.appKey = appKey;
		this.secretApiKey = secretApiKey;
	}
	
	
	public void createAuthRequest(WorkTrailScope[] scopes) {
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
		requestPage("rest/token/request/", args);
	}


	private void requestPage(String path, Map<String, String> args) {
		try {
			URL url = new URL(WORKTRAIL_SERVER + "/" + path);
			String query = WorkTrailConnectionUtils.getQuery(args.entrySet());
			StringBuilder ret = WorkTrailConnectionUtils.requestDataFromUrl(url, query.getBytes("UTF-8"), "WorkTrail Hub");
			
		} catch (IOException e) {
			throw new RuntimeException("Error when requesting page {" + path + "}", e);
		}
	}
}
