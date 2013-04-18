package com.timetogo.waze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.inject.Inject;
import com.timetogo.model.LocationResult;
import com.timetogo.model.RouteResult;

public class RetreivesWazeRouteResult {

  //	private static final String WAZE_ROUTES_BY_LOCATION = "http://www.waze.co.il/RoutingManager/routingRequest?to=x%3A%s+y%3A%s+bd%3Atrue&from=x%3A%s+y%3A%s+bd%3Atrue&returnJSON=true";
  private static final String PARAMS = "x:%s y:%s bd:true";
  private static final String WAZE_ROUTES_BY_LOCATION = "http://www.waze.co.il/RoutingManager/routingRequest?to=%s&from=%s&returnJSON=true";
  private final HttpClient client;

  @Inject
  public RetreivesWazeRouteResult(final HttpClient client) {
    this.client = client;
  }

  public RouteResult[] retreive(final LocationResult from, final LocationResult to) throws ClientProtocolException, IOException, JSONException {
    final String url = String.format(WAZE_ROUTES_BY_LOCATION, URLEncoder.encode(String.format(PARAMS, to.getX(), to.getY()), "UTF-8"),
        URLEncoder.encode(String.format(PARAMS, from.getX(), from.getY()), "UTF-8"));
    System.out.println(url);
    final HttpGet getMethod = new HttpGet(url);

    final HttpResponse response = client.execute(getMethod);
    final String content = extractResponseContent(response);

    return buildRouteResults(content);
  }

  private String extractResponseContent(final HttpResponse response) throws UnsupportedEncodingException, IOException {
    final BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

    String line = "";
    final StringBuilder sb = new StringBuilder();
    while ((line = rd.readLine()) != null) {
      sb.append(line);
    }
    return sb.toString();
  }

  private RouteResult[] buildRouteResults(final String responseBody) throws JSONException {
    final JSONObject body = new JSONObject(responseBody);
    final JSONObject response = body.getJSONObject("response");
    final String routeName = response.getString("routeName");
    final long crossTime = calcCrossTimeInMinutes(response.getJSONArray("results"));
    return new RouteResult[] { new RouteResult(routeName, crossTime) };

  }

  private long calcCrossTimeInMinutes(final JSONArray results) throws JSONException {
    int crossTimeInSeconds = 0;
    for (int i = 0; i < results.length(); i++) {
      final JSONObject result = results.getJSONObject(i);
      crossTimeInSeconds += result.getInt("crossTime");
    }
    return crossTimeInSeconds / 60;
  }

}
