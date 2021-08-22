package net.finch.calendar.Dialogs;

import android.content.Context;
import android.os.Build;
//import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.RequiresApi;

import net.finch.calendar.R;
import net.finch.calendar.Schedules.DBSchedules;

import java.util.ArrayList;

import static net.finch.calendar.CalendarVM.TAG;

@RequiresApi(api = Build.VERSION_CODES.N)
public class PopupEdit extends  PopupAdd{

    public PopupEdit(Context ctx, int layout, int sqlId) {
        super(ctx, layout, sqlId);

    }


    protected void setSpinnerAdapter() {
        ArrayList<String> arr = new ArrayList<>();
        Log.d(TAG, "setSpinnerAdapter: sql_ID = "+ sqlId);
        String name = new DBSchedules(activity).readSdlName(sqlId);

        arr.add(name);
        ArrayAdapter<String> sdlSpinnerAdapter = new ArrayAdapter<String>(activity, R.layout.sdl_list_tvitem, arr);
        spinner.setAdapter(sdlSpinnerAdapter);
        spinner.setEnabled(false);
    }
}
