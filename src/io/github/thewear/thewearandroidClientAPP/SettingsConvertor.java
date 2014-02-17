package io.github.thewear.thewearandroidClientAPP;

public class SettingsConvertor {

	/**
	 * SettingsConvertor contains static methods used to convert data.
	 */

	/**
	 * celsiusToFahrenheit() converts the temperature in degrees celcius to
	 * degrees farhrenheit. (static method)
	 */

	public static int celsiusToFahrenheit(int celsius) {
		int fahrenheit = (int) Math.round((celsius * 1.8) + 32);
		return fahrenheit;
	}

	/**
	 * fahrenheitToCelsius() converts the temperature in degrees fahrenheit to
	 * degrees celcius. (static method)
	 */

	public static int fahrenheitToCelsius(int fahrenheit) {
		int celsius = (int) Math.round((fahrenheit - 32) / 1.8);
		return celsius;
	}

	/**
	 * celsiusToFahrenheit() converts the temperature in degrees celcius to
	 * degrees farhrenheit. (static method)
	 */

	public static int celsiusToFahrenheit(double celsius) {
		int fahrenheit = (int) Math.round((celsius * 1.8) + 32);
		return fahrenheit;
	}

	// /**
	// * fahrenheitToCelsius() converts the temperature in degrees fahrenheit to
	// * degrees celcius. (static method)
	// */
	//
	// public static int fahrenheitToCelsius(double fahrenheit) {
	// int celsius = (int) Math.round((fahrenheit - 32) / 1.8);
	// return celsius;
	// }

	/**
	 * metersToBeaufort converts the wind speed from meters/second to beaufort
	 * (static method)
	 */

	public static int metersToBeaufort(double meters) {
		int beaufort = 0;
		if (meters < 0.2) {
			beaufort = 0;
		} else if (meters < 1.5) {
			beaufort = 1;
		} else if (meters < 3.3) {
			beaufort = 2;
		} else if (meters < 5.4) {
			beaufort = 3;
		} else if (meters < 7.9) {
			beaufort = 4;
		} else if (meters < 10.7) {
			beaufort = 5;
		} else if (meters < 13.8) {
			beaufort = 6;
		} else if (meters < 17.1) {
			beaufort = 7;
		} else if (meters < 20.7) {
			beaufort = 8;
		} else if (meters < 24.4) {
			beaufort = 9;
		} else if (meters < 28.4) {
			beaufort = 10;
		} else if (meters < 32.6) {
			beaufort = 11;
		} else {
			beaufort = 12;
		}
		return beaufort;
	}

	/**
	 * metersToKnots() converts the wind speed from meters/second to knots
	 * (static method)
	 */

	public static float metersToKnots(double meters) {
		float knots = Math.round((meters / (1.852 / 3.6)) * 100) / (float) 100;
		return knots;
	}

	/**
	 * beaufortToMeters() converts the wind speed from Beaufort to meters/second
	 * (static method)
	 */

	public static float beaufortToMeters(int beaufort) {
		float meters = Math
				.round((Math.pow(beaufort, 1.5) * 0.836) * 100) / (float) 100;
		return meters;
	}

	/**
	 * knotsToMeters() converts the wind speed from knots to meters/second
	 * (static method)
	 */

	public static float knotsToMeters(double knots) {
		float meters = Math.round((knots * (1.852 / 3.6)) * 100) / (float) 100;
		return meters;
	}
}
