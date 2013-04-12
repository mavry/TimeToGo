package com.timetogo.facade;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.timetogo.model.LocationResult;

public interface IRetrievesGeoLocation {

	public LocationResult[] retreive(final String query) throws ClientProtocolException, IOException, JSONException, URISyntaxException;

}
