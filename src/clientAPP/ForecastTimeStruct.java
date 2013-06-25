package clientAPP;

public class ForecastTimeStruct {

	/**
	 * Class which contains no method but solely for purpose of saving the time
	 * title.
	 */

	public String[] forecastTimeString = { null, null, null };

	public ForecastTimeStruct(String[] forecastTimeString) {
		this.forecastTimeString = forecastTimeString;
	}

	/**
	 * setForecastTimeStruct() is a method to change the saved time title
	 */

	public void setForecastTimeStruct(String[] forecastTimeString) {
		this.forecastTimeString = forecastTimeString;
	}
}
