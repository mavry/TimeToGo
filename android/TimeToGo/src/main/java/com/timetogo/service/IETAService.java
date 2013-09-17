package com.timetogo.service;

import com.timetogo.model.LocationResult;

import java.util.Date;

public interface IETAService {
  public void setParameters(final LocationResult from, final LocationResult to, final long maxDrivingTimeInMinutes);

  public Date getLastExecutionDate();

  public void pause();

  public long getDrivingTime();

  public String getRouteName();

}
