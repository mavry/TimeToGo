package com.timetogo.facade;

import android.os.AsyncTask;

import com.timetogo.Contants;
import com.timetogo.model.LocationResult;
import com.timetogo.model.RouteResult;
import com.timetogo.model.RouteResultForLocations;
import com.timetogo.waze.RetreivesWazeRouteResult;
import com.timetogo.waze.RetrievesWazeGeoLocation;

import de.akquinet.android.androlog.Log;

class RetrievesGeoLocationTask extends AsyncTask<String, String, RouteResultForLocations> {

  private final RetrievesWazeGeoLocation retrievesGeoLocation;
  private final RetreivesWazeRouteResult retreivesRouteResult;
  private final IRouteResultForLocationsHandler routeResultForLocationsHandler;

  public RetrievesGeoLocationTask(final RetrievesWazeGeoLocation retrievesGeoLocation, final RetreivesWazeRouteResult retreivesRouteResult,
      final IRouteResultForLocationsHandler routeResultForLocationsHandler) {
    super();
    this.retrievesGeoLocation = retrievesGeoLocation;
    this.retreivesRouteResult = retreivesRouteResult;
    this.routeResultForLocationsHandler = routeResultForLocationsHandler;
  }

  @Override
  protected RouteResultForLocations doInBackground(final String... params) {
    try {
      final String fromText = params[0];
      final String toText = params[1];
      Log.i(Contants.TIME_TO_GO, "query waze - from");

      final LocationResult fromLocation = retrievesGeoLocation.retreive(fromText)[0];
      Log.i(Contants.TIME_TO_GO, "query waze - to");
      final LocationResult toLocation = retrievesGeoLocation.retreive(toText)[0];
      Log.i(Contants.TIME_TO_GO, "query waze - route");
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
