package io.github.thewear.thewearandroidClientAPP;

import io.github.thewear.thewearandroid.R;

/**
 * enumerator WeatherEnums contains the strings used to determine which images
 * each weather type uses (can be retrieved as boolean[] show_imgs; and the
 * paths to the weather type descriptions (can be retrieved using
 * weatherStringPath)
 */

public enum WeatherEnums {
	LIGHTRAIN("1011000000001", R.string.LIGHTRAIN), // umbrella
	HEAVYRAIN("1110000111000", R.string.HEAVYRAIN), // raincoat
	STORMYRAIN("1110000111000", R.string.STORMYRAIN), // raincoat
	SNOW("1110000111000", R.string.SNOW), // not used yet
	COOLWINDY("1110000000100", R.string.COOLWINDY), // scarf
	COLD("1110011000100", R.string.COLD), // scarf, hat, mittens
	WARMLIGHTRAIN("1011000000001", R.string.WARMLIGHTRAIN), // short-pants and
	// umbrella
	WARM("1110000000010", R.string.WARM), // short-pants
	SUNNYWEATHER("1110000000000", R.string.SUNNYWEATHER), WARMSUNNYWEATHER(
			"1110000000010", R.string.WARMSUNNYWEATHER), DEFAULT(
			"1110000000000", R.string.DEFAULT);

	public boolean[] show_imgs;
	public int weatherStringPath;

	private WeatherEnums(String imgs, int weatherStringPath) {
		this.show_imgs = new boolean[imgs.length()];
		for (int i = 0; i < imgs.length(); i++) {
			this.show_imgs[i] = imgs.charAt(i) == '1';
		}
		this.weatherStringPath = weatherStringPath;
	}

}
