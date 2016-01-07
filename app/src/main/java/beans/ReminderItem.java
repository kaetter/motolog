package beans;

import java.io.Serializable;

public class ReminderItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7552815792716092868L;
	String vehicle, maintElem, maintType, interval, intervalSize, lastInterval,
			nextInterval, details, dateInserted;
	int key, reminderType;

	public ReminderItem(int key, String vehicle, String maintElem,
			String maintType, int reminderType, String interval,
			String intervalSize, String lastInterval, String nextInterval,
			String details, String dateInserted) {
		super();
		this.vehicle = vehicle;
		this.maintElem = maintElem;
		this.maintType = maintType;
		this.interval = interval;
		this.intervalSize = intervalSize;
		this.lastInterval = lastInterval;
		this.nextInterval = nextInterval;
		this.details = details;
		this.dateInserted = dateInserted;
		this.key = key;
		this.reminderType = reminderType;
	}

	public ReminderItem(String vehicle, String maintElem, String maintType,
			int reminderType, String interval, String intervalSize,
			String lastInterval, String nextInterval, String details,
			String dateInserted) {
		super();
		this.vehicle = vehicle;
		this.maintElem = maintElem;
		this.maintType = maintType;
		this.interval = interval;
		this.intervalSize = intervalSize;
		this.lastInterval = lastInterval;
		this.nextInterval = nextInterval;
		this.details = details;
		this.dateInserted = dateInserted;
		this.reminderType = reminderType;
	}

	public ReminderItem(String vehicle, String maintElem, String maintType,
			int reminderType, String interval, String intervalSize,
			String lastInterval, String details, String dateInserted) {
		super();
		this.vehicle = vehicle;
		this.maintElem = maintElem;
		this.maintType = maintType;
		this.interval = interval;
		this.intervalSize = intervalSize;
		this.lastInterval = lastInterval;
		this.details = details;
		this.dateInserted = dateInserted;
		this.reminderType = reminderType;
	}

	public String getLastInterval() {
		return lastInterval;
	}

	public void setLastInterval(String lastInterval) {
		this.lastInterval = lastInterval;
	}

	public String getVehicle() {
		return vehicle;
	}

	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}

	public String getMaintElem() {
		return maintElem;
	}

	public void setMaintElem(String maintElem) {
		this.maintElem = maintElem;
	}

	public String getMaintType() {
		return maintType;
	}

	public void setMaintType(String maintType) {
		this.maintType = maintType;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public String getNextInterval() {
		return nextInterval;
	}

	public void setNextInterval(String nextInterval) {
		this.nextInterval = nextInterval;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getDateInserted() {
		return dateInserted;
	}

	public void setDateInserted(String dateInserted) {
		this.dateInserted = dateInserted;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int getReminderType() {
		return reminderType;
	}

	public void setReminderType(int reminderType) {
		this.reminderType = reminderType;
	}

	public String getIntervalSize() {
		return intervalSize;
	}

	public void setIntervalSize(String intervalSize) {
		this.intervalSize = intervalSize;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String toString = new String(vehicle + " vehicle  " + maintElem
				+ " maintElem " + maintType + " maintType " + interval
				+ " interval " + intervalSize + " intervalSize " + lastInterval
				+ "lastInterval   " + nextInterval + " nextInterval " + details
				+ " details " + dateInserted + " " + key + " " + reminderType);
		return toString;
	}

}
