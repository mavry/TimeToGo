package com.timetogo.activity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.timetogo.Contants;
import com.timetogo.R;
import com.timetogo.facade.ILocationController;
import com.timetogo.model.LocationResult;
import com.timetogo.model.RouteResult;
import com.timetogo.model.RouteResultForLocations;
import com.timetogo.service.ETAService;
import com.timetogo.service.IETAService;
import com.timetogo.view.ILocationView;

import de.akquinet.android.androlog.Log;

@ContentView(R.layout.main)
public class LocationActivity extends RoboActivity implements ILocationView {

  @InjectView(R.id.debug)
  TextView debugView;

  @InjectView(R.id.eta)
  TextView etaView;

  @InjectView(R.id.inputMaxDrivingTime)
  EditText maxDrivingTimeText;

  @InjectView(R.id.inputFrom)
  EditText fromText;

  @InjectView(R.id.inputTo)
  EditText toText;

  @InjectView(R.id.notifyMe)
  Button notifyMeButton;

  @Inject
  ILocationController locationController;

  @Inject
  AlarmManager alarmManager;

  Intent intent;

  private PendingIntent pintent;
  private long eta;

  private LocationResult fromLocation;

  private LocationResult toLocation;

  private long initialEta;

  Handler h = new Handler();
  DateFormat sdf = new DateFormat();
  private IETAService service;
  private final ServiceConnection mConnection = new ServiceConnection() {

    public void onServiceConnected(final ComponentName className, final IBinder binder) {
      service = ((ETAService.MyBinder) binder).getService();
      Toast.makeText(LocationActivity.this, "Connected", Toast.LENGTH_SHORT).show();
      Log.i(Contants.TIME_TO_GO, "@@ got reference to service");
    }

    public void onServiceDisconnected(final ComponentName className) {
      //      service = null;
    }
  };

  private Dialog x;

  public LocationActivity() {
  }

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    Log.i(Contants.TIME_TO_GO, "@@ onCreate " + savedInstanceState);
    // setContentView(R.layout.main);
    super.onCreate(savedInstanceState);
    intent = new Intent(this, ETAService.class);
    intent.putExtra("kuku", "riku");
    pintent = PendingIntent.getService(this, 0, intent, 0);

    locationController.setView(this);
    alarmManager.cancel(pintent);
    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 60 * 1000, pintent);
    doBindService();

    h.postDelayed(new Runnable() {

      public void run() {
        if (service != null) {
          final Date date = service.getLastExecutionDate();
          eta = service.getEta();
          final String text = String.format("eta: %s @ %s", String.valueOf(eta), DateFormat.format("HH:mm", date));
          Log.i(Contants.TIME_TO_GO, "@@ updateing debug with " + text);
          debugView.setText(text);
          h.postDelayed(this, 60 * 1000);
        }
      }
    }, 60 * 1000);
  }

  public void onNotifyMeBtnClicked(final View v) throws ClientProtocolException, IOException, JSONException, URISyntaxException {
    if (v.getId() == R.id.notifyMe) {
      notifyMe(Long.parseLong(maxDrivingTimeText.getText().toString()));
    }
  }

  public void notifyMe(final long maxDrivingTimeInMin) {
    Log.i(Contants.TIME_TO_GO,
        "@@ NotifyME button was clicked  with maxDrivingTimeInMin=" + maxDrivingTimeInMin + " ThreadID = " + Thread.currentThread());
    service.setParameters(fromLocation, toLocation, maxDrivingTimeInMin);
  }

  void doBindService() {
    final boolean bind = bindService(new Intent(this, ETAService.class), mConnection, Context.BIND_AUTO_CREATE);
    Log.i("bind sucess = " + bind);
  }

  public void onGoBtnClicked(final View v) throws ClientProtocolException, IOException, JSONException, URISyntaxException {
    if (v.getId() == R.id.go) {
      final String from = fromText.getText().toString();
      final String to = toText.getText().toString();
      locationController.retrievesGeoLocations(from, to);

    }
  }

  public void onTimeToGo() {
    debugView.setText("@@ TIME TO GO");
    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
    tg.startTone(ToneGenerator.TONE_PROP_BEEP);
  }

  public void onGeoLocations(final RouteResultForLocations routeResultForLocations) {
    final RouteResult routeResult = routeResultForLocations.getRouteResult();
    Log.i("@@ in LocationActivity.onGeoLocations() fromText=" + fromText + " debugView=" + debugView);

    debugView.setText(String.format("%d min via %s", routeResult.getETAInMinutes(), routeResult.getRouteName()));
    etaView.setText(String.valueOf(routeResult.getETAInMinutes()));
    updateFields(routeResultForLocations, routeResult);

    final LayoutInflater inflater = getLayoutInflater();
    final View dialoglayout = inflater.inflate(R.layout.popup, null);
    //    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    //    builder.setView(dialoglayout);
    //    builder.show();

    //
    //    x = new Dialog(this) {
    //
    //      @Override
    //      protected void onCreate(final Bundle savedInstanceState) {
    //        setContentView(R.layout.popup);
    //        super.onCreate(savedInstanceState);
    //        setTitle("Driving Time: " + initialEta + "min (via " + routeResult.getRouteName() + ")");
    //
    //      }
    //
    //    };
    //    x.show();

    final EditText view = new EditText(this);

    new AlertDialog.Builder(LocationActivity.this).setTitle("Driving Time: " + initialEta + "min (via " + routeResult.getRouteName() + ")")
    //                                                  .setMessage("Notify me when driving time goes below ").
                                                  .setView(dialoglayout).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(final DialogInterface dialog, final int whichButton) {
                                                      final Editable value = view.getText();
                                                    }
                                                  }).setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                                                    public void onClick(final DialogInterface dialog, final int whichButton) {
                                                      // Do nothing.
                                                    }
                                                  }).show();
  }

  private void updateFields(final RouteResultForLocations routeResultForLocations, final RouteResult routeResult) {
    initialEta = routeResult.getETAInMinutes();
    fromLocation = routeResultForLocations.getFrom();
    toLocation = routeResultForLocations.getTo();
  }
}
