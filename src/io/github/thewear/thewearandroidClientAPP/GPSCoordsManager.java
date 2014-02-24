package io.github.thewear.thewearandroidClientAPP;

import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import io.github.thewear.thewearandroidGUI.MainActivity;
import io.github.thewear.thewearandroid.R;

/**
 * GPSCoordsManager is an AsyncTask used to process the coordinates the user
 * gives to a location.
 */

public class GPSCoordsManager extends AsyncTask<String, Integer, String> {

	private final MainActivity mainActivityView;
	private EditText locationEditText;
	private String currentLocation;
	private ProgressBar progressBar;
	private GPSTracker gps;
	private boolean canFindLocation = true;

	/**
	 * Contructor to import the location EditText in the GPSCoordsManager
	 * AsyncTask
	 */
	public GPSCoordsManager(MainActivity mainActivity, EditText locationEditText) {
		this.locationEditText = locationEditText;
		this.mainActivityView = mainActivity;
	}

	/**
	 * onPreExecute() opens the progressBar to show the user the App is loading
	 * the location --- Has access to the GUI
	 */

	@Override
	protected void onPreExecute() {
		// show progressBar
		progressBar = (ProgressBar) mainActivityView
				.findViewById(R.id.mainProgressBar);
		progressBar.setVisibility(View.VISIBLE);
	}

	/**
	 * doInBackGround(String... latLong) processes what has to be done in a
	 * separate background thread: it gets the user coordinates using
	 * GPSTrackger and gets the location name from the coordinates using
	 * GridCoach input is the latitude and longitude as string array, returns
	 * the current location as string --- Has no access to the GUI
	 */

	@Override
	protected String doInBackground(String... latLong) {
		currentLocation = null;
		gps = new GPSTracker(mainActivityView);

		if (gps.canGetLocation()) {
			String GPSLatitudeText = String.valueOf(gps.getLatitude());
			String GPSLongitudeText = String.valueOf(gps.getLongitude());
			String latLng = GPSLatitudeText + "," + GPSLongitudeText;
			Log.d("the Wear Debug", "latlng=" + latLng);
			GridCoach myGridCoach = new GridCoach(latLng, true);
			String strUrl = myGridCoach.PlaceToURL("dummy");
			LocationStruct locationInfo = myGridCoach.URLToJSonString(strUrl);
			Log.d("the wear debug", "lat=" + locationInfo.lat);
			Log.d("the wear debug", "lng=" + locationInfo.lng);
			Log.d("the wear debug", "address=" + locationInfo.address);
			if (locationInfo != null) {
				if (locationInfo.address == "Unknown location") {
					canFindLocation = false;
				} else {
					this.currentLocation = locationInfo.address;
				}
			}
		}

		return currentLocation;
	}

	/**
	 * onPostExecute(String currentLocation) puts the location in the location
	 * editText, and closes the progressBar. input is the currentLocation as
	 * string --- Has access to the GUI
	 */

	@Override
	protected void onPostExecute(String currentLocation) {
		// Close progressBar
		progressBar.setVisibility(View.INVISIBLE);
		if (gps.canGetLocation()) {
			if (canFindLocation == true) {
				Toast myToast = Toast.makeText(
						mainActivityView.getApplicationContext(),
						R.string.GPSLocationFound, Toast.LENGTH_LONG);
				myToast.setGravity(Gravity.CENTER, 0, 0);
				myToast.show();
				// Set location in the location EditText
				locationEditText.setText(currentLocation);
			} else {
				Toast myToast = Toast.makeText(
						mainActivityView.getApplicationContext(),
						R.string.GPSLocationNotFound, Toast.LENGTH_LONG);
				myToast.setGravity(Gravity.CENTER, 0, 0);
				myToast.show();
			}
		} else {
			gps.showSettingsAlert();
		}
		gps.stopUsingGPS();
	}
}
