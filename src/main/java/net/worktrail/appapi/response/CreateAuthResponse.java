package net.worktrail.appapi.response;

import java.net.URL;

/**
 * Response to a create auth request - returns a URL the user has to visit to authenticate
 * the app. After the user has authorized the app, the app can use the given authToken
 * to access private data of the user.
 * @author herbert
 */
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
