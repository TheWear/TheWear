package io.github.thewear.thewearandroidClientAPP;

public class PreferenceConvertor {

	/**
	 * This class Adjusts the preference range for use with a SeekBar: It makes
	 * it possible to use other minimum values than 0.
	 * 
	 * Constructor PreferenceConvertor(int unitNotation, int notation) sets the
	 * notation and notationCorrection to be used by this class.
	 * 
	 * initiatePreferenceConvertor(int miniumValue, int MaximumValue) has to be
	 * called to calculate the offset between the real preference value and the
	 * preference value used by the SeekBar.
	 * 
	 * The two units currently available are Temperature (unitNotation = 0) and
	 * WindSpeed (unitNotation = 1). If you don't want to use any corrections
	 * for units, use 'unitNotation = 0, notation = 0'
	 */

	private int adjustment;
	private int notationCorrection = 0;
	public int prefMax = -1;
	public int prefMin = -1;
	private int notation;

	/**
	 * PreferenceConvertor(int unitNotation, int notation) sets the notation and
	 * notationCorrection to be used by this class. the notationCorrection is
	 * for use with the Temperature (unitNotation = 0) and WindSpeed
	 * (unitNotation = 1) notation.
	 * 
	 * If you don't want to use any corrections for units, use 'unitNotation =
	 * 0, notation = 0'
	 */

	public PreferenceConvertor(int unitNotation, int notation) {
		if (unitNotation == 0) {
			notationCorrection = 0; // Temperature
		} else {
			notationCorrection = 2; // WindSpeed
		}
		this.notation = notation;
	}

	/**
	 * initiatePreferenceConvertor(int miniumValue, int MaximumValue) calculates
	 * the adjustment needed to use 'NormalToAdjusted(int normal)' and
	 * AdjustedToNormal(int adjusted). to use this, the constructor
	 * PreferenceConvertor(int unitNotation, int notation) has to be used.
	 * 
	 * initiatePreferenceConvertor(int miniumValue, int MaximumValue) also sets
	 * the for units and notation adjusted prefMax and prefMin public variables.
	 */

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
			throw new IllegalArgumentException(
					"PreferenceConvertor: error - No such preference notation");
		}
		adjustment = prefMax - pref;
	}

	/**
	 * NormalToAdjusted(int normal) converts the real preference value to the
	 * preference value used by the SeekBar, and returns the adjusted preference
	 * value. The constructor PreferenceConvertor(int unitNotation, int
	 * notation) and method initiatePreferenceConvertor(int miniumValue, int
	 * MaximumValue) have to be used before this method to work properly.
	 */

	public int NormalToAdjusted(int normal) {
		int adjusted = normal - adjustment;
		return adjusted;
	}

	/**
	 * 
	 * NormalToAdjusted(int normal) converts the real preference value to the
	 * preference value used by the SeekBar, and returns the adjusted preference
	 * value. The constructor PreferenceConvertor(int unitNotation, int
	 * notation) and method initiatePreferenceConvertor(int miniumValue, int
	 * MaximumValue) have to be used before this method to work properly.
	 */

	public int AdjustedToNormal(int adjusted) {
		int normal = adjusted + adjustment;
		return normal;
	}

	/**
	 * convertValueForDisplaying(int value) converts 'value' to 'valueToDisplay'
	 * (returns as int). Conversion is only possible for temperature and
	 * windspeed. the constructor PreferenceConvertor(int unitNotation, int
	 * notation) and method initiatePreferenceConvertor(int miniumValue, int
	 * MaximumValue) have to be used before this method to work properly.
	 */

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
			throw new IllegalArgumentException(
					"PreferenceConvertor: error - No such preference notation");
		}
		return valueToDisplay;
	}

	/**
	 * convertValueForPersisting(int value) converts 'value' to 'valueToStore'
	 * (returns as int). Conversion is only possible for temperature and
	 * windspeed. the constructor PreferenceConvertor(int unitNotation, int
	 * notation) and method initiatePreferenceConvertor(int miniumValue, int
	 * MaximumValue) have to be used before this method to work properly.
	 */

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
			throw new IllegalArgumentException(
					"PreferenceConvertor: error - No such preference notation");
		}
		return valueToStore;
	}
}
