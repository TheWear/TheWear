package io.github.thewear.dataTree;

import io.github.thewear.thewearandroidClientAPP.WeatherEnums;

public class FifthNode extends Node {

	/**
	 * processNode() compares the cloud cover (80% threshold);
	 * 
	 * cloud cover > 80% --> WeatherEnums.WARM (other)
	 * 
	 * cloud cover <= 80% --> WeatherEnums.WARMSUNNYWEATHER
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
		// Convert cloud cover
		double cloudCover = Double.parseDouble((String) dataToCompare[4]);
		// compare cloud cover > 80%
		if (cloudCover> (Integer) dataToCompareWith[0]) {
			WeatherEnums weatherType = WeatherEnums.WARM;
			return weatherType;
		} else {
			WeatherEnums weatherType = WeatherEnums.WARMSUNNYWEATHER;
			return weatherType;
		}
	}
}