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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SettingsFragment extends DialogFragment {

	/**
	 * The Java code linked with settings_dialog.xml layout. These two files
	 * together display a dialog in which the user can chose their own preferred
	 * settings for the forecast.
	 * 
	 * The preferred settings are stored in SharedPreferences.
	 * 
	 * Current available settings:
	 * 
	 * time notation setting: 12-hours or 24-hours notation.
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
	 * as °C or °F
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

		builder.setPositiveButton(R.string.positive_button,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						// Show progressBar
						myProgressBar.setIndeterminate(true);
						myProgressBar.setVisibility(0);

						// Setting 2

						// get checked button
						switch (setting2RadioGroup.getCheckedRadioButtonId()) {
						case R.id.radioButton1Setting2:
							temperatureNotationPreference = 0;
							Log.d("BUG", "temperatureNotationPreference = 0");
							break;
						case R.id.radioButton2Setting2:
							temperatureNotationPreference = 1;
							Log.d("BUG", "temperatureNotationPreference = 1");
							break;
						default:
							Log.e("TheWearDebug", "No such RadioButton");
						}

						// Setting 1

						// get checked button
						switch (setting1RadioGroup.getCheckedRadioButtonId()) {
						case R.id.radioButton1Setting1:
							timeNotationPreference = 0;
							Log.d("BUG", "timeNotationPreference = 0");
							break;
						case R.id.radioButton2Setting1:
							timeNotationPreference = 1;
							Log.d("BUG", "timeNotationPreference = 1");
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
						editor.commit();
						Log.d("TheWearDebug", "Saved the changed Preferences");

						// Change the time title
						int[] forecastTimeHour = forecastTimeStruct.forecastTimeHour;
						int[] forecastTimeMinute = forecastTimeStruct.forecastTimeMinute;
						String[] titleString = TimeHandler
								.constructTitleStrings(forecastTimeHour,
										forecastTimeMinute,
										timeNotationPreference);
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

					// Setting 1
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

					Log.d("TheWearDebug", "Settings preferences set to default");
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
	 * ViewPager mViewPager
	 */

	public void passNecessaryInformation(ForecastTimeStruct forecastTimeStruct,
			TextView titleTextView, ViewPager mViewPager) {
		this.forecastTimeStruct = forecastTimeStruct;
		this.titleTextView = titleTextView;
		this.mViewPager = mViewPager;
	}
}