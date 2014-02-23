package io.github.thewear.thewearandroidGUI;

import io.github.thewear.guiSupport.IntegerListPreference;
import io.github.thewear.guiSupport.PreferencesResetObserver;
import io.github.thewear.guiSupport.ResetPreferences;
import io.github.thewear.thewearandroid.R;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;

public class TheWearPreferenceActivity extends PreferenceActivity {

	/**
	 * TheWearPreferenceActivity is the activity containing the Java code for
	 * the user preferences. it is connected to the R.xml.preferences;
	 * containing the time notation preference, the temperature notation
	 * preference, the wind speed notation preference, the automatic region
	 * detection preference and the region preference.
	 */

	/**
	 * onCreate is the method used to create the PreferenceActivity and sets the
	 * observable/observer. It contains some methods deprecated in API level 11
	 */

	@SuppressWarnings("deprecation")
	// addPreferencesFromResource and getPreferenceManager are deprecated in API
	// level 11; but we are using API level 10
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(
				getString(R.string.TheWear_preference_key));
		getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
		addPreferencesFromResource(R.xml.preferences);
		setUpInActionBar();

		// set observable/observer to be able to visually reset the preferences
		final ResetPreferences resetPreferencesDialog = (ResetPreferences) getPreferenceManager()
				.findPreference(getString(R.string.resetPreferencesKey));
		final PreferencesResetHandler responseHandler = new PreferencesResetHandler();
		resetPreferencesDialog.addObserver(responseHandler);
	}

	/**
	 * setUpInActionBar() is the method used to set the 'Up' functionality for
	 * the PreferenceActivity. It targets API level 11+
	 */

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	// The new API is only used if the device has API level 11 or higher
	public void setUpInActionBar() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	/**
	 * PreferencesResetHandler implements the Observer to be able to visually
	 * reset the preferences
	 */

	public class PreferencesResetHandler implements PreferencesResetObserver {

		/**
		 * update(ResetPreferences resetPreferences, Object data) is the method
		 * used to visually reset the preferences when the preferences are reset
		 * by the ResetPreferences class.
		 * 
		 * It visually resets the timeNotationPreference,
		 * temperatureNotationPreference, windSpeedNotation and
		 * regionPreference, and calls the method that resets the
		 * autoRegionDetectionPreference.
		 * 
		 * This method contains deprecated methods.
		 */

		@SuppressWarnings("deprecation")
		@Override
		public void update(ResetPreferences resetPreferences, Object data) {
			// Reset the values of the preference widgets
			Resources res = getResources();
			final IntegerListPreference timeNotationPreference = (IntegerListPreference) getPreferenceManager()
					.findPreference(
							getString(R.string.time_notation_preference_key));
			int defaultTimeNotation = res
					.getInteger(R.integer.defaultTimeNotation);
			timeNotationPreference.setValue(defaultTimeNotation);
			final IntegerListPreference temperatureNotationPreference = (IntegerListPreference) getPreferenceManager()
					.findPreference(
							getString(R.string.temperature_notation_preference_key));
			int defaultTemperatureNotation = res
					.getInteger(R.integer.defaultTemperatureNotation);
			temperatureNotationPreference.setValue(defaultTemperatureNotation);
			final IntegerListPreference windSpeedNotation = (IntegerListPreference) getPreferenceManager()
					.findPreference(
							getString(R.string.windspeed_notation_preference_key));
			int defaultWindSpeedNotation = res
					.getInteger(R.integer.defaultWindSpeedNotation);
			windSpeedNotation.setValue(defaultWindSpeedNotation);
			resetAutoRegionDetection(res);
			final ListPreference regionPreference = (ListPreference) getPreferenceManager()
					.findPreference(getString(R.string.region_preference_key));
			String defaultRegionPreference = getString(R.string.default_region);
			regionPreference.setValue(defaultRegionPreference);
		}

		/**
		 * resetAutoRegionDetection(Resources res) is called to visually reset
		 * the autoRegionDetectionPreference.
		 * 
		 * It resets it for a SwitchPreference (API level => 14) or
		 * CheckBoxPreference (API level > 14) depending on the device version
		 * 
		 * this method contains deprecated methods and has a target API level 14
		 * or higher
		 */

		// The new API is only used if the device has API level 14 or higher
		@SuppressWarnings("deprecation")
		@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		private void resetAutoRegionDetection(Resources res) {
			boolean defaultWindSpeedNotation = res
					.getBoolean(R.integer.defaultTimeNotation);
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2) {
				final SwitchPreference autoRegionDetectionPreference = (SwitchPreference) getPreferenceManager()
						.findPreference(
								getString(R.string.autoRegionDetection_preference_key));
				autoRegionDetectionPreference
						.setChecked(defaultWindSpeedNotation);
			} else {
				final CheckBoxPreference autoRegionDetectionPreference = (CheckBoxPreference) getPreferenceManager()
						.findPreference(
								getString(R.string.autoRegionDetection_preference_key));
				autoRegionDetectionPreference
						.setChecked(defaultWindSpeedNotation);
			}
		}
	}

	// TODO: Move Forecast Preferences here if we think this won't be changed a
	// lot
}