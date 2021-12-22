package net.finch.calendar.Views;


import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.gridlayout.widget.GridLayout;
import net.finch.calendar.DayInfo;
import java.util.ArrayList;


public class CalendarLayout extends GridLayout {
    Context context;
    public CalendarLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CalendarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public CalendarLayout(Context context) {
        super(context);
        this.context = context;

        init();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        setLayoutParams(params);
        setColumnCount(7);
    }

    private void init() {
        for (int i=0; i<42; i++) {
            DayView dvi = new DayView(context);
            dvi.setTag(i);
            addView(dvi);
        }
    }

    public void clear() {
        removeAllViewsInLayout();
        init();
    }

    public void setDays(ArrayList<DayInfo> fod) {
//        Log.d(TAG, "setDays: "+ getChildCount());
        for (DayInfo di : fod) {
            DayView dv = findViewWithTag(di.getId());
            dv.setDayInfo(di);
        }
    }
}
