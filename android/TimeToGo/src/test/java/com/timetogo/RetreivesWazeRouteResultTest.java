package com.timetogo;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.timetogo.model.LocationResult;
import com.timetogo.model.RouteResult;
import com.timetogo.waze.RetreivesWazeRouteResult;

public class RetreivesWazeRouteResultTest {

  private static final String JSON = "{     'response': {         'routeName': 'Kerem Maharal',         'results': [             {                 'length': 66,                 'crossTime': 99,                 'crossTimeWithoutRealTime': 0,                              },             {                 'length': 66,                 'crossTime': 99,                 'crossTimeWithoutRealTime': 0,                              }          ],              } }";
  private HttpClient httpClient;
  private RetreivesWazeRouteResult subject;

  @SuppressWarnings("unchecked")
  @Before
  public void setup() throws ClientProtocolException, IOException {
    httpClient = Mockito.mock(HttpClient.class);
    Mockito.when(httpClient.execute(Matchers.any(HttpUriRequest.class), Matchers.any(ResponseHandler.class))).thenReturn(JSON);
    subject = new RetreivesWazeRouteResult(httpClient);
  }

  @Test
  public void testReturnsRoute() throws ClientProtocolException, IOException, JSONException {
    final LocationResult from = new LocationResult("Kerem Maharal", "32.64564167035331", "34.99047239259299");
    final LocationResult to = new LocationResult("Kerem Maharal", "32.64564167035331", "34.99047239259299");
    Assert.assertArrayEquals(new RouteResult[] { new RouteResult("Kerem Maharal", 198) }, subject.retreive(from, to));
  }

}
