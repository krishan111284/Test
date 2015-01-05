package com.dineout.search.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class DateUtil {
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static Logger logger = Logger.getLogger(DateUtil.class);
	
	public static String getTodaysDate(){
		
		return DATE_FORMAT.format(new Date());
	}
	
	public static String getTomorrowssDate(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		return DATE_FORMAT.format(cal.getTime());
	}
	
	public static String getComingFridayDate(){
		
		
		return (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY)?DATE_FORMAT.format(new Date()):DATE_FORMAT.format(nextDayOfWeek(Calendar.FRIDAY).getTime());
	}
	
	public static String getComingSundayDate(){
		
		return DATE_FORMAT.format(nextDayOfWeek(Calendar.SUNDAY).getTime());
	}
	
	public static String getComingMondayDate(){
		
		return DATE_FORMAT.format(nextDayOfWeek(Calendar.MONDAY).getTime());
	}
	
	public static String getNextWeeksFriday(){
		 Calendar cal = Calendar.getInstance();
		 if(Calendar.SATURDAY == cal.get(Calendar.DAY_OF_WEEK) || Calendar.SUNDAY == cal.get(Calendar.DAY_OF_WEEK ) || Calendar.FRIDAY == cal.get(Calendar.DAY_OF_WEEK)){
			cal = nextDayOfWeek(Calendar.FRIDAY);
		 }else{
			 cal = nextDayOfWeek(Calendar.FRIDAY);
			 cal.add(Calendar.DATE, 7);
		 }
		 
		return DATE_FORMAT.format(cal.getTime());
	}
	public static String getDay(String date){
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(DATE_FORMAT.parse(date));
		} catch (ParseException e) {
			logger.error(e.getMessage(),e);
		}
		return new Integer(cal.get(Calendar.DAY_OF_WEEK)).toString();
	}
	
	public static String getNextDate(String date){
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(DATE_FORMAT.parse(date));
		} catch (ParseException e) {
			logger.error(e.getMessage(),e);
		}
		cal.add(Calendar.DATE, 1);
		return DATE_FORMAT.format(cal.getTime());
	}
	
	public static String getPreviousDate(String date){
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(DATE_FORMAT.parse(date));
		} catch (ParseException e) {
			logger.error(e.getMessage(),e);
		}
		cal.add(Calendar.DATE, -1);
		return DATE_FORMAT.format(cal.getTime());
		}
	 public static Calendar nextDayOfWeek(int dow) {
	        Calendar date = Calendar.getInstance();
	        int diff = dow - date.get(Calendar.DAY_OF_WEEK);
	        if (!(diff > 0)) {
	            diff += 7;
	        }
	        date.add(Calendar.DAY_OF_MONTH, diff);
	        return date;
	    }
	 
}
