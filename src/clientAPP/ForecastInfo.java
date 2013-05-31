package clientAPP;

import android.graphics.Bitmap;

public class ForecastInfo {

	/**
	 * Class which contains no method but solely for purpose of saving the
	 * address, the detailed forecast information, and the images displaying the
	 * forecast.
	 */

	public String address = null;
	public String[] detailedForecastInformation = { null, null, null };
	public Bitmap[] mergedImages = { null, null, null };

	public ForecastInfo(String address, String[] mouseOverInfo,
			Bitmap[] mergedImages) {
		this.address = address;
		this.detailedForecastInformation = mouseOverInfo;
		this.mergedImages = mergedImages;
	}

}
