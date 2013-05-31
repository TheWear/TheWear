package clientAPP;

import src.gui.thewearandroid.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Forecaster extends AsyncTask<String, Integer, ForecastInfo> {

	/**
	 * The Forecaster AsyncTask is used to retrieve the full location and
	 * coordinates of that location from the location the user entered, to
	 * retrieve the forecast data for those coordinates from our server; compare
	 * the data to our defined thresholds and the user preferences; and combine
	 * the results of that into the forecast image.
	 * 
	 * The Forecaster AsyncTask can be called with myForecaster.execute(String
	 * userLocation);
	 * 
	 * The Forecaster updates the GUI when the Forecaster AsincTask has finished
	 * the background thread: the full location is shown in the location
	 * EditText, and the ImageViews are changed to the forecast images for the
	 * forecast. A warning is shown if an error occurs during the running of the
	 * background thread, and the EditText and ImageViews are not changed.
	 * 
	 * Some information can be retrieved of the last run trough
	 * myForecasterObject.get(); this method returns a ForecastInfo.
	 * 
	 * While the Forecaster AsyncTask is running, a progress dialog is shown.
	 */

	private Context applicationContext;
	private ImageView[] myImageViews;
	private ProgressDialog myProgressDialog;
	private EditText locationField;
	private String[] dataset;
	private WeatherEnumHandler weather_data;
	private boolean isCancelled;
	private Bitmap[] mergedImage = { null, null, null };
	private String[] mouseOverInfo = { null, null, null };
	private int progressCounter;
	private String reason;

	/**
	 * Constructor for the Forecaster AsyncTask to be able to import the
	 * Application Context. This enables the ability to manipulate the UI from
	 * this Class, and can be used to show a ProgressDialog.
	 * 
	 * Parameters for this constructor: Context context, EditText editText,
	 * ImageView[] imageViews
	 */

	public Forecaster(Context context, EditText editText, ImageView[] imageViews) {
		// can take other parameters if needed
		this.applicationContext = context;
		this.locationField = editText;
		this.myImageViews = imageViews;
	}

	// Log.d("TheWearDebug","");

	/**
	 * onPreExecute is called when before the background thread is called: the
	 * progress dialog is instantiated and shown here.
	 * 
	 * This method has access to the GUI
	 */

	@Override
	protected void onPreExecute() {
		Log.d("TheWearDebug", "onPreExecute of Forecaster AsyncTask Started");
		// Show a ProgressDialog while the Forecast AsyncTask runs
		myProgressDialog = new ProgressDialog(applicationContext);
		myProgressDialog.setTitle("Retrieving Forecast");
		myProgressDialog.setIndeterminate(false);
		myProgressDialog.setMax(100);
		myProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		myProgressDialog.setProgress(0);
		myProgressDialog.setCancelable(false);
		myProgressDialog.show();
		isCancelled = false;
	}

	/**
	 * doInBackground contains all the code we want to have executed in the
	 * background thread.
	 * 
	 * This method contains all the code for retrieving retrieve the full
	 * location and coordinates of that location from the location the user
	 * entered; to retrieve the forecast data for those coordinates from our
	 * server; and to compare the data to our defined thresholds and the user
	 * preferences.
	 * 
	 * There is a progressCounter that gets raised trough this method (and
	 * trough the specificForecast too) to keep track of the progress. This
	 * progress is shown to the user through publisProgress.
	 * 
	 * When an 'expected' error occurs, the forecaster will be cancelled, and a
	 * cause is 'stored' for the onPostExecute to determine what to show.
	 * 
	 * This method has NO access to the GUI
	 */

	@Override
	protected ForecastInfo doInBackground(String... userLocation) {

		Log.d("TheWearDebug", "doInBackground of Forecaster AsyncTask Started");

		// Initialize the progressCounter
		progressCounter = 0;

		// Initialise ForecastInfo
		ForecastInfo forecastInfo = null;
		String[] mouseOverInfo = { null, null, null };

		// Get the coordinates from the location using GridCoach

		// Replace spaces with %20
		GridCoach myGridCoach = new GridCoach(userLocation[0]);
		progressCounter = progressCounter + 4;
		publishProgress(progressCounter); // Total: 4/100

		// Construct URL for call to google maps
		String strUrl = myGridCoach.PlaceToURL();
		progressCounter = progressCounter + 4;
		publishProgress(progressCounter); // Total: 8/100
		Log.d("TheWearDebug", "URL to google maps Constructed");

		// Initialize LocationStruct
		LocationStruct locationInfo = null;

		// Get Location Info
		locationInfo = myGridCoach.URLToJSonString(strUrl);
		progressCounter = progressCounter + 7;
		publishProgress(progressCounter); // Total: 15/100
		Log.d("TheWearDebug", "LocationInfo retrieved");

		if (locationInfo == null) {
			// No connection
			isCancelled = true;
			reason = "internetConnection";
		} else {

			// Store Location Coordinates for further use
			myGridCoach.setLocation(locationInfo.lat, locationInfo.lng);
			progressCounter = progressCounter + 4;
			publishProgress(progressCounter); // Total: 19/100
			Log.d("TheWearDebug",
					"LocationInfo coordinates stored for further use");

			// Save the Location in the
			SharedPreferences sharedPref = applicationContext
					.getSharedPreferences(applicationContext
							.getString(R.string.TheWear_preference_key),
							Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(
					applicationContext.getString(R.string.location_preference),
					locationInfo.address);
			editor.commit();
			progressCounter = progressCounter + 4;
			publishProgress(progressCounter); // Total: 23/100
			Log.d("TheWearDebug", "saved userLocation: " + locationInfo.address);

			// Convert location coordinates to gridpoints used by GFS
			Double[] gridCoos = myGridCoach.convertToGridCoos();
			progressCounter = progressCounter + 4;
			publishProgress(progressCounter); // Total: 27/100
			Log.d("TheWearDebug", "Converted coordinates to Gridpoints");

			// Get the different specific forecasts, and cancel when
			// necessary
			specificForecast(12, gridCoos, 0); // Total: 50/100
			if (isCancelled == false) {
				specificForecast(18, gridCoos, 1); // Total: 73/100
				if (isCancelled == false) {
					specificForecast(24, gridCoos, 2); // Total: 96/100
					if (isCancelled == false) {
						forecastInfo = new ForecastInfo(locationInfo.address,
								mouseOverInfo, mergedImage);
						progressCounter = progressCounter + 4;
						publishProgress(progressCounter); // Total: 100/100
					}
				}
			}
		}

		return forecastInfo;
	}

	/**
	 * onProgressUpdate updates the progressDialog to the set progress when
	 * publishProcess is called in doInBackground.
	 * 
	 * This method has access to the GUI
	 */

	@Override
	protected void onProgressUpdate(Integer... progress) {
		Log.i("TheWearDebug", "onProgressUpdate Executed. Progress: "
				+ progress[0] + "/100");
		// Update the progress bar of the ProgressDialog
		myProgressDialog.setProgress(progress[0]);
	}

	/**
	 * ¨ onPostExecute contains all the code we want to have executed when the
	 * background thread is finished: it closes the progressDialog, and either
	 * shows an error message when the background thread encountered a
	 * problem/an error, or updates the ImageViews with the forecast image and
	 * the EditText with the full location
	 * 
	 * This method has access to the GUI
	 */

	@Override
	protected void onPostExecute(ForecastInfo forecastInfo) {
		Log.d("TheWearDebug", "onPostExecute of Forecaster AsyncTask Started");

		if (isCancelled == true) {
			Log.d("TheWearDebug", "Forecaster AsyncTask is Canceled");
			// Close the ProgressDialog
			myProgressDialog.dismiss();

			// Show Toast to explain error
			if (reason == "internetConnection") {
				Toast myToast = Toast.makeText(applicationContext,
						"Cannot connect to the Internet", Toast.LENGTH_LONG);
				myToast.setGravity(Gravity.CENTER, 0, 0);
				myToast.show();
			} else if (reason == "serverConnection") {
				Toast myToast = Toast.makeText(applicationContext,
						"Cannot connect to TheWear server", Toast.LENGTH_LONG);
				myToast.setGravity(Gravity.CENTER, 0, 0);
				myToast.show();
			} else if (reason == "server") {
				Toast myToast = Toast
						.makeText(
								applicationContext,
								"An error occured while retrieving the forecast from TheWear server",
								Toast.LENGTH_LONG);
				myToast.setGravity(Gravity.CENTER, 0, 0);
				myToast.show();
			} else {
				Log.e("TheWearDebug", "Error: reason doesn't exist.");
			}
		} else {

			// Return (show) the during the Forecaster AsyncTask looked up
			// location to the user.
			Log.d("TheWearDebug", "Show Location " + forecastInfo.address);
			Log.d("TheWearDebug", "locationField: " + locationField);
			locationField.setText(forecastInfo.address);
			locationField.clearFocus();

			// set the new Images
			for (int position = 0; position <= 2; position++) {
				Log.d("TheWearAndroid",
						"Setting new forecastImage for position " + position);
				if (myImageViews[position] == null) {
					Log.i("TheWearDebug", "myImageViews == null");
				}
				if (mergedImage[position] == null) {
					Log.i("TheWearDebug", "mergedImage == null");
				}
				myImageViews[position].setImageBitmap(mergedImage[position]);
			}

			// Close the ProgressDialog
			myProgressDialog.dismiss();
		}
	}

	// THIS DOESN'T WORK
	// onCancelled is only called when cancelled from outside the Forecaster
	// protected void onCancelled(LocationStruct locationInfo) {
	// Log.d("TheWearDebug", "Forecaster AsyncTask is Canceled");
	// // Close the ProgressDialog
	// myProgressDialog.dismiss();
	// // Show Toast to explain error
	// Toast myToast = Toast.makeText(applicationContext,
	// "Cannot connect to the Internet", Toast.LENGTH_LONG);
	// myToast.setGravity(Gravity.CENTER, 0, 0);
	// myToast.show();
	// }

	/**
	 * specificForecast retrieves the forecast for a specific hour (12, 18, 24)
	 * and processes it to the corresponding Bitmap. Inputs are the specific
	 * hour (forecast); the coordinates (gridCoos); and the forecastNumber for
	 * further processing (0, 1, 2) this method stores the bitmap and the
	 * mousOver information directly in the right variables for further use.
	 * 
	 * Input is forecast, Double[] gridCoos, int forecastNumber;
	 * 
	 * returns void.
	 * 
	 * This method has NO access to the GUI
	 */

	public void specificForecast(int forecast, Double[] gridCoos,
			int forecastNumber) {

		// Get Weather data from our server
		ServerCommunicator myServerCommunicator = new ServerCommunicator();
		dataset = myServerCommunicator.getWeatherData(forecast, gridCoos[0],
				gridCoos[1]);
		Log.d("TheWearDebug", "dataset length: " + dataset.length);
		progressCounter = progressCounter + 7;
		publishProgress(progressCounter); // Total: 7/22
		if (dataset == null) {
			isCancelled = true;
			reason = "serverConnection";
			Log.d("TheWearDebug", "Canceling Forecaster... (connection)");
		} else if (dataset.length == 1) {
			isCancelled = true;
			reason = "server";
			Log.d("TheWearDebug", "Canceling Forecaster... (server)");
		} else {
			Log.d("TheWearDebug", "Got weather dataset from our server");

			weather_data = new WeatherEnumHandler();
			weather_data.handleWeatherEnum(dataset, applicationContext);
			progressCounter = progressCounter + 4;
			publishProgress(progressCounter); // Total: 11/23
			Log.d("TheWearDebug", "handled WeatherEnum");

			boolean[] advice = weather_data.weathertype.show_imgs;

			MouseOverManager myMouseOver = new MouseOverManager(dataset);
			mouseOverInfo[forecastNumber] = myMouseOver.getString();
			progressCounter = progressCounter + 4;
			publishProgress(progressCounter); // Total: 15/23

			MergeImage myMergeImage = new MergeImage();
			mergedImage[forecastNumber] = myMergeImage.MergeForecastImage(
					advice, applicationContext);
			progressCounter = progressCounter + 8;
			publishProgress(progressCounter); // Total: 23/23
		}
	}
}