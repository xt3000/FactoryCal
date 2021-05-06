package net.finch.calendar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import java.util.ArrayList;

public class OnDayClickListener implements View.OnLongClickListener, View.OnClickListener {

    protected MainActivity ma;

    TextView tvSliderTitle;
    CalendarVM model;
    LiveData<ArrayList<DayInfo>> FODdata;
    ArrayList<DayInfo> fod;
    DBHelper db;
    DayInfo di;
    TreeNode listRoot;

    OnDayClickListener() {
        this.ma = (MainActivity) MainActivity.getContext();
        model = ViewModelProviders.of(ma).get(CalendarVM.class);
    }

    @Override
    public boolean onLongClick(View v) {
        model.setSliderState(false);
        db = new DBHelper(ma);
        DayInfo day = ma.frameOfDates.get(v.getId());
        boolean marked = day.isMarked();

        int y = day.getYear();
        int m = day.getMonth();
        int d = day.getDate();

        if(!marked) {
            db.saveDayMark(y, m, d, Time.NULLTIME,"");
        }else {
            db.deleteDayMark(y, m, d);
        }
        model.getFODLiveData();

        return true;
    }




    @Override
    public void onClick(View v) {
        int id = v.getId();
        tvSliderTitle = ma.findViewById(R.id.tv_slider_title);

        model.setDayId(id);
        tvSliderTitle.setText(ma.frameOfDates.get(id).getFullDateString());
        model.setSliderState(true);
    }

}
