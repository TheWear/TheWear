package clientAPP;

import java.io.*;
import java.net.*;

import android.util.Log;

public class ServerCommunicator {

	/**
	 * Class ServerCommunicator:
	 * retrieves the dataset stringarray from the server.
	 * Gridpoint latitude & longitude and forecast specific.
	 */

	public String[] getWeatherData(int forecast, double lat, double lng) {
		Log.d("TheWearDebug","Starting readFile()");
		String forecastString = Integer.toString(forecast);
		String latString = Double.toString(lat);
		String lngString = Double.toString(lng);
		String[] dataset = null;
		URL serverFile;
		HttpURLConnection conn;
		try {
			serverFile = new URL("http://www.swla.nl/getinfo.php?lat="
					+ latString + "&lng=" + lngString + "&time=" + forecastString);
			Log.d("TheWearDebug","connecting with URL" + serverFile);
			try {
				conn = (HttpURLConnection) serverFile.openConnection();
				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(conn.getInputStream(), "UTF-8"));
					StringBuilder sb = new StringBuilder();
					String inputData = null;
					try {
						while ((inputData = reader.readLine()) != null) {
							sb.append(inputData + "\n");
						}
						String data = sb.toString();
						dataset = data.split("\t");
						Log.d("TheWearDebug","date of the dataset: "+ dataset[0]);
					} catch (IOException e) {
						// TODO Add error message for IOException
						Log.e("TheWearDebug","IOException 1");
					} finally {
						try {
							reader.close();
							Log.d("TheWearDebug","readFile() reader closed");
						} catch (IOException e) {
							// TODO Add error message for IOException
							Log.e("TheWearDebug","IOException 2");
						}
					}
				} finally {
					conn.disconnect();
					Log.d("TheWearDebug","readFile() connection closed");
				}
			} catch (IOException e1) {
				// TODO Add error message for IOException
				Log.e("TheWearDebug","IOException 3");
				// This is called when the application can't retrieve the weather data from the server.
				dataset = null;
			}
		} catch (MalformedURLException e1) {
			// TODO Add error message for MalformedURLException
			Log.d("TheWearDebug","MalformedURLException");
			dataset = null;
		}

		return dataset;
	}
}
