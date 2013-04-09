package com.timetogo;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import com.timetogo.controller.RetrievesGeoLocation;
import com.timetogo.model.LocationResult;

public class RetrievesGeoLocationTest {
  private final HttpClient httpClient = new DefaultHttpClient();

  RetrievesGeoLocation subject() {
    return new RetrievesGeoLocation(httpClient);
  }

  @Test(expected = LocationNotFoundException.class)
  public void returnsErrorWhenNoLocationFound() throws ClientProtocolException, IOException, JSONException, URISyntaxException {
    subject().retreive("blah");
  }

  @Test
  public void returnsLocationsArray() throws ClientProtocolException, IOException, JSONException, URISyntaxException {
    Assert.assertArrayEquals("locations array",
        new LocationResult[] { new LocationResult("Kerem Maharal", "32.64564167035331", "34.99047239259299") }, subject().retreive("Kerem Maharal"));
  }

  @Test
  public void returnsThreeLocations() throws ClientProtocolException, IOException, JSONException, URISyntaxException {
    Assert.assertEquals("locations arrays size", 3, subject().retreive("Rishon Lezion").length);
  }
}