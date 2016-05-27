package com.dineout.search.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class DODateUtil {
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static Logger logger = Logger.getLogger(DODateUtil.class);

	public static String getTodaysDate(){
		return DATE_FORMAT.format(new Date());
	}

	public static String getTomorrowsDate(){
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

	public static Map<String,Set<?>> getBetweenDatesDays(Date startDate, Date endDate)
	{
		Map<String, Set<?>> dayDatesMap = new HashMap<String, Set<?>>();
		Set<Date> dates = new HashSet<Date>();
		Set<Integer> days = new HashSet<Integer>();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		int dayOfWeek;
		if(endDate.compareTo(startDate)==0){
			dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			days.add(dayOfWeek);
			dates.add(startDate);
		}
		else{
			while (calendar.getTime().before(endDate))
			{
				Date result = calendar.getTime();
				dates.add(result);
				dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
				days.add(dayOfWeek);
				calendar.add(Calendar.DATE, 1);
			}
		}
		dayDatesMap.put("dates", dates);
		dayDatesMap.put("days", days);
		return dayDatesMap;
	}

	public static Date getStringToDate(String dateString){
		Date dateDate = null;
		try {
			dateDate = DATE_FORMAT.parse(dateString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateDate;
	}

	public static String getDateToString(Date dateDate){
		String stringDate = null;
		try {
			stringDate = DATE_FORMAT.format(dateDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringDate;
	}

	public static long getCurrentTimeInMinutes(){
		Calendar c = Calendar.getInstance();
		long now = c.getTimeInMillis();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long passed = now - c.getTimeInMillis();
		long secondsPassed = passed / 1000;
		long minutesPassed = secondsPassed / 60;
		return minutesPassed;
	}

}
