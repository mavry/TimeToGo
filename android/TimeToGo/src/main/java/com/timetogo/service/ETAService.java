package com.timetogo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.PowerManager;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.timetogo.Contants;
import com.timetogo.R;
import com.timetogo.activity.LocationActivity;
import com.timetogo.model.LocationResult;
import com.timetogo.model.RouteResult;
import com.timetogo.waze.RetreivesWazeRouteResult;
import com.timetogo.waze.RetrievesWazeGeoLocation;

import org.json.JSONException;

import java.io.IOException;
import java.util.Date;

import de.akquinet.android.androlog.Log;
import roboguice.RoboGuice;
import roboguice.event.EventManager;
import roboguice.inject.ContextScope;
import roboguice.service.RoboIntentService;
import roboguice.service.event.OnCreateEvent;

public class ETAService extends RoboIntentService { //implements IETAService {
  private static final int NOTIFICATION_ID = 0;
  public static final String TRAFFIC_UPDATE_EVENT = "com.timetogo.ETAService.TRAFFIC_UPDATE_EVENT" ;
//  private final DefaultHttpClient client;
//  LocationResult fromLocation;
//  LocationResult toLocation;
//  long drivingTime;
//  String routeName;
//
//  public class MyBinder extends Binder {
//    public ETAService getService() {
//      return ETAService.this;
//    }
//  }

  private Date lastExecution = new Date();

  @Inject
  RetreivesWazeRouteResult retreivesRouteResult;
  @Inject
  RetrievesWazeGeoLocation retrievesWazeGeoLocation;

  @Inject
  PowerManager powerManager;

  @Inject
  NotificationManager notificationManager;


  private PowerManager.WakeLock wakeLock;


  public ETAService() {
    super("ETAService");
    Log.i(Contants.TIME_TO_GO, "@@ ["+Thread.currentThread().getName()+"] in ETAService() constrcutor");

  }
  private void handleCommand(LocationResult startLocation, LocationResult destinationLocation, long maxDrivingTime) throws JSONException, IOException {


      final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

      final RouteResult[] routeResultForLocations = retreivesRouteResult.retreive(startLocation, destinationLocation);
      String routeName = routeResultForLocations[0].getRouteName();
      long drivingTime = routeResultForLocations[0].getDrivingTimeInMinutes();
      //
      Log.i(Contants.TIME_TO_GO, "@@ ["+Thread.currentThread().getName()+"] ETAService got info from waze. drivingTime:" + drivingTime + " min via " + routeName);
      if (isItTimeToGo(drivingTime, maxDrivingTime)) {
        notify(drivingTime);
      }
      tg.startTone(ToneGenerator.TONE_PROP_BEEP);
      lastExecution = new Date();

  }

  @SuppressWarnings("deprecation")
  private void notify(final long drivingTime) {

    notifyActivity(drivingTime);
    final Intent intent = new Intent(this, LocationActivity.class);
    updateIntentWithData(drivingTime, intent);

    Log.i(Contants.TIME_TO_GO, "@@ it is time to go, broadcast event to the activity");

    sendBroadcast(intent);

    createSystemNotificationAndSound(drivingTime, intent);
  }

  private void createSystemNotificationAndSound(long drivingTime, Intent intent) {
    MediaPlayer player = MediaPlayer.create(this, R.raw.its_time_to_go);

    final AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
    am.getStreamVolume(AudioManager.STREAM_MUSIC);
    am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

    player.setAudioStreamType(AudioManager.STREAM_MUSIC);


    final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    @SuppressWarnings("deprecation")
    final Notification notification = new Notification(R.drawable.ic_launcher, "Time To Go", System.currentTimeMillis());

    notification.setLatestEventInfo(this, "TimeToGo", "drivingTime is " + drivingTime + " minutes", contentIntent);
    notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

    Log.i(Contants.TIME_TO_GO, "@@ it is time to go, fire notification");
    notificationManager.notify(NOTIFICATION_ID, notification);
    player.start();
  }

  private void notifyActivity(long drivingTime) {
    final Intent intent = new Intent(TRAFFIC_UPDATE_EVENT);
    updateIntentWithData(drivingTime, intent);

    Log.i(Contants.TIME_TO_GO, "@@ it is time to go, broadcast event to the activity");

    sendBroadcast(intent);
  }

  private void updateIntentWithData(long drivingTime, Intent intent) {
    intent.putExtra("timeToGo", true);
    intent.putExtra("drivingTime", drivingTime);
  }

  private boolean isItTimeToGo(final long eta, long maxDrivingTimeInMinutes) {
    Log.i(Contants.TIME_TO_GO, "comparing eta " + eta + " vs " + maxDrivingTimeInMinutes);
    return eta <= maxDrivingTimeInMinutes;
  }

  private void releaseWakeLock() {
//    wakeLock.release();
  }

  private void acquireWakeLock() {
//    wakeLock.acquire();
  }

  @Override
  public void onCreate() {
    final Injector injector = getInjector();
    eventManager = injector.getInstance(EventManager.class);
    final ContextScope scope = injector.getInstance(ContextScope.class);
    scope.enter(this);
    injector.injectMembers(this);
//    wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Contants.TIME_TO_GO);
    super.onCreate();
    Log.i(Contants.TIME_TO_GO, "@@ ["+Thread.currentThread().getName()+"] ETAService.onCreate()");

    eventManager.fire(new OnCreateEvent());
    super.onCreate();
  }

  public Date getLastExecutionDate() {
    return lastExecution;
  }

  private Injector getInjector() {
    return RoboGuice.getInjector(this);
  }

  @Override
  protected void onHandleIntent(final Intent intent) {
    try {
      acquireWakeLock();

      Log.i(Contants.TIME_TO_GO, "@@ ["+Thread.currentThread().getName()+"] in ETAService.onHandleIntent()");
  //    if (shouldIDoSomething()) {
      Bundle extras = intent.getExtras();
      LocationResult startLocation = (LocationResult) extras.getSerializable("startLocation");
      LocationResult destinationLocation = (LocationResult) extras.getSerializable("destinationLocation");
      long maxDrivingTime = extras.getLong("maxDrivingTime");

      handleCommand(startLocation, destinationLocation, maxDrivingTime);
    } catch (Exception ex) {
      Log.e(Contants.TIME_TO_GO, "@@ got exception in ETAService ", ex);
    }
    finally {
       releaseWakeLock();;
    }
  }




}