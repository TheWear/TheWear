package io.github.thewear.thewearandroidClientAPP;

// Construct output for GridCoach
public class LocationStruct {

	/**
	 * Class which contains no method but solely for purpose of saving the
	 * address, latitude and longitude.
	 */

	public String address = null;
	public Double lat = null;
	public Double lng = null;
	
	public LocationStruct(String address, Double lat, Double lng) {
		this.address = address;
		this.lat = lat;
		this.lng = lng;
	}
}