package net.finch.calendar;

import android.arch.lifecycle.ViewModelProviders;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.TextView;

public class OnDayClickListener implements View.OnLongClickListener, View.OnClickListener {

    protected MainActivity ma;

    TextView tvSliderTitle;
    CalendarVM model;
    DBHelper db;

    OnDayClickListener() {
        this.ma = (MainActivity) MainActivity.getContext();
        model = ViewModelProviders.of(ma).get(CalendarVM.class);
    }

    @Override
    public boolean onLongClick(View v) {
        model.setSliderState(false);
        db = new DBHelper(ma);
        MyDate day = ma.frameOfDates.get(v.getId());
        boolean marked = day.isMarked();

        int y = day.getYear();
        int m = day.getMonth();
        int d = day.getDate();

        if(!marked) {
            db.saveDayMark(y, m, d, "");
        }else {
            db.deleteDayMark(y, m, d);
        }
        model.update();

        return true;
    }




    @Override
    public void onClick(View v) {
        int id = v.getId();
        boolean marked = ma.frameOfDates.get(id).isMarked();
        tvSliderTitle = ma.findViewById(R.id.tv_slider_title);

        if(!marked) {
            // TODO: see MARK create slider
        }else {
            // TODO: see MARK info slider
        }

        tvSliderTitle.setText(ma.frameOfDates.get(id).getFullDateString());

        model.setSliderState(true);
    }
}
