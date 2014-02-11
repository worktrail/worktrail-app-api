package net.worktrail.appapi.response;

import java.net.URL;

public class CreateAuthResponse extends WorkTrailResponse {
	
	private String requestKey;
	private String authToken;
	private URL redirectUrl;

	public CreateAuthResponse(String requestKey, String authToken, URL redirectUrl) {
		this.requestKey = requestKey;
		this.authToken = authToken;
		this.redirectUrl = redirectUrl;
	}

	public String getRequestKey() {
		return requestKey;
	}
	
	public String getAuthToken() {
		return authToken;
	}
	
	public URL getRedirectUrl() {
		return redirectUrl;
	}
}
