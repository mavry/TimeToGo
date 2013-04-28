package com.timetogo.activity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
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

//@ContentView(R.layout.main)
public class LocationActivity extends RoboActivity implements ILocationView {

  //  @InjectView(R.id.debug)
  TextView debugView;

  //  @InjectView(R.id.eta)
  TextView etaView;

  //  @InjectView(R.id.inputFrom)
  EditText fromText;

  //  @InjectView(R.id.inputTo)
  EditText toText;

  @InjectView(R.id.webview)
  WebView mywebview;

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

  private final AtomicInteger evalJsIndex = new AtomicInteger(0);
  private final Map<Integer, String> jsReturnValues = new HashMap<Integer, String>();
  private final Object jsReturnValueLock = new Object();

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

  long maxDrivingTime;

  public LocationActivity() {
  }

  final class MyWebChromeClient extends WebChromeClient {
    @Override
    public boolean onJsAlert(final WebView view, final String url, final String message, final JsResult result) {
      Log.i(Contants.TIME_TO_GO, message);
      result.confirm();
      return true;
    }
  }

  private static class MyJavascriptInterface {
    private final LocationActivity activity;

    public MyJavascriptInterface(final LocationActivity activity) {
      this.activity = activity;
    }

    // this annotation is required in Jelly Bean and later:
    //    @JavascriptInterface
    public void onGo(final String fromAddress, final String toAddress) throws ClientProtocolException, IOException, JSONException, URISyntaxException {
      Log.i(Contants.TIME_TO_GO, "onGO [" + fromAddress + "][" + toAddress + "]");
      activity.onGoBtnClicked(fromAddress, toAddress);
    }
  }

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    Log.i(Contants.TIME_TO_GO, "@@ onCreate mywebview=" + mywebview);
    super.onCreate(savedInstanceState);

    setContentView(R.layout.main);
    //mywebview.loadUrl("http://mavry.github.io/");
    mywebview.loadUrl("file:///android_asset/index.html");
    mywebview.getSettings().setJavaScriptEnabled(true);
    mywebview.addJavascriptInterface(new MyJavascriptInterface(this), "androidInterface");
    mywebview.setWebChromeClient(new MyWebChromeClient());

    intent = new Intent(this, ETAService.class);
    pintent = PendingIntent.getService(this, 0, intent, 0);

    locationController.setView(this);
    //    alarmManager.cancel(pintent);
    //    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 60 * 1000, pintent);
    //    doBindService();

    //    h.postDelayed(new Runnable() {
    //
    //      public void run() {
    //        if (service != null) {
    //          final Date date = service.getLastExecutionDate();
    //          eta = service.getEta();
    //          final String text = String.format("eta: %s @ %s", String.valueOf(eta), DateFormat.format("hh", date));
    //          Log.i(Contants.TIME_TO_GO, "@@ updateing debug with " + text);
    //          h.postDelayed(this, 60 * 1000);
    //        }
    //      }
    //    }, 60 * 1000);
  }

  public void notifyMe() {
    Log.i(Contants.TIME_TO_GO, "@@ NotifyME button was clicked  with maxDrivingTimeInMin=" + maxDrivingTime + " ThreadID = " + Thread.currentThread());
    service.setParameters(fromLocation, toLocation, maxDrivingTime);
  }

  void doBindService() {
    final boolean bind = bindService(new Intent(this, ETAService.class), mConnection, Context.BIND_AUTO_CREATE);
    Log.i("bind sucess = " + bind);
  }

  public void onGoBtnClicked(final String fromAddress, final String toAddress) throws ClientProtocolException, IOException, JSONException,
      URISyntaxException {
    locationController.retrievesGeoLocations(fromAddress, toAddress);
  }

  public void onTimeToGo() {
    debugView.setText("@@ TIME TO GO");
    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
    tg.startTone(ToneGenerator.TONE_PROP_BEEP);
  }

  public void onGeoLocations(final RouteResultForLocations routeResultForLocations) {
    final RouteResult routeResult = routeResultForLocations.getRouteResult();
    Log.i("@@ in LocationActivity.onGeoLocations() routeResult=" + routeResult + " fromText=" + fromText + " debugView=" + debugView);

    Log.i(Contants.TIME_TO_GO, String.format("%d min via %s", routeResult.getETAInMinutes(), routeResult.getRouteName()));
    h.post(new Runnable() {

      public void run() {
        final String now = DateFormat.format("kk:mm", new Date()).toString();
        //mywebview.loadUrl("javascript:onETA(" + routeResult.getETAInMinutes() + "," + routeResult.getRouteName() + "," + 12 + ");");
        mywebview.loadUrl("javascript:onETA(" + routeResult.getETAInMinutes() + ",'" + routeResult.getRouteName() + "','" + now + "');");
      }
    });
    if (1 < 2) {
      return;
    }

    etaView.setText(String.valueOf(routeResult.getETAInMinutes()));
    updateFields(routeResultForLocations, routeResult);

    final LayoutInflater inflater = getLayoutInflater();
    final View dialoglayout = inflater.inflate(R.layout.popup, null);

    final Builder adb = new AlertDialog.Builder(LocationActivity.this);
    adb.setTitle(createTitle(routeResult)).setView(dialoglayout).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
      public void onClick(final DialogInterface dialog, final int whichButton) {
        final TextView mdt = (TextView) dialoglayout.findViewById(R.id.inputMaxDrivingTime);
        maxDrivingTime = Integer.parseInt(mdt.getText().toString());
        Log.i(Contants.TIME_TO_GO, "@@ textView=" + mdt);
        notifyMe();
      }
    }).setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
      public void onClick(final DialogInterface dialog, final int whichButton) {
      }
    }).show();

  }

  private String createTitle(final RouteResult routeResult) {
    return "Driving Time: " + initialEta + "min (via " + routeResult.getRouteName() + ")";
  }

  private void updateFields(final RouteResultForLocations routeResultForLocations, final RouteResult routeResult) {
    initialEta = routeResult.getETAInMinutes();
    fromLocation = routeResultForLocations.getFrom();
    toLocation = routeResultForLocations.getTo();
  }
}
