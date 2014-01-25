package io.github.thewear.dataTree;

/**
 * 
 */

public abstract class Node {

	/**
	 * processNode() compares the dataToCompare with the dataToCompareWith, and
	 * chooses the right node to go to or returns the data (in which case the
	 * end of the branch is reached.
	 * 
	 * @param nodeNumber
	 *            integer indicating which node we are in
	 * @param dataToCompare
	 *            object[] array with data which has to be compared with
	 *            dataToCompareWith;
	 * @param dataToCompareWith
	 *            object[] array containing all the data that determine which
	 *            node has to be used;
	 * @return Object: this object is the next nodeNumber (an integer), or the
	 *         data that's located at the end of that branch.
	 */

	public abstract Object processNode(int nodeNumber, Object[] dataToCompare,
			Object[] dataToCompareWith);
}
