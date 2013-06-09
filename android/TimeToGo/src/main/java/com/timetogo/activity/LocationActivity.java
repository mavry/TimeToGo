package com.timetogo.activity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
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

import de.akquinet.android.androlog.Log;

//@ContentView(R.layout.main)
public class LocationActivity extends RoboActivity implements ILocationView, LocationListener {

	@InjectView(R.id.webview)
	WebView mywebview;

	@Inject
	ILocationController locationController;

	@Inject
	AlarmManager alarmManager;

	LocationManager locationManager;

	private PendingIntent pintent;
	Intent intent;

	// private long drivingTime;
	// private Date lastUpdated;
	// private String routeName;

	private LocationResult fromLocation;
	private LocationResult toLocation;

	Handler h = new Handler();
	DateFormat sdf = new DateFormat();
	private IETAService service;

	private final ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(final ComponentName className,
				final IBinder binder) {
			service = ((ETAService.MyBinder) binder).getService();
			Toast.makeText(LocationActivity.this, "Connected",
					Toast.LENGTH_SHORT).show();
			Log.i(Contants.TIME_TO_GO, "@@ got reference to service");
		}

		public void onServiceDisconnected(final ComponentName className) {
		}
	};

	long maxDrivingTime;

	@SuppressWarnings("unused")
	private long initialDrivingTime;

	private String provider;
    private Location myLocation;

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
	}

	private class MyJavascriptInterface {
		private final LocationActivity activity;

		public MyJavascriptInterface(final LocationActivity activity) {
			this.activity = activity;
		}

		@SuppressWarnings("unused")
		public void onGo(final String fromLat,final String fromLng, final String toLat, final String toLng)
        throws ClientProtocolException, IOException, JSONException,
				URISyntaxException {
			Log.i(Contants.TIME_TO_GO, "onGO [" + fromLat+","+fromLng + "]->["+ toLat+","+toLng+ "]");
            LocationResult fromLocation = new LocationResult("", fromLng, fromLat);
            LocationResult toLocation = new LocationResult("", toLng, toLat);
			activity.onGoBtnClicked(fromLocation,toLocation);
		}

		@SuppressWarnings("unused")
		public void onNotify(final long mdt) {
			Log.i(Contants.TIME_TO_GO,
					"@@ NotifyME button was clicked  with maxDrivingTimeInMin="
							+ mdt);
			maxDrivingTime = mdt;
			service.setParameters(fromLocation, toLocation, maxDrivingTime);
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
		// mywebview.loadUrl("http://mavry.github.io/");
		mywebview.loadUrl("file:///android_asset/index.html");
		mywebview.getSettings().setJavaScriptEnabled(true);
		mywebview.addJavascriptInterface(new MyJavascriptInterface(this),
				"androidInterface");
		mywebview.setWebChromeClient(new MyWebChromeClient());

		intent = new Intent(this, ETAService.class);
		pintent = PendingIntent.getService(this, 0, intent, 0);

		locationController.setView(this);
		alarmManager.cancel(pintent);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 1000, 60 * 1000, pintent);
		doBindService();
        obtainProvider();
		
		h.postDelayed(new Runnable() {

			public void run() {
				if (service != null && maxDrivingTime > 0) {
					updateUI(service.getDrivingTime(), service.getRouteName(),
							service.getLastExecutionDate());
				}
				h.postDelayed(this, 20 * 1000);
			}

		}, 60 * 1000);
	}

	// private void updateDrivingTimeFeilds(final long drivingTime, final String
	// routeName, final Date lastExecutionDate) {
	// this.drivingTime = drivingTime;
	// this.routeName = routeName;
	// lastUpdated = lastExecutionDate;
	// }

	private void obtainProvider() {
        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE | Criteria.ACCURACY_COARSE );
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

		provider = locationManager.getBestProvider(criteria, true);
		Log.i(Contants.TIME_TO_GO, "provider = " + provider);
	}

	private void printLocation(Location location) {
		String lat = String.valueOf((location.getLatitude()));
		String lng = String.valueOf(location.getLongitude());
		Log.i(Contants.TIME_TO_GO, "got Location: " + lat + "," + lng);
	}

	void doBindService() {
		final boolean bind = bindService(new Intent(this, ETAService.class),
                mConnection, Context.BIND_AUTO_CREATE);
		Log.i(Contants.TIME_TO_GO, "bind success = " + bind);
	}

	public void onGoBtnClicked(LocationResult fromLocation, LocationResult toLocation)
			throws ClientProtocolException, IOException, JSONException,
			URISyntaxException {
		locationController.retrievesGeoLocations(fromLocation, toLocation);
	}

    public void updateL()
            throws ClientProtocolException, IOException, JSONException,
            URISyntaxException {
        if (myLocation==null) {
            invokeJS("onCurrentLocation");
        } else {
            h.post(
            new Runnable() {
                public void run() {
                    invokeJS("onCurrentLocation", String.valueOf(myLocation.getLatitude()), String.valueOf(myLocation.getLongitude()));
                }
            });
        }
    }

	public void onTimeToGo() {
		final ToneGenerator tg = new ToneGenerator(
				AudioManager.STREAM_NOTIFICATION, 100);
		tg.startTone(ToneGenerator.TONE_PROP_BEEP);
	}

	public void onGeoLocations(
			final RouteResultForLocations routeResultForLocations) {
		final RouteResult routeResult = routeResultForLocations
				.getRouteResult();
		Log.i(Contants.TIME_TO_GO,
                "@@ in LocationActivity.onGeoLocations() routeResult="
                        + routeResult);
		Log.i(Contants.TIME_TO_GO,
				String.format(Locale.getDefault(), "%d min via %s",
						routeResult.getDrivingTimeInMinutes(),
						routeResult.getRouteName()));
		updateRequestField(routeResultForLocations, routeResult);
		//
		h.post(new Runnable() {

			public void run() {
				invokeJS("onDrivingTime",
						String.valueOf(routeResult.getDrivingTimeInMinutes()));
				updateUI(routeResult.getDrivingTimeInMinutes(),
						routeResult.getRouteName(), new Date());
			}

		});

	}

	@Override
	protected void onNewIntent(final Intent intent) {
		Log.i(Contants.TIME_TO_GO, "onNewIntent");
		if (intent.getExtras() == null) {
			Log.i(Contants.TIME_TO_GO, "no extras");
		} else {
			final boolean timeToGo = intent.getExtras().getBoolean("timeToGo");
			Log.i(Contants.TIME_TO_GO, "timeToGo = " + timeToGo);
			invokeJS("onTimeToGo", String.valueOf(service.getDrivingTime())
					+ " min", service.getRouteName(),
					formatUpdateTime(service.getLastExecutionDate()));
		}
		super.onNewIntent(intent);

	}

	private void updateRequestField(
			final RouteResultForLocations routeResultForLocations,
			final RouteResult routeResult) {
		initialDrivingTime = routeResult.getDrivingTimeInMinutes();
		fromLocation = routeResultForLocations.getFrom();
		toLocation = routeResultForLocations.getTo();
	}

	private void updateUI(final long drivingTime, final String routeName,
			final Date lastUpdated) {
		final String text = formatUpdateTime(lastUpdated);
		invokeJS("onUpdate", String.valueOf(drivingTime), routeName, text);
	}

	private void invokeJS(final String methodName, final String... args) {
		final StringBuilder sb = new StringBuilder("javascript:");
		sb.append(methodName + "(");
		boolean firstTime = true;
		for (final String arg : args) {
			if (!firstTime) {
				sb.append(",");
			}
			sb.append("'");
			sb.append(arg);
			sb.append("'");
			firstTime = false;
		}
		sb.append(");");
		mywebview.loadUrl(sb.toString());
	}

	private String formatUpdateTime(final Date d) {
		final Date now = new Date();
		final int minDiff = now.getMinutes() - d.getMinutes();
		if (minDiff <= 0) {
			return "now";
		}
		if (minDiff <= 1) {
			return "1 min ago";
		}
		if (minDiff <= 10) {
			return minDiff + " minutes ago";
		}
		return DateFormat.format("kk:mm", d).toString();
	}
    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(Contants.TIME_TO_GO, "@@ onResume");
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(Contants.TIME_TO_GO, "@@ onPause");
        locationManager.removeUpdates(this);
    }
    public void onLocationChanged(Location location){
        Log.i(Contants.TIME_TO_GO,"got location update");
        if (location==null)
        {
            Log.i(Contants.TIME_TO_GO,"null");
            return;
        }
        updateLocation(location);
    }

    private void updateLocation(Location location) {
        printLocation(location);
        myLocation=location;
        h.post(
            new Runnable() {
                public void run() {
                    if (myLocation==null) {
                        invokeJS("onCurrentLocation");
                    } else{
                    invokeJS("onCurrentLocation", String.valueOf(myLocation.getLatitude()), String.valueOf(myLocation.getLongitude()), provider);
                    }
                }
        });
    }

    protected void onStart () {
        super.onStart();
        Log.i(Contants.TIME_TO_GO, "on Start");
    }

    public void onStatusChanged(java.lang.String provider, int status, android.os.Bundle extras){

    }
    public void onProviderEnabled(java.lang.String provider){
        Log.i(Contants.TIME_TO_GO,"provider "+provider+" was enabled");
    }
    public void onProviderDisabled(java.lang.String provider){
        Log.i(Contants.TIME_TO_GO,"provider "+provider+" was disabled");
    }
}
