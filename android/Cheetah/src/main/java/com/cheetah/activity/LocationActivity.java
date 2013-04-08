package com.cheetah.activity;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cheetah.R;
import com.cheetah.controller.ETAService;
import com.cheetah.controller.ILocationController;
import com.cheetah.model.RouteResult;
import com.cheetah.model.RouteResultForLocations;
import com.cheetah.view.ILocationView;
import com.google.inject.Inject;

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

  @Inject
  AlarmManager alarmManager;

  Intent intent;

  private PendingIntent pintent;

  public LocationActivity() {
  }

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    //    setContentView(R.layout.main);
    super.onCreate(savedInstanceState);

    locationController.setView(this);
    intent = new Intent(this, ETAService.class);
    pintent = PendingIntent.getService(this, 0, intent, 0);
    Log.i("@ in LocationActivity.onCreate() intent=" + intent);// " fromText=" + fromText + " debugView=" + debugView);

  }

  //  @Override
  //  public boolean onCreateOptionsMenu(final Menu menu) {
  //    getMenuInflater().inflate(R.menu.location, menu);
  //    return true;
  //  }

  public void onNotifyMeBtnClicked(final View v) throws ClientProtocolException, IOException, JSONException, URISyntaxException {
    if (v.getId() == R.id.notifyMe) {
      //      final long maxDrivingTimeInMin = Integer.parseInt(maxDrivingTimeView.getText().toString());
      notifyMe(0);

    }
  }

  public void notifyMe(final long maxDrivingTimeInMin) {

    Log.i("@ NotifyME button was clicked  with maxDrivingTimeInMin=" + maxDrivingTimeInMin + " ThreadID = " + Thread.currentThread());

    alarmManager.cancel(pintent);
    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 1000, pintent);

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
    Log.i("@ in LocationActivity.onGeoLocations() fromText=" + fromText + " debugView=" + debugView);

    debugView.setText(String.format("%d min via %s", routeResult.getSeconds() / 60, routeResult.getRouteName()));
    etaView.setText(String.valueOf(routeResult.getSeconds() / 60));
  }

  /*
    private class MyTimer extends TimerTask {

      @Override
      public void run() {
        new AsyncTask<RouteResultForLocations, String, RouteResult>() {
          @Override
          protected RouteResult doInBackground(final RouteResultForLocations... params) {
            RouteResult[] roteResults;
            try {
              roteResults = retreivesRouteResult.retreive(routeResultForLocations.getFrom(), routeResultForLocations.getTo());
              return roteResults[0];
            } catch (final Exception ex) {
              Log.e(ex.getMessage());
              return null;
            }
          }

          @Override
          protected void onPostExecute(final RouteResult routeResult) {
            final String now = DATE_FROMATTER.format(new Date());
            debugView.setText(String.format("(%s) %d min via %s", now, routeResult.getSeconds() / 60, routeResult.getRouteName()));
            if (routeResult.getSeconds() < maxDrivingTimeInMin * 60) {
              debugView.setText(String.format("(%s) TIME TO GO: %d min via %s", now, routeResult.getSeconds() / 60, routeResult.getRouteName()));
            } else {
              handler.postDelayed(null, 10000);
            }
          }
        }.execute(routeResultForLocations);
      }
    }
  */
}
