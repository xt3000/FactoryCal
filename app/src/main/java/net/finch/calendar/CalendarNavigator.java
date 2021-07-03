package net.finch.calendar;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import net.finch.calendar.Marks.DBMarks;
import net.finch.calendar.Marks.Mark;
import net.finch.calendar.Schedules.DBSchedules;
import net.finch.calendar.Schedules.Schedule;
import net.finch.calendar.Schedules.ScheduleNav;
import net.finch.calendar.Schedules.Shift;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static net.finch.calendar.CalendarVM.TAG;


public class CalendarNavigator
{
	private DBMarks dbMarks;
	private DBSchedules dbSDLs;
	private Calendar c;
	private int month;
	private int year;
	private Map<Integer, ArrayList<Mark>> markDates;
//	private Map<Integer, ArrayList<Schedule>> sdlDates;
	private LinkedList<ScheduleNav> sdlList;

	public CalendarNavigator() {
		c = new GregorianCalendar();
		init();
	}
	
	public CalendarNavigator(int y, int m, int d) {
		c = new GregorianCalendar(y, m, d);
		init();
	}
	
	
	protected void init() {
		year = c.get(GregorianCalendar.YEAR);
		month = c.get(GregorianCalendar.MONTH);
		
		
	}

	protected void nextMonth() {
		c.add(GregorianCalendar.MONTH, 1);
		init();
	}
	protected void previousMonth() {
		c.add(GregorianCalendar.MONTH, -1);
		init();
	}

	protected Calendar getNow() {
		Calendar now = new GregorianCalendar();
		now.set(now.get(GregorianCalendar.YEAR)
				, now.get(GregorianCalendar.MONTH)
				, now.get(GregorianCalendar.DATE)
				, 0, 0, 0);
		return now;
	}


	protected int getMonth() {
		return c.get(GregorianCalendar.MONTH);
	}

	protected int getYear() {
		return c.get(GregorianCalendar.YEAR);
	}

	protected int firstWeakDayOfMonth() {
		Calendar c1 = new GregorianCalendar(year, month, 1);
		int day = c1.get(GregorianCalendar.DAY_OF_WEEK);
		if(day > 1) day--;
		else day = 7;
		
		return day;
	}

	protected int maxDateInPreviousMonth() {
		Calendar cpm = new GregorianCalendar(year, month-1, 1);
		
		return cpm.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	protected ArrayList<DayInfo> frameOfDates() {
		int cnt = 0;
		int fwd = firstWeakDayOfMonth();
		int mda = c.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		int mdp = maxDateInPreviousMonth();
		ArrayList<DayInfo> fod = new ArrayList<>();
		
		dbReadMarks(-1);
		dbReadSdls(-1);
//		Log.d(TAG, "frameOfDates: sdlList_size = "+ sdlList.size());
		if (fwd > 1) {
			int firstDate = mdp-fwd+2;
			for (int i=0; i<fwd-1; i++) {
				Calendar tgtDay = new GregorianCalendar(year, month-1, firstDate+i);
				fod.add(new DayInfo(
						cnt,
						tgtDay,
						-1,
						markDates.get(firstDate+i),
						getDayShifts(tgtDay)
				));
				cnt++;
			}
		}
		
		dbReadMarks(0);
		dbReadSdls(0);
//		Log.d(TAG, "frameOfDates: sdlList_size = "+sdlList.size());
		for (int i=1; i<=mda; i++) {
			Calendar tgtDay = new GregorianCalendar(year, month, i);
			fod.add(new DayInfo(
					cnt,
					tgtDay,
					0,
					markDates.get(i),
					getDayShifts(tgtDay)
			));
			cnt++;
		}
		
		dbReadMarks(1);
		dbReadSdls(1);
//		Log.d(TAG, "frameOfDates: sdlList_size = "+sdlList.size());
		for (int i=1; i<=(42-cnt); i++) {
			Calendar tgtDay = new GregorianCalendar(year, month+1, i);
			fod.add(new DayInfo(
					cnt+i-1,
					tgtDay,
					1,
					markDates.get(i),
					getDayShifts(tgtDay)
			));
		}
		return fod;
	}


	protected void dbReadMarks(int offset) {
		if(dbMarks == null) dbMarks = new DBMarks(MainActivity.getContext());
		markDates = new HashMap<>();
		Calendar mc = new GregorianCalendar(year, month+offset,1);
		SQLiteDatabase db = dbMarks.getWritableDatabase();

		String select = "year = ? and month = ?";
		String[] selArgs = {String.valueOf(mc.get(GregorianCalendar.YEAR)), String.valueOf(mc.get(GregorianCalendar.MONTH))};
		Cursor cur = db.query(DBMarks.DB_NAME, null, select, selArgs, null, null, null);
		
		int d;

		ArrayList<Mark> markList;
		if (cur.moveToFirst()) {
			do {
				d = cur.getInt(cur.getColumnIndex("date"));
				int time = cur.getInt(cur.getColumnIndex("time"));
				String info = cur.getString(cur.getColumnIndex("note"));
				int db_id = cur.getInt(cur.getColumnIndex("id"));
				Mark ilItem = new Mark(db_id, Time.toStr(time), info);

				if (markDates.containsKey(d)) {
					markList = markDates.get(d);
					markList.add(ilItem);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						markDates.replace(d, markList);
					}
				}else {
					markList = new ArrayList<>();
					markList.add(ilItem);
					markDates.put(d, markList);
				}

			}while(cur.moveToNext());

		}
		cur.close();
		db.close();
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	protected void dbReadSdls(int offset) {
		if(dbSDLs == null) dbSDLs = new DBSchedules(MainActivity.getContext());
//		Map<Integer, ArrayList<Schedule>> sdlDates = new HashMap<>();
		sdlList = new LinkedList<>();
		Calendar mc = new GregorianCalendar(year, month+offset,1);
		SQLiteDatabase db = dbSDLs.getWritableDatabase();

//		String select = "(year = ? and month <= ?) or (year < ?)";
//		String[] selArgs = {String.valueOf(mc.get(GregorianCalendar.YEAR)), String.valueOf(mc.get(GregorianCalendar.MONTH)), String.valueOf(mc.get(GregorianCalendar.YEAR))};
		@SuppressLint("Recycle") Cursor cur = db.query(DBSchedules.DB_NAME, null, "", null, null, null, null);


		if (cur.moveToFirst()) {

			do {
				boolean prime = cur.getInt(cur.getColumnIndex("prime")) == 1;
				Schedule sdl = new Schedule(
						cur.getString(cur.getColumnIndex("name")),
						cur.getString(cur.getColumnIndex("sdl"))
				);
				ScheduleNav sdlNav = new ScheduleNav(
						cur.getInt(cur.getColumnIndex("id")),
						sdl,
						prime,
						new GregorianCalendar(
							cur.getInt(cur.getColumnIndex("year")),
							cur.getInt(cur.getColumnIndex("month")),
							cur.getInt(cur.getColumnIndex("date")
						)
				));
				if (prime) sdlList.addFirst(sdlNav);
				else sdlList.add(sdlNav);

			}while (cur.moveToNext());
		}
		cur.close();
		db.close();
	}

	protected ArrayList<Shift> getDayShifts(Calendar tgtDate) {
		if (sdlList.size() == 0) return null;
		ArrayList<Shift> dayShifts = new ArrayList<>();
		for (ScheduleNav sdlNav : sdlList) {
			dayShifts.add(sdlNav.getShift(tgtDate));
		}
		return dayShifts;
	}
}
