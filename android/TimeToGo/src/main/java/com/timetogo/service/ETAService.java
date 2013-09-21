package com.timetogo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Binder;
import android.os.IBinder;
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

import java.util.Date;

import de.akquinet.android.androlog.Log;
import roboguice.RoboGuice;
import roboguice.event.EventManager;
import roboguice.inject.ContextScope;
import roboguice.service.RoboIntentService;
import roboguice.service.event.OnCreateEvent;

public class ETAService extends RoboIntentService implements IETAService {
  private static final int NOTIFICATION_ID = 0;
  public static final String TRAFFIC_UPDATE_EVENT = "com.timetogo.ETAService.TRAFFIC_UPDATE_EVENT" ;
  LocationResult fromLocation;
  LocationResult toLocation;
  long drivingTime;
  String routeName;

  public class MyBinder extends Binder {
    public ETAService getService() {
      return ETAService.this;
    }
  }

  private Date lastExecution = new Date();

  @Inject
  RetreivesWazeRouteResult retreivesRouteResult;
  @Inject
  RetrievesWazeGeoLocation retrievesWazeGeoLocation;

  @Inject
  PowerManager powerManager;

  @Inject
  NotificationManager notificationManager;

  private final IBinder mBinder = new MyBinder();
  private long maxDrivingTimeInMinutes;
  private PowerManager.WakeLock wakeLock;
  private boolean waitingForTrafficToGoDown = true;
  private MediaPlayer mplayer;

  public ETAService() {
    super("ETAService");
    Log.i(Contants.TIME_TO_GO, "@@ ["+Thread.currentThread().getName()+"] in ETAService() constrcutor");

  }

  @Override
  public IBinder onBind(final Intent intent) {
    Log.i(Contants.TIME_TO_GO, "@@ ["+Thread.currentThread().getName()+"] ETAService was binded");
    return mBinder;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  private boolean shouldIDoSomething() {
    return (fromLocation != null) && waitingForTrafficToGoDown;
  }

  private void handleCommand(final Intent intent) {
    try {
      acquireWakeLock();
      final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

      final RouteResult[] routeResultForLocations = retreivesRouteResult.retreive(fromLocation, toLocation);
      routeName = routeResultForLocations[0].getRouteName();
      drivingTime = routeResultForLocations[0].getDrivingTimeInMinutes();
      //
      drivingTime = 12;
      Log.i(Contants.TIME_TO_GO, "@@ ["+Thread.currentThread().getName()+"] ETAService got info from waze. drivingTime:" + drivingTime + " min via " + routeName);
      if (isItTimeToGo(drivingTime)) {
        notify(drivingTime);
        waitingForTrafficToGoDown = false;
      }
      tg.startTone(ToneGenerator.TONE_PROP_BEEP);

      lastExecution = new Date();

    } catch (final Exception ex) {
      Log.e(Contants.TIME_TO_GO, "@@ got exception while retreving ETA ", ex);
    } finally {
      releaseWakeLock();
    }
  }

  @SuppressWarnings("deprecation")
  private void notify(final long drivingTime) {

    notifyActivity(drivingTime);
    final Intent intent = new Intent(this, LocationActivity.class);
    updateIntentWithData(drivingTime, intent);

    Log.i(Contants.TIME_TO_GO, "@@ it is time to go, broadcast event to the activity");

    sendBroadcast(intent);

    final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    @SuppressWarnings("deprecation")
    final Notification notification = new Notification(R.drawable.ic_launcher, "Time To Go", System.currentTimeMillis());

    notification.setLatestEventInfo(this, "TimeToGo", "drivingTime is " + drivingTime + " minutes", contentIntent);
    notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

    Log.i(Contants.TIME_TO_GO, "@@ it is time to go, fire notification");
    notificationManager.notify(NOTIFICATION_ID, notification);
    mplayer.start();
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

  private boolean isItTimeToGo(final long eta) {
    Log.i(Contants.TIME_TO_GO, "comparing eta " + eta + " vs " + maxDrivingTimeInMinutes);
    return eta <= maxDrivingTimeInMinutes;
  }

  private void releaseWakeLock() {
    wakeLock.release();
  }

  private void acquireWakeLock() {
    wakeLock.acquire();
  }

  @Override
  public void onCreate() {
    final Injector injector = getInjector();
    eventManager = injector.getInstance(EventManager.class);
    final ContextScope scope = injector.getInstance(ContextScope.class);
    scope.enter(this);
    injector.injectMembers(this);
    wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Contants.TIME_TO_GO);
    super.onCreate();
    Log.i(Contants.TIME_TO_GO, "@@ ["+Thread.currentThread().getName()+"] ETAService.onCreate()");

    eventManager.fire(new OnCreateEvent());
    mplayer = MediaPlayer.create(this, R.raw.its_time_to_go);

    final AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
    am.getStreamVolume(AudioManager.STREAM_MUSIC);
    am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

    mplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

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
    Log.i(Contants.TIME_TO_GO, "@@ ["+Thread.currentThread().getName()+"] in ETAService.onHandleIntent()");
    if (shouldIDoSomething()) {
      handleCommand(intent);
    } else {
      Log.i(Contants.TIME_TO_GO, "@@ do nothing....");
    }
  }

  //TODO: should be synchronized
  public void setParameters(final LocationResult fromLocation, final LocationResult toLocation, final long maxDrivingTimeInMinutes) {
    this.fromLocation = fromLocation;
    this.toLocation = toLocation;
    this.maxDrivingTimeInMinutes = maxDrivingTimeInMinutes;
    waitingForTrafficToGoDown = true;
  }

  public void pause() {
  }

  public long getDrivingTime() {
    return drivingTime;
  }

  public String getRouteName() {
    return routeName;
  }

}