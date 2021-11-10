package net.finch.calendar.Settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import net.finch.calendar.R;
import net.finch.calendar.Result;
import net.finch.calendar.Schedules.Schedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static net.finch.calendar.CalendarVM.TAG;

public class SDLSettings {
    private static final String OPTIONS = "OPTIONS";
    private static final String SCHEDULES = "schedules";
    private static final String MARKS = "marks";
    private static final int MODE = Context.MODE_PRIVATE;

    private final Context context;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;


    public SDLSettings(Context context) {
        this.context = context;
    }



    public Result removeSchedule(Schedule sdl) throws JSONException {
        editor = context.getSharedPreferences(OPTIONS, MODE).edit();
        Integer sdlID = isExistsSdl(sdl.getName());
//        ArrayList<Schedule> sdlList = getSdlArray();
        JSONArray sdlArr = getJSONsdls();
        if (sdlID != null) {
            sdlArr.remove(sdlID);
        }
        editor.putString(SCHEDULES, sdlArr.toString()).apply();

        return new Result(Result.OK);
    }

    public Result saveSchedule(Schedule sdl) throws JSONException {
        editor = context.getSharedPreferences(OPTIONS, MODE).edit();
        Integer existsSdlId = isExistsSdl(sdl.getName());
        JSONArray sdlArr = new JSONArray();
        if (existsSdlId == null) {
            sdlArr = getJSONsdls();
            sdlArr.put(sdl.serialize());
        }else {
            ArrayList<Schedule> sdlList = getSdlArray();
            sdlList.set(existsSdlId, sdl);

            for (Schedule s : sdlList) {
                sdlArr.put(s.serialize());
            }
        }
        editor.putString(SCHEDULES, sdlArr.toString()).apply();

        return new Result(Result.OK);
    }

    private JSONArray getJSONsdls() throws JSONException {
        prefs = context.getSharedPreferences(OPTIONS, MODE);
        String sdls = prefs.getString(SCHEDULES, "");
        if (sdls != null && !sdls.equals(""))return new JSONArray(sdls);
        return new JSONArray();
    }

    public ArrayList<String> getSdlNames() throws JSONException {
        JSONArray sdlArr = getJSONsdls();
        ArrayList<String> sdlNamesArr = new ArrayList<>();
        for (int i=0; i<sdlArr.length(); i++) {
            sdlNamesArr.add(sdlArr.getJSONObject(i).getString("name"));
        }

        return sdlNamesArr;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Schedule> getSdlArray() throws JSONException {
        ArrayList<Schedule> sdlArr = new ArrayList<>();
        JSONArray jsonSdlsArr = getJSONsdls();
        if (jsonSdlsArr.length() == 0) {
            Schedule defSdl = new Schedule("default", "UNWW");
            defSdl.setId(0);
            saveSchedule(defSdl);
            sdlArr.add(defSdl);
            return sdlArr;
        }

        for (int i=0; i<jsonSdlsArr.length(); i++) {
            sdlArr.add(new Schedule(jsonSdlsArr.getJSONObject(i)));
        }
        return sdlArr;
    }

    public int getNewSdlId() throws JSONException {
        JSONArray sdlArr = getJSONsdls();
        int id = 0;
        while(isUsedId(sdlArr, id)) {
            id++;
        }
        Log.d(TAG, "getNewSdlId: newID = "+id);
        return id;
    }





    //****** вспомогательные методы ******
    private boolean isUsedId(JSONArray sdlArr, int id) throws JSONException {
        for (int i=0; i<sdlArr.length(); i++) {
            if (sdlArr.getJSONObject(i).getInt("id") == id) return true;
        }
        return false;
    }

    private Integer isExistsSdl(String sdlName) throws JSONException {

        ArrayList<String> sdlNames = getSdlNames();
        if (sdlNames.size() == 0) return null;
        for (int i=0; i<sdlNames.size(); i++) {
            if (sdlName.equals(sdlNames.get(i))) return i;
        }
        return null;
    }
}
