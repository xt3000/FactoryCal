package net.finch.calendar.Dialogs;

import android.content.Context;
import android.os.Build;
//import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.PopupWindow;

import androidx.annotation.RequiresApi;

import net.finch.calendar.R;
import net.finch.calendar.Schedules.DBSchedules;

import org.json.JSONException;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
public class PopupSdlEdit extends  PopupAdd{
    CheckBox chbNewDay;

    public PopupSdlEdit(Context ctx, int sqlId) {
        super(ctx, PopupAdd.SCHEDULE, sqlId);
    }

    @Override
    protected void layoutSettings(PopupWindow pw) {
        super.layoutSettings(pw);

        chbNewDay = pwView.findViewById(R.id.chb_newDayStart);
        chbNewDay.setVisibility(View.VISIBLE);
        chbSdlPrime.setChecked(new DBSchedules(activity).isPrime(sqlId));
    }

    @Override
    protected void setSpinnerAdapter() {
//        Log.d(TAG, "setSpinnerAdapter: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        ArrayList<String> arr = new ArrayList<>();
//        Log.d(TAG, "setSpinnerAdapter: sql_ID = "+ sqlId);
        final String name = new DBSchedules(activity).readSdlName(sqlId);


        arr.add(name);
        ArrayAdapter<String> sdlSpinnerAdapter = new ArrayAdapter<String>(activity, R.layout.sdl_spiner_tvitem, arr);
        spinner.setAdapter(sdlSpinnerAdapter);
        spinner.setEnabled(false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                PopupSdlEdit.super.sdlSelected = name;
//                Log.d(CalendarVM.TAG, "onItemSelected: itemID = "+pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                Log.d(TAG, "onNothingSelected: ");
            }
        });
    }
}
