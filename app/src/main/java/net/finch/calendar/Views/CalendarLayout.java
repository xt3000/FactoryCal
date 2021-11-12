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
    public CalendarLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CalendarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarLayout(Context context) {
        super(context);
        removeAllViews();
        for (int i=0; i<42; i++) {
            DayView dvi = new DayView(context);
            dvi.setTag(i);
//            dvi.setText(String.valueOf(i));
            addView(dvi);
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        setLayoutParams(params);
//        setBackgroundColor(context.getColor(R.color.rbHint));
        setColumnCount(7);

        Log.d(TAG, "CalendarLayout: childCount = "+getChildCount());
    }

    public void setDays(ArrayList<DayInfo> fod) {
        Log.d(TAG, "setDays: fod: "+fod.get(0).getDate());
        for (DayInfo di : fod) {
            DayView dv = findViewWithTag(di.getId());
//            dv.setTag(di.getId());
            dv.setDayText(di.getDateString());

            /// Выделение текущего месяца
            if (di.getMonthOffset() == 0){
                dv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.open_sans_semibold));
            }else dv.setTextColor(0x55808080);

            /// Выделение дат с заметками
            dv.markedUp(di.isMarked());

            ///  Выделение смен графика
            if (di.isShifted()
                    && di.getShiftList().size() > 0
                    && di.getShiftList().get(0).isPrime()) {
//                Log.d(TAG, "setDays: sdlSetMarked id = "+di.getId());
                dv.markedDown(true, di.getShiftList().get(0).getColor());
            }else dv.markedDown(false, 0);

            /// Выделение сегодняшней даты
            Calendar now = CalendarNavigator.getNow();
            if (now.get(Calendar.YEAR) == di.getCalendar().get(Calendar.YEAR)
                    && now.get(Calendar.DAY_OF_YEAR) == di.getCalendar().get(Calendar.DAY_OF_YEAR)
                    && di.getMonthOffset() == 0) {
                dv.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.circle));
            }

            /// Слушатель нажатия на дату
            dv.setOnClickListener(new OnDayClickListener());
//            dv.setBackgroundColor(getContext().getColor(R.color.colorAccent));
//            addView(dv);

        }
    }
}
