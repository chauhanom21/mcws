package com.eclat.mcws.report.utility;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtil {
	
	public static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	public static final DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	public static final int ZERO = 0;
	private static final int ONE = 1;
	private static final int TWENTY_THIRD = 23;
	private static final int FIFTY_NINE = 59;
	private static final int NINE_NINE_NINE = 999;
	
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
	
	//convertMillisToHHMM
	public static String convertMillisToHHMM(Long millis) {
		if(millis != null ) {
			 String hhmm = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
			            TimeUnit.MILLISECONDS.toMinutes(millis) - 
			            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
			return hhmm;
		} 
		return "00:00";
	}
		
		
	//convertMillsToMins
	public static Integer convertMillsToMins(Long millis){
		Long mins = TimeUnit.MILLISECONDS.toMinutes(millis);
		return mins.intValue();
	}
	
	//convertMillsToMins
		public static Integer convertMillsecondToHours(Long millis){
			Long mins = TimeUnit.MILLISECONDS.toHours(millis);
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
	
	public static Date convertStringValueToDate(final String date){
		Calendar cal = new GregorianCalendar();
		if(date != null && date.length() > 0) {
			final String[] values = date.split(",");
			cal.set(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
		}  else {
			cal.add(Calendar.DATE, -ONE);
		}
		cal.set(Calendar.HOUR_OF_DAY, ZERO);
		cal.set(Calendar.MINUTE, ZERO);
		cal.set(Calendar.SECOND, ONE);
		return new Date(cal.getTimeInMillis());		
	}
	
	public static Timestamp convertStringValueToDateFormat(final String date){
		Calendar cal = new GregorianCalendar();
		if(date != null && date.length() > 0) {
			final String[] values = date.split("/");
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(values[1]));
			cal.set(Calendar.MONTH, Integer.parseInt(values[0])- ONE);
			cal.set(Calendar.YEAR, Integer.parseInt(values[2]));
		}  else {
			cal.add(Calendar.DATE, -ONE);
		}
		
		return new Timestamp(cal.getTimeInMillis());		
	}
	
	public static Timestamp constructFromDateValue(final Long fromDate){
		Calendar cal = new GregorianCalendar();
		if(fromDate != null) {
			cal.setTimeInMillis(fromDate);
			cal.set(Calendar.HOUR_OF_DAY, ZERO);
			cal.set(Calendar.MINUTE, ZERO);
			cal.set(Calendar.SECOND, ZERO);
			cal.set(Calendar.MILLISECOND, ONE);
		}  
		return new Timestamp(cal.getTimeInMillis());		
	}
	
	public static Timestamp constructEndDateValue(final Long endDate) {
		Calendar cal = new GregorianCalendar();
		if(endDate != null) {
			cal.setTimeInMillis(endDate);
			cal.set(Calendar.HOUR_OF_DAY, TWENTY_THIRD);
			cal.set(Calendar.MINUTE, FIFTY_NINE);
			cal.set(Calendar.SECOND, FIFTY_NINE);
			cal.set(Calendar.MILLISECOND, NINE_NINE_NINE);
		}  else {
			cal.set(Calendar.HOUR_OF_DAY, TWENTY_THIRD);
			cal.set(Calendar.MINUTE, FIFTY_NINE);
			cal.set(Calendar.SECOND, FIFTY_NINE);
			cal.set(Calendar.MILLISECOND, NINE_NINE_NINE);
		}
		return new Timestamp(cal.getTimeInMillis());		
	}
}
