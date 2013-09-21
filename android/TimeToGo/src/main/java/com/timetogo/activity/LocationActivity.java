package com.timetogo.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Locale;

import de.akquinet.android.androlog.Log;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

//@ContentView(R.layout.main)
public class LocationActivity extends RoboActivity implements ILocationView {

	@InjectView(R.id.webview)
  private WebView webView;

	@Inject
	ILocationController locationController;

	@Inject
	AlarmManager alarmManager;

	LocationManager locationManager;


  Handler h = new Handler();
	private IETAService service;

  private LocationControl locationControlTask;
  SuppliesLocation suppliesLocation;



	private final ServiceConnection mConnection = new ServiceConnection() {

		@Override
    public void onServiceConnected(final ComponentName className,
				final IBinder binder) {
			service = ((ETAService.MyBinder) binder).getService();
			Toast.makeText(LocationActivity.this, "Connected",
					Toast.LENGTH_SHORT).show();
			Log.i(Contants.TIME_TO_GO, "@@ got reference to service");
		}

		@Override
    public void onServiceDisconnected(final ComponentName className) {
		}
	};

	long maxDrivingTime;

	@SuppressWarnings("unused")
	private long initialDrivingTime;

  private TimeToGoServiceReceiver eventReceiver = new TimeToGoServiceReceiver();

  public LocationActivity() {
	}

	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(final WebView view, final String url,
				final String message, final JsResult result) {
			Log.i(Contants.TIME_TO_GO, "alert: "+message);
			result.confirm();
			return true;
		}
    @SuppressWarnings("deprecation")
    @Override
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            Log.i(Contants.TIME_TO_GO+" FROM BROWSER: "+message + " -- From line "
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
		public void onGo(final String startLat,final String startLng, final String destinationLat, final String destinationLng)
        throws IOException, JSONException,
				URISyntaxException {
			Log.i(Contants.TIME_TO_GO, "onGO [" + startLat+","+startLng + "]->["+ destinationLat+","+destinationLng+ "]");
            LocationResult startLocation = new LocationResult("", startLat, startLng);
            LocationResult destinationLocation = new LocationResult("", destinationLat, destinationLng);
			activity.onGoBtnClicked(startLocation,destinationLocation);
		}

		@SuppressWarnings("unused")
        @JavascriptInterface
		public void onNotify(String startLocation, String destinationLocation, final long mdt) {
			Log.i(Contants.TIME_TO_GO,
					"@@ NotifyME button was clicked  with maxDrivingTimeInMin="	+ mdt+" startLocation = "+startLocation);
			maxDrivingTime = mdt;
      try {
        JSONObject stratLocationAsJosn = new JSONObject(startLocation);
        JSONObject destinationLocationAsJson = new JSONObject(destinationLocation);

        service.setParameters(new LocationResult("", stratLocationAsJosn.getString("lat"), stratLocationAsJosn.getString("lng")),
              new LocationResult("", destinationLocationAsJson.getString("lat"), destinationLocationAsJson.getString("lng")), maxDrivingTime);
      } catch (JSONException e) {
        e.printStackTrace();
      }
		}
    @JavascriptInterface
        public String getLocation() {
          Log.i(Contants.TIME_TO_GO, "@@ getLocation");
      final LocationActivity act = this.activity;
      h.postDelayed(new Runnable() {
        @Override
        public void run() {

          suppliesLocation.getLocation();
          locationControlTask = new LocationControl();
          locationControlTask.execute(act);

        }
      }, 100);
          return "";
        }
	}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(Contants.TIME_TO_GO, "@@ HERE AT ON CONFIGURATION CHANGED "+newConfig.orientation);
    }

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.i(Contants.TIME_TO_GO, "@@ onCreate");
		super.onCreate(savedInstanceState);
  		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		setContentView(R.layout.main);
    suppliesLocation = new SuppliesLocation(locationManager);
    webView.loadUrl("file:///android_asset/angularApp/timeToGo.html?_=" + System.currentTimeMillis());
//        mywebview.loadUrl("http://mavry.github.io/angularApp/timeToGo.html?_="+System.currentTimeMillis());

    webView.addJavascriptInterface(new MyJavascriptInterface(this), "androidInterface");

    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setAllowUniversalAccessFromFileURLs(true);
    settings.setDomStorageEnabled(true);
    settings.setDatabasePath(getFilesDir().getPath() + "/databases/");


    webView.setWebChromeClient(new MyWebChromeClient());

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                invokeJS("onCreate");
            }
        }, 1000);

    Intent intent = new Intent(this, ETAService.class);
    PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

		alarmManager.cancel(pintent);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 60 * 1000, pintent);

		doBindService();
	}

	private void printLocation(Location location) {
    if (location==null) {
      Log.w(Contants.TIME_TO_GO, "got Location: null");
    } else {
		  String lat = String.valueOf((location.getLatitude()));
		  String lng = String.valueOf(location.getLongitude());
		  Log.i(Contants.TIME_TO_GO, "got Location: " + lat + "," + lng);
    }
	}

	void doBindService() {
		final boolean bind = bindService(new Intent(this, ETAService.class),
                mConnection, Context.BIND_AUTO_CREATE);
		Log.i(Contants.TIME_TO_GO, "bind success = " + bind);
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
    Log.i(Contants.TIME_TO_GO, "@@ in LocationActivity.onRetrievesDrivingTime() routeResultForLocations="+ routeResultForLocations);
    final RouteResult routeResult = routeResultForLocations.getRouteResult();
		Log.i(Contants.TIME_TO_GO, "@@ in LocationActivity.onRetrievesDrivingTime() routeResult="+ routeResult);
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
      updateUI(intent);
		}
		super.onNewIntent(intent);

	}

  private void updateUI(Intent intent) {
     boolean timeToGo  = false;
    if (intent.getExtras().getBoolean("timeToGo")) timeToGo = true;
    final Long drivingTime = intent.getExtras().getLong("drivingTime");
    final String routeName = intent.getExtras().getString("routeName");

    Log.i(Contants.TIME_TO_GO, "@@ drivingTime = "+drivingTime+"min via:"+routeName+" timeToGo = " + timeToGo);
    invokeJS("onTimeToGo", "'"+drivingTime+"'", "'"+routeName+"'");

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
        if (methodName.equals("onCurrentLocation") || methodName.equals("onDrivingTime") || methodName.equals("onTimeToGo")){
            Log.i(Contants.TIME_TO_GO, "@@ "+methodName+ " "+sb.toString());
          webView.loadUrl(sb.toString());
        }
	}

    /* Request updates at startup */
    @Override
    protected void onResume() {
      registerReceiver(eventReceiver, new IntentFilter(ETAService.TRAFFIC_UPDATE_EVENT));
      super.onResume();
      Log.i(Contants.TIME_TO_GO, "@@ onResume");
      invokeJS("onResume");
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
      if (eventReceiver != null) unregisterReceiver(eventReceiver);

      Log.i(Contants.TIME_TO_GO, "@@ onPause");
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
                            invokeJS("onCurrentLocation", getLocationAsJson(location), "'"+location.getProvider()+"'");
                        }
                    }
                });
    }

    @Override
    protected void onStart () {
        super.onStart();
        Log.i(Contants.TIME_TO_GO, "on Start");
        invokeJS("onStart");

    }

  @Override
  protected void onStop () {
    super.onStop();
    Log.i(Contants.TIME_TO_GO, "on Stop");
    suppliesLocation.stop();
  }
    public String getLocationAsJson(Location location){
      if (location==null) return "null";
        return String.format("{\"lat\": \"%s\", \"lng\":\"%s\", \"accuracy\":\"%s\", \"provider\":\"%s\" }", location.getLatitude() , location.getLongitude(), location.getAccuracy(), location.getProvider());
    }


  private class LocationControl extends AsyncTask<Context, Void, Void>
  {

    @Override
    protected void onPreExecute()
    {
    }

    @Override
    protected Void doInBackground(Context... params)
    {
      //Wait 10 seconds to see if we can get a location from either network or GPS, otherwise stop
      Log.i(Contants.TIME_TO_GO, "@@ ****["+Thread.currentThread().getName()+"] background checking... ");

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
    protected void onPostExecute(final Void unused)
    {
      Location loc = suppliesLocation.getBestLocation();
      SuppliesLocation.LocationQuality q = suppliesLocation.getQuality();
      int counter = suppliesLocation.getCounter();
      Log.i(Contants.TIME_TO_GO, "@@ **** got location with q = " + q + " counter = " + counter + " *** " + getLocationAsJson(loc));
      if (suppliesLocation.canUseLocation())
      {
        Log.i(Contants.TIME_TO_GO, "@@ It is a great location");
      }
      else
      {
        Log.w(Contants.TIME_TO_GO, "@@ **** NOT good enough location *** ");
      }
      suppliesLocation.stop();
      updateLocation(loc);
    }
  }



  public class TimeToGoServiceReceiver extends BroadcastReceiver
  {
    @Override
    public void onReceive(Context context, Intent intent)
    {
      Log.i(Contants.TIME_TO_GO, "@@ got update from service... it is probably time to go. action is "+intent.getAction());
      updateUI(intent);
    }
  }
}
