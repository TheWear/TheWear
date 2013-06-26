package clientAPP;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.util.Log;

public class TimeHandler {

	/**
	 * The TimeHandler class contains all the methods to construct strings for
	 * the title containing the time information about how long the forecast is
	 * applicable whilst taking the time zones into account.
	 */

	private final int forecast1UTCTimeMins = 0; // 00:00h
	private final int forecast2UTCTimeMins = 360; // 06:00h
	private final int forecast3UTCTimeMins = 720; // 12:00h
	private final int forecast4UTCTimeMins = 1080; // 18:00h
	private final int oneHour = 60; // minutes

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

		Log.d("TheWearDebug", "Offset from UTC is (minutes): "
				+ offsetFromUTCMin);

		return offsetFromUTCMin;
	}

	/**
	 * getCurrentForecastTime returns a string array containing the three hours
	 * at which the the three forecasts 'expire'.
	 */

	public String[] getCurrentForecastedTime() {

		int offset = getOffsetFromUTC();
		DateTimeZone myDateTimeZone = DateTimeZone.getDefault();
		DateTime myDateTime = new DateTime(myDateTimeZone);
		int hourOfDay = myDateTime.getHourOfDay();
		int minuteOfHour = myDateTime.getMinuteOfHour();
		int currentHourMinute = (hourOfDay * 60) + minuteOfHour;
		String[] timeString = null;

		int[] forecastTimes = new int[4];
		forecastTimes[0] = correctForecastedTime(forecast1UTCTimeMins, offset);
		forecastTimes[1] = correctForecastedTime(forecast2UTCTimeMins, offset);
		forecastTimes[2] = correctForecastedTime(forecast3UTCTimeMins, offset);
		forecastTimes[3] = correctForecastedTime(forecast4UTCTimeMins, offset);
		// sort the forecastTimes array to have them in ascending order after
		// calculating the correctedForecastedTime
		Arrays.sort(forecastTimes);
		if (currentHourMinute > forecastTimes[0]) {
			if (currentHourMinute > forecastTimes[1]) {
				if (currentHourMinute > forecastTimes[2]) {
					if (currentHourMinute > forecastTimes[3]) {
						// forecastTime4 <= currentHourMinute
						timeString = getTimeString(forecastTimes[0]);

					} else {
						// forecastTime3 <= currentHourMinute < forecastTime4
						timeString = getTimeString(forecastTimes[3]);

					}
				} else {
					// forecastTime2 <= currentHourMinute < forecastTime3
					timeString = getTimeString(forecastTimes[2]);

				}
			} else {
				// forecastTime1 <= currentHourMinute < forecastTime2
				timeString = getTimeString(forecastTimes[1]);

			}
		} else {
			// currentHourMinute < forecastTime1
			timeString = getTimeString(forecastTimes[0]);

		}
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
	 * as 'HH'. input is the DateTime.
	 */

	public String getHourOfDayString(DateTime dateTime) {
		int hourOfDayInt = dateTime.getHourOfDay();
		String hourOfDayString = null;
		if (hourOfDayInt < 10) {
			hourOfDayString = "0" + hourOfDayInt;
		} else {
			hourOfDayString = "" + hourOfDayInt;
		}
		return hourOfDayString;
	}

	/**
	 * getMinuteOfHourString() returns the minutes of the hour as a string
	 * formatted as 'MM'. input is the DateTime.
	 */

	public String getMinuteOfHourString(DateTime dateTime) {
		int minuteOfHourInt = dateTime.getMinuteOfHour();
		String minuteOfHourString = null;
		if (minuteOfHourInt < 10) {
			minuteOfHourString = "0" + minuteOfHourInt;
		} else {
			minuteOfHourString = "" + minuteOfHourInt;
		}
		return minuteOfHourString;
	}

	/**
	 * getTimeString() returns the time as a string formatted as 'HH:MMh' (HH
	 * and MM representing hours and minutes respectively from the forecastTime
	 * in minutes. (forecastTime is the time the first forecast ends).
	 */

	public String[] getTimeString(int forecastTime) {
		DateTime FirstForecastEndTime = getFirstForecastEndTime(forecastTime);
		DateTime SecondforecastEndTime = getNextForecastEndTime(FirstForecastEndTime);
		DateTime ThirdforecastEndTime = getNextForecastEndTime(SecondforecastEndTime);
		String[] timeString = new String[3];
		timeString[0] = getHourOfDayString(FirstForecastEndTime) + ":"
				+ getMinuteOfHourString(FirstForecastEndTime) + "h";
		timeString[1] = getHourOfDayString(SecondforecastEndTime) + ":"
				+ getMinuteOfHourString(SecondforecastEndTime) + "h";
		timeString[2] = getHourOfDayString(ThirdforecastEndTime) + ":"
				+ getMinuteOfHourString(ThirdforecastEndTime) + "h";
		return timeString;
	}

	/**
	 * getTimeTitles() returns the full time titles as a String array with 3
	 * elements.
	 */

	public String[] getTimeTitles() {
		String[] timeString = getCurrentForecastedTime();
		String[] timeTitles = new String[3];
		timeTitles[0] = "now – " + timeString[0];
		timeTitles[1] = timeString[0] + " – " + timeString[1];
		timeTitles[2] = timeString[1] + " – " + timeString[2];
		return timeTitles;
	}
}