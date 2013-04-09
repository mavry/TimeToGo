package com.cheetah.controller;

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

import com.cheetah.model.LocationResult;
import com.cheetah.model.RouteResult;
import com.google.inject.Inject;

import de.akquinet.android.androlog.Log;

public class RetreivesRouteResult {

  //	private static final String WAZE_ROUTES_BY_LOCATION = "http://www.waze.co.il/RoutingManager/routingRequest?to=x%3A%s+y%3A%s+bd%3Atrue&from=x%3A%s+y%3A%s+bd%3Atrue&returnJSON=true";
  private static final String PARAMS = "x:%s y:%s bd:true";
  private static final String WAZE_ROUTES_BY_LOCATION = "http://www.waze.co.il/RoutingManager/routingRequest?to=%s&from=%s&returnJSON=true";
  private final HttpClient client;

  @Inject
  public RetreivesRouteResult(final HttpClient client) {
    this.client = client;
  }

  public RouteResult[] retreive(final LocationResult from, final LocationResult to) throws ClientProtocolException, IOException, JSONException {
    Log.i("@@ checking RouteResult");
    final String url = String.format(WAZE_ROUTES_BY_LOCATION, URLEncoder.encode(String.format(PARAMS, to.getX(), to.getY()), "UTF-8"),
        URLEncoder.encode(String.format(PARAMS, from.getX(), from.getY()), "UTF-8"));
    System.out.println(url);
    final HttpGet getMethod = new HttpGet(url);
    final ResponseHandler<String> responseHandler = new BasicResponseHandler();
    final String responseBody = client.execute(getMethod, responseHandler);
    return buildRouteResults(responseBody);
  }

  private RouteResult[] buildRouteResults(final String responseBody) throws JSONException {
    final JSONObject body = new JSONObject(responseBody);
    final JSONObject response = body.getJSONObject("response");
    final String routeName = response.getString("routeName");
    final int crossTime = calcCrossTime(response.getJSONArray("results"));
    return new RouteResult[] { new RouteResult(routeName, crossTime) };

  }

  private int calcCrossTime(final JSONArray results) throws JSONException {
    int crossTime = 0;
    for (int i = 0; i < results.length(); i++) {
      final JSONObject result = results.getJSONObject(i);
      crossTime += result.getInt("crossTime");
    }
    return crossTime;
  }

}
