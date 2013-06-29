package clientAPP;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class PreferenceConvertor {

	/**
	 * This class Adjusts the preference range for use with a SeekBar: It makes
	 * it possible to use other minimum values than 0.
	 * 
	 * initiatePreferenceConvertor(Context context, String preference) has to be
	 * called to calculate the offset between the real preference value and the
	 * preference value used by the SeekBar. Input for these methods is the
	 * Context 'context' to be able to retrieve the resources; and String
	 * 'preferenceName' which is the preference name linked to the resources.
	 * Minimum and maximum values have to be stored in "res/values/integers.xml"
	 * with a name in the following format: [preferenceName]_min and
	 * [preferenceName]_max
	 * 
	 * NormalToAdjusted(int normal) converts the real preference value to the
	 * preference value used by the SeekBar, and returns the adjusted preference
	 * value
	 * 
	 * AdjustedToNormal(int adjusted) converts the preference value used by the
	 * SeekBar to the real preference value, and returns the real preference
	 * value
	 */

	private int adjustment;

	public void initiatePreferenceConvertor(Context context, String preference,
			int temperatureNotation) {
		// Initiate the PreferenceConvertor: calculate the adjustment.
		Resources res = context.getResources();

		// Construct resources Path to get the resources
		String maxPreference = preference + "_max";
		int maxPreferenceIdentifier = res.getIdentifier(maxPreference,
				"integer", context.getPackageName());
		String minPreference = preference + "_min";
		int minPreferenceIdentifier = res.getIdentifier(minPreference,
				"integer", context.getPackageName());
		// If getIdentifier can't find an integer with that name, it returns the
		// non-existing identifier '0'
		if ((maxPreferenceIdentifier == 0) || (minPreferenceIdentifier == 0)) {
			Log.e("TheWearDebug",
					"Could not retrieve the integer resources to be able to do conversions.");
		} else {
			int prefMax = -1;
			int pref = -1;
			switch (temperatureNotation) {
			case -1: // No Temperature, so we don't want to change anything.
				prefMax = res.getInteger(maxPreferenceIdentifier);
				pref = prefMax - (res.getInteger(minPreferenceIdentifier));
				break;
			case 0: // °C, no conversion needed
				prefMax = res.getInteger(maxPreferenceIdentifier);
				pref = prefMax - (res.getInteger(minPreferenceIdentifier));
				break;
			case 1: // °F, conversion needed.
				prefMax = SettingsConvertor.celsiusToFahrenheit(res.getInteger(maxPreferenceIdentifier));
				pref = prefMax - SettingsConvertor.celsiusToFahrenheit((res.getInteger(minPreferenceIdentifier)));
				break;
			default:
				Log.e("TheWearDebug", "No such temperature notation");
			}
			adjustment = prefMax - pref;
		}
	}

	public int NormalToAdjusted(int normal) {
		int adjusted = normal - adjustment;
		return adjusted;
	}

	public int AdjustedToNormal(int adjusted) {
		int normal = adjusted + adjustment;
		return normal;
	}
}
