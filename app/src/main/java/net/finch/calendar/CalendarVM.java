package net.finch.calendar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import net.finch.calendar.Marks.Mark;

import java.util.ArrayList;

public class CalendarVM extends ViewModel {
    public static final String TAG = "FINCH_TAG";

    MutableLiveData<ArrayList<DayInfo>> FOD_ld;
    CalendarNavigator nCal;
    ArrayList<DayInfo> frameOfDates;
    Integer selectedDayId = null;

    MutableLiveData<Boolean> SState_ld;
    MutableLiveData<DayInfo> tgtDayInfo_ld;


//*********** Frame Of Dates live data **************
    @RequiresApi(api = Build.VERSION_CODES.M)
    public LiveData<ArrayList<DayInfo>> getFODLiveData() {
        if (FOD_ld == null) {
            FOD_ld = new MutableLiveData<>();
            nCal = new CalendarNavigator();
        }
        frameOfDates = nCal.frameOfDates();
        FOD_ld.setValue(frameOfDates);
        return FOD_ld;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public  void nextMonth() {
        nCal.nextMonth();
        frameOfDates = nCal.frameOfDates();
        FOD_ld.setValue(frameOfDates);
//        setSliderState(false);
//        setDayId(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public  void previousMonth() {
        nCal.previousMonth();
        frameOfDates = nCal.frameOfDates();
        FOD_ld.setValue(frameOfDates);
//        setSliderState(false);
//        setDayId(null);
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
        if (SState_ld.getValue() != ss) {
            SState_ld.setValue(ss);
            Log.d(TAG, "setSliderState: "+ss);
        }
    }


//*********** Selected Day Info List live data **************
    public LiveData<DayInfo> getDayInfoLiveData() {
        if (tgtDayInfo_ld == null) {
            tgtDayInfo_ld = new MutableLiveData<>();
//            selDayInfoList_ld.setValue(null);
        }

        return tgtDayInfo_ld;
    }

    public void setDayId(Integer dayId) {
        selectedDayId = dayId;
        if (dayId != null) tgtDayInfo_ld.setValue(frameOfDates.get(dayId));
        else tgtDayInfo_ld.setValue(null);
        Log.d(TAG, "setDayId: "+selectedDayId);
    }

    public void updInfoList() {
        setDayId(selectedDayId);
        Log.d(TAG, "setDayId: "+selectedDayId);

    }

}

