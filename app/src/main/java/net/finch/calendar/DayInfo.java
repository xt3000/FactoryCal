package net.finch.calendar;
import android.util.Log;

import net.finch.calendar.Marks.Mark;
import net.finch.calendar.Schedules.Shift;

import java.util.*;

import static net.finch.calendar.CalendarVM.TAG;

public class DayInfo
{
	private final int id;
	private final Calendar cal;
	private final int monthOffset; // (-1,0,1)
	//int month;
	//int year;
	private final Boolean mark;
	private final Boolean shift;
	private final ArrayList<Mark> markList;
	private final ArrayList<Shift> shiftList;

	public DayInfo(int id, Calendar cal, int monthOffset, ArrayList<Mark> markList, ArrayList<Shift> shiftList) {
//		Log.d(TAG, "DayInfo: id = "+id);
		this.id = id;
		this.cal = cal;
		this.monthOffset = monthOffset;

		//this.year = cal.get(GregorianCalendar.YEAR);
		//this.month = cal.get(GregorianCalendar.MONTH);

		if (markList != null) {
			this.markList = markList;
			this.mark = true;
		}else {
			this.markList = new ArrayList<>();
			this.mark = false;
		}

		if (shiftList != null) {
//			Log.d(TAG, "DayInfo: shiftList = NOT null; size = "+ shiftList.size());
			this.shiftList = shiftList;
			this.shift = true;
		}else {
//			Log.d(TAG, "DayInfo: shiftList = null");
			this.shiftList = new ArrayList<>();
			this.shift = false;
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

		if (Integer.parseInt(m) < 10) m = "0"+m;

		return  d +"."+ m +"."+ y;
	}

	public boolean isMarked(){
		return mark;
	}
	public boolean isShifted() {
		return shift;
	}

	public ArrayList<Mark> getMarkList() {
		return markList;
	}
	public ArrayList<Shift> getShiftList() {
		return shiftList;
	}

}
