package clientAPP;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class ForecastInfo {

	/**
	 * Class which contains no method but solely for purpose of saving the
	 * address, the images displaying the forecast and the dataset of the
	 * forecast.
	 */

	public String address = null;
	public Bitmap[] mergedImages = { null, null, null };
	public ArrayList<String[]> dataset = null;

	public ForecastInfo(String address, Bitmap[] mergedImages,
			ArrayList<String[]> dataset) {
		this.address = address;
		this.mergedImages = mergedImages;
		this.dataset = dataset;
	}

}
