package beans;

import java.io.Serializable;

public class MaintenanceItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5053363052962897098L;
	public int key;
	public String Vehicle;
	public String MaintElem;
	public String MaintType;
	public double FuelAmount;
	public double Consumption;
	public String Date;
	public int Odometer;
	public String Details;
	public int MileageType;
	public double cash;

	public MaintenanceItem(String vehicle, String maintElem, String maintType,
			double fuelAmount, double consumption, String date, int odometer,
			String details, int mileageType) {
		super();

		Vehicle = vehicle;
		MaintElem = maintElem;
		MaintType = maintType;
		FuelAmount = fuelAmount;
		Consumption = consumption;
		Date = date;
		Odometer = odometer;
		Details = details;
		MileageType = mileageType;
	}

	public MaintenanceItem(String vehicle, String maintElem, String maintType,
			double fuelAmount, double consumption, String date, int odometer,
			String details, int mileageType, double cash) {
		super();

		Vehicle = vehicle;
		MaintElem = maintElem;
		MaintType = maintType;
		FuelAmount = fuelAmount;
		Consumption = consumption;
		Date = date;
		Odometer = odometer;
		Details = details;
		MileageType = mileageType;
		this.cash = cash;

	}

	public MaintenanceItem(int key, String vehicle, String maintElem,
			String maintType, double fuelAmount, double consumption,
			String date, int odometer, String details, int mileageType,
			double cash) {
		super();
		this.key = key;
		Vehicle = vehicle;
		MaintElem = maintElem;
		MaintType = maintType;
		FuelAmount = fuelAmount;
		Consumption = consumption;
		Date = date;
		Odometer = odometer;
		Details = details;
		MileageType = mileageType;
		this.cash = cash;
	}

	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}

	public String getVehicle() {
		return Vehicle;
	}

	public void setVehicle(String vehicle) {
		Vehicle = vehicle;
	}

	public String getMaintElem() {
		return MaintElem;
	}

	public void setMaintElem(String maintElem) {
		MaintElem = maintElem;
	}

	public String getMaintType() {
		return MaintType;
	}

	public void setMaintType(String maintType) {
		MaintType = maintType;
	}

	public double getFuelAmount() {

		return FuelAmount;
	}

	public void setFuelAmount(double fuelAmount) {
		FuelAmount = fuelAmount;
	}

	public double getConsumption() {
		return Consumption;
	}

	public void setConsumption(double consumption) {
		Consumption = consumption;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}

	public int getOdometer() {
		return Odometer;
	}

	public void setOdometer(int odometer) {
		Odometer = odometer;
	}

	public String getDetails() {
		return Details;
	}

	public void setDetails(String details) {
		Details = details;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int getMileageType() {
		return MileageType;
	}

	public void setMileageType(int mileageType) {
		MileageType = mileageType;
	}

}
