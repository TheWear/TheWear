package clientAPP;

/**
 * loads correct strings
 */

public enum WeatherEnums {
	LIGHTRAIN("10110000000010"), // umbrella
	HEAVYRAIN("11100001110000"), // raincoat
	STORMYRAIN("11100001110000"), // raincoat
	SNOW("11100001110000"), // not used yet
	COOLWINDY("11100000001000"), // scarf
	COLD("11100110001000"), // scarf, hat, mittens
	WARMLIGHTRAIN("10110000000010"), // short-pants and
	// umbrella
	WARM("11100000000100"), // short-pants
	SUNNYWEATHER("11100000000001"),
	WARMSUNNYWEATHER("11100000000101"),
	DEFAULT("11100000000000");

	public boolean[] show_imgs;

	private WeatherEnums(String imgs) {
		this.show_imgs = new boolean[imgs.length()];
		for (int i = 0; i < imgs.length(); i++) {
			this.show_imgs[i] = imgs.charAt(i) == '1';
		}
	}

}
