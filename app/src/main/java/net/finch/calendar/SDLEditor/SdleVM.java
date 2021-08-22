package net.finch.calendar.SDLEditor;

import android.os.Build;
//import android.support.annotation.RequiresApi;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.finch.calendar.Schedules.Schedule;

import java.util.ArrayList;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SdleVM extends ViewModel {

    private MutableLiveData<Map<String, Integer>> colorsLD;
    private Map<String, Integer> colors;

    private MutableLiveData<ArrayList<SdleListObj>> sdlLD;
    private ArrayList<SdleListObj> sdl;
    private int newId = 0;


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
    public LiveData<ArrayList<SdleListObj>> getSdlLD(ArrayList<SdleListObj> s) {
        if (sdlLD == null) {
            sdlLD = new MutableLiveData<>();
            sdl = new ArrayList<>();
        }

        if (s != null) sdl = s;
        sdlLD.setValue(sdl);
        return sdlLD;
    }

    public void setSdl(ArrayList<SdleListObj> s) {
        sdl = s;
        sdlLD.setValue(s);
    }

    public int getNewId(){
        newId++;
        return newId-1;
    }
}
