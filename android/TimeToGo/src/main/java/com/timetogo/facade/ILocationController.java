package com.timetogo.facade;

import com.timetogo.activity.LocationActivity;
import com.timetogo.model.LocationResult;

public interface ILocationController {

  public void retrievesGeoLocations(LocationResult fromLocation, LocationResult toLocation);

  public void setView(LocationActivity locationActivity);


}
