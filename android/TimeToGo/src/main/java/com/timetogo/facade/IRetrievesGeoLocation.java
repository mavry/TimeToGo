package com.timetogo.facade;

import com.timetogo.model.LocationResult;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface IRetrievesGeoLocation {

	public LocationResult[] retreive(final String query) throws ClientProtocolException, IOException, JSONException, URISyntaxException;

}
