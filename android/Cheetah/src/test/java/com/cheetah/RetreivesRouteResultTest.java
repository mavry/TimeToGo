package com.cheetah;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RetreivesRouteResultTest {

	private static final String JSON = "{     'response': {         'routeName': 'Kerem Maharal',         'results': [             {                 'length': 66,                 'crossTime': 99,                 'crossTimeWithoutRealTime': 0,                              },             {                 'length': 66,                 'crossTime': 99,                 'crossTimeWithoutRealTime': 0,                              }          ],              } }";
	private HttpClient httpClient;
	private RetreivesRouteResult  subject;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setup() throws ClientProtocolException, IOException {
		httpClient = Mockito.mock(HttpClient.class);
		Mockito.when(httpClient.execute(Mockito.any(HttpUriRequest.class), Mockito.any(ResponseHandler.class))).thenReturn(JSON);
		subject = new RetreivesRouteResult(httpClient);
	}

	// -routeName
	// -results [-crossTime]

	

	@Test
	public void testReturnsRoute() throws ClientProtocolException, IOException, JSONException {
		LocationResult from = new LocationResult("Kerem Maharal",
				"32.64564167035331", "34.99047239259299");
		LocationResult to = new LocationResult("Kerem Maharal",
				"32.64564167035331", "34.99047239259299");
		Assert.assertArrayEquals(
				new RouteResult[] { new RouteResult("Kerem Maharal", 198) },
				subject.retreive(from, to));
	}

}
