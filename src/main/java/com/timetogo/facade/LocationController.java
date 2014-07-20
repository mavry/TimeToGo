package com.timetogo.facade;

import com.google.inject.Inject;
import com.timetogo.Contants;
import com.timetogo.model.LocationResult;
import com.timetogo.model.RouteResultForLocations;
import com.timetogo.view.ILocationView;
import com.timetogo.waze.RetreivesWazeRouteResult;
import de.akquinet.android.androlog.Log;

public class LocationController implements ILocationController {

  @Inject
  private RetreivesWazeRouteResult retreivesRouteResult;


  public LocationController() {
  }

  public void retrievesDrivingTime(LocationResult fromLocation, LocationResult toLocation, final ILocationView locationView) {
    Log.i(Contants.TIME_TO_GO, "query Waze");
    new RetrievesGeoLocationTask(retreivesRouteResult, new IRouteResultForLocationsHandler() {

      public void onRoutesResultForLocations(final RouteResultForLocations routeResultForLocations) {
        Log.i(Contants.TIME_TO_GO, "got response from Waze");
        locationView.onRetrievesDrivingTime(routeResultForLocations);
      }
    }).execute(fromLocation, toLocation);
  }


}
