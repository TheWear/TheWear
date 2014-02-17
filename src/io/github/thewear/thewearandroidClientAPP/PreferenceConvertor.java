package io.github.thewear.thewearandroidClientAPP;

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
	private int notationCorrection = 0;
	public int prefMax = -1;
	public int prefMin = -1;
	private int notation;

	/**
	 * General constructor
	 */

	public PreferenceConvertor() {

	}

	/**
	 * Constructor for use with the Temperature (=0) and WindSpeed (=1) notation
	 */

	public PreferenceConvertor(int unitNotation, int notation) {
		if (unitNotation == 0) {
			notationCorrection = 0; // Temperature
		} else {
			notationCorrection = 2; // WindSpeed
		}
		this.notation = notation;
	}

	public void initiatePreferenceConvertor(int miniumValue, int MaximumValue) {
		int pref = -1;
		switch (notation + notationCorrection) {
		case 0: // °C, no conversion needed
			prefMax = MaximumValue;
			prefMin = miniumValue;
			pref = prefMax - prefMin;
			break;
		case 1: // °F, conversion needed.
			prefMax = SettingsConvertor.celsiusToFahrenheit(MaximumValue);
			prefMin = SettingsConvertor.celsiusToFahrenheit(miniumValue);
			pref = prefMax - prefMin;
			break;
		case 2: // m/s, no conversion needed
			prefMax = MaximumValue;
			prefMin = miniumValue;
			pref = prefMax - prefMin;
			break;
		case 3: // Beaufort, conversion needed
			prefMax = SettingsConvertor.metersToBeaufort(MaximumValue);
			prefMin = SettingsConvertor.metersToBeaufort(miniumValue);
			pref = prefMax - prefMin;
			break;
		case 4: // Knots, conversion needed
			prefMax = Math.round(SettingsConvertor.metersToKnots(MaximumValue));
			prefMin = Math.round(SettingsConvertor.metersToKnots(miniumValue));
			pref = prefMax - prefMin;
			break;
		default:
			Log.e("TheWearDebug", "No such preference notation");
		}
		adjustment = prefMax - pref;
	}

	public int NormalToAdjusted(int normal) {
		int adjusted = normal - adjustment;
		return adjusted;
	}

	public int AdjustedToNormal(int adjusted) {
		int normal = adjusted + adjustment;
		return normal;
	}

	public int convertValueForDisplaying(int value) {
		int valueToDisplay = -1;
		switch (notation + notationCorrection) {
		case 0: // °C, no conversion needed
			valueToDisplay = value;
			break;
		case 1: // °F, conversion needed.
			valueToDisplay = SettingsConvertor.celsiusToFahrenheit(value);
			break;
		case 2: // m/s, no conversion needed
			valueToDisplay = value;
			break;
		case 3: // Beaufort, conversion needed
			valueToDisplay = SettingsConvertor.metersToBeaufort(value);
			break;
		case 4: // Knots, conversion needed
			valueToDisplay = Math.round(SettingsConvertor.metersToKnots(value));
			break;
		default:
			Log.e("TheWearDebug", "No such preference notation");
		}
		return valueToDisplay;
	}

	public int convertValueForPersisting(int value) {
		int valueToStore = -1;
		switch (notation + notationCorrection) {
		case 0: // °C, no conversion needed
			valueToStore = value;
			break;
		case 1: // °F, conversion needed.
			valueToStore = Math.round(SettingsConvertor
					.fahrenheitToCelsius(value));
			break;
		case 2: // m/s, no conversion needed
			valueToStore = value;
			break;
		case 3: // Beaufort, conversion needed
			valueToStore = Math
					.round(SettingsConvertor.beaufortToMeters(value));
			break;
		case 4: // Knots, conversion needed
			valueToStore = Math.round(SettingsConvertor.knotsToMeters(value));
			break;
		default:
			Log.e("TheWearDebug", "No such preference notation");
		}
		return valueToStore;
	}
}
