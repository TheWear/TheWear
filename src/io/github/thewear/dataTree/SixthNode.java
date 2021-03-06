package io.github.thewear.dataTree;

import io.github.thewear.thewearandroidClientAPP.WeatherEnums;

public class SixthNode extends Node {

	/**
	 * processNode() compares the temperature (user chosen threshold);
	 * 
	 * temperature > user minimum temperature --> To node 8
	 * 
	 * temperature <= user minimum temperature --> WeatherEnums.COLD
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
		// Convert temperature
		double temperature = Double.parseDouble((String) dataToCompare[1]);
		// compare temperature > user minimum temperature
		if (temperature > (Integer) dataToCompareWith[4]) {
			return nodeNumber = 8;
		} else {
			WeatherEnums weatherType = WeatherEnums.COLD;
			return weatherType;
		}
	}
}