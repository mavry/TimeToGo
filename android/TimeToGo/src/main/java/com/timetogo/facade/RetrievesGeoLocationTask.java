package com.timetogo.facade;

import android.os.AsyncTask;

import com.timetogo.Contants;
import com.timetogo.model.LocationResult;
import com.timetogo.model.RouteResult;
import com.timetogo.model.RouteResultForLocations;
import com.timetogo.waze.RetreivesWazeRouteResult;
import com.timetogo.waze.RetrievesWazeGeoLocation;

import de.akquinet.android.androlog.Log;

class RetrievesGeoLocationTask extends AsyncTask<LocationResult, String, RouteResultForLocations> {

  private final RetreivesWazeRouteResult retreivesRouteResult;
  private final IRouteResultForLocationsHandler routeResultForLocationsHandler;

  public RetrievesGeoLocationTask(final RetreivesWazeRouteResult retreivesRouteResult,
      final IRouteResultForLocationsHandler routeResultForLocationsHandler) {
    super();
    this.retreivesRouteResult = retreivesRouteResult;
    this.routeResultForLocationsHandler = routeResultForLocationsHandler;
  }

  @Override
  protected RouteResultForLocations doInBackground(final LocationResult... params) {
    try {
      final LocationResult fromLocation = params[0];
      final LocationResult toLocation= params[1];
      final RouteResult[] routeResults = retreivesRouteResult.retreive(fromLocation, toLocation);
      Log.i(Contants.TIME_TO_GO, "back from waze");
      return new RouteResultForLocations(fromLocation, toLocation, routeResults[0]);
    } catch (final Exception e) {
      Log.e(e.getMessage());
      return null;
    }
  }

  @Override
  protected void onPostExecute(final RouteResultForLocations routeResultForLocations) {
    routeResultForLocationsHandler.onRoutesResultForLocations(routeResultForLocations);
  }
}
