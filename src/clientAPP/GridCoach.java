package clientAPP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.util.Log;

/**
 * public class GridCoach; input = string location (example: "Wageningen");
 * output = gridCoos{lat , lon} convert to latitude&longitude (method) convert
 * to Gridpoint (method)
 **/
public class GridCoach {

	private String strLocation;
	private Double dblLatitude;
	private Double dblLongitude;

	public GridCoach(String location) {
		String locationSpace = replace(location, " ", "%20");
		this.strLocation = locationSpace;
	}

	public void setLocation(Double lat, Double lng) {
		this.dblLatitude = lat;
		this.dblLongitude = lng;
	}

	private String replace(String text, String searchString,
			String replacementString) {
		StringBuffer sBuffer = new StringBuffer();
		int pos = 0;
		while ((pos = text.indexOf(searchString)) != -1) {
			sBuffer.append(text.substring(0, pos) + replacementString);
			text = text.substring(pos + searchString.length());
		}
		sBuffer.append(text);
		return sBuffer.toString();
	}

	public Double getLatitude() {
		return this.dblLatitude;
	}

	public Double getLongitude() {
		return this.dblLongitude;
	}

	/*** rounds the coordinates to halves of degrees **/
	public Double[] convertToGridCoos() {
		dblLatitude = ((Math.round((dblLatitude * 2.0))) / 2.0);
		dblLongitude = ((Math.round((dblLongitude * 2.0))) / 2.0);

		Double[] gridCoos = { dblLatitude, dblLongitude };
		return gridCoos;
	}

	public String PlaceToURL() {
		String urlString = ("http://maps.googleapis.com/maps/api/geocode/json?address="
				+ this.strLocation + "&sensor=false");
		return urlString;
	}

	public LocationStruct URLToJSonString(String strUrl) {
		// TODO clean the Try - Catch Structures (Remove obsolete catches)
		// TODO Need a check that a response was not redirected to an unexpected host?
		// TODO Add a way to check if there is a network available: maybe trying to ping;
		// ConnectivityManager doesn't always work

		LocationStruct data = null;

		URL jsonURL;
		HttpURLConnection conn;
		try {
			jsonURL = new URL(strUrl);

			try {
				conn = (HttpURLConnection) jsonURL.openConnection();
				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(conn.getInputStream(), "UTF-8"));
					StringBuilder sb = new StringBuilder();
					String inputData = null;
					try {
						while ((inputData = reader.readLine()) != null) {
							sb.append(inputData + "\n");
						}
					} catch (IOException e) {
						e.printStackTrace();
						// error
					} finally {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
							// error
						}
					}

					JSONParser parser = new JSONParser();
					JSONObject json_obj;
					try {
						json_obj = (JSONObject) parser.parse(sb.toString());
	
						if (((String) json_obj.get("status")).equals("OK")) {
							String address = null;
							Double lat = null;
							Double lng = null;
							if (json_obj.containsKey("results")) {
								JSONArray results = (JSONArray) json_obj
										.get("results");
								for (Object result : results.toArray()) {
									JSONObject result2 = (JSONObject) result;
									if (result2.containsKey("formatted_address")) {
										address = (String) result2
												.get("formatted_address");
										if (result2.containsKey("geometry")) {
											JSONObject geometry = (JSONObject) result2
													.get("geometry");
											if (geometry.containsKey("location")) {
												JSONObject location = (JSONObject) geometry
														.get("location");
												if (location.containsKey("lat")
														&& location
																.containsKey("lng")) {
													lat = (Double) location
															.get("lat");
													lng = (Double) location
															.get("lng");
													data = new LocationStruct(
															address, lat, lng);
												}
											}
										}
									}
								}
							}
						} else {
							String address = "Unknown location";
							Double lat = null;
							Double lng = null;
							data = new LocationStruct(address, lat, lng);
						}
	
					} catch (ParseException e) {
						e.printStackTrace();
						// error
					}
				} finally {
					conn.disconnect();
				}

			} catch (MalformedURLException e1) {
				Log.e("Connection Error", "Connection Error 1");
				Log.e("TheWearDebug","MalformedURLException in GridCoach");
				data = null;
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				Log.e("TheWearDebug","UnsupportedEncodingException in GridCoach");
				data = null;
			} catch (IOException e1) {
				e1.printStackTrace();
				Log.e("TheWearDebug","IOException in GridCoach");
				data = null;
			}
			
			Log.d("TheWearDebug", "Completed URLToJSonString");

		} catch (MalformedURLException e2) {
			e2.printStackTrace();
			// Auto-generated catch block
			data = null;
		}
		return data;
	}
}