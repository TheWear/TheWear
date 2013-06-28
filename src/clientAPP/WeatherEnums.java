package clientAPP;

/**
 * loads correct strings
 */

public enum WeatherEnums {
	LIGHTRAIN("1011000000001"), // umbrella
	HEAVYRAIN("1110000111000"), // raincoat
	STORMYRAIN("1110000111000"), // raincoat
	SNOW("1110000111000"), // not used yet
	COOLWINDY("1110000000100"), // scarf
	COLD("1110011000100"), // scarf, hat, mittens
	WARMLIGHTRAIN("1011000000001"), // short-pants and
	// umbrella
	WARM("1110000000010"), // short-pants
	SUNNYWEATHER("1110000000000"),
	WARMSUNNYWEATHER("1110000000010"),
	DEFAULT("1110000000000");

	public boolean[] show_imgs;

	private WeatherEnums(String imgs) {
		this.show_imgs = new boolean[imgs.length()];
		for (int i = 0; i < imgs.length(); i++) {
			this.show_imgs[i] = imgs.charAt(i) == '1';
		}
	}

}
