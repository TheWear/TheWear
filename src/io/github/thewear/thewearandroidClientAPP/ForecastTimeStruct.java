package io.github.thewear.thewearandroidClientAPP;

public class ForecastTimeStruct {

	/**
	 * Class which contains no method but solely for purpose of saving the time
	 * title.
	 */

	public String[] forecastTimeString = { null, null, null };
	public int[] forecastTimeHour = { -1, -1, -1 };
	public int[] forecastTimeMinute = { -1, -1, -1 };

	public ForecastTimeStruct(String[] forecastTimeString) {
		this.forecastTimeString = forecastTimeString;
	}

	/**
	 * setForecastTimeStruct() is a method to save the changed time title and to
	 * save the information needed to change the time on settings change.
	 */

	public void setForecastTimeStruct(String[] forecastTimeString,
			int[] forecastTimeHour, int[] forecastTimeMinute) {
		this.forecastTimeString = forecastTimeString;
		this.forecastTimeHour = forecastTimeHour;
		this.forecastTimeMinute = forecastTimeMinute;
	}

	/**
	 * setForecastTimeString() is a method to save the changed time title.
	 */

	public void setForecastTimeString(String[] forecastTimeString) {
		this.forecastTimeString = forecastTimeString;
	}
}
