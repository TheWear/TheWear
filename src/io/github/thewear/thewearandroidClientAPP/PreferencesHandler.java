package io.github.thewear.thewearandroidClientAPP;

import src.gui.thewearandroid.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferencesHandler {

	/**
	 * PreferencesHandler is the class that contains the method that reads from
	 * the SharedPreferences.
	 */

	// TODO: update JavaDoc

	public static String coldLimitString;
	public static String umbrellaLimitString;
	public static String warmLimitString;

	public static int warmLimit;
	public static int umbrellaLimit;
	public static int coldLimit;
	public static int[] array;

	public static int[] getPreferences(Context context) {
		// Get default Preference Values
		int defaultPreference1Value = Integer.parseInt(context
				.getString(R.string.preference1_defaultValue));
		int defaultPreference2Value = Integer.parseInt(context
				.getString(R.string.preference2_defaultValue));
		int defaultPreference3Value = Integer.parseInt(context
				.getString(R.string.preference3_defaultValue));
		Log.d("TheWearDebug", "Got default Vales");

		// Read the preference values from Shared Preferences
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.TheWear_preference_key),
				Context.MODE_PRIVATE);
		int coldLimit = sharedPref.getInt(
				context.getString(R.string.forecast_preference1),
				defaultPreference1Value);
		int umbrellaLimit = sharedPref.getInt(
				context.getString(R.string.forecast_preference2),
				defaultPreference2Value);
		int warmLimit = sharedPref.getInt(
				context.getString(R.string.forecast_preference3),
				defaultPreference3Value);

		array = new int[3];
		array[0] = coldLimit;
		array[1] = umbrellaLimit;
		array[2] = warmLimit;
		return array;

	}
}
