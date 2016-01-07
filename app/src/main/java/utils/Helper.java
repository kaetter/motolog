package utils;


public class Helper {
	private static String thedate;
	public static String formatDate(int year, int month, int day ) {
		if (month+1<10) {
			thedate=year+"-0"+(month+1);
		} else {
			thedate=""+year+"-"+ (month+1);
		}
		if (day<10) {
			thedate=thedate+"-0"+day;
		} else {
			thedate=""+thedate+"-"+day;
		}
		return thedate;
		
	}
	
	
	
}
