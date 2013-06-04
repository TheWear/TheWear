package src.gui.thewearandroid;

import java.util.concurrent.ExecutionException;

import clientAPP.ForecastInfo;
import clientAPP.Forecaster;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends Activity {

	/**
	 * The GUI of the Application.
	 */

	private ViewPager mViewPager;
	private ImageView[] myImageViews = { null, null, null };
	private Forecaster myForecasterObject;

	/**
	 * onCreate of the GUI contains all the code we want to have executed on
	 * creation of the App instance:
	 * 
	 * * set the layout;
	 * 
	 * * hard coded keyboard for the location field ( the keyboard didn't want
	 * to show up without);
	 * 
	 * * set a listener for the button that shows with the keyboard: clicking
	 * that button also gives you the forecast;
	 * 
	 * * setting the ViewPager for the forecast ImageViews; and an
	 * onClickListener for a changed view to make the correlated buttons
	 * clickable non-clickable when necessary, and to dynamically change the
	 * title (moment for the forecast) to the applicable one;
	 * 
	 * * starting the forecast on creation of the App when we do have a saved
	 * location preference (so when the user did run the App at least once),
	 * otherwise a toast is shown.
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("TheWearDebug", "onCreate()...");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final EditText locationField = (EditText) findViewById(R.id.editText1);
		locationField.clearFocus();

		// Show and hide keyboard when focused/unfocused:
		final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		locationField
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus == true) {
							// Show keyboard
							imm.showSoftInput(locationField,
									InputMethodManager.SHOW_FORCED);
						} else {
							// Hide keyboard
							imm.hideSoftInputFromWindow(
									locationField.getWindowToken(), 0);
						}
					}
				});

		locationField.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView editedTextView,
					int actionId, KeyEvent keyEvent) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {

					String userLocation = editedTextView.getText().toString();
					// Remove whitespace in front and at the end of the string
					userLocation = userLocation.trim();
					Log.d("TheWearDebug", "userLocation: " + userLocation);

					// check for empty location field and display Toast if so.
					if (userLocation.equals("")) {
						Toast myToast = Toast
								.makeText(
										getApplicationContext(),
										"Please Enter Location and press Play for the Forecast",
										Toast.LENGTH_SHORT);
						myToast.setGravity(Gravity.CENTER, 0, 0);
						myToast.show();
					} else {
						handleStartForecast(userLocation);
					}

					handled = true;
				}
				return handled;
			}
		});

		final ImageButton goForwardButton = (ImageButton) findViewById(R.id.button_forward);
		final ImageButton goBackButton = (ImageButton) findViewById(R.id.button_back);

		// Making the goBackButton unClickable on App startup (you start with
		// the first Forecast)
		goBackButton.setClickable(false);

		// Set ViewPager to be able to swipe the Images
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		ImagePagerAdapter adapter = new ImagePagerAdapter();
		mViewPager.setAdapter(adapter);

		final TextView textView = (TextView) findViewById(R.id.textView1);
		// Set the TextView for startup
		final String tab1 = getString(R.string.title_section1);
		final String tab2 = getString(R.string.title_section2);
		final String tab3 = getString(R.string.title_section3);
		textView.setText(tab1);

		// Initiate ImageViews for use in the Forecaster
		myImageViews[0] = new ImageView(this);
		myImageViews[1] = new ImageView(this);
		myImageViews[2] = new ImageView(this);

		// Set OnPageChangeListener to make buttons unClickable when they are
		// not used, and Clickable again when they can be used.
		// The listener also changes the title (day) when swiping.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						Log.d("TheWearDebug",
								"SimpleOnPageChangeListener triggered");
						if (position == 0) {
							// Forecast 1: Set the goBackButton unClickable
							goBackButton.setClickable(false);
							Log.d("TheWearDebug", "goBackButton unClickable");
							textView.setText(tab1);
						} else if (position == 1) {
							// Forecast 2: Set the goBackButton & the
							// goForwardButton Clickable
							goBackButton.setClickable(true);
							Log.d("TheWearDebug", "goBackButton Clickable");
							goForwardButton.setClickable(true);
							Log.d("TheWearDebug", "goForwardButton Clickable");
							textView.setText(tab2);
						} else if (position == 2) {
							// Forecast 3: Set the goForwardButton unClickable
							goForwardButton.setClickable(false);
							Log.d("TheWearDebug", "Forecast 3");
							textView.setText(tab3);
						} else {
							// Should not happen
							Log.e("TheWearDebug",
									"ERROR: Forecast selected that doesn't exist");
						}
					}
				});
		Log.i("TheWearDebug", "onCreate() finished");

		// Forecast on startup:

		// Get the Location Preference.
		Context context = this;
		SharedPreferences sharedPref = context.getSharedPreferences(
				getString(R.string.TheWear_preference_key),
				Context.MODE_PRIVATE);
		String startLocation = sharedPref.getString(
				getString(R.string.location_preference), null);
		Log.d("TheWearDebug", "Got startLocation: " + startLocation);

		// Check if the preference is empty. If the Preference is empty, it
		// means the application is not yet used.
		if (startLocation != null) {
			Log.d("TheWearDebug", "startLocation != null");

			// Start Forecast
			handleStartForecast(startLocation);

		} else {
			Log.d("TheWearDebug", "startLocation == null");
			// A toast shown only on the first startup
			Toast myToast = Toast.makeText(getApplicationContext(),
					"Enter Location and press Play for the Forecast",
					Toast.LENGTH_LONG);
			myToast.setGravity(Gravity.CENTER, 0, 0);
			myToast.show();
		}
		Log.i("TheWearDebug", "onStart() finished");
	} // End onCreate

	/**
	 * Methods used by the buttons. These buttons are linked to these methods
	 * using Android:onClick="methodName"
	 * 
	 * startForecast is called by the 'button_forecast' and starts the forecast;
	 * 
	 * goForward is called by the 'button_forward' and let you go to the next
	 * tab;
	 * 
	 * goBack is called by the 'button_back' and let you go to the next tab;
	 * 
	 * goToSettings is called by imageButton1 and shows the settings window of
	 * our app;
	 * 
	 * showForecastInformation is called by imageButton2 and shows in-dept
	 * forecast information (for example: wind speed, mm rain...)
	 */

	// Start Forecast, used by all 3 of the pages
	public void startForecast(View v) {
		Log.d("Buttons", "Start Forecast");

		// Read the location out of the TextField to use in the Forecaster
		EditText locationField = (EditText) findViewById(R.id.editText1);
		String userLocation = locationField.getText().toString();
		// Remove whitespace in front and at the end of the string
		userLocation = userLocation.trim();
		Log.d("TheWearDebug", "userLocation: " + userLocation);

		// check for empty location field and display Toast if so.
		if (userLocation.equals("")) {
			Toast myToast = Toast.makeText(getApplicationContext(),
					"Please Enter Location and press Play for the Forecast",
					Toast.LENGTH_SHORT);
			myToast.setGravity(Gravity.CENTER, 0, 0);
			myToast.show();
		} else {
			handleStartForecast(userLocation);
		}
	}

	// go one page further, used by 1st and 2nd page (not by 3th)
	public void goForward(View v) {
		Log.d("Buttons", "We go forward!");
		// Get current Forecast number
		int tabNumber = mViewPager.getCurrentItem();
		Log.i("SelectedTab", "The current Selected tabNumber is \'" + tabNumber
				+ "\'");
		if (tabNumber == 0) { // Tab 1 to tab 2
			Log.d("Buttons", "Going to Forecast 2");
			mViewPager.setCurrentItem(1, true); // True is for smoothScroll
		} else if (tabNumber == 1) { // Tab 2 to tab 3
			Log.d("Buttons", "Going to Forecast 3");
			mViewPager.setCurrentItem(2, true);
		} else { // In case of an Error
			Log.e("SwitchForecastButton",
					"An error occured while switching Forecast (forward). Check the Selected Forecast");
		}
	}

	// go one page back, used by 2nd and 3th Forecast (not by 1st)
	public void goBack(View v) {
		Log.d("Buttons", "We go backward!");
		// Get current Forecast number
		int tabNumber = mViewPager.getCurrentItem();
		Log.i("SelectedTab", "The current Selected tabNumber is \'" + tabNumber
				+ "\'");
		if (tabNumber == 1) { // Tab 2 to tab 1
			Log.d("Buttons", "Going to Forecast 1");
			mViewPager.setCurrentItem(0, true);
		} else if (tabNumber == 2) { // Tab 3 to tab 2
			Log.d("Buttons", "Going to Forecast 2");
			mViewPager.setCurrentItem(1, true);
		} else { // In case of an Error
			Log.e("SwitchForecastButton",
					"An error occured while switching Forecast (back). Check the Selected Forecast");
		}
	}

	public void goToSettings(View v) {
		// Show the Preferences Window
		ForecastPreferencesFragment myForecastPreferencesFragment = new ForecastPreferencesFragment();
		myForecastPreferencesFragment.show(getFragmentManager(), "Preferences");
	}

	public void showForecastInformation(View v) {
		// Show the additional forecast information
		ForecastInfo myForecastInfo = null;
		Log.d("TheWearDebug", "\'about\' clicked");
		try {
			myForecastInfo = myForecasterObject.get();
		} catch (InterruptedException e) {
			// Auto-generated catch block
			Log.e("TheWearDebug", "InterruptedException");
			e.printStackTrace();
		} catch (ExecutionException e) {
			// Auto-generated catch block
			Log.e("TheWearDebug", "ExecutionException");
			e.printStackTrace();
		}
		int tabNumber = mViewPager.getCurrentItem();
		String detailedInformation = myForecastInfo.detailedForecastInformation[tabNumber];
		if (detailedInformation != null) {
			Log.d("TheWearDebug", "Weather information available");

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(detailedInformation)
					.setTitle(R.string.forecastInfo_title);
			builder.setPositiveButton(R.string.positive_button,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User clicked OK button
						}
					});
			// 3. Get the AlertDialog from create()
			AlertDialog dialog = builder.create();
			dialog.show();

		} else {
			Log.d("TheWearDebug", "No detailed weather information available");
			Toast myToast = Toast.makeText(getApplicationContext(),
					"No detailed weather information available", Toast.LENGTH_SHORT);
			myToast.setGravity(Gravity.CENTER, 0, 0);
			myToast.show();
		}
	}

	/**
	 * handleStartForecast starts a Forecaster AsyncTask with all the necessary
	 * parameters.
	 * 
	 * handleStartForecast has 'String userLocation' as input, and returns void.
	 */

	public void handleStartForecast(String userLocation) {
		// Initiate the EditText Object to be used by the Forecaster
		// AsyncTask to return (show) the during the Forecaster AsyncTask
		// looked up location to the user.
		EditText locationField = (EditText) findViewById(R.id.editText1);
		locationField.clearFocus();

		// Call the Forecaster AsyncTask
		myForecasterObject = new Forecaster(this, locationField, myImageViews);
		myForecasterObject.execute(userLocation);
		Log.d("TheWearDebug", "Finished the Forecast AsyncTask");
	}

	/**
	 * ImagePagerAdapter contains the methods used to display the ImageViews
	 * that show the forecast.
	 */

	public class ImagePagerAdapter extends PagerAdapter {

		// TODO Get the images centered. This might be solved by adding images
		// with the right dimensions in the corresponding drawable-folders.

		// Set the Images to be used for the swipe
		private int[] mImages = new int[] { R.drawable.today, R.drawable.today,
				R.drawable.day_after_tomorrow };

		/**
		 * getImages is the method called to get (and construct) the images
		 * shown if there is no forecast available.
		 * 
		 * Input is the position of the tab where the image is shown;
		 * 
		 * the method returns a bitmap.
		 */

		// TODO change the default images to a default one instead of the random
		// ones I choose

		public Bitmap getImages(int position) {
			Bitmap myBitmap = Bitmap.createBitmap(300, 500, Config.ARGB_8888);
			Resources res = getResources();
			if (position == 0) {
				Bitmap body = BitmapFactory.decodeResource(res,
						mImages[position]);
				Bitmap leftArm = BitmapFactory.decodeResource(res,
						R.drawable.left_arm_down);
				Bitmap rightArm = BitmapFactory.decodeResource(res,
						R.drawable.right_arm_down);
				Canvas canvas = new Canvas(myBitmap);
				canvas.drawColor(0x00AAAAAA);
				canvas.drawBitmap(body, 0, 0, null);
				canvas.drawBitmap(leftArm, 0, 0, null);
				canvas.drawBitmap(rightArm, 0, 0, null);
			} else {
				myBitmap = BitmapFactory.decodeResource(res, mImages[position]);
			}

			return myBitmap;
		}

		@Override
		public int getCount() {
			// Returns the number of images
			return mImages.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			// Identifies whether a page View is associated with a given key
			// object
			return view == ((ImageView) object);
		}

		/**
		 * instantiateItem is called by the ViewPager to instantiate the tab
		 * with our ImageView to show the forecast.
		 * 
		 * This method checks if a forecast got retrieved: if so, the image
		 * displaying the forecast is shown, otherwise getImages is called, and
		 * a default image is displayed.
		 */

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Context context = MainActivity.this;
			ImageView imageView = myImageViews[position];
			Log.d("TheWearDebug", "Setting Image for position " + position);

			ForecastInfo myForecastInfo = null;
			try {
				myForecastInfo = myForecasterObject.get();
			} catch (InterruptedException e) {
				// Auto-generated catch block
				Log.e("TheWearDebug", "InterruptedException");
				e.printStackTrace();
			} catch (ExecutionException e) {
				// Auto-generated catch block
				Log.e("TheWearDebug", "ExecutionException");
				e.printStackTrace();
			}
			Bitmap bitmap = null;
			if (myForecastInfo.mergedImages[position] != null) {
				bitmap = myForecastInfo.mergedImages[position];
			} else {
				bitmap = getImages(position);
			}

			// set offset from the edges
			int padding = context.getResources().getDimensionPixelSize(
					R.dimen.padding_medium);
			int paddingBottom = context.getResources().getDimensionPixelSize(
					R.dimen.padding_bottom_medium);
			// setPadding(int left, int top, int right, int bottom)
			imageView.setPadding(padding, padding, padding, paddingBottom);

			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			// Options: CENTER, CENTER_INSIDE, FIT_CENTER
			imageView.setImageBitmap(bitmap);
			((ViewPager) container).addView(imageView, 0);

			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((ImageView) object);
		}
	}
}