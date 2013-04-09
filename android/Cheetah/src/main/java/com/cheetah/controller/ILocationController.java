package com.cheetah.controller;

import com.cheetah.activity.LocationActivity;

public interface ILocationController {

  public void retrievesGeoLocations(final String from, final String to);

  public void setView(LocationActivity locationActivity);

  void onNotifyMe(String from, String to, long maxDrivingTimeInMin);

}
