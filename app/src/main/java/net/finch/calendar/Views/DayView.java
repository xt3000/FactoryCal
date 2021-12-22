package net.finch.calendar.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.ViewGroup;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.gridlayout.widget.GridLayout;
import net.finch.calendar.CalendarNavigator;
import net.finch.calendar.DayInfo;
import net.finch.calendar.OnDayClickListener;
import net.finch.calendar.R;
import java.util.Calendar;


public class DayView extends AppCompatTextView
{
    private int monthOffset = 0;
    private String msg="";
    private boolean markedUp = false;
    private boolean markedDown = false;
    private final int colorUp;
    private final int color0;
    private final int color1;

    private final Paint paintUp = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintDown = new Paint();
    Rect rect = new Rect();


    public DayView(Context context){
        super(context);
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        int MARGIN = 15;
        lp.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
        setLayoutParams(lp);
        setGravity(Gravity.CENTER);
        setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        setTypeface(ResourcesCompat.getFont(getContext(), R.font.open_sans_semibold));
        colorUp = context.getColor(R.color.colorAccent);
        color0 = context.getColor(R.color.rbActive);
        color1 = 0x55808080;
        setTextColor(color0);
    }

    public void setDayText(String s){
        msg = s;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        float w = getMeasuredWidth();
        float h = getMeasuredHeight();

//  *** DRAW TEXT ***
        Paint paintTxt = getPaint();
        if (monthOffset == 0) paintTxt.setColor(color0);
        else paintTxt.setColor(color1);
        paintTxt.setTextAlign(Paint.Align.CENTER);
        paintTxt.getTextBounds(msg, 0, msg.length(), rect);
        float y = h / 2f + rect.height() / 2f - rect.bottom;
        canvas.drawText(msg, (w /2), y, paintTxt);

//  *** DRAW MARKS ***
        if(w ==0 || h ==0) return;
        if (markedUp) {
            int radius = 5;
            canvas.drawCircle(w /2, h /6, radius, paintUp);
        }

//  *** DRAW SHIFTS ***
        if (markedDown) {
            float r = 3;
            canvas.drawRect(w /r, h -(h /5)+5, w -(w /r), h -(h /5), paintDown);
        }
    }

    private void markedUp(boolean m) {
        markedUp = m;
        paintUp.setStyle(Paint.Style.FILL);
        paintUp.setColor(colorUp);
    }

    private void markedDown(boolean m, int color) {
        markedDown = m;
        paintDown.setStyle(Paint.Style.FILL);
        paintDown.setColor(color);
    }

    private void setMonthOffset(int offset) {
        this.monthOffset = offset;
    }

    protected void setDayInfo(DayInfo di) {
        setMonthOffset(di.getMonthOffset());
        setDayText(di.getDateString());

        /// Выделение дат с заметками
        markedUp(di.isMarked());
//
        ///  Выделение смен графика
        if (di.isShifted() && di.getShiftList().get(0).isPrime()) {
            markedDown(true, di.getShiftList().get(0).getColor());
        }
//
        /// Выделение сегодняшней даты
        Calendar now = CalendarNavigator.getNow();
        if (now.get(Calendar.YEAR) == di.getCalendar().get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == di.getCalendar().get(Calendar.DAY_OF_YEAR)
                && di.getMonthOffset() == 0) {
            setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.circle));
        }

        /// Слушатель нажатия на дату
        setOnClickListener(new OnDayClickListener());

        /// Перерисовка изменений
        invalidate();
    }
}