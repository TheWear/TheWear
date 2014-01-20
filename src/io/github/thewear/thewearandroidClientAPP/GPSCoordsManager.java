package io.github.thewear.thewearandroidClientAPP;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

/**
 * GPSCoordsManager is an AsyncTask used to process the coordinates the user
 * gives to a location.
 */

public class GPSCoordsManager extends AsyncTask<String, Integer, String> {

    private EditText locationEditText;
    private String currentLocation;

    /**
	 * Contruct to import the location EditText in the GPSCoordsManager
	 * AsyncTask
	 */
	public GPSCoordsManager(EditText locationEditText) {
		this.locationEditText = locationEditText;

	}


    /**
	 * onPreExecute() opens the progressbar to show the user the app is loading
	 * the location
	 */

	@Override
	protected void onPreExecute() {
		// TODO add Indeterminate progressbar
        this.currentLocation = null;
	}

	/**
	 * doInBackGround(String... latLong) processes what has to be done in a
	 * separate background thread; input is the latitude and longitude as string
	 * array, returns the current location as string
	 */

	@Override
	protected String doInBackground(String... latLong) {
		// TODO Auto-generated method stub
        Log.d("the Wear Debug","latlng="+latLong[0]);
        GridCoach myGridCoach = new GridCoach(latLong[0],true);
        String strUrl = myGridCoach.PlaceToURL("dummy");
        LocationStruct locationInfo = myGridCoach.URLToJSonString(strUrl);

        if (locationInfo == null){
            // No connection
            //TODO Error no internetconnection popup/toast.
        }else{
            if (locationInfo.address == "Unknown location") {
                // No known location
                //TODO Error unknown location! popup/toast.
            } else {
                this.currentLocation = locationInfo.address;
            }

        }


		return currentLocation;
	}

	/**
	 * onPostExecute(String currentLocation) puts the location in the location
	 * editText, and closes the progressbar. input is the currentlocation as
	 * string
	 */

	@Override
	protected void onPostExecute(String currentLocation) {
		locationEditText.setText(currentLocation);
		// TODO Close progressbar
	}
}
