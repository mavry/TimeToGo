package com.timetogo.facade;

import com.google.inject.Inject;
import com.timetogo.Contants;
import com.timetogo.activity.LocationActivity;
import com.timetogo.model.LocationResult;
import com.timetogo.model.RouteResultForLocations;
import com.timetogo.view.ILocationView;
import com.timetogo.waze.RetreivesWazeRouteResult;
import com.timetogo.waze.RetrievesWazeGeoLocation;

import de.akquinet.android.androlog.Log;

public class LocationController implements ILocationController {

  @Inject
  private RetreivesWazeRouteResult retreivesRouteResult;
  @Inject
  private ILocationView locationView;

  public LocationController() {
  }

  public void retrievesGeoLocations(LocationResult fromLocation, LocationResult toLocation) {
    Log.i(Contants.TIME_TO_GO, "query waze");
    new RetrievesGeoLocationTask(retreivesRouteResult, new IRouteResultForLocationsHandler() {

      public void onRoutesResultForLocations(final RouteResultForLocations routeResultForLocations) {
        Log.i(Contants.TIME_TO_GO, "got response from waze");
        locationView.onGeoLocations(routeResultForLocations);
      }
    }).execute(fromLocation, toLocation);
  }


  public void setView(final LocationActivity locationActivity) {
    locationView = locationActivity;
  }

}
