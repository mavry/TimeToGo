package com.timetogo.activity;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.timetogo.Contants;

import de.akquinet.android.androlog.Log;

public class LocationHelper
{
  final LocationManager locationManager;
  private NLocationResult locationResult;
  boolean gpsEnabled = false;
  boolean networkEnabled = false;


  LocationHelper(LocationManager locationManager) {
    this.locationManager = locationManager;
  }

  LocationListener locationListener = new LocationListener() {
    public void onLocationChanged(Location location)
    {
      locationResult.gotLocation(location);

    }
    public void onProviderDisabled(String provider) {}
    public void onProviderEnabled(String provider) {}
    public void onStatusChanged(String provider, int status, Bundle extra) {}
  };


  public boolean getLocation(NLocationResult result)
  {
    locationResult = result;

    Log.i(Contants.TIME_TO_GO, "@@ locationManager is "+locationManager);

    //exceptions thrown if provider not enabled
    try
    {
      gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }
    catch (Exception ex) {
    }
    try
    {
      networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }
    catch (Exception ex) {
      Log.w(Contants.TIME_TO_GO, "@@ can't use Network");

    }

    //dont start listeners if no provider is enabled
    if(!gpsEnabled && !networkEnabled)
    {
      Log.w(Contants.TIME_TO_GO, "@@ both NETWORK and GPS are not available ");

      return false;
    }

    if (gpsEnabled) {
      Log.i(Contants.TIME_TO_GO, "@@ can use GPS");
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0l, 0.0f, locationListener);

    } else
    {
      Log.w(Contants.TIME_TO_GO, "@@ can't use GPS");
    }
    if (networkEnabled) {
      Log.i(Contants.TIME_TO_GO, "@@ can use NETWORK");
      locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0l, 0f, locationListener);
    } else
    {
      Log.w(Contants.TIME_TO_GO, "@@ can't use NETWORK");
    }

    GetLastLocation();
    return true;
  }


  private void GetLastLocation()
  {
//    locationManager.removeUpdates(locationListenerGps);
//    locationManager.removeUpdates(locationListenerNetwork);

    Location gpsLocation = null;
    Location networkLocation = null;

    if(gpsEnabled)
    {
      gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
    if(networkEnabled)
    {
      networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    //if there are both values use the latest one
    if(gpsLocation != null && networkLocation != null)
    {
      if(gpsLocation.getTime() > networkLocation.getTime())
      {
        locationResult.gotLocation(gpsLocation);
      }
      else
      {
        locationResult.gotLocation(networkLocation);
      }

      return;
    }

    if(gpsLocation != null)
    {
      locationResult.gotLocation(gpsLocation);
      return;
    }

    if(networkLocation != null)
    {
      locationResult.gotLocation(networkLocation);
      return;
    }

    locationResult.gotLocation(null);
  }

  public void stopLocationUpdates() {
    locationManager.removeUpdates(locationListener);
  }

  public static abstract class NLocationResult
  {
    public abstract void gotLocation(Location location);
  }
}