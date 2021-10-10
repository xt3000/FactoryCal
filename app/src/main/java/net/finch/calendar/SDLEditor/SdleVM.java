package net.finch.calendar.SDLEditor;

import android.os.Build;
//import android.support.annotation.RequiresApi;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.finch.calendar.Schedules.Schedule;
import net.finch.calendar.Settings.SDLSettings;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SdleVM extends ViewModel {
    private MutableLiveData<Boolean> editorModeLD;
    private boolean editorMode = SdlEditorActivity.sdlMODE;

    private MutableLiveData<Map<String, Integer>> colorsLD;
    private Map<String, Integer> colors;

    private MutableLiveData<Schedule> sftsLD;
    private Schedule sdl;
    private int newId = 0;

    private MutableLiveData<ArrayList<Schedule>> sdlsListLD;
    private ArrayList<Schedule>sdlsList;

// *** COLOR LiveData ***
    public LiveData<Map<String, Integer>> getColorsLD(Map<String, Integer> c) {
        if (colorsLD == null) {
            colorsLD = new MutableLiveData<>();
            colors = Schedule.getDefaultShiftColors();
        }

        if (c != null) colors = c;
        colorsLD.setValue(colors);
        return colorsLD;
    }

    public void setColor(String sft, int color){
        if (colors != null) {
            colors.replace(sft, color);
            colorsLD.setValue(colors);
        }
    }

    public int getCurrentSftColor(String sft){
        return colors.get(sft);
    }


// *** SDL LiveData ***
    public LiveData<Schedule> getSftsLD(Schedule s) {
        if (sftsLD == null) {
            sftsLD = new MutableLiveData<>();
            sdl = new Schedule("", "");
        }

        if (s != null) sdl = s;
        sftsLD.setValue(sdl);
        return sftsLD;
    }

    public void setSfts(Schedule sdl) {
        this.sdl = sdl;
        sftsLD.setValue(sdl);
    }


    public int getNewId(){
        newId++;
        return newId-1;
    }



// *** SDLS List LiveData ***
    public LiveData<ArrayList<Schedule>> getSdlsListLD() {
        if (sdlsListLD == null) {
            sdlsListLD = new MutableLiveData<>();
        }

        sdlsList = new ArrayList<>();
        try {
            sdlsList = new SDLSettings(SdlEditorActivity.getInstance()).getSdlArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sdlsListLD.setValue(sdlsList);
        return sdlsListLD;
    }

// ***  EDITOR MODE  *** //
    public LiveData<Boolean> getEditorModeLD() {
        if (editorModeLD ==null) editorModeLD = new MutableLiveData<>();
        editorModeLD.setValue(editorMode);

        return editorModeLD;
    }

    public void setEditorMode(boolean editorMode) {
        this.editorMode = editorMode;
        editorModeLD.setValue(editorMode);
    }
}
