package io.github.thewear.dataTree;

import io.github.thewear.thewearandroidClientAPP.WeatherEnums;

public class SeventhNode extends Node {

	/**
	 * processNode() compares the wind speed (user chosen threshold);
	 * 
	 * wind speed < user wind speed --> WeatherEnums.LIGHTRAIN (other)
	 * 
	 * wind speed >= user wind speed --> WeatherEnums.STORMYRAIN
	 * 
	 * @param nodeNumber
	 *            integer indicating which node we are in
	 * @param dataToCompare
	 *            object[] array with data which has to be compared with
	 *            dataToCompareWith; elements are determined using the
	 *            nodeNumber
	 * @param dataToCompareWith
	 *            object[] array containing all the data that determine which
	 *            node has to be used; elements are determined using the
	 *            nodeNumber
	 * @return Object: this object is the next nodeNumber (an integer), or the
	 *         data that's located at the end of that branch
	 */

	@Override
	public Object processNode(int nodeNumber, Object[] dataToCompare,
			Object[] dataToCompareWith) {
		// Convert wind speed
		double windSpeed = Double.parseDouble((String) dataToCompare[6]);
		// compare wind speed < user wind speed
		if (windSpeed < (Double) dataToCompareWith[5]) {
			WeatherEnums weatherType = WeatherEnums.LIGHTRAIN;
			return weatherType;
		} else {
			WeatherEnums weatherType = WeatherEnums.STORMYRAIN;
			return weatherType;
		}
	}
}