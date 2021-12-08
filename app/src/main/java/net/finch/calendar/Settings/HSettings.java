package net.finch.calendar.Settings;

import android.content.Context;
import android.util.Log;

import static net.finch.calendar.CalendarVM.TAG;

public class HSettings extends Settings {
    public final static int RDC_MAX = 5;
    private final String RDC = HIDDEN+"_rdc";

    public HSettings(Context context) {
        super(context);
    }


    public void rdcAdd() {
        int rdc = getRdc();

        if (rdc == 0) return;
        else if (rdc >= RDC_MAX) rdc = 1;
        else rdc++;
        editor = prefs.edit();
        editor.putInt(RDC, rdc).apply();
    }

    public int getRdc() {
        int rdc = prefs.getInt(RDC, 1);
        Log.d(TAG, "getRdc: rdc = "+ rdc);
        return rdc;
    }

    public void rdcZero() {
        editor = prefs.edit();
        editor.putInt(RDC, 0).apply();
    }
}
