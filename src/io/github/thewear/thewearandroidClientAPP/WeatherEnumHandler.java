package io.github.thewear.thewearandroidClientAPP;

import io.github.thewear.dataTree.EightNode;
import io.github.thewear.dataTree.FifthNode;
import io.github.thewear.dataTree.FirstNode;
import io.github.thewear.dataTree.FourthNode;
import io.github.thewear.dataTree.Node;
import io.github.thewear.dataTree.SecondNode;
import io.github.thewear.dataTree.SeventhNode;
import io.github.thewear.dataTree.SixthNode;
import io.github.thewear.dataTree.ThirdNode;
import io.github.thewear.thewearandroidClientAPP.PreferencesHandler;
import android.content.Context;

public class WeatherEnumHandler {

	/**
	 * Class WeatherEnumHandler is determines which clothing is drawn, given the
	 * weather conditions. It simply works with a series of if-statements.
	 */

	public WeatherEnums weatherType = WeatherEnums.DEFAULT;
	public boolean sunglasses = false;

	/**
	 * handleWeatherEnum() determines the weather type based on the weather data
	 * using nodes, and determines if sunglasses are necessary.
	 * 
	 * In this method is a comment explaining which array element contains which
	 * values, the full structure of the Tree can be found on our Google Drive
	 * in the 'Documentation' section.
	 * 
	 * @param weather_data
	 *            is the data as String[] used to determine which weather type
	 *            it currently is for the user
	 * @param context
	 *            of the activity, used to get SharedPreference values
	 */

	public void handleWeatherEnum(String[] weather_data, Context context) {

		/*
		 * weather_data information: String array
		 * 
		 * weather_data[0] = forecast information // date and time
		 * 
		 * weather_data[1] = temperature // Kelvin (average)
		 * 
		 * weather_data[2] = precipitation // mm
		 * 
		 * weather_data[3] = daylight // seconds
		 * 
		 * weather_data[4] = cloud cover // percentage
		 * 
		 * weather_data[5] = relative humidity // percentage
		 * 
		 * weather_data[6] = wind speed // m/s
		 * 
		 * weather_data[7] = snow depth // cm???
		 * 
		 * weather_data[8] = measured maximum temperature // Kelvin
		 * 
		 * weather_data[9] = measured minimum temperature // Kelvin
		 * 
		 * 
		 * conditions:
		 * 
		 * conditions[0] = Cloud cover threshold: 80%; (int)
		 * 
		 * conditions[1] = precipitation/dry threshold: 0.5mm (float)
		 * 
		 * conditions[2] = heavy precipitation threshold: 4.0mm (float)
		 * 
		 * conditions[3] = user maximum temperature threshold: get from
		 * preferences (Integer)
		 * 
		 * conditions[4] = user minimum temperature threshold: get from
		 * preferences (Integer)
		 * 
		 * conditions[5] = user wind speed threshold: get from preferences
		 * (double)
		 */

		int[] preferencesArray = PreferencesHandler.getPreferences(context);
		Object[] conditions = new Object[6];
		conditions[0] = (int) 80;
		conditions[1] = (float) 0.5;
		conditions[2] = (float) 4;
		conditions[3] = (int) (preferencesArray[2] + 273.15);
		conditions[4] = (int) (preferencesArray[0] + 273.15);
		conditions[5] = (double) (Math.pow(preferencesArray[1], 1.5) * 0.836);

		// percentage cloudCover
		double cloudCover = Double.parseDouble(weather_data[4]);// 80;

		// daylight in seconds
		int daylight = Integer.parseInt(weather_data[3]);

		// sunglasses or not:
		if (cloudCover <= 80) {
			if (daylight > 3600) { // = daylight > 1h
				sunglasses = true;
			} else {
				sunglasses = false;
			}
		} else {
			sunglasses = false;
		}

		Node[] nodes = new Node[9];
		nodes[1] = new FirstNode();
		nodes[2] = new SecondNode();
		nodes[3] = new ThirdNode();
		nodes[4] = new FourthNode();
		nodes[5] = new FifthNode();
		nodes[6] = new SixthNode();
		nodes[7] = new SeventhNode();
		nodes[8] = new EightNode();

		// No stopcondition (we use break);
		// No increment or decrement.
		for (int nodeNumber = 1;;) {
			Object returnedData = nodes[nodeNumber].processNode(nodeNumber,
					weather_data, conditions);
			if (returnedData instanceof Integer) {
				nodeNumber = (Integer) returnedData;
			} else {
				weatherType = (WeatherEnums) returnedData;
				break;
			}
		}
	}
}