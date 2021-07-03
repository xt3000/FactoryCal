package net.finch.calendar;

import android.text.format.DateFormat;
import android.util.Log;

import static net.finch.calendar.CalendarVM.TAG;

public class Time {
    final static int NULLTIME = 1500;
    private static boolean PM = false;

    public static int toInt(String strTime) {
        Log.d(TAG, "toInt: "+strTime);
        if (strTime.equals("- - : - -")) return NULLTIME;
        if (strTime.endsWith("M")) {
            if (strTime.endsWith("PM")) PM = true;
            strTime = strTime.split(" ")[0];
        }else PM = false;

        String[] t = strTime.split(":");
        int h = Integer.parseInt(t[0]);
        if (PM && h!=12) h += 12;
        int m = Integer.parseInt(t[1]);

        return (h*60)+m;
    }

    public static String toStr(int intTime) {
        String pref = "";
        if (intTime == NULLTIME) return " ";
        int h = intTime/60;
        int m = intTime%60;

        if (!DateFormat.is24HourFormat(MainActivity.getContext())) {
            if (h <= 12) pref = " AM";
            else {
                pref = " PM";
                h -= 12;
            }
        }

        return conv(h) + ":" + conv(m) + pref;
    }

    private static String conv(int i) {
        if (i<10) return "0"+i;
        else return ""+i;
    }


}
