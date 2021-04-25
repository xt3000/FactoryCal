package net.finch.calendar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

public class CalendarVM extends ViewModel {
    public static final String TAG = "FINCH_TAG";

    MutableLiveData<ArrayList<DayInfo>> FOD_ld;
    NavCalendar nCal;
    ArrayList<DayInfo> frameOfDates;
    Integer selectedDayId = null;

    MutableLiveData<Boolean> SState_ld;
    MutableLiveData<ArrayList<String>> selDayInfoList_ld;


//*********** Frame Of Dates live data **************
    public LiveData<ArrayList<DayInfo>> getFODLiveData() {
        if (FOD_ld == null) {
            FOD_ld = new MutableLiveData<>();
            nCal = new NavCalendar();
        }
        frameOfDates = nCal.frameOfDates();
        FOD_ld.setValue(frameOfDates);
        return FOD_ld;
    }

    public  void nextMonth() {
        nCal.nextMonth();
        frameOfDates = nCal.frameOfDates();
        FOD_ld.setValue(frameOfDates);
        setSliderState(false);
        setDayId(null);
    }

    public  void previousMonth() {
        nCal.previousMonth();
        frameOfDates = nCal.frameOfDates();
        FOD_ld.setValue(frameOfDates);
        setSliderState(false);
        setDayId(null);
    }


//*********** Slider State live data **************
    public LiveData<Boolean> getSStateLiveData() {
        if (SState_ld == null) {
            SState_ld = new MutableLiveData<>();
            SState_ld.setValue(false);
        }
        return SState_ld;
    }

    public void setSliderState(boolean ss) {
        SState_ld.setValue(ss);
    }


//*********** Selected Day Info List live data **************
    public LiveData<ArrayList<String>> getDayInfoListLiveData() {
        if (selDayInfoList_ld == null) {
            selDayInfoList_ld = new MutableLiveData<>();
//            selDayInfoList_ld.setValue(null);
        }

        return selDayInfoList_ld;
    }

    public void setDayId(Integer dayId) {
        selectedDayId = dayId;
        if (dayId != null) selDayInfoList_ld.setValue(frameOfDates.get(dayId).getInfoList());
        else selDayInfoList_ld.setValue(new ArrayList<String>());
        Log.d(TAG, "setDayId: "+selectedDayId);
    }

    public void updInfoList() {
        setDayId(selectedDayId);
        Log.d(TAG, "setDayId: "+selectedDayId);

    }
}

