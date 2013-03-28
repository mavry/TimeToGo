package com.cheetah;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LocationActivity extends Activity {

	private RetrievesGeoLocation retrievesGeoLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		retrievesGeoLocation = new RetrievesGeoLocation(new DefaultHttpClient());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location, menu);
		return true;
	}

	public void onBtnClicked(View v) throws ClientProtocolException,
			IOException, JSONException, URISyntaxException {
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

	private class RetrievesGeoLocationTask extends
			AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			LocationResult fromLocation;
			try {
				fromLocation = retrievesGeoLocation.retreive(params[0])[0];
				LocationResult toLocation = retrievesGeoLocation
						.retreive(params[1])[0];
				return "from: " + fromLocation.getName() + " to: "
						+ toLocation.getName();
			} catch (Exception e) {
				return "got exception " + e.getMessage();
			}
		}

		protected void onPostExecute(String result) {
			final TextView debugView = (TextView) (findViewById(R.id.debug));
			debugView.append(result);
		}
	}
}
