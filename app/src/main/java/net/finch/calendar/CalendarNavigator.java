package net.finch.calendar;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
//import android.support.annotation.NonNull;
//import android.support.annotation.RequiresApi;
import android.util.Log;

import androidx.annotation.RequiresApi;

import net.finch.calendar.Marks.DBMarks;
import net.finch.calendar.Marks.Mark;
import net.finch.calendar.Schedules.DBSchedules;
import net.finch.calendar.Schedules.Schedule;
import net.finch.calendar.Schedules.ScheduleNav;
import net.finch.calendar.Schedules.Shift;
import net.finch.calendar.Settings.SDLSettings;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static net.finch.calendar.CalendarVM.TAG;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CalendarNavigator
{
	private DBMarks dbMarks;
	private DBSchedules dbSDLs;
	private ArrayList<Schedule> prefSDLs;
	private Calendar initDate;
	private int month;
	private int year;
	private Map<Integer, ArrayList<Mark>> markDates;
//	private Map<Integer, ArrayList<Schedule>> sdlDates;
	private LinkedList<ScheduleNav> sdlList;

	public CalendarNavigator() {
		initDate = new GregorianCalendar();
		init();
	}
	
	public CalendarNavigator(Calendar initDate) {
		this.initDate = initDate;
		init();
	}

	public CalendarNavigator(int monthOffset) {
		initDate = new GregorianCalendar();
		initDate.add(Calendar.MONTH, monthOffset);
		init();
	}
	
	
	protected void init() {
		year = initDate.get(GregorianCalendar.YEAR);
		month = initDate.get(GregorianCalendar.MONTH);
	}

	protected void nextMonth() {
		initDate.add(GregorianCalendar.MONTH, 1);
		init();
	}
	protected void previousMonth() {
		initDate.add(GregorianCalendar.MONTH, -1);
		init();
	}

	public static Calendar getNow() {
		Calendar now = new GregorianCalendar();
		now.set(now.get(GregorianCalendar.YEAR)
				, now.get(GregorianCalendar.MONTH)
				, now.get(GregorianCalendar.DATE)
				, 0, 0, 0);
		return now;
	}


	protected int getMonth() {
		return initDate.get(GregorianCalendar.MONTH);
	}

	protected int getYear() {
		return initDate.get(GregorianCalendar.YEAR);
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

	protected ArrayList<DayInfo> frameOfDates() {
		int cnt = 0;
		int fwd = firstWeakDayOfMonth();
		int mda = initDate.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
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

		prefSDLs = null;
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

	protected void dbReadSdls(int offset) {
		if(dbSDLs == null) dbSDLs = new DBSchedules(MainActivity.getContext());
		if (prefSDLs == null) {
			try {
				prefSDLs = new SDLSettings(MainActivity.getContext()).getSdlArray();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		sdlList = new LinkedList<>();
		Calendar mc = new GregorianCalendar(year, month+offset,1);
		SQLiteDatabase db = dbSDLs.getWritableDatabase();
		@SuppressLint("Recycle") Cursor cur = db.query(DBSchedules.DB_NAME, null, "", null, null, null, null);

		ArrayList<Integer> sqlIdsToRemove = new ArrayList<>();
		if (cur.moveToFirst()) {

			do {
				boolean prime = cur.getInt(cur.getColumnIndex("prime")) == 1;
				Schedule sdl = getSdlByName(cur.getString(cur.getColumnIndex("name")));
				/////////////////////////////////////////////
//				Schedule sdl = new Schedule(
//						cur.getString(cur.getColumnIndex("name")),
//						cur.getString(cur.getColumnIndex("sdl"))
//						// TODO: Settings.getSdlByName("name") => return new Schedule(name, sdl, colorMap)!
//				);
				//////////////////////////////////////////////

				if (sdl != null) {
					Log.d(TAG, "dbReadSdls: addSdl > "+sdl.getName());
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
				}else sqlIdsToRemove.add(cur.getInt(cur.getColumnIndex("id")));


			}while (cur.moveToNext());
		}
		cur.close();
		db.close();
		for (Integer id : sqlIdsToRemove) {
			dbSDLs.delete(id);
		}
	}

	private Schedule getSdlByName(String name) {
		Log.d(TAG, "getSdlByName!: name = "+name);
		for (Schedule sdl : prefSDLs) {
			if (name.equals(sdl.getName())) return sdl;
		}
		Log.d(TAG, "getSdlByName!: --"+name);
		return null;
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
