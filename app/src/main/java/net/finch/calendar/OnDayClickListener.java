package net.finch.calendar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import net.finch.calendar.Marks.DBMarks;

import java.util.ArrayList;

import static net.finch.calendar.CalendarVM.TAG;

public class OnDayClickListener implements /*View.OnLongClickListener,*/ View.OnClickListener {

    protected MainActivity ma;

    TextView tvSliderTitle;
    CalendarVM model;
    LiveData<ArrayList<DayInfo>> FODdata;
    ArrayList<DayInfo> fod;
    DBMarks db;
    DayInfo di;
    TreeNode listRoot;

    OnDayClickListener() {
        this.ma = (MainActivity) MainActivity.getContext();
        model = ViewModelProviders.of(ma).get(CalendarVM.class);
    }

//    @Override
//    public boolean onLongClick(View v) {
//        model.setSliderState(false);
//        db = new DBMarks(ma);
//        DayInfo day = ma.frameOfDates.get(v.getId());
//        boolean marked = day.isMarked();
//
//        int y = day.getYear();
//        int m = day.getMonth();
//        int d = day.getDate();
//
//        if(!marked) {
//            db.saveDayMark(y, m, d, Time.NULLTIME,"");
//        }else {
//            db.deleteDayMark(y, m, d);
//        }
//        model.getFODLiveData();
//
//        return true;
//    }




    @Override
    public void onClick(View v) {
        int id = v.getId();
        tvSliderTitle = ma.findViewById(R.id.tv_slider_title);

        model.setDayId(id);
        tvSliderTitle.setText(ma.frameOfDates.get(id).getFullDateString());
        model.setSliderState(true);

        if (ma.frameOfDates.get(id).getShiftList().size() > 0) {

        }

    }

}
