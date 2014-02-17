package io.github.thewear.thewearandroidGUI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import io.github.thewear.thewearandroidClientAPP.DetailedForecastInformationManager;
import io.github.thewear.thewearandroidClientAPP.ForecastInfo;
import io.github.thewear.thewearandroidClientAPP.ForecastTimeStruct;
import io.github.thewear.thewearandroidClientAPP.Forecaster;
import io.github.thewear.thewearandroidClientAPP.GPSCoordsManager;
import io.github.thewear.thewearandroid.R;

public class MainActivity extends FragmentActivity {

	/**
	 * The GUI of the Application.
	 */

	private ViewPager mViewPager;
	private ImageView[] myImageViews = { null, null, null };
	private boolean[] imageViewAddedToImagePagerAdapter = { false, false, false };
	private Forecaster myForecasterObject;
	private SocialMediaPickerFragment mySocialMediaPickerFragment;
	private MenuFragment myMenuFragment;
	private ForecastTimeStruct myForecastTimeStruct;
	private TextView titleTextView;
	private String startLocation;
	private ImagePagerAdapter imagePagerAdapter;
	private ImageButton goForwardButton;
	private ImageButton goBackButton;

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
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set defaults for the preferences.
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

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

					// check for empty location field and display Toast if so.
					if (userLocation.equals("")) {
						Toast myToast = Toast.makeText(getApplicationContext(),
								R.string.firstTimeStartupMessage,
								Toast.LENGTH_LONG);
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

		goForwardButton = (ImageButton) findViewById(R.id.button_forward);
		goBackButton = (ImageButton) findViewById(R.id.button_back);

		// Making the goBackButton and goForwardButton unClickable and invisible
		// on App startup (you start with the first Forecast)
		goBackButton.setClickable(false);
		goBackButton.setVisibility(View.INVISIBLE);
		goForwardButton.setClickable(false);
		goForwardButton.setVisibility(View.INVISIBLE);

		// Initiate ImageViews for use in the Forecaster
		myImageViews[0] = new ImageView(this);
		myImageViews[1] = new ImageView(this);
		myImageViews[2] = new ImageView(this);

		// Set ViewPager to be able to swipe the Images
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		imagePagerAdapter = new ImagePagerAdapter();
		mViewPager.setAdapter(imagePagerAdapter);
		// Add first Image to the ViewPager
		int position = 0;
		Resources res = getResources();
		// TODO change the default images to a default one instead of the random
		// ones I choose
		Drawable drawable = res.getDrawable(R.drawable.ic_launcher);
		ImageView myImageView = myImageViews[position];
		ImageView imageView = setImageView(myImageView, drawable);
		imagePagerAdapter.addView(imageView, position);
		// Set imageViewStatus initiated
		imageViewAddedToImagePagerAdapter[position] = true;

		titleTextView = (TextView) findViewById(R.id.textView1);
		// Set the TextView for startup
		final String tab1 = getString(R.string.title);
		final String tab2 = null;
		final String tab3 = null;
		String[] tabTitles = { tab1, tab2, tab3 };
		// Save a temporary title until the one corresponding with the forecast
		// is saved.
		myForecastTimeStruct = new ForecastTimeStruct(tabTitles);
		titleTextView.setText(myForecastTimeStruct.forecastTimeString[0]);
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
				getString(R.string.location_preference_key), null);

		// Check if the preference is empty. If the Preference is empty, it
		// means the application is not yet used.
		if (startLocation != null) {

			// Set Location in the location field
			EditText locationField = (EditText) findViewById(R.id.editText1);
			locationField.setText(startLocation);

			// Dialog to ask user if they want to (re)download the forecast
			// information. (Additional use: delay downloading the forecast so
			// the App loads faster)
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.startupDialogContent);
			builder.setPositiveButton(R.string.dialogButtonYes,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int id) {
							// Start Forecast
							handleStartForecast(startLocation);

						}
					});
			builder.setNegativeButton(R.string.dialogButtonNo,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int id) {
							// nothing

						}
					});

			AlertDialog dialog = builder.create();
			dialog.show();

		} else {
			// A toast shown only on the first startup
			Toast myToast = Toast.makeText(getApplicationContext(),
					R.string.firstTimeStartupMessage, Toast.LENGTH_LONG);
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
			myMenuFragment.show(getSupportFragmentManager(), "Menu");

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
	 * setImageView(ImageView imageView, Bitmap bitmap)
	 * 
	 * * sets the padding for the imageView used in the ViewPager (the swiped
	 * image)
	 * 
	 * * sets the ScaleType (how the image is centered)
	 * 
	 * * places the bitmap in the imageView.
	 * 
	 * Returns the imageView with added borders, ScaleType and image.
	 */

	public ImageView setImageView(ImageView imageView, Bitmap bitmap) {
		int padding = getResources().getDimensionPixelSize(
				R.dimen.padding_medium);
		int paddingBottom = getResources().getDimensionPixelSize(
				R.dimen.padding_bottom_medium);
		// setPadding(integer left, integer top, integer right, integer bottom)
		imageView.setPadding(padding, padding, padding, paddingBottom);
		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		// Options: CENTER, CENTER_INSIDE, FIT_CENTER
		imageView.setImageBitmap(bitmap);
		return imageView;
	}

	/**
	 * setImageView(ImageView imageView, Drawable drawable)
	 * 
	 * * sets the padding for the imageView used in the ViewPager (the swiped
	 * image)
	 * 
	 * * sets the ScaleType (how the image is centered)
	 * 
	 * * places the drawable in the imageView.
	 * 
	 * Returns the imageView with added borders, ScaleType and image.
	 */

	public ImageView setImageView(ImageView imageView, Drawable drawable) {
		int padding = getResources().getDimensionPixelSize(
				R.dimen.padding_medium);
		int paddingBottom = getResources().getDimensionPixelSize(
				R.dimen.padding_bottom_medium);
		// setPadding(integer left, integer top, integer right, integer bottom)
		imageView.setPadding(padding, padding, padding, paddingBottom);
		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		// Options: CENTER, CENTER_INSIDE, FIT_CENTER
		imageView.setImageDrawable(drawable);
		return imageView;
	}

	/**
	 * setImageViewOnClickListener (ImageView imageView, int position) adds an
	 * onClickListener to show the detailed forecast information to the
	 * corresponding imageView using the position
	 * 
	 * Returns the imageView with onClickListener
	 */

	public ImageView setImageViewOnClickListener(ImageView imageView,
			final int position) {
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showForecastInformation(position);
			}
		});
		return imageView;
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
		// TODO add locations history thingy
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

		// Read the location out of the TextField to use in the Forecaster
		EditText locationField = (EditText) findViewById(R.id.editText1);
		String userLocation = locationField.getText().toString();
		// Remove whitespace in front and at the end of the string
		userLocation = userLocation.trim();

		// check for empty location field and display Toast if so.
		if (userLocation.equals("")) {
			Toast myToast = Toast.makeText(getApplicationContext(),
					R.string.firstTimeStartupMessage, Toast.LENGTH_LONG);
			myToast.setGravity(Gravity.CENTER, 0, 0);
			myToast.show();
		} else {
			handleStartForecast(userLocation);
		}
	}

	// go one page further, used by 1st and 2nd page (not by 3th)
	public void goForward(View v) {
		// Get current Forecast number
		int tabNumber = mViewPager.getCurrentItem();
		if (tabNumber == 0) { // Tab 1 to tab 2
			mViewPager.setCurrentItem(1, true); // True is for smoothScroll
		} else if (tabNumber == 1) { // Tab 2 to tab 3
			mViewPager.setCurrentItem(2, true);
		} else { // In case of an Error
			Log.e("SwitchForecastButton",
					"An error occured while switching Forecast (forward). Check the Selected Forecast");
		}
	}

	// go one page back, used by 2nd and 3th Forecast (not by 1st)
	public void goBack(View v) {
		// Get current Forecast number
		int tabNumber = mViewPager.getCurrentItem();
		if (tabNumber == 1) { // Tab 2 to tab 1
			mViewPager.setCurrentItem(0, true);
		} else if (tabNumber == 2) { // Tab 3 to tab 2
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
		// TODO add locations history thingy
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
				Toast myToast = Toast.makeText(getApplicationContext(),
						R.string.noForecastDetailedInfo, Toast.LENGTH_LONG);
				myToast.setGravity(Gravity.CENTER, 0, 0);
				myToast.show();
			} else {
				String[] dataset = myForecastInfo.dataset.get(position);

				if (dataset != null) {

					// Make the detailed forecast information string
					DetailedForecastInformationManager myDetailedForecastInformationManager = new DetailedForecastInformationManager(
							dataset);
					SharedPreferences sharedPref = getSharedPreferences(
							getString(R.string.TheWear_preference_key),
							Context.MODE_PRIVATE);
					String detailedInformation = myDetailedForecastInformationManager
							.getString(sharedPref, getResources());

					// Make dialog that shows the detailed forecast information
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(detailedInformation).setTitle(
							R.string.forecastInfo_title);
					builder.setPositiveButton(R.string.dialogButtonClose,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked Close button
								}
							});
					// Get the AlertDialog from create()
					AlertDialog dialog = builder.create();
					dialog.show();
				} else {
					Toast myToast = Toast.makeText(getApplicationContext(),
							R.string.noForecastDetailedInfo, Toast.LENGTH_LONG);
					myToast.setGravity(Gravity.CENTER, 0, 0);
					myToast.show();
				}
			}
		} else {
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
				Toast myToast = Toast.makeText(getApplicationContext(),
						R.string.noForecast, Toast.LENGTH_LONG);
				myToast.setGravity(Gravity.CENTER, 0, 0);
				myToast.show();
			} else {
				int tabNumber = mViewPager.getCurrentItem();
				Bitmap currentBitmap = myForecastInfo.mergedImages[tabNumber];
				if (currentBitmap != null) {
					mySocialMediaPickerFragment = new SocialMediaPickerFragment();
					mySocialMediaPickerFragment.show(
							getSupportFragmentManager(), "Preferences");
				} else {
					Toast myToast = Toast.makeText(getApplicationContext(),
							R.string.noForecast, Toast.LENGTH_LONG);
					myToast.setGravity(Gravity.CENTER, 0, 0);
					myToast.show();
				}
			}
		} else {
			Toast myToast = Toast.makeText(getApplicationContext(),
					R.string.noForecast, Toast.LENGTH_LONG);
			myToast.setGravity(Gravity.CENTER, 0, 0);
			myToast.show();
		}
	}

	/**
	 * getGPSLocation(View view) gets the location of the user using GPS based
	 * on wireless networks, and opens the GPSCoordsManager to get the place
	 * name for those coordinates
	 */

	public void getGPSLocation(View view) {
		EditText locationField = (EditText) findViewById(R.id.editText1);
		new GPSCoordsManager(this, locationField).execute();

	}

	/**
	 * showRegionPreferenceInfo(View v) shows a dialog displaying information
	 * about the region preference
	 */

	public void showRegionPreferenceInfo(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.regionPreferenceInformation).setTitle(
				R.string.regionPreferenceInformationTitle);
		builder.setPositiveButton(R.string.dialogButtonClose,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// User clicked Close button
					}
				});
		// Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		dialog.show();
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
		// if (myForecasterObject != null) {
		// ForecastInfo myForecastInfo = null;
		// Log.d("TheWearDebug", "\'Preferences\' clicked");
		// try {
		// myForecastInfo = myForecasterObject.get();
		// } catch (InterruptedException e) {
		// // Auto-generated catch block
		// Log.e("TheWearDebug", "InterruptedException");
		// e.printStackTrace();
		// } catch (ExecutionException e) {
		// // Auto-generated catch block
		// Log.e("TheWearDebug", "ExecutionException4");
		// e.printStackTrace();
		// }
		// ForecastPreferencesFragment myForecastPreferencesFragment = new
		// ForecastPreferencesFragment();
		// myForecastPreferencesFragment.passNecessaryInformation(
		// myImageViews, myForecastInfo, this);
		// myForecastPreferencesFragment.show(getSupportFragmentManager(),
		// "Preferences");
		// } else {
		// // Catch an empty myForecastInfo and show dialog
		// Log.d("TheWearAndroid", "myForecastObject == null");
		// ForecastPreferencesFragment myForecastPreferencesFragment = new
		// ForecastPreferencesFragment();
		// myForecastPreferencesFragment.show(getSupportFragmentManager(),
		// "Preferences");
		// }
		Intent intent = new Intent(this, ForecastPreferenceActivity.class);
		startActivity(intent);
	}

	/**
	 * showSettings() shows the settings menu (the TheWearPreferenceActivity)
	 * when called.
	 */

	public void showSettings() {
		Intent intent = new Intent(this, TheWearPreferenceActivity.class);
		startActivity(intent);
	}

	/**
	 * showAbout() shows the user a dialog containing information of our App.
	 */

	public void showAboutApp() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String aboutText = getString(R.string.about_text) + "\nVersion: "
				+ getString(R.string.testVersion);
		builder.setMessage(aboutText).setTitle(R.string.action_about);
		builder.setPositiveButton(R.string.closeDialog,
				new DialogInterface.OnClickListener() {
					@Override
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
				myForecastTimeStruct, titleTextView, mViewPager,
				imagePagerAdapter, this, imageViewAddedToImagePagerAdapter,
				goForwardButton, goBackButton);
		myForecasterObject.execute(userLocation);
	}

	/**
	 * ImagePagerAdapter contains the methods used to display the ImageViews
	 * that show the forecast.
	 */

	public class ImagePagerAdapter extends PagerAdapter {

		// TODO Get the images centered. This might be solved by adding images
		// with the right dimensions in the corresponding drawable-folders.

		// This holds all the currently displayable views, in order from left to
		// right.
		private ArrayList<View> views = new ArrayList<View>();

		/**
		 * Used by ViewPager. "Object" represents the page; tell the ViewPager
		 * where the page should be displayed, from left-to-right. If the page
		 * no longer exists, return POSITION_NONE.
		 */
		@Override
		public int getItemPosition(Object object) {
			int index = views.indexOf(object);
			if (index == -1)
				return POSITION_NONE;
			else
				return index;
		}

		/**
		 * Used by ViewPager; can be used by app as well. Returns the total
		 * number of pages that the ViewPage can display. This must never be 0.
		 */

		@Override
		public int getCount() {
			// Returns the number of images
			return views.size();
		}

		/** Used by ViewPager. */

		@Override
		public boolean isViewFromObject(View view, Object object) {
			// Identifies whether a page View is associated with a given key
			// object
			return view == object;
		}

		/**
		 * Used by ViewPager. Called when ViewPager needs a page to display; it
		 * is our job to add the page to the container, which is normally the
		 * ViewPager itself. Since all our pages are persistent, we simply
		 * retrieve it from our "views" ArrayList.
		 */

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			View v = views.get(position);
			container.addView(v);
			return v;
		}

		/**
		 * Used by ViewPager. Called when ViewPager no longer needs a page to
		 * display; it is our job to remove the page from the container, which
		 * is normally the ViewPager itself. Since all our pages are persistent,
		 * we do nothing to the contents of our "views" ArrayList.
		 */

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views.get(position));
		}

		/**
		 * Add "view" at "position" to "views". Returns position of new view.
		 * The app should call this to add pages; not used by ViewPager.
		 */
		public int addView(View v, int position) {
			views.add(position, v);
			return position;
		}

		/**
		 * Returns the "view" at "position". The app should call this to
		 * retrieve a view; not used by ViewPager.
		 */
		public View getView(int position) {
			return views.get(position);
		}
	}
}