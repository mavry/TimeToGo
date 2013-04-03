package com.cheetah.controller;

import com.cheetah.LocationActivity;

public interface ILocationController {

  public void retrievesGeoLocations(final String from, final String to);

  public void setView(LocationActivity locationActivity);
}
