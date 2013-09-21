package com.timetogo.model;

import java.io.Serializable;

public class LocationResult implements Serializable{

  private final String name;
  private final String lat;
  private final String lng;

  public LocationResult(final String name, final String lat, final String lng) {
    this.name = name;
    this.lat = lat;
    this.lng = lng;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((lng == null) ? 0 : lng.hashCode());
    result = prime * result + ((lat == null) ? 0 : lat.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    final LocationResult other = (LocationResult) obj;
    if (lat == null) {
      if (other.lat != null) {
        return false;
      }
    } else if (!lat.equals(other.lat)) {
      return false;
    }
    if (lng == null) {
      if (other.lng != null) {
        return false;
      }
    } else if (!lng.equals(other.lng)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }

  public String getName() {
    return name;
  }

  public String getX() {
    return lng;
  }

  public String getY() {
    return lat;
  }

}
