
public class MyData {

	String postalcode;
	String streetaddress;
	double currentLandValue;
	double previousLandValue;
	int yearBuilt;
	String zones;
	double totalLandChange;

	public double getTotalLandChange(){
		totalLandChange = getCurrentLandValue() - getPreviousLandValue();
		return totalLandChange;
	}
	public String getZones() {
		return zones;
	}
	public void setZones(String zones) {
		this.zones = zones;
	}
	public String getPostalcode() {
		return postalcode;
	}
	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}
	public String getStreetaddress() {
		return streetaddress;
	}
	public void setStreetaddress(String streetaddress) {
		this.streetaddress = streetaddress;
	}
	public double getCurrentLandValue() {
		return currentLandValue;
	}
	public void setCurrentLandValue(double currentLandValue) {
		this.currentLandValue = currentLandValue;
	}
	public double getPreviousLandValue() {
		return previousLandValue;
	}
	public void setPreviousLandValue(double previousLandValue) {
		this.previousLandValue = previousLandValue;
	}
	public int getYearBuilt() {
		return yearBuilt;
	}
	public void setYearBuilt(int yearBuilt) {
		this.yearBuilt = yearBuilt;
	}
	public MyData(String sa, String pc,  double clv, double plv, int yb, String z){
		postalcode = pc;
		streetaddress = sa;
		currentLandValue = clv;
		previousLandValue = plv;
		yearBuilt = yb;
		zones = z;
	}	

}
