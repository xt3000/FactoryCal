package net.finch.calendar;
import java.util.*;

public class DayInfo
{
	private int id;
	private Calendar cal;
	private int monthOffset; // (-1,0,1)
	//int month;
	//int year;
	private Boolean mark;
	private ArrayList<MarkItem> infoList;
	
	public DayInfo(int id, Calendar cal, int monthOffset, ArrayList<MarkItem> infoList) {
		this.id = id;
		this.cal = cal;
		this.monthOffset = monthOffset;
		//this.year = cal.get(GregorianCalendar.YEAR);
		//this.month = cal.get(GregorianCalendar.MONTH);

		if (infoList != null) {
			this.infoList = infoList;
			this.mark = true;
		}else {
			this.infoList = new ArrayList<>();
			this.mark = false;
		}
		
		//Calendar c = new GregorianCalendar(year, m
	}


	public int getId() {
		return id;
	}

	public int getDate() {
		return cal.get(GregorianCalendar.DATE);
	}

	public String getDateString() {
		return String.valueOf(getDate());
	}

	public int getMonthOffset() {
		return monthOffset;
	}

	public int getMonth() {
		return cal.get(GregorianCalendar.MONTH);
	}

	public int getYear() {
		return cal.get(GregorianCalendar.YEAR);
	}

	public Calendar getCalendar() {
		return cal;
	}

	public String getFullDateString() {
		String d = String.valueOf(cal.get(GregorianCalendar.DATE));
		String m = String.valueOf(cal.get(GregorianCalendar.MONTH)+1);
		String y = String.valueOf(cal.get(GregorianCalendar.YEAR));

		if (Integer.valueOf(m) < 10) m = "0"+m;

		return  d +"."+ m +"."+ y;
	}

	public boolean isMarked(){
		return mark;
	}

	public ArrayList<MarkItem> getInfoList() {
		return infoList;
	}
}
