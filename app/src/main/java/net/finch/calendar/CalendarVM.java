package net.finch.calendar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

public class CalendarVM extends ViewModel {

    MutableLiveData<ArrayList<MyDate>> FODdata;
    NavCalendar nCal;
    ArrayList<MyDate> frameOfDates;

    MutableLiveData<Boolean> SStateData;

    public LiveData<ArrayList<MyDate>> getFODLiveData() {
        if (FODdata == null) {
            FODdata = new MutableLiveData<>();
            nCal = new NavCalendar();
            frameOfDates = nCal.frameOfDates();
            FODdata.setValue(frameOfDates);
        }
        return FODdata;
    }

    public  void nextMonth() {
        nCal.nextMonth();
        frameOfDates = nCal.frameOfDates();
        FODdata.setValue(frameOfDates);
    }

    public  void previousMonth() {
        nCal.previousMonth();
        frameOfDates = nCal.frameOfDates();
        FODdata.setValue(frameOfDates);
    }

    public void update() {
        frameOfDates = nCal.frameOfDates();
        FODdata.setValue(frameOfDates);
    }


    public LiveData<Boolean> getSStateLiveData() {
        if (SStateData == null) {
            SStateData = new MutableLiveData<>();
           SStateData.setValue(false);
        }
        return SStateData;
    }

    public void setSliderState(boolean ss) {
        SStateData.setValue(ss);
    }
}
