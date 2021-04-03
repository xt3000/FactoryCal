package net.finch.calendar;
import java.util.*;

public class MyDate
{
	int id;
	Calendar cal;
	int monthOffset; // (-1,0,1)
	//int month;
	//int year;
	Boolean mark;
	
	public MyDate(int id, Calendar cal, int monthOffset, Boolean mark) {
		this.id = id;
		this.cal = cal;
		this.monthOffset = monthOffset;
		//this.year = cal.get(GregorianCalendar.YEAR);
		//this.month = cal.get(GregorianCalendar.MONTH);
		this.mark = mark;
		
		//Calendar c = new GregorianCalendar(year, m
	}
	
	
	int getId() {
		return id;
	}
	
	int getDate() {
		return cal.get(GregorianCalendar.DATE);
	}
	
	String getDateString() {
		return String.valueOf(getDate());
	}
	
	int getMonthOffset() {
		return monthOffset;
	}
	
	int getMonth() {
		return cal.get(GregorianCalendar.MONTH);
	}
	
	int getYear() {
		return cal.get(GregorianCalendar.YEAR);
	}
	
	Calendar getCalendar() {
		return cal;
	}

	String getFullDateString() {
		String d = String.valueOf(cal.get(GregorianCalendar.DATE));
		String m = String.valueOf(cal.get(GregorianCalendar.MONTH)+1);
		String y = String.valueOf(cal.get(GregorianCalendar.YEAR));

		if (Integer.valueOf(m) < 10) m = "0"+m;

		return  d +"."+ m +"."+ y;
	}
	
	boolean isMarked(){
		return mark;
	}
}
