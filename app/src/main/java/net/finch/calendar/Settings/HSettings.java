package net.finch.calendar.Settings;


import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.Purchase;

import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import static net.finch.calendar.CalendarVM.TAG;


public class HSettings extends Settings {
    public final static int OC_MAX = 3;
    public final static int DC_MAX = 1;
    public static final String ROC = HIDDEN + "_roc";   // RateAs Open Count
    public static final String AOC = HIDDEN + "_aoc";   // Ads Open Count
    public static final String ADC = HIDDEN + "_adc";   // Ads Day Count
    public static final String PRO = HIDDEN + "_pro";   // It is PRO?

    public HSettings(Context context) {
        super(context);
    }


//  |====================================|
//  |      control HIDDEN SETTINGS       |
//  |====================================|

    public boolean needShowADS() {
        return getCount(ROC) == 0 && !isPro() && (getCount(AOC) >=OC_MAX || isFullADC());
    }

    public boolean needShowRate() {
        return getCount(ROC) >= OC_MAX;
    }

    public void countAdd(String prefName) {
        int pref = getCount(prefName);

        if (pref == 0) return;
        else if (pref >= OC_MAX) pref = 1;
        else pref++;
        editor = prefs.edit();
        editor.putInt(prefName, pref).apply();
    }

    public void noRate() {
        editor = prefs.edit();
        editor.putInt(ROC, 0).apply();
    }

    public int getCount(String prefName) {
        return prefs.getInt(prefName, 1);
    }

    //  Check Ads Day Count is full
    private boolean isFullADC() {
        long startMills = prefs.getLong(ADC, 0);
        long nowMills = new GregorianCalendar().getTimeInMillis();
        long resMills = nowMills-startMills;
        long days = TimeUnit.MILLISECONDS.toDays(resMills);
        if (days>=DC_MAX) {
            resetADSCounts();
            return true;
        }
        return false;
    }  // TODO: реализовать проверку переполнения дней с последнего показа ADS

    // Save new day start Ads Day Count
    public void resetADSCounts() {
        // TODO: реализовать сброс дней с последнего показа ADS
        long mills = new GregorianCalendar().getTimeInMillis();
        editor = prefs.edit();
        editor.putInt(AOC, 1);
        editor.putLong(ADC, mills).apply();
    }

    // check No Ads Billing
    public boolean isPro() {
        boolean pro = prefs.getBoolean(PRO, false);
        Log.d(TAG, "isPro: "+pro);
        return pro;
    }

    public void setPro(boolean isPro) {
        Log.d(TAG, "setPro: "+isPro);
        editor = prefs.edit();
        editor.putBoolean(PRO, isPro).apply();
    }

}
