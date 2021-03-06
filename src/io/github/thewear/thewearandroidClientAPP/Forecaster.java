package io.github.thewear.thewearandroidClientAPP;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

import io.github.thewear.thewearandroidGUI.MainActivity;
import io.github.thewear.thewearandroidGUI.MainActivity.ImagePagerAdapter;
import io.github.thewear.thewearandroid.R;

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
	private ArrayList<String[]> dataset = new ArrayList<String[]>();
	private boolean isCancelled;
	private Bitmap[] mergedImage = { null, null, null };
	private int progressCounter;
	private String reason;
	private String[][] localDataset = { null, null, null };
	private ForecastTimeStruct myForecastTimeStruct;
	private TextView titleTextView;
	private ViewPager mViewPager;
	private int firstForecastEndTime;
	private ImagePagerAdapter imagePagerAdapter;
	private MainActivity mainActivity;
	private boolean[] imageViewAddedToImagePagerAdapter;
	private ImageButton goForwardButton;
	private ImageButton goBackButton;

	/**
	 * Constructor for the Forecaster AsyncTask to be able to import the
	 * Application Context. This enables the ability to manipulate the UI from
	 * this Class, and can be used to show a ProgressDialog.
	 * 
	 * Parameters for this constructor: Context context, EditText editText,
	 * ImageView[] imageViews, TextView titleTextView, ViewPager mViewPager,
	 * ImagePagerAdapter imagePagerAdapter, MainActivity mainActivity, boolean[]
	 * imageViewAddedToImagePagerAdapter, ImageButton goForwardButton,
	 * ImageButton goBackButton
	 */

	public Forecaster(Context context, EditText editText,
			ImageView[] imageViews, ForecastTimeStruct myForecastTimeStruct,
			TextView titleTextView, ViewPager mViewPager,
			ImagePagerAdapter imagePagerAdapter, MainActivity mainActivity,
			boolean[] imageViewAddedToImagePagerAdapter,
			ImageButton goForwardButton, ImageButton goBackButton) {
		// can take other parameters if needed
		this.applicationContext = context;
		this.locationField = editText;
		this.myImageViews = imageViews;
		this.myForecastTimeStruct = myForecastTimeStruct;
		this.titleTextView = titleTextView;
		this.mViewPager = mViewPager;
		this.imagePagerAdapter = imagePagerAdapter;
		this.mainActivity = mainActivity;
		this.imageViewAddedToImagePagerAdapter = imageViewAddedToImagePagerAdapter;
		this.goForwardButton = goForwardButton;
		this.goBackButton = goBackButton;
	}

	/**
	 * onPreExecute is called when before the background thread is called: the
	 * progress dialog is instantiated and shown here.
	 * 
	 * This method has access to the GUI
	 */

	@Override
	protected void onPreExecute() {
		// Show a ProgressDialog while the Forecast AsyncTask runs
		Resources res = applicationContext.getResources();
		myProgressDialog = new ProgressDialog(applicationContext);
		myProgressDialog.setTitle(res
				.getString(R.string.forecastProgressDialogTitle));
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
	 * server; to compare the data to our defined thresholds and the user
	 * preferences; and to get the time corresponding with the forecast in the
	 * title.
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

		// Initialize the progressCounter
		progressCounter = 0;

		// Initialize ForecastInfo
		ForecastInfo forecastInfo = null;

		// Get the coordinates from the location using GridCoach

		// Replace spaces with %20
		GridCoach myGridCoach = new GridCoach(userLocation[0], false);
		progressCounter = progressCounter + 4;
		publishProgress(progressCounter); // Total: 4/100

		Resources res = applicationContext.getResources();
		SharedPreferences sharedPref = applicationContext.getSharedPreferences(
				applicationContext.getString(R.string.TheWear_preference_key),
				Context.MODE_PRIVATE);
		String regionPreference;
		// Get default autoRegionDetection
		boolean defaultAutoRegionDetection = res
				.getBoolean(R.bool.defaultAutoRegionDetection);
		// Read the autoRegionDetection value from SharedPreferences
		boolean autoRegionDetection = sharedPref.getBoolean(
				res.getString(R.string.autoRegionDetection_preference_key),
				defaultAutoRegionDetection);

		// Check if te user wants auto detection of their region
		if (autoRegionDetection == true) {
			// get MCC (Mobile Country Code)
			Configuration config = res.getConfiguration();
			int mcc = config.mcc;
			// get MMC codes List and Index of the MCC of the user
			int mccIndex = MCCList.get().indexOf(mcc);
			// get region code List (ccTLDCodes) and check if the MCC of te user
			// is known
			if (mccIndex != -1) {
				// get MCC codes and index of user MCC
				regionPreference = MCCList.ccTLDForMcc[mccIndex];
			} else {
				regionPreference = getRegionSharedPreference(res, sharedPref);
			}
		} else {
			regionPreference = getRegionSharedPreference(res, sharedPref);
		}

		// Construct URL for call to google maps
		String strUrl = myGridCoach.PlaceToURL(regionPreference);
		progressCounter = progressCounter + 4;
		publishProgress(progressCounter); // Total: 8/100

		// Initialize LocationStruct
		LocationStruct locationInfo = null;

		// Get Location Info
		locationInfo = myGridCoach.URLToJSonString(strUrl);
		progressCounter = progressCounter + 7;
		publishProgress(progressCounter); // Total: 15/100

		if (locationInfo == null) {
			// No connection
			isCancelled = true;
			reason = "internetConnection";
		} else {

			// Check if the location is known
			if (locationInfo.address == "Unknown location") {
				isCancelled = true;
				reason = "unknownLocation";
			} else {

				// Store Location Coordinates for further use
				myGridCoach.setLocation(locationInfo.lat, locationInfo.lng);
				progressCounter = progressCounter + 4;
				publishProgress(progressCounter); // Total: 19/100

				// Save the Location in the
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(applicationContext
						.getString(R.string.location_preference_key),
						locationInfo.address);
				editor.commit();
				progressCounter = progressCounter + 4;
				publishProgress(progressCounter); // Total: 23/100

				// Convert location coordinates to gridpoints used by GFS
				Double[] gridCoos = myGridCoach.convertToGridCoos();
				progressCounter = progressCounter + 4;
				publishProgress(progressCounter); // Total: 27/100

				// Get the different specific forecasts, and cancel when
				// necessary
				specificForecast(12, gridCoos, 0); // Total: 49/100
				if (isCancelled == false) {
					specificForecast(18, gridCoos, 1); // Total: 71/100
					if (isCancelled == false) {
						specificForecast(24, gridCoos, 2); // Total: 93/100
						if (isCancelled == false) {
							forecastInfo = new ForecastInfo(
									locationInfo.address, mergedImage, dataset);
							progressCounter = progressCounter + 4;
							publishProgress(progressCounter); // Total: 97/100

							// set the time for the titles corresponding with
							// the forecast
							TimeHandler myTimeHandler = new TimeHandler(
									sharedPref, res);
							myForecastTimeStruct
									.setForecastTimeStruct(
											myTimeHandler
													.getCurrentForecastedTimeTitles(firstForecastEndTime),
											myTimeHandler.getForecastTimeHour(),
											myTimeHandler
													.getForecastTimeMinute());
							progressCounter = progressCounter + 3;
							publishProgress(progressCounter); // Total: 100/100
						}
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
		// Update the progress bar of the ProgressDialog
		myProgressDialog.setProgress(progress[0]);
	}

	/**
	 * � onPostExecute contains all the code we want to have executed when the
	 * background thread is finished: it closes the progressDialog, and either
	 * shows an error message when the background thread encountered a
	 * problem/an error, or updates the ImageViews with the forecast image if
	 * the imageViews are already set or, if not, set the imageViews too,
	 * updates the EditText with the full location, and the EditText with the
	 * time title.
	 * 
	 * This method has access to the GUI
	 */

	@Override
	protected void onPostExecute(ForecastInfo forecastInfo) {

		if (isCancelled == true) {
			// Close the ProgressDialog
			myProgressDialog.dismiss();
			Resources res = applicationContext.getResources();

			// Show Toast to explain error
			if (reason == "internetConnection") {
				Toast myToast = Toast.makeText(applicationContext,
						res.getString(R.string.noInternetConnection),
						Toast.LENGTH_LONG);
				myToast.setGravity(Gravity.CENTER, 0, 0);
				myToast.show();
			} else if (reason == "serverConnection") {
				Toast myToast = Toast.makeText(applicationContext,
						res.getString(R.string.noServerConnection),
						Toast.LENGTH_LONG);
				myToast.setGravity(Gravity.CENTER, 0, 0);
				myToast.show();
			} else if (reason == "server") {
				Toast myToast = Toast.makeText(applicationContext,
						res.getString(R.string.connectionError),
						Toast.LENGTH_LONG);
				myToast.setGravity(Gravity.CENTER, 0, 0);
				myToast.show();
			} else if (reason == "unknownLocation") {
				locationField.setText("");
				locationField.clearFocus();
				Toast myToast = Toast.makeText(applicationContext,
						res.getString(R.string.locationUnknown),
						Toast.LENGTH_LONG);
				myToast.setGravity(Gravity.CENTER, 0, 0);
				myToast.show();
				// CLear the locationField
			} else {
				throw new IllegalArgumentException("Forecaster: error - reason doesn't exist.");
			}
		} else {

			// Return (show) the during the Forecaster AsyncTask looked up
			// location to the user.
			locationField.setText(forecastInfo.address);
			locationField.clearFocus();

			// set images
			for (int position = 0; position <= 2; position++) {
				// Check if images are available
				if (mergedImage[position].equals(null)) {
					// No Images to show; this shouldn't be called

				} else {
					// Check if ImageViews are fully set
					if (imageViewAddedToImagePagerAdapter[position] == false) {
						// ImageView not yet Set
						// Create new imageViews in the imagePagerAdapter
						ImageView myImageView = myImageViews[position];
						// make imageView
						ImageView imageView = mainActivity.setImageView(
								myImageView, mergedImage[position]);
						// Add new imageView to the imagePagerAdapter for swipe
						imagePagerAdapter.addView(imageView, position);
						// Set imageView onClickListener for the imageView
						imageView = mainActivity.setImageViewOnClickListener(
								imageView, position);
						// check if ImageView 2 is being set
						if (position == 1) {
							// Set onClickListener for the imageView for
							// imageView 1
							imageView = mainActivity
									.setImageViewOnClickListener(
											myImageViews[0], 0);
							// Make goForwardButton clickable and visable for
							// first forecast
							goForwardButton.setClickable(true);
							goForwardButton.setVisibility(View.VISIBLE);

						}
						// Set imageViewStatus initiated
						imageViewAddedToImagePagerAdapter[position] = true;
					} else {
						// set the new Images
						myImageViews[position]
								.setImageBitmap(mergedImage[position]);
					}
				}
			}

			// Set OnPageChangeListener to make buttons unClickable when they
			// are not used, and Clickable again when they can be used.
			// The listener also changes the title (day) when swiping.
			mViewPager
					.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
						@Override
						public void onPageSelected(int position) {
							if (position == 0) {
								// Forecast 1: Set the goBackButton unClickable
								// and invisible
								goBackButton.setClickable(false);
								goBackButton.setVisibility(View.INVISIBLE);
								titleTextView
										.setText(myForecastTimeStruct.forecastTimeString[0]);
							} else if (position == 1) {
								// Forecast 2: Set the goBackButton & the
								// goForwardButton Clickable and visible
								goBackButton.setClickable(true);
								goBackButton.setVisibility(View.VISIBLE);
								goForwardButton.setClickable(true);
								goForwardButton.setVisibility(View.VISIBLE);
								titleTextView
										.setText(myForecastTimeStruct.forecastTimeString[1]);
							} else if (position == 2) {
								// Forecast 3: Set the goForwardButton
								// unClickable and invisible
								goForwardButton.setClickable(false);
								goForwardButton.setVisibility(View.INVISIBLE);
								titleTextView
										.setText(myForecastTimeStruct.forecastTimeString[2]);
							} else {
								// Should not happen
								throw new NoSuchElementException("DetailedForecastInformationManager: error - Forecast selected that doesn't exist");
							}
						}
					});

			// Set new title for current tab:
			int tab = mViewPager.getCurrentItem();
			titleTextView.setText(myForecastTimeStruct.forecastTimeString[tab]);

			// Close the ProgressDialog
			myProgressDialog.dismiss();
		}
	}

	/**
	 * specificForecast retrieves the forecast for a specific hour (12, 18, 24)
	 * and processes it to the corresponding Bitmap. Inputs are the specific
	 * hour (forecast); the coordinates (gridCoos); and the forecastNumber for
	 * further processing (0, 1, 2) this method stores the bitmap, the mousOver
	 * information and the dataset directly in the right variables for further
	 * use.
	 * 
	 * The forecast time information of the first forecast is saved as an int
	 * variable for furter use.
	 * 
	 * Input is forecast, Double[] gridCoos, int forecastNumber;
	 * 
	 * returns void.
	 * 
	 * This method has NO access to the GUI
	 */

	public void specificForecast(int forecast, Double[] gridCoos,
			int forecastNumber) {
		ServerCommunicator myServerCommunicator = new ServerCommunicator();
		// Get Weather data from our server
		if (forecastNumber == 0) {
			localDataset = myServerCommunicator.getWeatherData(forecast,
					gridCoos[0], gridCoos[1]);
			progressCounter = progressCounter + 7;
			publishProgress(progressCounter); // Total: 7/22
		}
		if (localDataset == null) {
			isCancelled = true;
			reason = "serverConnection";
		} else if (localDataset.length == 1) {
			isCancelled = true;
			reason = "server";
		} else {

			if (forecastNumber == 0) {
				String firstForecastServerInformation = localDataset[0][0];
				firstForecastEndTime = myServerCommunicator
						.extractForecastRetrievedTime(firstForecastServerInformation);
			}

			dataset.add(forecastNumber, localDataset[forecastNumber]);

			WeatherEnumHandler weather_data;
			weather_data = new WeatherEnumHandler();
			weather_data.handleWeatherEnum(localDataset[forecastNumber],
					applicationContext);
			progressCounter = progressCounter + 4;
			publishProgress(progressCounter); // Total: 11/23

			boolean[] advice = weather_data.weatherType.show_imgs;
			advice = Arrays.copyOf(advice, advice.length + 1);
			advice[advice.length - 1] = weather_data.sunglasses;

			progressCounter = progressCounter + 4;
			publishProgress(progressCounter); // Total: 15/23

			MergeImage myMergeImage = new MergeImage();
			mergedImage[forecastNumber] = myMergeImage.MergeForecastImage(
					advice, applicationContext);
			progressCounter = progressCounter + 7;
			publishProgress(progressCounter); // Total: 22/22
		}
	}

	/**
	 * getRegionSharedPreference(Resources res, SharedPreference sharedPref)
	 * returns the region preference from the SharedPreferences
	 */

	public String getRegionSharedPreference(Resources res,
			SharedPreferences sharedPref) {
		// Get default Preference Values
		String defaultRegion = res.getString(R.string.default_region);
		// Read the preference values from SharedPreferences
		String regionPreference = sharedPref.getString(
				res.getString(R.string.region_preference_key), defaultRegion);
		return regionPreference;
	}
}