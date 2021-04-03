package net.finch.calendar;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

public class OnRBChecked implements RadioGroup.OnCheckedChangeListener {
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        MainActivity ma = (MainActivity) MainActivity.getContext();
        LinearLayout llMark = ma.findViewById(R.id.ll_mark);
        LinearLayout llSchedule = ma.findViewById(R.id.ll_schedule);

        if(id == R.id.rb_mark) {
            llSchedule.setVisibility(View.GONE);
            llMark.setVisibility(View.VISIBLE);
        }else {
            llMark.setVisibility(View.GONE);
            llSchedule.setVisibility(View.VISIBLE);
        }
    }
}
