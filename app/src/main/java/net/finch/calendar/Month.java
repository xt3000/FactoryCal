package net.finch.calendar;

import android.content.Context;

public class Month {
	static String[] month;


	public Month(Context ctx) {
		month = ctx.getResources().getStringArray(R.array.month);
	}
	
	public String getString(int m) {
		return month[m];
	}
}
