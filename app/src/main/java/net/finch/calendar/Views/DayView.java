package net.finch.calendar.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.gridlayout.widget.GridLayout;

import static net.finch.calendar.CalendarVM.TAG;


public class DayView extends AppCompatTextView
{
    private Canvas offscreen;
    private String msg="";
    private boolean markedUp = false;
    private boolean markedDown = false;
    private int colorDown;
    private final int MARGIN = 15;

    public DayView(Context context){
        super(context);
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        lp.setMargins(MARGIN,MARGIN,MARGIN,MARGIN);
        setLayoutParams(lp);
        setGravity(Gravity.CENTER);

//        setBackground(context.getDrawable(R.drawable.circle));
    }

//    public DayView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }

    public void setDayText(String s){
        msg = s;
        setText(s);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas)
    {
        if(canvas==offscreen){
            super.onDraw(offscreen);
        }
        else if (markedUp || markedDown) {
            float x = getMeasuredWidth();
            float y = getMeasuredHeight();
            if(x==0 || y==0) return;
            Bitmap bitmap=Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            offscreen=new Canvas(bitmap);
            super.draw(offscreen);

            if (markedUp) {
                Paint paint = new Paint();
                int radius = 5;

                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.parseColor("#CD5C5C"));
                canvas.drawCircle(x/2, y/6, radius, paint);
            }

            if (markedDown) {
//                Log.d(TAG, "onDraw: sdlMarked = "+markedDown);
                float r = 3;
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(colorDown);
                canvas.drawLine(x/r, y-(y/5), x-(x/r), y-(y/5), paint);
                canvas.drawLine(x/r, y-(y/5)+1, x-(x/r), y-(y/5)+1, paint);
                canvas.drawLine(x/r, y-(y/5)+2, x-(x/r), y-(y/5)+2, paint);
            }
            setText(msg);
            super.onDraw(canvas);
        }
        else{
            super.onDraw(canvas);
        }
    }

    public void markedUp(boolean m) {
        markedUp = m;
    }

    public void markedDown(boolean m, int color) {
        markedDown = m;
        colorDown = color;
    }
}