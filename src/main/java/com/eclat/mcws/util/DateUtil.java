package com.eclat.mcws.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtil {
	
	public static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	public static final DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	public static String convertDateToString(Date date) {
		if(date == null)
			return null;
		else {
			return df.format(date);
		}
	}
	
	public static Date convertStrToDate(String date) {
		if(date == null)
			return null;
		else {
			try {
				return df.parse(date);
			} catch (ParseException e) {
				DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
				try {
					return formater.parse(date);
				} catch (ParseException pe) {
					return null;
				}
			}
		}			
	}	
	
	public static String fomatDate(String date) {
		if(date == null)
			return null;
		else {
			return df.format(convertStrToDate(date));
		}
	}
	
	//convertMinsToHHMM
	public static String convertMinsToHHMM(Integer minutes) {
		if(minutes != null ) {
			long hours = TimeUnit.MINUTES.toHours(minutes.longValue());
			long remainMinute = minutes.longValue() - TimeUnit.HOURS.toMinutes(hours);
			String hhmm = String.format("%02d", hours) + ":" 
			                    + String.format("%02d", Math.abs(remainMinute));
			
			return hhmm;
		} 
		return "00:00";
	}
	
	//convertMillsToMins
	public static Integer convertMillsToMins(Long millis){
		Long mins = TimeUnit.MILLISECONDS.toMinutes(millis);
		return mins.intValue();
	}
	
	//convertMillsToHours
	public static Integer convertMillsToHours(Long milliSeconds){
		Long mins = TimeUnit.MILLISECONDS.toHours(milliSeconds);
		return mins.intValue();
	}
	
	//convert Time in current JVM timezone to IST timezone
	public static String convertToTimeZone(Date date, String fromTZ, String toTZ) {

		// Construct FROM and TO TimeZone instances
		TimeZone fromTimeZone = TimeZone.getTimeZone(fromTZ);
		// Always set the TO TimeZone to IST ==> 'Asia/Calcutta'
		TimeZone toTimeZone = TimeZone.getTimeZone("Asia/Calcutta");

		// Get a Calendar instance using the default time zone and locale.
		Calendar calendar = Calendar.getInstance();

		// Set the calendar's time with the given date
		calendar.setTimeZone(fromTimeZone);
		calendar.setTime(date);

		System.out.println("Input: " + calendar.getTime() + " in " + fromTimeZone.getDisplayName());

		// FROM TimeZone to UTC
		calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);

		if (fromTimeZone.inDaylightTime(calendar.getTime())) {
			calendar.add(Calendar.MILLISECOND, calendar.getTimeZone()
					.getDSTSavings() * -1);
		}

		// UTC to TO TimeZone
		calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());

		if (toTimeZone.inDaylightTime(calendar.getTime())) {
			calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
		}
		
		return df2.format(calendar.getTime());

	}
	
	public static Long convertMinutesToMilliseconds(final Long mins){
		if(mins != null){
			return mins*60*1000;
		}
		return new Long(0);
	}
}
