package io.github.thewear.thewearandroidClientAPP;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

/**
 * GPSCoordsManager is an AsyncTask used to process the coordinates the user
 * gives to a location.
 */

public class GPSCoordsManager extends AsyncTask<String, Integer, String> {

	private EditText locationEditText;
	private String currentLocation;
	private ProgressBar progressBar;

	/**
	 * Contructor to import the location EditText in the GPSCoordsManager
	 * AsyncTask
	 */
	public GPSCoordsManager(EditText locationEditText, ProgressBar progressBar) {
		this.locationEditText = locationEditText;
		this.progressBar = progressBar;
	}

	/**
	 * onPreExecute() opens the progressBar to show the user the App is loading
	 * the location
	 */

	@Override
	protected void onPreExecute() {
		this.currentLocation = null;
	}

	/**
	 * doInBackGround(String... latLong) processes what has to be done in a
	 * separate background thread: it gets the location name from the
	 * coordinates using GridCoach input is the latitude and longitude as string
	 * array, returns the current location as string
	 */

	@Override
	protected String doInBackground(String... latLong) {
		// TODO Auto-generated method stub
		Log.d("the Wear Debug", "latlng=" + latLong[0]);
		GridCoach myGridCoach = new GridCoach(latLong[0], true);
		String strUrl = myGridCoach.PlaceToURL("dummy");
		LocationStruct locationInfo = myGridCoach.URLToJSonString(strUrl);

		if (locationInfo == null) {
			// No connection
			// TODO Error no internetconnection popup/toast.
		} else {
			if (locationInfo.address == "Unknown location") {
				// No known location
				// TODO Error unknown location! popup/toast.
			} else {
				this.currentLocation = locationInfo.address;
			}

		}

		return currentLocation;
	}

	/**
	 * onPostExecute(String currentLocation) puts the location in the location
	 * editText, and closes the progressBar. input is the currentLocation as
	 * string
	 */

	@Override
	protected void onPostExecute(String currentLocation) {
		// Set location in the location EditText
		locationEditText.setText(currentLocation);
		// Close progressBar
		progressBar.setVisibility(View.INVISIBLE);
	}
}
