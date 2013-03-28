package com.cheetah;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RetrievesGeoLocation {

	private final  static String WAZE_LOCATION_BY_ADDRESS_URL="http://www.waze.co.il/WAS/mozi?q=%s";
	private HttpClient client;
	
	
	
	public RetrievesGeoLocation(HttpClient client) {
		this.client = client;
	}



	public LocationResult[] retreive(String query) throws ClientProtocolException, IOException, JSONException, URISyntaxException{ 
		
		String url = String.format(WAZE_LOCATION_BY_ADDRESS_URL,URLEncoder.encode(query,"UTF-8")); 
	    HttpGet getMethod = new HttpGet(url);
	    ResponseHandler<String> responseHandler = new BasicResponseHandler();
	    String responseBody = client.execute(getMethod, responseHandler);
	    return buildLocationResults(responseBody);
	}



	private LocationResult[] buildLocationResults(String responseBody) throws JSONException {	
		JSONArray arr = new JSONArray(responseBody);
		if (arr.length()==0)  throw  new LocationNotFoundException();
		LocationResult[] results = new LocationResult[arr.length()]; 
		for (int i=0; i < arr.length(); i++){
			JSONObject location = arr.getJSONObject(i);
			results[i] = new LocationResult(location.getString("name"), location.getJSONObject("location").getString("lat"), location.getJSONObject("location").getString("lon"));			
		}
		return results;
	}

}
