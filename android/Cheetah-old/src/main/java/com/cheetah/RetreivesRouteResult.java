package com.cheetah;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RetreivesRouteResult {

//	private static final String WAZE_ROUTES_BY_LOCATION = "http://www.waze.co.il/RoutingManager/routingRequest?to=x%3A%s+y%3A%s+bd%3Atrue&from=x%3A%s+y%3A%s+bd%3Atrue&returnJSON=true";
	private static final String PARAMS="x:%s y:%s bd:true";
	private static final String WAZE_ROUTES_BY_LOCATION = "http://www.waze.co.il/RoutingManager/routingRequest?to=%s&from=%s&returnJSON=true";
	private final HttpClient client;

	public RetreivesRouteResult(HttpClient client) {
		this.client = client;
	}

	public RouteResult[] retreive(LocationResult from, LocationResult to) throws ClientProtocolException, IOException, JSONException {
		String url = String.format(WAZE_ROUTES_BY_LOCATION,  URLEncoder.encode(String.format(PARAMS, to.getX(), to.getY()), "UTF-8"), URLEncoder.encode(String.format(PARAMS, from.getX(), from.getY()), "UTF-8"));
		System.out.println(url);
	    HttpGet getMethod = new HttpGet(url);
	    ResponseHandler<String> responseHandler = new BasicResponseHandler();
	    String responseBody = client.execute(getMethod, responseHandler);
	    return buildRouteResults(responseBody);
	}

	private RouteResult[] buildRouteResults(String responseBody) throws JSONException {
		JSONObject body = new JSONObject(responseBody);
		JSONObject response = body.getJSONObject("response");
		String routeName = response.getString("routeName");
		int crossTime = calcCrossTime(response.getJSONArray("results"));
		return new RouteResult[]{new RouteResult(routeName, crossTime)};
		
	}

	private int calcCrossTime(JSONArray results) throws JSONException {
		int crossTime=0;
		for (int i=0; i<results.length(); i++){
			JSONObject result = results.getJSONObject(i);
			crossTime+=result.getInt("crossTime");
		}
		return crossTime;
	}

}