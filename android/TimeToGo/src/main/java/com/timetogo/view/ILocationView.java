package com.timetogo.view;

import com.timetogo.model.RouteResultForLocations;

public interface ILocationView {

  void onGeoLocations(RouteResultForLocations routeResultForLocations);

  void onTimeToGo();

}
