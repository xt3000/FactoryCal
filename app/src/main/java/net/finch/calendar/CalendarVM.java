package net.finch.calendar;

import android.os.Build;
//import android.support.annotation.RequiresApi;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CalendarVM extends ViewModel {
    public static final String TAG = "FINCH_TAG";

    private MutableLiveData<ArrayList<DayInfo>> FOD_ld;
    protected Integer selectedDayId = null;

    private MutableLiveData<Boolean> SState_ld;
    private MutableLiveData<DayInfo> tgtDayInfo_ld;
    private int monthOffset;

    private MutableLiveData<Boolean> proState_ld;
    private MutableLiveData<Map<String, Integer>> billingMsg_ld;


//*********** Frame Of Dates live data **************
    @RequiresApi(api = Build.VERSION_CODES.N)
    public LiveData<ArrayList<DayInfo>> getFODLiveData(Integer mOffset) {
//        Log.d(TAG, "getFODLiveData: VM");
        if (FOD_ld == null) {
            FOD_ld = new MutableLiveData<>();
        }
        if (mOffset != null) this.monthOffset = mOffset;
        CalendarNavigator nCal = new CalendarNavigator(monthOffset);
        ArrayList<DayInfo> frameOfDates = nCal.frameOfDates();
        FOD_ld.setValue(frameOfDates);
        return FOD_ld;
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
//            Log.d(TAG, "setSliderState: "+ss);
        }
    }


//*********** Selected Day Info List live data **************
    public LiveData<DayInfo> getDayInfoLiveData() {
        if (tgtDayInfo_ld == null) {
            tgtDayInfo_ld = new MutableLiveData<>();
        }

        return tgtDayInfo_ld;
    }

    public void setDayId(Integer dayId) {
        selectedDayId = dayId;
        ArrayList<DayInfo> f = (ArrayList<DayInfo>) FOD_ld.getValue();
        if (dayId != null && f != null) tgtDayInfo_ld.setValue(f.get(dayId));
        else tgtDayInfo_ld.setValue(null);
//        Log.d(TAG, "setDayId: "+selectedDayId);
    }

    public void updInfoList() {
        setDayId(selectedDayId);
//        Log.d(TAG, "setDayId: "+selectedDayId);
    }


//***************** ProState live data ***********************
    public LiveData<Boolean> proState(Boolean proState) {
        if (proState_ld == null) {
            proState_ld = new MutableLiveData<>();
        }
        proState_ld.postValue(proState);
        return proState_ld;
    }

    public LiveData<Map<String, Integer>> billingMsg(Integer txtRes, Integer type) {
        if (billingMsg_ld == null) {
            billingMsg_ld = new MutableLiveData<>();
        }
        Map<String, Integer> msg = new HashMap<>();
        msg.put(Billing.MSG_TYPE, type);
        msg.put(Billing.MSG_TXTRES, txtRes);
        billingMsg_ld.postValue(msg);
        return billingMsg_ld;
    }

}

