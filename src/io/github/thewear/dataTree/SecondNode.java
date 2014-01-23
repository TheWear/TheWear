package io.github.thewear.dataTree;

import io.github.thewear.thewearandroidClientAPP.WeatherEnums;

public class SecondNode extends Node {

	/**
	 * processNode() compares the precipitation (4.0mm threshold);
	 * 
	 * precipitation > 4.0mm --> WeatherEnums.HEAVYRAIN
	 * 
	 * precipitation <= 4.0mm --> To node 4
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
		// Convert precipitation
		double precipitation = Double.parseDouble((String) dataToCompare[2]);
		// compare precipitation > 4.0mm
		if (precipitation > (Float) dataToCompareWith[2]) {
			WeatherEnums weatherType = WeatherEnums.HEAVYRAIN;
			return weatherType;
		} else {
			return nodeNumber = 4;
		}
	}
}