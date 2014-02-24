package io.github.thewear.thewearandroidClientAPP;

import io.github.thewear.thewearandroid.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

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
		Resources res = context.getResources();
		int defaultPreference1Value = res
				.getInteger(R.integer.preference1_defaultValue);
		int defaultPreference2Value = res
				.getInteger(R.integer.preference2_defaultValue);
		int defaultPreference3Value = res
				.getInteger(R.integer.preference3_defaultValue);

		// Read the preference values from Shared Preferences
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.TheWear_preference_key),
				Context.MODE_PRIVATE);
		int coldLimit = sharedPref.getInt(
				context.getString(R.string.forecast_preference1_key),
				defaultPreference1Value);
		int umbrellaLimit = sharedPref.getInt(
				context.getString(R.string.forecast_preference2_key),
				defaultPreference2Value);
		int warmLimit = sharedPref.getInt(
				context.getString(R.string.forecast_preference3_key),
				defaultPreference3Value);

		array = new int[3];
		array[0] = coldLimit;
		array[1] = umbrellaLimit;
		array[2] = warmLimit;
		return array;

	}
}
