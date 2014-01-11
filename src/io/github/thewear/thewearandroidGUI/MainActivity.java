package io.github.thewear.thewearandroidGUI;

import io.github.thewear.thewearandroidClientAPP.DetailedForecastInformationManager;
import io.github.thewear.thewearandroidClientAPP.ForecastInfo;
import io.github.thewear.thewearandroidClientAPP.ForecastTimeStruct;
import io.github.thewear.thewearandroidClientAPP.Forecaster;

import java.util.concurrent.ExecutionException;

import src.gui.thewearandroid.R;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	private SocialMediaPickerFragment mySocialMediaPickerFragment;
	private MenuFragment myMenuFragment;
	private ForecastTimeStruct myForecastTimeStruct;
	private TextView titleTextView;
	private String startLocation;

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
	 * 
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("TheWearDebug", "onCreate()...");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final EditText locationField = (EditText) findViewById(R.id.editText1);
		locationField.clearFocus();

		// TODO Add a check if there is a hardware keyboard?

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
										R.string.firstTimeStartupMessage,
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

		// Making the goBackButton unClickable and invisible on App startup (you
		// start with
		// the first Forecast)
		goBackButton.setClickable(false);
		goBackButton.setVisibility(View.INVISIBLE);

		// Set ViewPager to be able to swipe the Images
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		ImagePagerAdapter adapter = new ImagePagerAdapter();
		mViewPager.setAdapter(adapter);

		titleTextView = (TextView) findViewById(R.id.textView1);
		// Set the TextView for startup
		final String tab1 = getString(R.string.title_section1);
		final String tab2 = getString(R.string.title_section2);
		final String tab3 = getString(R.string.title_section3);
		String[] tabTitles = { tab1, tab2, tab3 };
		// Save a temporary title until the one corresponding with the forecast
		// is saved.
		myForecastTimeStruct = new ForecastTimeStruct(tabTitles);
		titleTextView.setText(myForecastTimeStruct.forecastTimeString[0]);

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
							// Forecast 1: Set the goBackButton unClickable and
							// invisible
							goBackButton.setClickable(false);
							goBackButton.setVisibility(View.INVISIBLE);
							Log.d("TheWearDebug", "goBackButton unClickable");
							titleTextView
									.setText(myForecastTimeStruct.forecastTimeString[0]);
						} else if (position == 1) {
							// Forecast 2: Set the goBackButton & the
							// goForwardButton Clickable and visible
							goBackButton.setClickable(true);
							goBackButton.setVisibility(View.VISIBLE);
							Log.d("TheWearDebug", "goBackButton Clickable");
							goForwardButton.setClickable(true);
							goForwardButton.setVisibility(View.VISIBLE);
							Log.d("TheWearDebug", "goForwardButton Clickable");
							titleTextView
									.setText(myForecastTimeStruct.forecastTimeString[1]);
						} else if (position == 2) {
							// Forecast 3: Set the goForwardButton unClickable
							// and invisible
							goForwardButton.setClickable(false);
							goForwardButton.setVisibility(View.INVISIBLE);
							Log.d("TheWearDebug", "Forecast 3");
							titleTextView
									.setText(myForecastTimeStruct.forecastTimeString[2]);
						} else {
							// Should not happen
							Log.e("TheWearDebug",
									"ERROR: Forecast selected that doesn't exist");
						}
					}
				});

		Log.i("TheWearDebug", "onCreate() finished");
	} // End onCreate

	/**
	 * onResume is called when the application is visible and just before the
	 * application starts interacting with the user.
	 * 
	 * The Forecast will be started only if the user just started the App
	 * (checked with appCreated) and if we do have a saved location preference
	 * (so when the user did run the App at least once), otherwise a toast is
	 * shown.
	 */

	@Override
	public void onResume() {
		super.onResume();

		// Get the Location Preference.
		Context context = this;
		SharedPreferences sharedPref = context.getSharedPreferences(
				getString(R.string.TheWear_preference_key),
				Context.MODE_PRIVATE);
		startLocation = sharedPref.getString(
				getString(R.string.location_preference), null);
		Log.d("TheWearDebug", "Got startLocation: " + startLocation);

		// Check if the preference is empty. If the Preference is empty, it
		// means the application is not yet used.
		if (startLocation != null) {
			Log.d("TheWearDebug", "startLocation != null");

			// Dialog to ask user if they want to (re)download the forecast
			// information. (Additional use: delay downloading the forecast so
			// the App loads faster)
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			Log.e("BUG", "AlertDialog builder: " + builder);
			builder.setMessage(R.string.startupDialogContent);
			Log.d("BUG", "Message Set");
			builder.setPositiveButton(R.string.dialogButtonYes,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int id) {
							// Start Forecast
							handleStartForecast(startLocation);

						}
					});
			Log.d("BUG", "Positive Button Set");
			builder.setNegativeButton(R.string.dialogButtonNo,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int id) {
							// Set Location in the location field
							EditText locationField = (EditText) findViewById(R.id.editText1);
							locationField.setText(startLocation);

						}
					});
			Log.d("BUG", "Negative Button Set");

			AlertDialog dialog = builder.create();
			Log.e("BUG", "Dialog Created. Dialog: " + dialog);
			dialog.show();

		} else {
			Log.d("TheWearDebug", "startLocation == null");
			// A toast shown only on the first startup
			Toast myToast = Toast.makeText(getApplicationContext(),
					R.string.firstTimeStartupMessage,
					Toast.LENGTH_LONG);
			myToast.setGravity(Gravity.CENTER, 0, 0);
			myToast.show();
		}

	} // End onResume()

	/**
	 * Opens the options menu when the menu button is clicked.
	 */

	@Override
	public boolean onKeyDown(int keycode, KeyEvent event) {
		switch (keycode) {
		case KeyEvent.KEYCODE_MENU:

			// Open Options Menu
			myMenuFragment = new MenuFragment();
			myMenuFragment.passNecessaryInformation(this, this);
			myMenuFragment.show(getFragmentManager(), "Menu");

			return true;
		}

		return super.onKeyDown(keycode, event);
	}

	/**
	 * onCreateOptionsMenu() is only called on creating the activity and creates
	 * the menu in the ActionBar from the menu file main.xml
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * goToPreferences() calls showForecastPreferences() to show the forecast
	 * preferences dialog
	 */

	public boolean goToPreferences(MenuItem menu) {
		showForecastPreferences();
		return true;
	}

	/**
	 * goToLocationPreference() calls showForecastPreferences() to show the
	 * forecast preferences dialog
	 */

	public boolean goToLocationPreference(MenuItem menu) {
		showLocationPreference();
		return true;
	}

	/**
	 * goToSettings() calls showAboutApp to show the about dialog.
	 */

	public boolean goToSettings(MenuItem menu) {
		showSettings();
		return true;
	}

	/**
	 * showAbout() calls showAboutApp to show the about dialog.
	 */

	public boolean showAbout(MenuItem menu) {
		showAboutApp();
		return true;
	}

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
					R.string.firstTimeStartupMessage,
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

	/**
	 * shareFacebook() is called when the Facebook button on the
	 * social_media_dialog.xml is clicked on the social_media_dialog.xml.
	 */

	public void shareFacebook(View v) {
		Log.d("TheWearDebug", "Sharing over Facebook");
		// Close the SocialMediaPickerFragment
		mySocialMediaPickerFragment.dismiss();
		// TODO Implement sharing over Facebook
		Log.i("TheWearDebug", "Sharing over Facebook");
	}

	/**
	 * shareTwitter() is called when the Twitter button on the
	 * social_media_dialog.xml is clicked on the social_media_dialog.xml.
	 */

	public void shareTwitter(View v) {
		Log.d("TheWearDebug", "Sharing over Twitter");
		// Close the SocialMediaPickerFragment
		mySocialMediaPickerFragment.dismiss();
		// TODO implement sharing over Twitter
		Log.i("TheWearDebug", "Sharing over Twitter");
	}

	/**
	 * showMenuItem1() calls showForecastPreferences() to show the forecast
	 * preferences dialog and closes the menu dialog.
	 */

	public void showMenuItem1() {
		myMenuFragment.dismiss();
		showForecastPreferences();
	}

	/**
	 * showMenuItem2() calls showLocationPreference() to show the forecast
	 * location preference dialog and closes the menu dialog.
	 */

	public void showMenuItem2() {
		myMenuFragment.dismiss();
		showLocationPreference();
	}

	/**
	 * showMenuItem3() calls showAboutApp() to show the about dialog and closes
	 * the menu dialog.
	 */

	public void showMenuItem3() {
		myMenuFragment.dismiss();
		showSettings();
	}

	/**
	 * showMenuItem4() calls showAboutApp() to show the about dialog and closes
	 * the menu dialog.
	 */

	public void showMenuItem4() {
		myMenuFragment.dismiss();
		showAboutApp();
	}

	/**
	 * showForecastInformation gets the detailed forecast information, and
	 * constructs the dialog and shows it.
	 * 
	 * if there is no detailed forecast information available, a toast is shown
	 * that notifies the user of this.
	 * 
	 * input: the tab position of the imageView to connect the detailed
	 * information with the corresponding imageView.
	 */

	public void showForecastInformation(int position) {
		// Show the additional forecast information
		Log.d("TheWearDebug", "Detailed Information clicked");
		if (myForecasterObject != null) {
			ForecastInfo myForecastInfo = null;
			try {
				myForecastInfo = myForecasterObject.get();
			} catch (InterruptedException e) {
				// Auto-generated catch block
				Log.e("TheWearDebug", "InterruptedException1");
				e.printStackTrace();
			} catch (ExecutionException e) {
				// Auto-generated catch block
				Log.e("TheWearDebug", "ExecutionException2");
				e.printStackTrace();
			}
			if (myForecastInfo == null) {
				Log.d("TheWearDebug",
						"No detailed weather information available");
				Toast myToast = Toast.makeText(getApplicationContext(),
						R.string.noForecastDetailedInfo,
						Toast.LENGTH_SHORT);
				myToast.setGravity(Gravity.CENTER, 0, 0);
				myToast.show();
			} else {
				String[] dataset = myForecastInfo.dataset.get(position);

				if (dataset != null) {
					Log.d("TheWearDebug", "Weather information available");

					DetailedForecastInformationManager myDetailedForecastInformationManager = new DetailedForecastInformationManager(
							dataset);
					SharedPreferences sharedPref = getSharedPreferences(
							getString(R.string.TheWear_preference_key),
							Context.MODE_PRIVATE);
					String detailedInformation = myDetailedForecastInformationManager
							.getString(sharedPref, getResources());

					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(detailedInformation).setTitle(
							R.string.forecastInfo_title);
					builder.setPositiveButton(R.string.dialogButtonClose,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked OK button
								}
							});
					// Get the AlertDialog from create()
					AlertDialog dialog = builder.create();
					dialog.show();
				} else {
					Log.d("TheWearDebug",
							"No detailed weather information available");
					Toast myToast = Toast.makeText(getApplicationContext(),
							"No detailed weather information available",
							Toast.LENGTH_LONG);
					myToast.setGravity(Gravity.CENTER, 0, 0);
					myToast.show();
				}
			}
		} else {
			Log.d("TheWearAndroid", "myForecastObject == null");
			Toast myToast = Toast.makeText(getApplicationContext(),
					R.string.noForecastDetailedInfo, Toast.LENGTH_LONG);
			myToast.setGravity(Gravity.CENTER, 0, 0);
			myToast.show();
		}
	}

	/**
	 * showSocialMediaPicker() opens a Dialog containing the social media that
	 * can be used for sharing, so the user can pick the social media they
	 * prefer.
	 * 
	 * This method also gets the current forecast image and passes it to the
	 * SocialMediaPickerFragment along with the tabNumber for the time
	 * reference.
	 */

	public void showSocialMediaPicker(View v) {
		// Get the Bitmap
		if (myForecasterObject != null) {
			ForecastInfo myForecastInfo = null;
			try {
				myForecastInfo = myForecasterObject.get();
			} catch (InterruptedException e) {
				// Auto-generated catch block
				Log.e("TheWearDebug", "InterruptedException");
				e.printStackTrace();
			} catch (ExecutionException e) {
				// Auto-generated catch block
				Log.e("TheWearDebug", "ExecutionException3");
				e.printStackTrace();
			}
			if (myForecastInfo == null) {
				Log.d("TheWearDebug", "No Bitmap to share");
				Toast myToast = Toast.makeText(getApplicationContext(),
						R.string.noForecast, Toast.LENGTH_LONG);
				myToast.setGravity(Gravity.CENTER, 0, 0);
				myToast.show();
			} else {
				int tabNumber = mViewPager.getCurrentItem();
				Bitmap currentBitmap = myForecastInfo.mergedImages[tabNumber];
				if (currentBitmap != null) {
					mySocialMediaPickerFragment = new SocialMediaPickerFragment();
					mySocialMediaPickerFragment.show(getFragmentManager(),
							"Preferences");
				} else {
					Log.d("TheWearDebug", "No Bitmap to share");
					Toast myToast = Toast.makeText(getApplicationContext(),
							R.string.noForecast, Toast.LENGTH_LONG);
					myToast.setGravity(Gravity.CENTER, 0, 0);
					myToast.show();
				}
			}
		} else {
			Log.d("TheWearAndroid", "myForecastObject == null");
			Toast myToast = Toast.makeText(getApplicationContext(),
					"No forecast to share", Toast.LENGTH_SHORT);
			myToast.setGravity(Gravity.CENTER, 0, 0);
			myToast.show();
		}
	}

	/**
	 * showForecastPreferences() shows the preferences menu (the
	 * ForecastPreferencesFragment) when called.
	 * 
	 * This method tries to retrieve the dataset of the last forecast, and
	 * passes it to the setter method of the ForecastPreferencesFragment(),
	 * together with the ImageViews that should be changed, and the
	 * applicationContext.
	 */

	public void showForecastPreferences() {
		// Show the Preferences Window
		if (myForecasterObject != null) {
			ForecastInfo myForecastInfo = null;
			Log.d("TheWearDebug", "\'Preferences\' clicked");
			try {
				myForecastInfo = myForecasterObject.get();
			} catch (InterruptedException e) {
				// Auto-generated catch block
				Log.e("TheWearDebug", "InterruptedException");
				e.printStackTrace();
			} catch (ExecutionException e) {
				// Auto-generated catch block
				Log.e("TheWearDebug", "ExecutionException4");
				e.printStackTrace();
			}
			// Catch an empty myForecastInfo
			ForecastPreferencesFragment myForecastPreferencesFragment = new ForecastPreferencesFragment();
			myForecastPreferencesFragment.passNecessaryInformation(
					myImageViews, myForecastInfo, this);
			myForecastPreferencesFragment.show(getFragmentManager(),
					"Preferences");
		} else {
			Log.d("TheWearAndroid", "myForecastObject == null");
		}
	}

	/**
	 * showLocationPreferences() shows the region preference menu (the
	 * RegionPreferencesFragment) when called.
	 * 
	 * This method tries to retrieve the dataset of the last forecast, and
	 * passes it to the setter method of the ForecastPreferencesFragment(),
	 * together with the ImageViews that should be changed, and the
	 * applicationContext.
	 */

	public void showLocationPreference() {
		// Show the Preferences Window
		if (myForecasterObject != null) {
			ForecastInfo myForecastInfo = null;
			Log.d("TheWearDebug", "\'Location Preferences\' clicked");
			try {
				myForecastInfo = myForecasterObject.get();
			} catch (InterruptedException e) {
				// Auto-generated catch block
				Log.e("TheWearDebug", "InterruptedException");
				e.printStackTrace();
			} catch (ExecutionException e) {
				// Auto-generated catch block
				Log.e("TheWearDebug", "ExecutionException5");
				e.printStackTrace();
			}
			// Catch an empty myForecastInfo
			RegionPreferencesFragment myRegionPreferencesFragment = new RegionPreferencesFragment();
			myRegionPreferencesFragment.passNecessaryInformation(myImageViews,
					myForecastInfo, this);
			myRegionPreferencesFragment.show(getFragmentManager(),
					"Preferences");
		} else {
			Log.d("TheWearAndroid", "myForecastObject == null");
		}
	}

	/**
	 * showSettings() shows the settings menu (the SettingsFragment) when
	 * called.
	 */

	public void showSettings() {
		Log.d("TheWearDebug", "Settings");
		SettingsFragment mySettingsFragment = new SettingsFragment();
		mySettingsFragment.passNecessaryInformation(myForecastTimeStruct,
				titleTextView, mViewPager);
		mySettingsFragment.show(getFragmentManager(), "Settings");
	}

	/**
	 * showAbout() shows the user a dialog containing information of our App.
	 */

	public void showAboutApp() {
		Log.d("TheWearDebug", "About our App");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String aboutText = getString(R.string.about_text) + "\nVersion: "
				+ getString(R.string.testVersion);
		builder.setMessage(aboutText).setTitle(R.string.action_about);
		builder.setPositiveButton(R.string.closeDialog,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User clicked OK button
					}
				});
		// Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		dialog.show();
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
		myForecasterObject = new Forecaster(this, locationField, myImageViews,
				myForecastTimeStruct, titleTextView, mViewPager);
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

		// TODO Change so only 1 view gets instantiated withouth a forecast

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
		 * 
		 * When this method constructs a View -- consisting of 1 ImageView --,
		 * an onClickListener is set for the ImageView. The onClickListener
		 * calls the showForecastInformation() method (located in the
		 * MainActivity class). The code in the showForecastInformation() can't
		 * be executed in the ImagePagerAdapter subclass because the
		 * AlertDialog.Builder doesn't work in this subclass. I have decided to
		 * put all the code in the showForecastInformation() method instead of
		 * making a method only for building the Dialog to keep that part of the
		 * code together.
		 */

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			Context context = MainActivity.this;
			ImageView imageView = myImageViews[position];
			Log.d("TheWearDebug", "Setting Image for position " + position);

			Bitmap bitmap = null;
			if (myForecasterObject != null) {
				ForecastInfo myForecastInfo = null;
				try {
					myForecastInfo = myForecasterObject.get();
				} catch (InterruptedException e) {
					// Auto-generated catch block
					Log.e("TheWearDebug", "InterruptedException");
					e.printStackTrace();
				} catch (ExecutionException e) {
					// Auto-generated catch block
					Log.e("TheWearDebug", "ExecutionException6");
					e.printStackTrace();
				}
				if (myForecastInfo != null) {
					if (myForecastInfo.mergedImages[position] != null) {
						bitmap = myForecastInfo.mergedImages[position];
					} else {
						Log.d("TheWearAndroid", "mergedImage[pos] == null");
						bitmap = getImages(position);
					}
				} else {
					Log.d("TheWearAndroid", "myForecastInfo == null");
					bitmap = getImages(position);
				}
			} else {
				Log.d("TheWearAndroid", "myForecastObject == null");
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

			// set OnclickListener for imageView
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showForecastInformation(position);
				}
			});

			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((ImageView) object);
		}
	}
}