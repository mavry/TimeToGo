package com.timetogo.activity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;
import com.timetogo.R;
import com.timetogo.facade.ILocationController;
import com.timetogo.model.RouteResult;
import com.timetogo.model.RouteResultForLocations;
import com.timetogo.view.ILocationView;

import de.akquinet.android.androlog.Log;

@ContentView(R.layout.main)
public class LocationActivity extends RoboActivity implements ILocationView {

  @InjectView(R.id.debug)
  TextView debugView;

  @InjectView(R.id.eta)
  TextView etaView;

  @InjectView(R.id.inputMaxDrivingTime)
  TextView maxDrivingTimeView;

  @InjectView(R.id.inputFrom)
  EditText fromText;

  @InjectView(R.id.inputTo)
  EditText toText;

  @Inject
  ILocationController locationController;

  Handler h = new Handler();

  //
  //  @Inject
  //  AlarmManager alarmManager;
  //
  //  Intent intent;
  //
  //  private PendingIntent pintent;

  public LocationActivity() {
  }

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    //    setContentView(R.layout.main);
    super.onCreate(savedInstanceState);

    locationController.setView(this);
    //    intent = new Intent(this, ETAService.class);
    //    pintent = PendingIntent.getService(this, 0, intent, 0);
    //    Log.i("@ in LocationActivity.onCreate() intent=" + intent);

  }

  public void onNotifyMeBtnClicked(final View v) throws ClientProtocolException, IOException, JSONException, URISyntaxException {
    if (v.getId() == R.id.notifyMe) {
      notifyMe(0);
    }
  }

  public void notifyMe(final long maxDrivingTimeInMin) {

    Log.i("@@ NotifyME button was clicked  with maxDrivingTimeInMin=" + maxDrivingTimeInMin + " ThreadID = " + Thread.currentThread());
    //
    //    alarmManager.cancel(pintent);
    //    intent.putExtra("kuku", "riku");
    //    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 1000, pintent);
    h.postDelayed(new Runnable() {

      public void run() {
        Log.i("@@ in thread " + Thread.currentThread());
        locationController.onNotifyMe(fromText.getText().toString(), toText.getText().toString(),
            Integer.parseInt(maxDrivingTimeView.getText().toString()));
        h.postDelayed(this, TimeUnit.SECONDS.toMillis(10));
      }
    }, TimeUnit.SECONDS.toMillis(10));

  }

  public void onGoBtnClicked(final View v) throws ClientProtocolException, IOException, JSONException, URISyntaxException {
    if (v.getId() == R.id.go) {
      final String from = fromText.getText().toString();
      final String to = toText.getText().toString();
      locationController.retrievesGeoLocations(from, to);
    }
  }

  public void onGeoLocations(final RouteResultForLocations routeResultForLocations) {
    final RouteResult routeResult = routeResultForLocations.getRouteResult();
    Log.i("@@ in LocationActivity.onGeoLocations() fromText=" + fromText + " debugView=" + debugView);

    debugView.setText(String.format("%d min via %s", routeResult.getSeconds() / 60, routeResult.getRouteName()));
    etaView.setText(String.valueOf(routeResult.getSeconds() / 60));
  }

  public void onTimeToGo() {
    debugView.setText("@@ TIME TO GO");
    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
    tg.startTone(ToneGenerator.TONE_PROP_BEEP);

  }

}
