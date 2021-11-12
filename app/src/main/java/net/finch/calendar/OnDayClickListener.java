package net.finch.calendar;

//import android.arch.lifecycle.LiveData;
//import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import net.finch.calendar.Marks.DBMarks;

import java.util.ArrayList;

import static net.finch.calendar.CalendarVM.TAG;

public class OnDayClickListener implements View.OnClickListener {

    protected MainActivity ma;

    TextView tvSliderTitle;
    CalendarVM model;

    public OnDayClickListener() {
        this.ma = (MainActivity) MainActivity.getContext();
        model = MainActivity.getCalendarVM();
    }


    @Override
    public void onClick(View v) {
        Integer id = (Integer) v.getTag();
        if (id != null) {
            model.setDayId(id);
            tvSliderTitle = ma.findViewById(R.id.tv_slider_title);
            tvSliderTitle.setText(ma.frameOfDates.get(id).getFullDateString());
            model.setSliderState(true);
        }


//        if (ma.frameOfDates.get(id).getShiftList().size() > 0) {
//
//        }

    }

}
