package net.finch.calendar.Views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.gridlayout.widget.GridLayout;

import net.finch.calendar.CalendarNavigator;
import net.finch.calendar.DayInfo;
import net.finch.calendar.OnDayClickListener;
import net.finch.calendar.R;

import java.util.ArrayList;
import java.util.Calendar;

import static net.finch.calendar.CalendarVM.TAG;

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
        Log.d(TAG, "setDays: "+ getChildCount());
//        init();
        for (DayInfo di : fod) {
            DayView dv = findViewWithTag(di.getId());
            dv.setDayInfo(di);
//            dv.setMonthOffset(di.getMonthOffset());
//            dv.setDayText(di.getDateString());
//
//
//            /// Выделение дат с заметками
//            dv.markedUp(di.isMarked());
////
//            ///  Выделение смен графика
//            if (di.isShifted() && di.getShiftList().get(0).isPrime()) {
//                dv.markedDown(true, di.getShiftList().get(0).getColor());
//            }
////
//            /// Выделение сегодняшней даты
//            Calendar now = CalendarNavigator.getNow();
//            if (now.get(Calendar.YEAR) == di.getCalendar().get(Calendar.YEAR)
//                    && now.get(Calendar.DAY_OF_YEAR) == di.getCalendar().get(Calendar.DAY_OF_YEAR)
//                    && di.getMonthOffset() == 0) {
//                dv.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.circle));
//            }
//
//            /// Слушатель нажатия на дату
//            dv.setOnClickListener(new OnDayClickListener());

        }
    }
}
