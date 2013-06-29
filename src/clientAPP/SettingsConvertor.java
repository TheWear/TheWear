package clientAPP;

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
	 * celsiusToFahrenheit() converts the temperature in degrees celcius to
	 * degrees farhrenheit. (static method)
	 */

	public static int fahrenheitToCelsius(int fahrenheit) {
		int celsius = (int) Math.round((fahrenheit - 32) / 1.8);
		return celsius;
	}

	/**
	 * celsiusToFahrenheit() converts the temperature in degrees celcius to
	 * degrees farhrenheit. (static method)
	 */

	public static double celsiusToFahrenheit(double celsius) {
		int fahrenheit = (int) Math.round((celsius * 1.8) + 32);
		return fahrenheit;
	}
}
