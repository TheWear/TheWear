package io.github.thewear.thewearandroidClientAPP;

import java.math.BigDecimal;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import src.gui.thewearandroid.R;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

public class TimeHandler {

	/**
	 * The TimeHandler class contains all the methods to construct strings for
	 * the title containing the time information about how long the forecast is
	 * applicable whilst taking the time zones into account.
	 */

	private final int oneHour = 60; // minutes
	private int[] forecastTimeHour;
	private int[] forecastTimeMinute;
	private SharedPreferences sharedPref;
	private Resources res;

	/**
	 * the TimeHandler(SharedPreferences sharedPref, Resources res) constructor
	 * is used to initiate the TimeHandler class with the necessary information
	 * for the calculations.
	 */

	public TimeHandler(SharedPreferences sharedPref, Resources res) {
		this.sharedPref = sharedPref;
		this.res = res;
	}

	/**
	 * getOffsetFromUTC() retrieves the offset of the current time from UTC
	 * while taking timezones and daylight savings time into account.
	 * 
	 * getOffsetFromUTC() returns the offset from the UTC in minutes.
	 */

	public int getOffsetFromUTC() {
		Date date = new Date();
		long dateMs = date.getTime();
		DateTimeZone myDateTimeZone = DateTimeZone.getDefault();
		int offsetFormUTC = myDateTimeZone.getOffset(dateMs);
		int offsetFromUTCMin = Math.round(offsetFormUTC / (1000 * 60));
		return offsetFromUTCMin;
	}

	/**
	 * getCurrentForecastTime() returns a string array containing the three time
	 * titles that indicates when the forecast is valid.
	 * 
	 * Input: first forecast end time in hours in UTC time zone
	 */

	public String[] getCurrentForecastedTimeTitles(
			int firstForecastEndTimeHoursUTC) {

		int offset = getOffsetFromUTC();
		int firstForecastTimeMinutesUTC = firstForecastEndTimeHoursUTC * 60;
		int correctForecastTime = correctForecastedTime(
				firstForecastTimeMinutesUTC, offset);
		String[] timeString = getTimeString(correctForecastTime);

		return timeString;
	}

	/**
	 * correctForecastedTime() calculates the time of the forecast in the
	 * current time of the user.
	 * 
	 * Input: time of the forecast in UTC in minutes; the offset of the user
	 * from UTC in minutes
	 * 
	 * Output: the adjusted forecast time in minutes.
	 */

	public int correctForecastedTime(int forecastedUTCTime, int offset) {
		int correctedTime = forecastedUTCTime + offset;
		if (correctedTime >= 0) {
			// Adjust the correction if the correctedTime is more than 24 hours
			if (correctedTime >= 1440) {
				correctedTime = correctedTime - 1440;
			}
			// Adjust the correction if the correctedTime is negative
		} else {
			correctedTime = correctedTime + 1440;
		}
		return correctedTime;
	}

	/**
	 * minsToHoursMins() converts time in minutes to the same time in hours and
	 * minutes
	 * 
	 * input: int minutes
	 * 
	 * output: int array with the hour as first element and the minutes as
	 * second element.
	 */

	public int[] minsToHoursMins(int minutes) {
		BigDecimal bigDecimalMinutes = new BigDecimal(minutes);
		BigDecimal bigDecimalDivisor = new BigDecimal(oneHour);
		BigDecimal[] bigDecimalHoursMins = bigDecimalMinutes
				.divideAndRemainder(bigDecimalDivisor);
		int hours = bigDecimalHoursMins[0].intValue();
		int mins = bigDecimalHoursMins[1].intValue();
		int[] hoursMins = { hours, mins };
		return hoursMins;
	}

	/**
	 * getFirstForecastEndTime() returns the end time of the first forecast as a
	 * DateTime when it has the end time of the forecast in minutes as input.
	 */

	public DateTime getFirstForecastEndTime(int ForecastEndTimeMinutes) {
		DateTimeZone myDateTimeZone = DateTimeZone.getDefault();
		DateTime forecastDateTime = new DateTime(myDateTimeZone);
		int[] hoursMins = minsToHoursMins(ForecastEndTimeMinutes);
		DateTime newForecastDateTime = forecastDateTime.withHourOfDay(
				hoursMins[0]).withMinuteOfHour(hoursMins[1]);
		return newForecastDateTime;
	}

	/**
	 * getNextForecastEndTime() returns the nextForecastEndTime (a DateTime)
	 * when it has the previousForecastEndTime (a DateTime) as input
	 */

	public DateTime getNextForecastEndTime(DateTime previousForecastEndTime) {
		DateTime nextForecastEndTime = previousForecastEndTime.plusHours(6);
		return nextForecastEndTime;
	}

	/**
	 * getHourOfDayString() returns the hours of the day as a string formatted
	 * as 'HH'. Input is the hourOfDay as int and an int indicating what time
	 * notation has to be used ('0': 12-hours notation; '1': 24-hours notation).
	 * 
	 * getHOurOfDayString is a static method.
	 */

	public static String getHourOfDayString(int hourOfDayInt, int timeNotation) {
		String hourOfDayString = null;
		// CHeck what time notation to use and correct the time if necessary
		switch (timeNotation) {
		case 0:
			hourOfDayInt = to12HourClock(hourOfDayInt);
			break;
		case 1:
			// Nothing to change
			break;
		default:
			Log.e("TheWearDebug",
					"No such time notation preference (TimeHandler)");
		}
		// To string
		hourOfDayString = to2CharString(hourOfDayInt);
		return hourOfDayString;
	}

	/**
	 * getMinuteOfHourString() returns the minutes of the hour as a string
	 * formatted as 'MM'. input is the minuteOfHour as int.
	 * 
	 * getMinuteOfHourString is a static method.
	 */

	public static String getMinuteOfHourString(int minuteOfHourInt) {
		String minuteOfHourString = null;
		minuteOfHourString = to2CharString(minuteOfHourInt);
		return minuteOfHourString;
	}

	/**
	 * getTimeString() returns the time as a string formatted as 'HH:MM*' (HH
	 * and MM representing hours and minutes respectively from the forecastTime
	 * in minutes. The star represents the hour symbol 'h' for the 24-hours
	 * notation or 'am'/'pm' for the 12-hours notation
	 * 
	 * Input is the time the first forecast ends.
	 */

	public String[] getTimeString(int forecastTime) {
		forecastTimeHour = new int[3];
		forecastTimeMinute = new int[3];
		DateTime FirstForecastEndTime = getFirstForecastEndTime(forecastTime);
		forecastTimeHour[0] = FirstForecastEndTime.getHourOfDay();
		forecastTimeMinute[0] = FirstForecastEndTime.getMinuteOfHour();
		DateTime SecondforecastEndTime = getNextForecastEndTime(FirstForecastEndTime);
		forecastTimeHour[1] = SecondforecastEndTime.getHourOfDay();
		forecastTimeMinute[1] = SecondforecastEndTime.getMinuteOfHour();
		DateTime ThirdforecastEndTime = getNextForecastEndTime(SecondforecastEndTime);
		forecastTimeHour[2] = ThirdforecastEndTime.getHourOfDay();
		forecastTimeMinute[2] = ThirdforecastEndTime.getMinuteOfHour();

		int timeNotationPreference = sharedPref.getInt(
				res.getString(R.string.time_notation_preference),
				res.getInteger(R.integer.defaultTimeNotation));
		String[] titleString = constructTitleStrings(forecastTimeHour,
				forecastTimeMinute, timeNotationPreference);
		return titleString;
	}

	/**
	 * getForecastTimeHour() is the getter method for the forecastTimeHour.
	 */

	public int[] getForecastTimeHour() {
		return forecastTimeHour;
	}

	/**
	 * getForecastTimeMinute() is the getter method for the forecastTimeMinute.
	 */

	public int[] getForecastTimeMinute() {
		return forecastTimeMinute;
	}

	/**
	 * getAmPm() checks if 'am' or 'pm' has to be used for the 12-hours
	 * notation, and returns the correct one as string.
	 * 
	 * Input is the hour as 24-hours time.
	 * 
	 * getAmPm() is a static method.
	 */

	public static String getAmPm(int hour) {
		String amPm = null;
		if (hour < 12) {
			amPm = "am";
		} else {
			amPm = "pm";
		}
		return amPm;
	}

	/**
	 * to12HourClock() converts the 24-hours time to the 12-hours time.
	 * 
	 * Input is the hour as 24-hours time.
	 * 
	 * to12HourClock() returns the 12-hours time as int.
	 * 
	 * to 12HoursClock() is a static method.
	 */

	public static int to12HourClock(int hour) {
		int newHour = -1;
		if (hour <= 12) {
			if (hour == 0) {
				newHour = 12;
			} else {
				newHour = hour;
			}
		} else {
			newHour = hour - 12;
		}
		return newHour;
	}

	/**
	 * to2CharString() converts an integer to an 2 character long string. A '0'
	 * will be added before all numbers below 10.
	 * 
	 * Input: integer; Returns: String.
	 * 
	 * to2CharString() is a static method.
	 */

	public static String to2CharString(int myInt) {
		String myString = null;
		if (myInt < 10) {
			myString = "0" + myInt;
		} else {
			myString = "" + myInt;
		}
		return myString;
	}

	/**
	 * constructTitleStrings() constructs the full time titles for the forecast
	 * titles whilst taking the time notation preference of the user into
	 * account.
	 * 
	 * Input: int[] hour and int[] minute, both containing 3 elements; int
	 * timeNotationPreference for the time notation that should be used ('0':
	 * 12-hours notation; '1': 24-hours notation)
	 * 
	 * Returns the full time titles as String[].
	 * 
	 * constructTitleStrings() is a static method.
	 */

	public static String[] constructTitleStrings(int[] hour, int[] minute,
			int timeNotationPreference) {
		String[] titleSuffix = { null, null, null };
		switch (timeNotationPreference) {
		case 0: // 12-hours notation
			// Convert time to 12-hours notation
			for (int i = 0; i <= 2; i++) {
				titleSuffix[i] = getAmPm(hour[i]);
				hour[i] = to12HourClock(hour[i]);
			}
			break;
		case 1: // 24-hours notation
			for (int i = 0; i <= 2; i++) {
				titleSuffix[i] = "h";
			}
			break;
		default: // ERROR
			Log.e("TheWearDebug",
					"No such time notation preference, can't change the title");
		}
		String[] titleString = new String[3];
		titleString[0] = "now – " + to2CharString(hour[0]) + ":"
				+ to2CharString(minute[0]) + titleSuffix[0];
		for (int i = 1; i <= 2; i++) {
			titleString[i] = to2CharString(hour[(i - 1)]) + ":"
					+ to2CharString(minute[(i - 1)]) + titleSuffix[(i - 1)]
					+ " – " + to2CharString(hour[i]) + ":"
					+ to2CharString(minute[i]) + titleSuffix[i];
		}
		return titleString;
	}
}