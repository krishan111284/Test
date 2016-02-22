package com.dineout.search.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class IndexUtils {
	private static final long minute = 60000l;

	static DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");	

	public static String[] getIndexes(int timing){
		Calendar c = Calendar.getInstance();
		List<String> indexes = new ArrayList<String>();
		switch(timing){
		case 1:
			//30 mins
		case 2:	
			//1 hr
		case 3:
			//6 hrs
		case 4:
			//12 hrs
		case 5:
			//1 day
			indexes.add("logstash-"+dateFormat.format(c.getTime()));
			break;

		case 6:
			//last 2 days
			indexes.add("logstash-"+dateFormat.format(c.getTime()));
			c.setTime(new Date());			
			c.add(Calendar.DATE, -1);
			indexes.add("logstash-"+dateFormat.format(c.getTime()));
			break;

		case 7:
			//last 7 days
			c.setTime(new Date());			
			c.add(Calendar.DATE, - 6);
			for(int j=0;j<7;j++){	
				indexes.add("logstash-"+dateFormat.format(c.getTime()));
				c.add(Calendar.DATE, 1);
			}
			break;
		}

		String[] indexArray = new String[indexes.size()];
		indexArray = indexes.toArray(indexArray);
		return indexArray;
	}

	public static long[] getstartEndTime(long startTime, int bytiming){
		Date endDate = new Date(startTime - (15 * minute));
		long[] estimings = new long[] {startTime, endDate.getTime()};

		switch(bytiming){
		case 1:
			//30 mins
			endDate = new Date(startTime - (30 * minute));
			estimings[1] = endDate.getTime();
			break;
		case 2:	
			//1 hr
			endDate = new Date(startTime - (60 * minute));
			estimings[1] = endDate.getTime();
			break;
		case 3:
			//6 hrs
			endDate = new Date(startTime - (60 * 6 * minute));
			estimings[1] = endDate.getTime();
			break;
		case 4:
			//12 hrs
			endDate = new Date(startTime - (60 * 12 * minute));
			estimings[1] = endDate.getTime();
			break;
		case 5:
			//1 day
			endDate = new Date(startTime - (60 * 24 * minute));
			estimings[1] = endDate.getTime();
			break;

		case 6:
			//last 2 days
			endDate = new Date(startTime - (60 * 48 * minute));
			estimings[1] = endDate.getTime();
			break;

		case 7:
			//last 7 days
			endDate = new Date(startTime - (60 * 24 * 7 * minute));
			estimings[1] = endDate.getTime();
			break;
		}
		return estimings;	
	}
}
