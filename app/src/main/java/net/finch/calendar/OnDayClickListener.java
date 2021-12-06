package net.finch.calendar;

import android.view.View;
import static net.finch.calendar.CalendarVM.TAG;

public class OnDayClickListener implements View.OnClickListener {

    protected MainActivity ma;

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
            model.getFODLiveData(MainActivity.pageOffset);

            model.setSliderState(true);
        }

    }

}
