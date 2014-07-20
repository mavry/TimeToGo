package com.timetogo.facade;

import com.timetogo.model.LocationResult;
import com.timetogo.view.ILocationView;

public interface ILocationController {

  public void retrievesDrivingTime(LocationResult fromLocation, LocationResult toLocation, ILocationView locationActivity);


}
