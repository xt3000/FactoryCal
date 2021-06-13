package net.finch.calendar.Schedules;

import android.util.Log;

import java.util.Calendar;

import static net.finch.calendar.CalendarVM.TAG;

public class ScheduleNav {
    private Schedule sdl;
    private boolean prime;
    private Calendar startDate;

    public ScheduleNav(Schedule sdl, boolean prime, Calendar startDate) {
        this.sdl = sdl;
        this.prime = prime;
        this.startDate = startDate;
    }


    public int length() {
        return sdl.getSdl().length();
    }

    public char getShiftSymbol(int offset) {

        int s = Math.abs(offset) % length();
        if (offset < 0) {
            if (s == 0) s = 1;
            else s = length() + 1 - s;
        }else if (offset == 0){
            s = 1;
        }else {
            s++;
//            if (s == 0) s++;       //s = length();
//            else s++;
        }

        return sdl.getSdl().toCharArray()[s-1];
    }

    public boolean isPrime() {
        return prime;
    }

    public Shift getShift(Calendar tgtDate) {
        long deff = tgtDate.getTimeInMillis() - startDate.getTimeInMillis();
        int days = (int) (deff/86400000);
//        Log.d(TAG, "getShift: days = "+days);
        char shift = getShiftSymbol(days);
//        Log.d(TAG, "getShift: "+shift);

        return new Shift(shift, sdl.getName(), sdl.getShiftColor(shift));
    }
}
