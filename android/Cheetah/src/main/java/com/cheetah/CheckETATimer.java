package com.cheetah;

import java.util.TimerTask;

import com.cheetah.controller.RetreivesRouteResult;
import com.cheetah.model.LocationResult;
import com.cheetah.model.RouteResult;

public class CheckETATimer extends TimerTask {

  private final RetreivesRouteResult retreivesRouteResult;
  private final LocationResult fromLocation;
  private final LocationResult toLocation;

  CheckETATimer(final RetreivesRouteResult retreivesRouteResult, final LocationResult fromLocation, final LocationResult toLocation) {
    this.retreivesRouteResult = retreivesRouteResult;
    this.fromLocation = fromLocation;
    this.toLocation = toLocation;
  }

  @Override
  public void run() {
    try {
      final RouteResult[] roteResults = retreivesRouteResult.retreive(fromLocation, toLocation);
      final RouteResult roteResult = roteResults[0];

    } catch (final Exception e) {
    }
  }

}
