package net.finch.calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.finch.calendar.Schedules.Schedule;

import org.json.JSONException;

public class Settings {
    private final Context context;
    private SharedPreferences.Editor editor;


    private static final String OPTIONS = "OPTIONS";
    private static final String SCHEDULES = "schedules";
    private static final String MARKS = "marks";

    private static final int MODE = Context.MODE_PRIVATE;




    Settings(Context context) {
        this.context = context;
    }

    public void saveSchedule(Schedule sdl) throws JSONException {
        SharedPreferences.Editor editor = context.getSharedPreferences(OPTIONS, MODE).edit();
        editor.putString(SCHEDULES, sdl.serialize()).apply();
    }



    //****** вспомогательные методы ******
//    protected String serialize(Schedule sdl) {
//        Gson gson = new GsonBuilder()
//                .setPrettyPrinting()
//                .create();
//        String sdlString = gson.toJson(sdl);
//        Log.d(CalendarVM.TAG, "serialize: "+sdlString);
//
//        return  sdlString;
//    }
}
