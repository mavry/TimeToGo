package com.cheetah;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

public class LocationActivity extends Activity {

  private RetrievesGeoLocation retrievesGeoLocation;
  private DefaultHttpClient client;
  private RetreivesRouteResult retreivesRouteResult;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    client = new DefaultHttpClient();
    retrievesGeoLocation = new RetrievesGeoLocation(client);
    retreivesRouteResult = new RetreivesRouteResult(client);
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    getMenuInflater().inflate(R.menu.location, menu);
    return true;
  }

  public void onBtnClicked(final View v) throws ClientProtocolException, IOException, JSONException, URISyntaxException {
    if (v.getId() == R.id.go) {
      final EditText fromText = (EditText) (findViewById(R.id.inputFrom));
      final EditText toText = (EditText) (findViewById(R.id.inputTo));

      final String from = fromText.getText().toString();
      final String to = toText.getText().toString();
      new RetrievesGeoLocationTask().execute(from, to);

      new Thread(new Runnable() {
        public void run() {

        }
      }).start();

    }
  }

  private class RetrievesGeoLocationTask extends AsyncTask<String, String, RouteResult> {

    @Override
    protected RouteResult doInBackground(final String... params) {
      try {
        final LocationResult fromLocation = retrievesGeoLocation.retreive(params[0])[0];
        final LocationResult toLocation = retrievesGeoLocation.retreive(params[1])[0];
        final RouteResult[] routeResults = retreivesRouteResult.retreive(fromLocation, toLocation);
        return routeResults[0];
      } catch (final Exception e) {
        Log.e(e.getMessage());
        return null;
      }
    }

    @Override
    protected void onPostExecute(final RouteResult result) {

      final TextView debugView = (TextView) (findViewById(R.id.debug));
      final TextView eta = (TextView) (findViewById(R.id.eta));
      debugView.setText(String.format("%d min via %s", result.getSeconds() / 60, result.getRouteName()));
      eta.setText(String.valueOf(result.getSeconds() / 60));
    }
  }
}
