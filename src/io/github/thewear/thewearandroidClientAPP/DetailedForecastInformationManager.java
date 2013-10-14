package io.github.thewear.thewearandroidClientAPP;

import src.gui.thewearandroid.R;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

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

	private String mouseOverStringFormat =
            "Temperature: %s%s%n" + // TMP
			"Maximum Temperature: %s%s%n" + // TMAX
			"Minimum Temperature: %s%s%n" + // TMIN
			"Precipitation: %s mm/6hours%n" + // APCP
			"Cloud cover: %s%%%n" + // TCDC
			"Wind speed: %s m/s%n" + // sqrt(UGRD^2+VGRD^2)
            "Humidity: %s%%" + // RH2
			"%s"; // WEASD
    private String snowDepthFormat =
            "%nSnowdepth: %s cm"; //WEASD
	private String[] dataset;
	private double attr1; // Temperature
	private String attr2; // Precipitation
	private String attr3; // Cloud Cover
	private String attr4; // Wind Speed
	private String attr5; // Snow Cover
	private double attr6; // Minimum Temperature
	private double attr7; // Maximum Temperature
    private String attr8; // Relative Humidity
	private Double kelvin = 273.2;

	public DetailedForecastInformationManager(String[] d) {
		dataset = d;
		attr1 = Math.round((Double.parseDouble(dataset[1]) - kelvin) * 100) / 100.0;
		attr2 = dataset[2];
		attr3 = String.valueOf(Math.round(Double.parseDouble(dataset[4])));
		attr4 = String.valueOf(Double.parseDouble(dataset[6]));
		if (Double.parseDouble(dataset[7]) > 0) {
			attr5 = String.format(snowDepthFormat,dataset[7]);
		} else {
			attr5 = "";
		}
		attr6 = Math.round((Double.parseDouble(dataset[8]) - kelvin) * 100) / 100.0;
		attr7 = Math.round((Double.parseDouble(dataset[9]) - kelvin) * 100) / 100.0;
        attr8 = String.valueOf(Double.parseDouble(dataset[5]));
	}

	/**
	 * getString() constructs and returns the string shown on the detailed
	 * forecast.
	 * 
	 * The temperatures are shown in �C or �F according to the user preference
	 * 
	 * Input: SharedPreferences sharedPref, Resources res
	 */

	public String getString(SharedPreferences sharedPref, Resources res) {
		int defaultTemperatureNotation = res
				.getInteger(R.integer.defaultTemperatureNotation);
		int temperatureNotation = sharedPref.getInt(
				res.getString(R.string.temperature_notation_preference),
				defaultTemperatureNotation);
		String tempUnit = null;
		String convertedAttr1 = null;
		String convertedAttr6 = null;
		String convertedAttr7 = null;
		switch (temperatureNotation) {
		case 0:
			tempUnit = res.getString(R.string.setting2RadioButton1);
			convertedAttr1 = String.valueOf(attr1);
			convertedAttr6 = String.valueOf(attr6);
			convertedAttr7 = String.valueOf(attr7);
			break;
		case 1:
			tempUnit = res.getString(R.string.setting2RadioButton2);
			convertedAttr1 = String.valueOf((double) SettingsConvertor
					.celsiusToFahrenheit(attr1));
			convertedAttr6 = String.valueOf((double) SettingsConvertor
					.celsiusToFahrenheit(attr6));
			convertedAttr7 = String.valueOf((double) SettingsConvertor
					.celsiusToFahrenheit(attr7));
			break;
		default:
			Log.e("TheWearDebug", "No such temperatureNotation");
		}
		String mouseOverString = String.format(mouseOverStringFormat, convertedAttr1, tempUnit,
                convertedAttr6, tempUnit, convertedAttr7, tempUnit,
				 attr2, attr3, attr4, attr8, attr5);
		return mouseOverString;
	}
}