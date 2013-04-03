package com.cheetah.model;

public class RouteResultForLocations {
  private final LocationResult from;
  private final LocationResult to;
  private final RouteResult routeResult;

  public RouteResultForLocations(final LocationResult from, final LocationResult to, final RouteResult routeResult) {
    super();
    this.from = from;
    this.to = to;
    this.routeResult = routeResult;
  }

  public LocationResult getFrom() {
    return from;
  }

  public LocationResult getTo() {
    return to;
  }

  public RouteResult getRouteResult() {
    return routeResult;
  }

}
