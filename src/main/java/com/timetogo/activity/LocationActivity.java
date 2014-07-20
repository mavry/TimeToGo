package com.timetogo.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.google.inject.Inject;
import com.timetogo.Contants;
import com.timetogo.R;
import com.timetogo.facade.ILocationController;
import com.timetogo.model.LocationResult;
import com.timetogo.model.RouteResult;
import com.timetogo.model.RouteResultForLocations;
import com.timetogo.service.ETAService;
import com.timetogo.view.ILocationView;
import de.akquinet.android.androlog.Log;
import org.json.JSONException;
import org.json.JSONObject;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//@ContentView(R.layout.main)
public class LocationActivity extends RoboActivity implements ILocationView {

  @InjectView(R.id.webview)
  private WebView webView;

  @Inject
  ILocationController locationController;

  @Inject
  AlarmManager alarmManager;

  LocationManager locationManager;

  PendingIntent pintent;

  Handler h = new Handler();

  private LocationControl locationControlTask;
  SuppliesLocation suppliesLocation;


  private TimeToGoServiceReceiver eventReceiver = new TimeToGoServiceReceiver();
  private Long drivingTime;
  private String routeName;
  private boolean timeToGo;
  private Date updatedAt;

  public LocationActivity() {
  }

  final class MyWebChromeClient extends WebChromeClient {
    @Override
    public boolean onJsAlert(final WebView view, final String url,
                             final String message, final JsResult result) {
      Log.i(Contants.TIME_TO_GO, "alert: " + message);
      result.confirm();
      return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
      Log.i(Contants.TIME_TO_GO + " FROM BROWSER: " + message + " -- From line "
        + lineNumber + " of "
        + sourceID);
    }
  }

  private class MyJavascriptInterface {
    private final LocationActivity activity;

    public MyJavascriptInterface(final LocationActivity activity) {
      this.activity = activity;
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void onGo(final String startLat, final String startLng, final String destinationLat, final String destinationLng)
      throws IOException, JSONException,
      URISyntaxException {
      Log.i(Contants.TIME_TO_GO, "onGO [" + startLat + "," + startLng + "]->[" + destinationLat + "," + destinationLng + "]");
      LocationResult startLocation = new LocationResult("", startLat, startLng);
      LocationResult destinationLocation = new LocationResult("", destinationLat, destinationLng);
      activity.onGoBtnClicked(startLocation, destinationLocation);
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void onNotify(String startLocationAsString, String destinationLocationAsString, final long maxDrivingTime) {
      Log.i(Contants.TIME_TO_GO,
        "@@ NotifyME button was clicked  with maxDrivingTimeInMin=" + maxDrivingTime + " startLocation = " + startLocationAsString);
      try {
        JSONObject stratLocationAsJosn = new JSONObject(startLocationAsString);
        JSONObject destinationLocationAsJson = new JSONObject(destinationLocationAsString);

        final LocationResult startLocation = new LocationResult("", stratLocationAsJosn.getString("lat"), stratLocationAsJosn.getString("lng"));

        LocationResult destinationLocation = new LocationResult("", destinationLocationAsJson.getString("lat"), destinationLocationAsJson.getString("lng"));

        launchETAService(startLocation, destinationLocation, maxDrivingTime);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    @JavascriptInterface
    public String getCurrentLocation() {
      Log.i(Contants.TIME_TO_GO, "@@ getCurrentLocation");
      final LocationActivity act = this.activity;
      h.postDelayed(new Runnable() {
        @Override
        public void run() {

          suppliesLocation.getCurrentLocation();
          locationControlTask = new LocationControl();
          locationControlTask.execute(act);

        }
      }, 100);
      return "";
    }


    @JavascriptInterface
    public void openUrl(String url) {
      Log.i(Contants.TIME_TO_GO, "@@ about to run url [" + url + "]");
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      startActivity(intent);
      Log.i(Contants.TIME_TO_GO, "@@ done");
    }

    @JavascriptInterface
    public String getLiveInfo() {
      return "{\"drivingTime\": " + drivingTime + ", \"routeName\": \"" + routeName + "\", \"updatedAt\": \"" + updatedAt + "\", \"timeToGo\": " + timeToGo + "}";
    }

    @JavascriptInterface
    public void reset() {
      Log.i(Contants.TIME_TO_GO, "@@ reset");
      if (pintent != null) {
        alarmManager.cancel(pintent);
      }
    }
  }

  private void launchETAService(LocationResult startLocation, LocationResult destinationLocation, long maxDrivingTime) {
    if (pintent != null) {
      alarmManager.cancel(pintent);
    }
    Intent intent = new Intent(this, ETAService.class);
    intent.putExtra("startLocation", startLocation);
    intent.putExtra("destinationLocation", destinationLocation);
    intent.putExtra("maxDrivingTime", maxDrivingTime);

    pintent = PendingIntent.getService(this, 0, intent, 0);

    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 20 * 1000, pintent);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    Log.i(Contants.TIME_TO_GO, "@@ HERE AT ON CONFIGURATION CHANGED " + newConfig.orientation);
  }

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    Log.i(Contants.TIME_TO_GO, "@@ [SYS] onCreate");
    super.onCreate(savedInstanceState);
    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    setContentView(R.layout.main);
    suppliesLocation = new SuppliesLocation(locationManager);
//  webView.loadUrl("file:///android_asset/angularApp/timeToGo.html?_=" + System.currentTimeMillis());
    webView.loadUrl("http://mavry.github.io/angularApp/timeToGo.html?_="+System.currentTimeMillis());

    webView.addJavascriptInterface(new MyJavascriptInterface(this), "androidInterface");

    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setDatabasePath(getFilesDir().getPath() + "/databases/");
    settings.setAllowUniversalAccessFromFileURLs(true);
    settings.setDomStorageEnabled(true);


    webView.setWebChromeClient(new MyWebChromeClient());

    h.postDelayed(new Runnable() {
      @Override
      public void run() {
        invokeJS("onCreate");
      }
    }, 1000);

  }

  private void printLocation(Location location) {
    if (location == null) {
      Log.w(Contants.TIME_TO_GO, "@@ got Location: null");
    } else {
      Log.i(Contants.TIME_TO_GO, "@@ got Location: " + SuppliesLocation.locationToString(location));
    }
  }


  public void onGoBtnClicked(LocationResult fromLocation, LocationResult toLocation)
    throws IOException, JSONException,
    URISyntaxException {
    locationController.retrievesDrivingTime(fromLocation, toLocation, this);
  }


  public void onTimeToGo() {
    final ToneGenerator tg = new ToneGenerator(
      AudioManager.STREAM_NOTIFICATION, 100);
    tg.startTone(ToneGenerator.TONE_PROP_BEEP);
  }

  @Override
  public void onRetrievesDrivingTime(
    final RouteResultForLocations routeResultForLocations) {
    Log.i(Contants.TIME_TO_GO, "@@ in LocationActivity.onRetrievesDrivingTime() routeResultForLocations=" + routeResultForLocations);
    final RouteResult routeResult = routeResultForLocations.getRouteResult();
    Log.i(Contants.TIME_TO_GO, "@@ in LocationActivity.onRetrievesDrivingTime() routeResult=" + routeResult);
    Log.i(Contants.TIME_TO_GO, String.format(Locale.getDefault(), "%d min via %s", routeResult.getDrivingTimeInMinutes(), routeResult.getRouteName()));

    h.post(new Runnable() {

      @Override
      public void run() {
        invokeJS("onDrivingTime",
          String.valueOf(routeResult.getDrivingTimeInMinutes()), "'" + routeResult.getRouteName() + "'");
      }

    });

  }

  @Override
  protected void onNewIntent(final Intent intent) {
    Log.i(Contants.TIME_TO_GO, "@@ Notification was clicked");
    if (intent.getExtras() == null) {
      Log.w(Contants.TIME_TO_GO, "no extras");
    } else {
      handleEvent(intent);
    }
    super.onNewIntent(intent);

  }

  private void handleEvent(Intent intent) {
    if (intent.getExtras().getBoolean("timeToGo")) timeToGo = true;
    drivingTime = intent.getExtras().getLong("drivingTime");
    routeName = intent.getExtras().getString("routeName");
    updatedAt = (Date) (intent.getExtras().get("updatedAt"));

    Log.i(Contants.TIME_TO_GO, "@@ drivingTime = " + drivingTime + "min via:" + routeName + " timeToGo = " + timeToGo);
    if (timeToGo) {
      invokeJS("onTimeToGo", "'" + drivingTime + "'", "'" + routeName + "'");
      alarmManager.cancel(pintent);
    }
  }


  private void invokeJS(final String methodName, final String... args) {
    final StringBuilder sb = new StringBuilder("javascript:");
    sb.append("window.Application.");
    sb.append(methodName).append("(");
    boolean firstTime = true;
    for (final String arg : args) {
      if (!firstTime) {
        sb.append(",");
      }
//			sb.append("'");
      sb.append(arg);
//			sb.append("'");
      firstTime = false;
    }
    sb.append(");");
    if (methodName.equals("onCurrentLocation") || methodName.equals("onDrivingTime") || methodName.equals("onTimeToGo")) {
      Log.i(Contants.TIME_TO_GO, "@@ " + methodName + " " + sb.toString());
      webView.loadUrl(sb.toString());
    }
  }

  /* Request updates at startup */
  @Override
  protected void onResume() {
    registerReceiver(eventReceiver, new IntentFilter(ETAService.TRAFFIC_UPDATE_EVENT));
    super.onResume();

    Log.i(Contants.TIME_TO_GO, "@@ [SYS] onResume");
    invokeJS("onResume");
  }

  /* Remove the locationlistener updates when Activity is paused */
  @Override
  protected void onPause() {
    super.onPause();
    if (eventReceiver != null) unregisterReceiver(eventReceiver);
    Log.i(Contants.TIME_TO_GO, "@@ [SYS] onPause");
    invokeJS("onPause");
  }


  private void updateLocation(final Location location) {
    printLocation(location);
    h.post(
      new Runnable() {
        @Override
        public void run() {
          if (location == null) {
            invokeJS("onCurrentLocation");
          } else {
            invokeJS("onCurrentLocation", getLocationAsJson(location), "'" + location.getProvider() + "'");
          }
        }
      }
    );
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.i(Contants.TIME_TO_GO, "@@ [SYS] onStart");
    invokeJS("onStart");

  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.i(Contants.TIME_TO_GO, "@@ [SYS] onStop");
    suppliesLocation.stop();
  }

  @Override
  protected void onDestroy() {
    Log.i(Contants.TIME_TO_GO, "@@ [SYS] onDestroy");

//    if (pintent != null) {
//      alarmManager.cancel(pintent);
//    }

    super.onDestroy();
  }

  public String getLocationAsJson(Location location) {
    if (location == null) return "null";
    return String.format("{\"lat\": \"%s\", \"lng\":\"%s\", \"accuracy\":\"%s\", \"provider\":\"%s\" }", location.getLatitude(), location.getLongitude(), location.getAccuracy(), location.getProvider());
  }


  private class LocationControl extends AsyncTask<Context, Void, Void> {

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(Context... params) {
      //Wait 10 seconds to see if we can get a location from either network or GPS, otherwise stop
      Log.i(Contants.TIME_TO_GO, "@@ ****[" + Thread.currentThread().getName() + "] background checking... ");

      Long t0 = Calendar.getInstance().getTimeInMillis();
      while (!suppliesLocation.canUseLocation() && stillHaveTimeToWaitUntilGettingAGoodLocation(t0)) {
        try {
          Log.i(Contants.TIME_TO_GO, "@@ **** no good enough location");

          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      return null;
    }

    private boolean stillHaveTimeToWaitUntilGettingAGoodLocation(Long t) {
      return Calendar.getInstance().getTimeInMillis() - t < 20000;
    }

    @Override
    protected void onPostExecute(final Void unused) {
      Location loc = suppliesLocation.getBestLocation();
      SuppliesLocation.LocationQuality q = suppliesLocation.getQuality();
      int counter = suppliesLocation.getCounter();
      Log.i(Contants.TIME_TO_GO, "@@ **** got location with q = " + q + " counter = " + counter + " *** " + getLocationAsJson(loc));
      if (suppliesLocation.canUseLocation()) {
        Log.i(Contants.TIME_TO_GO, "@@ It is a great location");
        updateLocation(loc);
      } else {
        Log.w(Contants.TIME_TO_GO, "@@ **** NOT good enough location *** ");
      }
      suppliesLocation.stop();
    }
  }


  public class TimeToGoServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      Log.i(Contants.TIME_TO_GO, "@@ got update from service...  " + intent.getAction());
      handleEvent(intent);
    }
  }
}
