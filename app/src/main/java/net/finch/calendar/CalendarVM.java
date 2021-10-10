package net.finch.calendar;

//import android.arch.lifecycle.LiveData;
//import android.arch.lifecycle.MutableLiveData;
//import android.arch.lifecycle.ViewModel;
import android.os.Build;
//import android.support.annotation.RequiresApi;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.finch.calendar.Marks.Mark;

import org.json.JSONException;

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
    @RequiresApi(api = Build.VERSION_CODES.N)
    public LiveData<ArrayList<DayInfo>> getFODLiveData() throws JSONException {
        Log.d(TAG, "getFODLiveData: VM");
        if (FOD_ld == null) {
            FOD_ld = new MutableLiveData<>();
        }
        nCal = new CalendarNavigator();
        frameOfDates = nCal.frameOfDates();
        FOD_ld.setValue(frameOfDates);
        return FOD_ld;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public  void nextMonth() throws JSONException {
        Log.d(TAG, "nextMonth: VM");
        nCal.nextMonth();
        frameOfDates = nCal.frameOfDates();
        FOD_ld.setValue(frameOfDates);
//        setSliderState(false);
//        setDayId(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public  void previousMonth() throws JSONException {
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

