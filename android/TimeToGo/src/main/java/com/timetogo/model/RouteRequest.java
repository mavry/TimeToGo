package com.timetogo.model;

public class RouteRequest {
  private final LocationResult from;
  private final LocationResult to;

  public RouteRequest(final LocationResult from, final LocationResult to) {
    this.from = from;
    this.to = to;
  }

  public LocationResult getFrom() {
    return from;
  }

  public LocationResult getTo() {
    return to;
  }

}
