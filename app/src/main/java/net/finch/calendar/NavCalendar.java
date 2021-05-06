package net.finch.calendar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


public class NavCalendar
{
	private DBHelper dbHelper;
//	private final Context context;
	 Calendar c;
	private int month;
	private int year;
	Map<Integer, ArrayList<MarkItem>> markDates;

	public NavCalendar() {
		c = new GregorianCalendar();
		init();
	}
	
	public NavCalendar(int y, int m, int d) {
		c = new GregorianCalendar(y, m, d);
		init();
	}
	
	
	 void init() {
		year = c.get(GregorianCalendar.YEAR);
		month = c.get(GregorianCalendar.MONTH);
		
		
	}
	
	 void nextMonth() {
		c.add(GregorianCalendar.MONTH, 1);
		init();
	}
	
	 void previousMonth() {
		c.add(GregorianCalendar.MONTH, -1);
		init();
	}
	
	
	 Calendar getNow() {
		Calendar now = new GregorianCalendar();
		now.set(now.get(GregorianCalendar.YEAR)
				, now.get(GregorianCalendar.MONTH)
				, now.get(GregorianCalendar.DATE)
				, 0, 0, 0);
		return now;
	}
	 int getMonth() {
		return c.get(GregorianCalendar.MONTH);
	}
	
	 int getYear() {
		return c.get(GregorianCalendar.YEAR);
	}
	
	 int firstWeakDayOfMonth() {
		Calendar c1 = new GregorianCalendar(year, month, 1);
		int day = c1.get(GregorianCalendar.DAY_OF_WEEK);
		if(day > 1) day--;
		else day = 7;
		
		return day;
	}
	
	 int maxDateInPreviousMonth() {
		Calendar cpm = new GregorianCalendar(year, month-1, 1);
		
		return cpm.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
	}
	
	 ArrayList<DayInfo> frameOfDates() {
		//SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		
//		boolean mark;
		int cnt = 0;
		int fwd = firstWeakDayOfMonth();
		int mda = c.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		int mdp = maxDateInPreviousMonth();
		ArrayList<DayInfo> fod = new ArrayList<>();
		
		dbReadMarkedDates(-1);
		if (fwd > 1) {
			int firstDate = mdp-fwd+2;
			for (int i=0; i<fwd-1; i++) {
//				mark = false;
//				if (markDates.containsKey(firstDate+i)) mark=true;
//				for (int m=0; m<markDates.size(); m++) {
//					if (firstDate+i == markDates.get(m)) mark = true;
//				}
				fod.add(new DayInfo(cnt, new GregorianCalendar(year, month-1, firstDate+i), -1, markDates.get(firstDate+i)));
				cnt++;
			}
		}
		
		dbReadMarkedDates(0);
		for (int i=1; i<=mda; i++) {
//			mark=false;
//			if (markDates.containsKey(i)) mark=true;
//			for (int m=0; m<markDates.size(); m++) {
//				if (i == markDates.get(m)) mark = true;
//			}
			fod.add(new DayInfo(cnt, new GregorianCalendar(year, month, i), 0, markDates.get(i)));
			cnt++;
		}
		
		dbReadMarkedDates(1);
		for (int i=1; i<=(42-cnt); i++) {
//			mark = false;
//			if (markDates.containsKey(i)) mark=true;
//			for (int m=0; m<markDates.size(); m++) {
//				if (i == markDates.get(m)) mark = true;
//			}
			fod.add(new DayInfo(cnt+i-1, new GregorianCalendar(year, month+1, i), 1, markDates.get(i)));
		}
		return fod;
	}


	 void dbReadMarkedDates(int offset) {
		if(dbHelper == null) dbHelper = new DBHelper(MainActivity.getContext());
		markDates = new HashMap<>();
		Calendar mc = new GregorianCalendar(year, month+offset,1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String select = "year = ? and month = ?";
		String[] selArgs = {String.valueOf(mc.get(GregorianCalendar.YEAR)), String.valueOf(mc.get(GregorianCalendar.MONTH))};
		Cursor cur = db.query(DBHelper.DB_NAME, null, select, selArgs, null, null, null);
		
		int n;

		ArrayList<MarkItem> infoList;
		if (cur.moveToFirst()) {
			do {
				n = cur.getInt(cur.getColumnIndex("date"));
				int time = cur.getInt(cur.getColumnIndex("time"));
				String info = cur.getString(cur.getColumnIndex("note"));
				MarkItem mi = new MarkItem(time, info);

				if (markDates.containsKey(n)) {
					infoList = markDates.get(n);
					infoList.add(mi);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						markDates.replace(n, infoList);
					}
				}else {
					infoList = new ArrayList<>();
					infoList.add(mi);
					markDates.put(n, infoList);
				}

			}while(cur.moveToNext());

		}
		cur.close();
		db.close();
	}
}
