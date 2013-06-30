package io.github.thewear.thewearandroidClientAPP;

import io.github.thewear.thewearandroidClientAPP.PreferencesHandler;
import android.content.Context;

public class WeatherEnumHandler {

	/**
	 * Class WeatherEnumHandler is determines which clothing is drawn, given the
	 * weather conditions. It simply works with a series of if-statements.
	 */

	public WeatherEnums weathertype = WeatherEnums.DEFAULT;
	public boolean sunglasses = false;

	public void handleWeatherEnum(String[] weather_data, Context context) {
		double temp = Double.parseDouble(weather_data[1]);// 293.15; // Kelvin
															// (average)
		double prec = Double.parseDouble(weather_data[2]);// 5; // mm
		double clcover = Double.parseDouble(weather_data[4]);// 80; //
																// percentage
		double wind = Double.parseDouble(weather_data[6]);// 10; // meter per
															// second
		int daylight = Integer.parseInt(weather_data[3]); // daylight in seconds

		int[] preferencesArray = PreferencesHandler.getPreferences(context);
		int minTemp = (int) (preferencesArray[0] + 273.15);
		double windSpeed = (double) (Math.pow(preferencesArray[1], 1.5) * 0.836);
		int maxTemp = (int) (preferencesArray[2] + 273.15);

		if (clcover <= 80) {
			if (daylight > 3600) { // = daylight > 1h
				sunglasses = true;
			} else {
				sunglasses = false;
			}
		} else {
			sunglasses = false;
		}

		if (prec > 4) {
			weathertype = WeatherEnums.HEAVYRAIN;
		} else if (prec > 0.5) {
			if (temp > maxTemp) {
				weathertype = WeatherEnums.WARMLIGHTRAIN;
			} else if (wind >= windSpeed) {
				weathertype = WeatherEnums.STORMYRAIN;
			} else {
				weathertype = WeatherEnums.LIGHTRAIN;
			}
		} else if (temp >= maxTemp) {
			if (clcover <= 80) {
				weathertype = WeatherEnums.WARMSUNNYWEATHER;
			} else {
				weathertype = WeatherEnums.WARM;
			}
		} else if (temp <= minTemp) {
			weathertype = WeatherEnums.COLD;
		} else if (clcover <= 80) {
			weathertype = WeatherEnums.SUNNYWEATHER;
		} else {
			weathertype = WeatherEnums.DEFAULT;
		}
	}

	public String getWeatherTypeString() {
		if (weathertype == WeatherEnums.LIGHTRAIN) {
			return "light rain";
		} else if (weathertype == WeatherEnums.HEAVYRAIN) {
			return "heavy rain";
		} else if (weathertype == WeatherEnums.SNOW) {
			return "snow";
		} else if (weathertype == WeatherEnums.COLD) {
			return "cold";
		} else if (weathertype == WeatherEnums.COOLWINDY) {
			return "cold and wind";
		} else if (weathertype == WeatherEnums.DEFAULT) {
			return "slightly uninteresting weather";
		} else if (weathertype == WeatherEnums.STORMYRAIN) {
			return "strom and rain";
		} else if (weathertype == WeatherEnums.WARM) {
			return "warm weather";
		} else if (weathertype == WeatherEnums.WARMLIGHTRAIN) {
			return "light rain and warm weather";
		}

		return "strange weather";
	}
}