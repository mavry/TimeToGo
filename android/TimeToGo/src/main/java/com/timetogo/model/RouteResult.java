package com.timetogo.model;

public class RouteResult {

  private final String routeName;
  private final long minutes;

  public RouteResult(final String routeName, final long minutes) {
    this.routeName = routeName;
    this.minutes = minutes;
  }

  public String getRouteName() {
    return routeName;
  }

  public long getETAInMinutes() {
    return minutes;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((routeName == null) ? 0 : routeName.hashCode());
    result = (int) (prime * result + minutes);
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final RouteResult other = (RouteResult) obj;
    if (routeName == null) {
      if (other.routeName != null) {
        return false;
      }
    } else if (!routeName.equals(other.routeName)) {
      return false;
    }
    if (minutes != other.minutes) {
      return false;
    }
    return true;
  }

}
