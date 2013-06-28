package clientAPP;

import java.util.Arrays;

import src.gui.thewearandroid.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class MergeImage {

	/**
	 * MergeForecastImage is applied to obtain the final image shown, after input of a
	 * location. An advice is obtained from the WeatherEnums Class, and
	 * the result is returned as a Bitmap.
	 */

	public Bitmap MergeForecastImage(boolean[] advice, Context context) {
		// Initiate Bitmap
		Bitmap myBitmap = Bitmap.createBitmap(300, 500, Config.ARGB_8888);
		// Initiate Canvas
		Canvas canvas = new Canvas(myBitmap);
		canvas.drawColor(0x00AAAAAA); // Set no background
		// Initiate resources
		Resources res = context.getResources();
		String[] imagePathname = res.getStringArray(R.array.pathnames);
		imagePathname = Arrays.copyOf(imagePathname, imagePathname.length + 1);
		imagePathname[imagePathname.length - 1] = res.getString(R.string.sunGlassesPathname);

		if (imagePathname.length == advice.length) {
			for (int i = 0; i < advice.length; i++) {
				if (advice[i] == true) {
					int imageIdentifier = res.getIdentifier(imagePathname[i],
							"drawable", context.getPackageName());
					// If getIdentifier can't find an integer with that name, it
					// returns the non-existing identifier '0'
					if (imageIdentifier == 0) {
						Log.e("TheWearDebug",
								"Could not retrieve the drawable resources. Path: "
								+ imagePathname[i]);
					} else {
						Bitmap bitmap = BitmapFactory.decodeResource(res, imageIdentifier);
						canvas.drawBitmap(bitmap, 0, 0, null);
					}
				}
			}
		} else {
			Log.e("TheWearDebug","The number of pathnames doesn't correspont with the advice length.");
		}
		return myBitmap;
	}

}
