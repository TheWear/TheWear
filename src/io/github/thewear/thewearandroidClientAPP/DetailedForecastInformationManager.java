package io.github.thewear.thewearandroidClientAPP;

import io.github.thewear.thewearandroid.R;
import android.content.SharedPreferences;
import android.content.res.Resources;

public class DetailedForecastInformationManager {

	/**
	 * MouseOverManager: Constructs a weather string from the parameter array;
	 * 
	 * A MouseOverManager instance is initiated with a dataset String[]
	 * parameter;
	 * 
	 * When there is no snow, Snowdepth is not included in the MouseOverString;
	 * Cloudcover is converted to Sunshine [%] and Temperature to �Celsius;
	 */

	private String[] dataset;
	private double attr1; // Temperature
	private String attr2; // Precipitation
	private String attr3; // Cloud Cover
	private double attr4; // Wind Speed
	private String attr5; // Snow Cover
	private double attr6; // Minimum Temperature
	private double attr7; // Maximum Temperature
	private Double kelvin = 273.2;

	public DetailedForecastInformationManager(String[] d) {
		dataset = d;
		attr1 = Math.round((Double.parseDouble(dataset[1]) - kelvin) * 100) / 100.0;
		attr2 = dataset[2];
		attr3 = String.valueOf(Math.round(Double.parseDouble(dataset[4])));
		attr4 = Double.parseDouble(dataset[6]);
		if (Double.parseDouble(dataset[7]) > 0) {
			attr5 = dataset[7];
		} else {
			attr5 = "";
		}
		attr6 = Math.round((Double.parseDouble(dataset[8]) - kelvin) * 100) / 100.0;
		attr7 = Math.round((Double.parseDouble(dataset[9]) - kelvin) * 100) / 100.0;
	}

	/**
	 * getString() constructs and returns the string shown on the detailed
	 * forecast.
	 * 
	 * The temperatures are shown in �C or �F according to the user preference
	 * 
	 * The Wind Speed is shown in m/s, Beaufort or Knots according to the user
	 * preference.
	 * 
	 * Input: SharedPreferences sharedPref, Resources res
	 */

	public String getString(SharedPreferences sharedPref, Resources res) {
		String[] mouseOverStringStrings = res
				.getStringArray(R.array.mouseOverStringStrings);
		String mouseOverStringFormat = mouseOverStringStrings[0]
				+ ": (%s): %s%n" // TMP
				+ mouseOverStringStrings[1] + ": (%s):  %s%n" // TMAX
				+ mouseOverStringStrings[2] + ": (%s):  %s%n" // TMIN
				+ mouseOverStringStrings[3] + ": %s%n" // APCP
				+ mouseOverStringStrings[4] + ": %s%%%n" // TCDC
				+ mouseOverStringStrings[5] + ": (%s): %s" // sqrt(UGRD^2+VGRD^2)
				+ "%s"; // WEASD
		String attr5WithText = "\n" + mouseOverStringStrings[6] + ": " + attr5; // WEASD
		int defaultTemperatureNotation = res
				.getInteger(R.integer.defaultTemperatureNotation);
		int temperatureNotation = sharedPref.getInt(
				res.getString(R.string.temperature_notation_preference_key),
				defaultTemperatureNotation);
		int defaultWindSpeedNotation = res
				.getInteger(R.integer.defaultWindSpeedNotation);
		int windSpeedNotation = sharedPref.getInt(
				res.getString(R.string.windspeed_notation_preference_key),
				defaultWindSpeedNotation);
		String tempUnit = null;
		String convertedAttr1 = null;
		String convertedAttr6 = null;
		String convertedAttr7 = null;
		switch (temperatureNotation) {
		case 0:
			tempUnit = res.getString(R.string.celcius);
			convertedAttr1 = String.valueOf(attr1);
			convertedAttr6 = String.valueOf(attr6);
			convertedAttr7 = String.valueOf(attr7);
			break;
		case 1:
			tempUnit = res.getString(R.string.fahrenheit);
			convertedAttr1 = String.valueOf((double) SettingsConvertor
					.celsiusToFahrenheit(attr1));
			convertedAttr6 = String.valueOf((double) SettingsConvertor
					.celsiusToFahrenheit(attr6));
			convertedAttr7 = String.valueOf((double) SettingsConvertor
					.celsiusToFahrenheit(attr7));
			break;
		default:
			throw new IllegalArgumentException(
					"DetailedForecastInformationManager: error - No such temperatureNotation");
		}

		String windSpeedUnit = null;
		String convertedAttr4 = null;
		switch (windSpeedNotation) {
		case 0:
			windSpeedUnit = res.getString(R.string.metersPerSecond);
			convertedAttr4 = String.valueOf(attr4);
			break;
		case 1:
			windSpeedUnit = res.getString(R.string.beaufort);
			convertedAttr4 = String.valueOf((double) SettingsConvertor
					.metersToBeaufort(attr4));
			break;
		case 2:
			windSpeedUnit = res.getString(R.string.knots);
			convertedAttr4 = String.valueOf((double) SettingsConvertor
					.metersToKnots(attr4));
			break;
		default:
			throw new IllegalArgumentException(
					"DetailedForecastInformationManager: error - No such windSpeedNotation");
		}

		String mouseOverString = String.format(mouseOverStringFormat, tempUnit,
				convertedAttr1, tempUnit, convertedAttr6, tempUnit,
				convertedAttr7, attr2, attr3, windSpeedUnit, convertedAttr4,
				attr5WithText);
		return mouseOverString;
	}
}