package com.cheetah.controller;

import android.os.AsyncTask;

import com.cheetah.LocationActivity;
import com.cheetah.model.LocationResult;
import com.cheetah.model.RouteResult;
import com.cheetah.model.RouteResultForLocations;
import com.cheetah.view.ILocationView;
import com.google.inject.Inject;

import de.akquinet.android.androlog.Log;

public class LocationController implements ILocationController {

  @Inject
  private RetrievesGeoLocation retrievesGeoLocation;
  @Inject
  private RetreivesRouteResult retreivesRouteResult;

  @Inject
  private ILocationView locationView;

  public LocationController() {
    //    this.locationView = locationView;
  }

  public void retrievesGeoLocations(final String from, final String to) {
    new RetrievesGeoLocationTask().execute(from, to);
  }

  private class RetrievesGeoLocationTask extends AsyncTask<String, String, RouteResultForLocations> {

    @Override
    protected RouteResultForLocations doInBackground(final String... params) {
      try {
        final String fromText = params[0];
        final String toText = params[1];
        final LocationResult fromLocation = retrievesGeoLocation.retreive(fromText)[0];
        final LocationResult toLocation = retrievesGeoLocation.retreive(toText)[0];
        final RouteResult[] routeResults = retreivesRouteResult.retreive(fromLocation, toLocation);
        return new RouteResultForLocations(fromLocation, toLocation, routeResults[0]);
      } catch (final Exception e) {
        Log.e(e.getMessage());
        return null;
      }
    }

    @Override
    protected void onPostExecute(final RouteResultForLocations routeResultForLocations) {
      locationView.onGeoLocations(routeResultForLocations);
    }
  }

  public void setView(final LocationActivity locationActivity) {
    locationView = locationActivity;
  }

}
