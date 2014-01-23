package io.github.thewear.dataTree;

/**
 *
 */

public class FirstNode extends Node {

	/**
	 * processNode() compares the precipitation (0.5mm threshold);
	 * 
	 * precipitation > 0.5mm --> To node 2
	 * 
	 * precipitation <= 0.5mm --> To node 3
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
		// compare precipitation > 0.5mm
		if (precipitation > (Float) dataToCompareWith[1]) {
			return nodeNumber = 2;
		} else {
			return nodeNumber = 3;
		}
	}
}