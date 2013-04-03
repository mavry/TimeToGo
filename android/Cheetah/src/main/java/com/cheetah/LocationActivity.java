package com.cheetah;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cheetah.controller.ILocationController;
import com.cheetah.model.RouteResult;
import com.cheetah.model.RouteResultForLocations;
import com.cheetah.view.ILocationView;
import com.google.inject.Inject;

@ContentView(R.layout.main)
public class LocationActivity extends RoboActivity implements ILocationView {

  private Handler handler;
  private static final SimpleDateFormat DATE_FROMATTER = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
  private RouteResultForLocations routeResultForLocations;

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

  public LocationActivity() {
  }

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    //    setContentView(R.layout.main);
    super.onCreate(savedInstanceState);
    System.out.println("@ in LocationActivity.onCreate() fromText=" + fromText + " debugView=" + debugView);
    locationController.setView(this);
    handler = new Handler();
  }

  //  @Override
  //  public boolean onCreateOptionsMenu(final Menu menu) {
  //    getMenuInflater().inflate(R.menu.location, menu);
  //    return true;
  //  }

  //  public void onNotifyMeBtnClicked(final View v) throws ClientProtocolException, IOException, JSONException, URISyntaxException {
  //    if (v.getId() == R.id.notifyMe) {
  //      final long maxDrivingTimeInMin = Integer.parseInt(maxDrivingTimeView.getText().toString());
  //      final TimerTask myTimer = new MyTimer();
  //
  //      handler.postDelayed(myTimer, 10000);
  //    }
  //  }

  public void onGoBtnClicked(final View v) throws ClientProtocolException, IOException, JSONException, URISyntaxException {
    if (v.getId() == R.id.go) {
      final String from = fromText.getText().toString();
      final String to = toText.getText().toString();
      locationController.retrievesGeoLocations(from, to);
    }
  }

  public void onGeoLocations(final RouteResultForLocations routeResultForLocations) {
    final RouteResult routeResult = routeResultForLocations.getRouteResult();
    System.out.println("routeResult=" + routeResult);
    System.out.println("debugView=" + debugView);
    System.out.println("@ in LocationActivity.onGeoLocations() fromText=" + fromText + " debugView=" + debugView);

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
