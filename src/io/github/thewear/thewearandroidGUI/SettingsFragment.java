package io.github.thewear.thewearandroidGUI;

import io.github.thewear.thewearandroidClientAPP.ForecastTimeStruct;
import io.github.thewear.thewearandroidClientAPP.TimeHandler;
import src.gui.thewearandroid.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsFragment extends DialogFragment {

	/**
	 * The Java code linked with settings_dialog.xml layout. These two files
	 * together display a dialog in which the user can chose their own preferred
	 * settings for the forecast.
	 * 
	 * The preferred settings are stored in SharedPreferences.
	 * 
	 * Current Available Settings: Time notation; Temperature notation; Wind
	 * speed notation, Automatic region detection, Region preference.
	 */

	private ProgressBar myProgressBar;
	private SharedPreferences sharedPref;
	private int timeNotationPreference;
	private int defaultTimeNotation;
	private RadioGroup setting1RadioGroup;
	private ForecastTimeStruct forecastTimeStruct;
	private TextView titleTextView;
	private ViewPager mViewPager;
	private int defaultTemperatureNotation;
	private int temperatureNotationPreference;
	private RadioGroup setting2RadioGroup;
	private int defaultWindSpeedNotation;
	private int windSpeedNotationPreference;
	private RadioGroup setting3RadioGroup;
	private String defaultRegion;
	private String regionPreference;
	private String[] ccTLDCodes;
	private Spinner spinner;
	private Context applicationContext;
	private boolean autoRegionDetectionPreference;
	private boolean defaultAutoRegionDetection;
	private CompoundButton toggleButton;

	/**
	 * onCreateDialog executes all the code we want to have executed when the
	 * dialog is created:
	 * 
	 * * Set the dialog layout and content
	 * 
	 * * Show the user the preferred setting on the RadioButons when the dialog
	 * is created, or if there isn't a preferred setting yet, show the default
	 * one
	 * 
	 * * Set a positive button (OK button) for saving the settings preferences,
	 * to close the window and to change the forecast time titles according to
	 * the new preferences settings. A progressBar is shown to notify the user
	 * that the titles are changing.
	 * 
	 * * Set a negative button (Cancel button) to cancel a change of preferences
	 * and to close the window
	 * 
	 * Current Settings:
	 * 
	 * Time notation: the user can choose between a time notation as 12-hours or
	 * 24-hours notation.
	 * 
	 * Temperature notation: the user can choose between a temperature notation
	 * as °C or °F.
	 * 
	 * Wind speed notation: the user can choose between a wind speed notation as
	 * m/s, Beaufort or Knots.
	 * 
	 * automatic Region Detection: The user can choose to use automatic Region
	 * Detection
	 * 
	 * Region Preference: The user can choose their preferred region to use as
	 * forecast reference point.
	 */

	@Override
	public Dialog onCreateDialog(Bundle SavedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// get the LayoutInflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because the dialog is going in the
		// dialog layout
		View dialogView = inflater.inflate(R.layout.settings_dialog, null);
		builder.setView(dialogView);
		Log.d("TheWearDebug", "Settings Dialog Created");

		// Set dialog Title
		builder.setTitle(R.string.settingsDialogTitle);

		// Get default preference
		final Resources res = getResources();
		defaultTimeNotation = res.getInteger(R.integer.defaultTimeNotation);
		defaultTemperatureNotation = res
				.getInteger(R.integer.defaultTemperatureNotation);
		defaultWindSpeedNotation = res
				.getInteger(R.integer.defaultWindSpeedNotation);

		// Get preference
		sharedPref = getActivity().getSharedPreferences(
				getString(R.string.TheWear_preference_key),
				Context.MODE_PRIVATE);
		timeNotationPreference = sharedPref.getInt(
				getString(R.string.time_notation_preference),
				defaultTimeNotation);
		temperatureNotationPreference = sharedPref.getInt(
				getString(R.string.temperature_notation_preference),
				defaultTimeNotation);
		windSpeedNotationPreference = sharedPref.getInt(
				getString(R.string.windspeed_notation_preference),
				defaultWindSpeedNotation);

		// Setting 1

		setting1RadioGroup = (RadioGroup) dialogView
				.findViewById(R.id.setting1RadioGroup);

		// check the preferenced radio button
		switch (timeNotationPreference) {
		case 0:
			setting1RadioGroup.check(R.id.radioButton1Setting1);
			break;
		case 1:
			setting1RadioGroup.check(R.id.radioButton2Setting1);
			break;
		default: // ERROR
			Log.e("TheWearDebug", "No such time notation preference");
		}

		// Setting 2

		setting2RadioGroup = (RadioGroup) dialogView
				.findViewById(R.id.setting2RadioGroup);

		// check the preferenced radio button
		switch (temperatureNotationPreference) {
		case 0:
			setting2RadioGroup.check(R.id.radioButton1Setting2);
			break;
		case 1:
			setting2RadioGroup.check(R.id.radioButton2Setting2);
			break;
		default: // ERROR
			Log.e("TheWearDebug", "No such time notation preference");
		}

		// Setting 3

		setting3RadioGroup = (RadioGroup) dialogView
				.findViewById(R.id.setting3RadioGroup);

		// check the preferenced radio button
		switch (windSpeedNotationPreference) {
		case 0:
			setting3RadioGroup.check(R.id.radioButton1Setting3);
			break;
		case 1:
			setting3RadioGroup.check(R.id.radioButton2Setting3);
			break;
		case 2:
			setting3RadioGroup.check(R.id.radioButton3Setting3);
			break;
		default: // ERROR
			Log.e("TheWearDebug", "No such time notation preference");
		}

		// Region Preference

		// Auto Region Detection

		// Get default autoRegionDetection
		defaultAutoRegionDetection = res
				.getBoolean(R.bool.defaultAutoRegionDetection);
		// Read the autoRegionDetection value from SharedPreferences
		autoRegionDetectionPreference = sharedPref.getBoolean(
				res.getString(R.string.autoRegionDetection_preference),
				defaultAutoRegionDetection);

		// Set CompoundButton state according to autoRegionDetection preference
		// CompoundButton can be a Switch or a ToggleButton
		toggleButton = (CompoundButton) dialogView
				.findViewById(R.id.autoRegionToggle);
		toggleButton.setChecked(autoRegionDetectionPreference);

		// Set OnClickListener for autoRegionDetection
		toggleButton
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							autoRegionDetectionPreference = true;
						} else {
							autoRegionDetectionPreference = false;
						}
					}
				});

		// Region List

		// Get default Region Preference
		defaultRegion = getString(R.string.default_region);

		// Read the region preference from Shared Preferences
		regionPreference = sharedPref.getString(
				getString(R.string.region_preference), defaultRegion);

		// Set Spinner
		spinner = (Spinner) dialogView.findViewById(R.id.spinner1);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				applicationContext, R.array.ccTLDCountries,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		// set the preferred item in the spinner
		ccTLDCodes = res.getStringArray(R.array.ccTLDCodes);
		int preferencePosition = -1;
		for (int i = 0; i < ccTLDCodes.length; i++) {
			int equalLocationPreference = ccTLDCodes[i].trim().compareTo(
					regionPreference.trim());
			if (equalLocationPreference == 0) {
				preferencePosition = i;
				break;
			}
		}
		if (preferencePosition == -1) {
			Log.e("TheWearDebug", "No PreferencePosition");
		} else {
			spinner.setSelection(preferencePosition);
		}

		// Set listener for the Spinner
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				regionPreference = ccTLDCodes[parent.getSelectedItemPosition()];
				Log.d("TheWearDebug", "regionPreference clicked: "
						+ regionPreference);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Nothing Happens
			}
		});

		builder.setPositiveButton(R.string.positive_button,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						// Show progressBar
						myProgressBar.setIndeterminate(true);
						myProgressBar.setVisibility(0);

						// Setting 1

						// get checked button
						switch (setting1RadioGroup.getCheckedRadioButtonId()) {
						case R.id.radioButton1Setting1:
							timeNotationPreference = 0;
							break;
						case R.id.radioButton2Setting1:
							timeNotationPreference = 1;
							break;
						default:
							Log.e("TheWearDebug", "No such RadioButton");
						}

						// Setting 2

						// get checked button
						switch (setting2RadioGroup.getCheckedRadioButtonId()) {
						case R.id.radioButton1Setting2:
							temperatureNotationPreference = 0;
							break;
						case R.id.radioButton2Setting2:
							temperatureNotationPreference = 1;
							break;
						default:
							Log.e("TheWearDebug", "No such RadioButton");
						}

						// Setting 3

						// get checked button
						switch (setting3RadioGroup.getCheckedRadioButtonId()) {
						case R.id.radioButton1Setting3:
							windSpeedNotationPreference = 0;
							break;
						case R.id.radioButton2Setting3:
							windSpeedNotationPreference = 1;
							break;
						case R.id.radioButton3Setting3:
							windSpeedNotationPreference = 2;
							break;
						default:
							Log.e("TheWearDebug", "No such RadioButton");
						}

						// Save new settings
						SharedPreferences.Editor editor = sharedPref.edit();
						editor.putInt(
								getString(R.string.time_notation_preference),
								timeNotationPreference);
						editor.putInt(
								getString(R.string.temperature_notation_preference),
								temperatureNotationPreference);
						editor.putInt(
								getString(R.string.windspeed_notation_preference),
								windSpeedNotationPreference);
						editor.putString(getString(R.string.region_preference),
								regionPreference);
						editor.putBoolean(
								getString(R.string.autoRegionDetection_preference),
								autoRegionDetectionPreference);
						editor.commit();
						Log.d("TheWearDebug", "Saved the changed Preferences");

						// Change the time title
						int[] forecastTimeHour = forecastTimeStruct.forecastTimeHour;
						int[] forecastTimeMinute = forecastTimeStruct.forecastTimeMinute;
						String[] titleString = TimeHandler
								.constructTitleStrings(forecastTimeHour,
										forecastTimeMinute,
										timeNotationPreference, res);
						forecastTimeStruct.setForecastTimeString(titleString);
						// Set the new time title
						int tab = mViewPager.getCurrentItem();
						titleTextView
								.setText(forecastTimeStruct.forecastTimeString[tab]);
						Log.d("TheWearDebug", "Changed time title");
					}
				})
				.setNeutralButton(R.string.neutral_button,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int id) {
								// Negative button listener is overwritten in
								// onStart
							}
						})
				.setNegativeButton(R.string.negative_button,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int id) {
								// Cancel and close dialog
							}
						});

		// Set the ProgressBar invisible
		myProgressBar = (ProgressBar) dialogView
				.findViewById(R.id.progressBar1);
		myProgressBar.setVisibility(4);

		return builder.create();
	}

	/**
	 * onStart() is used to create a custom onCLickListener for the 'default'
	 * button of our App: it does not close when clicked, and sets the settings
	 * back to their default value.
	 */

	@Override
	public void onStart() {
		super.onStart();
		AlertDialog d = (AlertDialog) getDialog();
		if (d != null) {
			Log.d("TheWearDebug", "Custom onClickDialog set");
			Button positiveButton = (Button) d.getButton(Dialog.BUTTON_NEUTRAL);
			positiveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Set preference values to default

					// Setting 1
					timeNotationPreference = defaultTimeNotation;
					switch (defaultTimeNotation) {
					case 0:
						setting1RadioGroup.check(R.id.radioButton1Setting1);
						break;
					case 1:
						setting1RadioGroup.check(R.id.radioButton2Setting1);
						break;
					default: // ERROR
						Log.e("TheWearDebug",
								"No such time notation preference");
					}

					// Setting 2
					temperatureNotationPreference = defaultTemperatureNotation;
					switch (defaultTemperatureNotation) {
					case 0:
						setting2RadioGroup.check(R.id.radioButton1Setting2);
						break;
					case 1:
						setting2RadioGroup.check(R.id.radioButton2Setting2);
						break;
					default: // ERROR
						Log.e("TheWearDebug",
								"No such time notation preference");
					}

					// Setting 3
					windSpeedNotationPreference = defaultWindSpeedNotation;
					switch (windSpeedNotationPreference) {
					case 0:
						setting3RadioGroup.check(R.id.radioButton1Setting3);
						break;
					case 1:
						setting3RadioGroup.check(R.id.radioButton2Setting3);
						break;
					case 2:
						setting3RadioGroup.check(R.id.radioButton3Setting3);
						break;
					default: // ERROR
						Log.e("TheWearDebug",
								"No such time notation preference");
					}
					Log.d("TheWearDebug", "Settings preferences set to default");

					// Set automatic region detection to default
					autoRegionDetectionPreference = defaultAutoRegionDetection;
					toggleButton.setChecked(autoRegionDetectionPreference);

					// Set region preference to default
					regionPreference = defaultRegion;
					// set the preferred region in the spinner
					Resources res = getResources();
					ccTLDCodes = res.getStringArray(R.array.ccTLDCodes);
					int preferencePosition = -1;
					for (int i = 0; i < ccTLDCodes.length; i++) {
						int equalLocationPreference = ccTLDCodes[i].trim()
								.compareTo(regionPreference.trim());
						if (equalLocationPreference == 0) {
							preferencePosition = i;
							break;
						}
					}
					if (preferencePosition == -1) {
						Log.e("TheWearDebug", "No PreferencePosition");
					} else {
						spinner.setSelection(preferencePosition);
					}

					Log.d("TheWearDebug", "Region preference set to default");
				}
			});
		} else {
			Log.e("TheWearDebug", "No Dialog yet");
		}
	}

	/**
	 * passNecessaryInformation() is used to set the necessary parameters.
	 * 
	 * Input: ForecastTimeStruct forecastTimeStruct, TextView titleTextView,
	 * ViewPager mViewPager, Context applicationContext
	 */

	public void passNecessaryInformation(ForecastTimeStruct forecastTimeStruct,
			TextView titleTextView, ViewPager mViewPager,
			Context applicationContext) {
		this.forecastTimeStruct = forecastTimeStruct;
		this.titleTextView = titleTextView;
		this.mViewPager = mViewPager;
		this.applicationContext = applicationContext;
	}
}