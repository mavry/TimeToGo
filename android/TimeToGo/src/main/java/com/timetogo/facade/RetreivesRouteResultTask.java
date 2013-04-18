package com.timetogo.facade;

import android.os.AsyncTask;
import android.util.Log;

import com.timetogo.Contants;
import com.timetogo.model.LocationResult;
import com.timetogo.model.RouteRequest;
import com.timetogo.model.RouteResult;
import com.timetogo.model.RouteResultForLocations;
import com.timetogo.waze.RetreivesWazeRouteResult;

public class RetreivesRouteResultTask extends AsyncTask<RouteRequest, String, RouteResultForLocations> {

  private final RetreivesWazeRouteResult retreivesRouteResult;
  private final IRouteResultForLocationsHandler routeResultForLocationsHandler;

  public RetreivesRouteResultTask(final RetreivesWazeRouteResult retreivesRouteResult,
      final IRouteResultForLocationsHandler routeResultForLocationsHandler) {
    super();
    this.retreivesRouteResult = retreivesRouteResult;
    this.routeResultForLocationsHandler = routeResultForLocationsHandler;
  }

  @Override
  protected RouteResultForLocations doInBackground(final RouteRequest... routeRequets) {
    final LocationResult fromLocation = routeRequets[0].getFrom();
    final LocationResult toLocation = routeRequets[0].getTo();
    try {
      final RouteResult[] routeResults = retreivesRouteResult.retreive(fromLocation, toLocation);
      return new RouteResultForLocations(fromLocation, toLocation, routeResults[0]);
    } catch (final Exception ex) {
      Log.e(Contants.TIME_TO_GO, "exception while retreivesRouteResult", ex);
    }
    return null;
  }

  @Override
  protected void onPostExecute(final RouteResultForLocations routeResultForLocations) {
    routeResultForLocationsHandler.onRoutesResultForLocations(routeResultForLocations);
  }
}
