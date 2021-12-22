package net.finch.calendar.Schedules;


import java.util.Calendar;

import static net.finch.calendar.Schedules.Shift.sftChr;


public class ScheduleNav {
    private final int db_id;
    private final Schedule sdl;
    private final boolean prime;
    private final Calendar startDate;

    public ScheduleNav(int db_id, Schedule sdl, boolean prime, Calendar startDate) {
        this.db_id = db_id;
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
        }

        return sdl.getSdl().toCharArray()[s-1];
    }

    public Shift getShift(Calendar tgtDate) {
        long deff = tgtDate.getTimeInMillis() - startDate.getTimeInMillis();
        int days = (int) (deff/86400000);
//        Log.d(TAG, "getShift: days = "+days);
        Character shift = getShiftSymbol(days);
//        Log.d(TAG, "getShift: "+shift);
        int sftId = 5;
        for (int i=0; i<sftChr.length; i++) {
            if (shift.equals(sftChr[i])) {
                sftId = i;
                break;
            }
        }

        return new Shift(db_id, sftId, sdl.getName(), sdl.getShiftColor(shift), prime);
    }
}
