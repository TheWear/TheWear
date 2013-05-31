package clientAPP;

public class MouseOverManager {

	/**
	 * MouseOverManager:
	 * Constructs a weather string from the parameter array;
	 * 
	 * A MouseOverManager instance is initiated with a dataset String[] parameter;
	 * 
	 * When there is no snow, Snowdepth is not included in the MouseOverString;
	 * Cloudcover is converted to Sunshine [%] and Temperature to °Celsius;
	 */

	private String mouseOverStringFormat = 
			"Temperature (°C) %s%n"+ //TMP
			"Precipitation (mm/6hours) %s%n"+ //APCP
			"Sunshine %s%%%n"+ //TCDC
			"Windspeed (m/s) %s%n"+ //sqrt(UGRD^2+VGRD^2)
			"%s"; //WEASD
	private String[] dataset;
	private String attr1;
	private String attr2;
	private String attr3;
	private String attr4;
	private String attr5;
	private Double kelvin = 273.2;

	public MouseOverManager(String[] d) {
		dataset=d;
		attr1=String.valueOf(
				Math.round((Double.parseDouble(dataset[1])-kelvin)*100)/100.0);
		attr2=dataset[2];
		attr3=String.valueOf(
				Math.round(100-Double.parseDouble(dataset[4])));
		attr4=dataset[6];
		if (Double.parseDouble(dataset[7])>0) {
			attr5 = "; Snowdepth (cm) " + dataset[7];
		}else{attr5 = "";}
	}

	public String getString() {
		String mouseOverString = String.format(
				mouseOverStringFormat,attr1,attr2,attr3,attr4,attr5);
		return mouseOverString;
	}
}
