package com.timetogo.facade;

import com.google.inject.Inject;
import com.timetogo.activity.LocationActivity;
import com.timetogo.model.RouteResultForLocations;
import com.timetogo.view.ILocationView;
import com.timetogo.waze.RetreivesWazeRouteResult;
import com.timetogo.waze.RetrievesWazeGeoLocation;

import de.akquinet.android.androlog.Log;

public class LocationController implements ILocationController {

  @Inject
  private RetrievesWazeGeoLocation retrievesGeoLocation;
  @Inject
  private RetreivesWazeRouteResult retreivesRouteResult;
  @Inject
  private ILocationView locationView;

  public LocationController() {
    //    this.locationView = locationView;
  }

  public void retrievesGeoLocations(final String from, final String to) {
    new RetrievesGeoLocationTask(retrievesGeoLocation, retreivesRouteResult, new IRouteResultForLocationsHandler() {

      public void onRoutesResultForLocations(final RouteResultForLocations routeResultForLocations) {
        locationView.onGeoLocations(routeResultForLocations);
      }
    }).execute(from, to);
  }

  public void onNotifyMe(final String from, final String to, final long maxDrivingTimeInMin) {
    new RetrievesGeoLocationTask(retrievesGeoLocation, retreivesRouteResult, new IRouteResultForLocationsHandler() {

      public void onRoutesResultForLocations(final RouteResultForLocations routeResultForLocations) {
        if (isItTimeToGo(maxDrivingTimeInMin, routeResultForLocations)) {
          locationView.onTimeToGo();
        } else {
          Log.i("@@ driving is too long. will keep check");
        }
      }

    }).execute(from, to);
  }

  public void setView(final LocationActivity locationActivity) {
    locationView = locationActivity;
  }

  private boolean isItTimeToGo(final long maxDrivingTimeInMin, final RouteResultForLocations routeResultForLocations) {
    final int routeInSeconds = routeResultForLocations.getRouteResult().getSeconds();
    Log.i("@@ comparing travel time " + routeInSeconds + " vs " + 60 * maxDrivingTimeInMin);
    return routeInSeconds <= 60 * maxDrivingTimeInMin;
  }

}
