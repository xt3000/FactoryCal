package net.finch.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.finch.calendar.Schedules.Schedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static net.finch.calendar.CalendarVM.TAG;

public class Settings {
    private final Context context;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;


    private static final String OPTIONS = "OPTIONS";
    private static final String SCHEDULES = "schedules";
    private static final String MARKS = "marks";

    private static final int MODE = Context.MODE_PRIVATE;


    @SuppressLint("CommitPrefEdits")
    public Settings(Context context) {
        this.context = context;
    }



    public void saveSchedule(JSONObject Jsdl) throws JSONException {
        JSONArray SDLarr = getJSONsdls();
        SDLarr.put(Jsdl);
        Log.d(TAG, "saveSchedule: savedJSON: "+SDLarr);
        editor = context.getSharedPreferences(OPTIONS, MODE).edit();
        editor.putString(SCHEDULES, SDLarr.toString()).apply();
    }

    public JSONArray getJSONsdls() throws JSONException {
        prefs = context.getSharedPreferences(OPTIONS, MODE);
        String sdls = prefs.getString(SCHEDULES, "");
        if (!sdls.equals("")) return new JSONArray(sdls);
        return new JSONArray();
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
