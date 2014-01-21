package io.github.thewear.thewearandroidClientAPP;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.github.thewear.thewearandroidGUI.MainActivity;
import src.gui.thewearandroid.R;

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

    /**
	 * Contructor to import the location EditText in the GPSCoordsManager
	 * AsyncTask
	 */
	public GPSCoordsManager(MainActivity mainActivity, EditText locationEditText, ProgressBar progressBar) {
		this.locationEditText = locationEditText;
		this.progressBar = progressBar;
        this.mainActivityView = mainActivity;
	}

	/**
	 * onPreExecute() opens the progressBar to show the user the App is loading
	 * the location
	 */

	@Override
	protected void onPreExecute() {
		this.currentLocation = null;
        this.gps = new GPSTracker(mainActivityView);
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

        if (gps.canGetLocation()) {
            String GPSLatitudeText = String.valueOf(gps.getLatitude());
            String GPSLongitudeText = String.valueOf(gps.getLongitude());
            String latLng = GPSLatitudeText + "," + GPSLongitudeText;
            Log.d("the Wear Debug", "latlng=" + latLng);
            GridCoach myGridCoach = new GridCoach(latLng, true);
            String strUrl = myGridCoach.PlaceToURL("dummy");
            LocationStruct locationInfo = myGridCoach.URLToJSonString(strUrl);
            Log.d("the wear debug", "lat="+locationInfo.lat);
            Log.d("the wear debug", "lng="+locationInfo.lng);
            Log.d("the wear debug", "address=" + locationInfo.address);
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
        // Close progressBar
		progressBar.setVisibility(View.INVISIBLE);
        if (gps.canGetLocation()){
            Toast.makeText(mainActivityView.getApplicationContext(), R.string.GPSLocationFound,Toast.LENGTH_LONG).show();
            // Set location in the location EditText
            locationEditText.setText(currentLocation);
        }else{
            gps.showSettingsAlert();
        }
        gps.stopUsingGPS();
	}
}
