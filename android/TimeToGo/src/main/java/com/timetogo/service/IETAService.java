package com.timetogo.service;

import java.util.Date;

import com.timetogo.model.LocationResult;

public interface IETAService {
  public void setParameters(final LocationResult from, final LocationResult to, final long maxDrivingTimeInMinutes);

  public Date getLastExecutionDate();

  public void pause();

  public long getEta();

}
