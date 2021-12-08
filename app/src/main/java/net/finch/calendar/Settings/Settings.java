package net.finch.calendar.Settings;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    protected static final int MODE = Context.MODE_PRIVATE;
    protected static final String OPTIONS = "OPTIONS";
    protected static final String SCHEDULES = "schedules";
    protected static final String MARKS = "marks";
    protected static final String MAIN = "main";
    protected static final String HIDDEN = "hidden";

    protected final Context context;
    protected final SharedPreferences prefs;
    protected SharedPreferences.Editor editor;

    public Settings(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(OPTIONS, MODE);
    }
}
