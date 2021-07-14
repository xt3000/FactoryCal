package net.finch.calendar.Dialogs;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ArrayAdapter;

import net.finch.calendar.R;
import net.finch.calendar.Schedules.DBSchedules;

import java.util.ArrayList;

import static net.finch.calendar.CalendarVM.TAG;

@RequiresApi(api = Build.VERSION_CODES.M)
public class PopupEdit extends  PopupAdd{

    public PopupEdit(int layout, int sqlId) {
        super(layout, sqlId);

    }


    protected void setSpinnerAdapter() {
        ArrayList<String> arr = new ArrayList<>();
        Log.d(TAG, "setSpinnerAdapter: sql_ID = "+ sqlId);
        String name = new DBSchedules(context).readSdlName(sqlId);

        arr.add(name);
        ArrayAdapter<String> sdlSpinnerAdapter = new ArrayAdapter<String>(context, R.layout.sdl_list_tvitem, arr);
        spinner.setAdapter(sdlSpinnerAdapter);
        spinner.setEnabled(false);
    }
}
