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

  private final static String WAZE_LOCATION_BY_ADDRESS_URL = "http://www.waze.co.il/WAS/mozi?q=%s";
  private final HttpClient client;

  public RetrievesGeoLocation(final HttpClient client) {
    this.client = client;
  }

  public LocationResult[] retreive(final String query) throws ClientProtocolException, IOException, JSONException, URISyntaxException {

    final String url = String.format(WAZE_LOCATION_BY_ADDRESS_URL, URLEncoder.encode(query, "UTF-8"));
    final HttpGet getMethod = new HttpGet(url);
    final ResponseHandler<String> responseHandler = new BasicResponseHandler();
    final String responseBody = client.execute(getMethod, responseHandler);
    return buildLocationResults(responseBody);
  }

  private LocationResult[] buildLocationResults(final String responseBody) throws JSONException {
    final JSONArray arr = new JSONArray(responseBody);
    if (arr.length() == 0) {
      throw new LocationNotFoundException();
    }
    final LocationResult[] results = new LocationResult[arr.length()];
    for (int i = 0; i < arr.length(); i++) {
      final JSONObject location = arr.getJSONObject(i);
      results[i] = new LocationResult(location.getString("name"), location.getJSONObject("location").getString("lat"), location.getJSONObject(
          "location").getString("lon"));
    }
    return results;
  }

}
