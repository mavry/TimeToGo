package com.timetogo.service;

import java.util.Date;

public interface IETAService {
  public void setParameters(final String from, final String to, final long maxDrivingTimeInMinutes);

  public Date getLastExecutionDate();

  public void pause();

  public long getEta();

}
