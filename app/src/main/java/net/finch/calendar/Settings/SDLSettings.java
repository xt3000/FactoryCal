package net.finch.calendar.Settings;

import android.content.Context;
import net.finch.calendar.Schedules.Schedule;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;


public class SDLSettings extends Settings {

    public SDLSettings(Context context) {
        super(context);
    }


    public void removeSchedule(Schedule sdl) {
        try {
            Integer sdlID = isExistsSdl(sdl.getName());
            JSONArray sdlArr = getJSONsdls();
            if (sdlID != null) {
                sdlArr.remove(sdlID);
            }
            editor = prefs.edit();
            editor.putString(SCHEDULES, sdlArr.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void saveSchedule(Schedule sdl) throws JSONException {
        editor = prefs.edit();
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
    }

    private JSONArray getJSONsdls() throws JSONException {
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
