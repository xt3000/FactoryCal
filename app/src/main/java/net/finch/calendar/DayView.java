package net.finch.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;


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
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
        ViewGroup llweak = (ViewGroup) this.getParent();
        int side = llweak.getWidth()/7-(MARGIN*2);
        setWidth(side);
        setHeight(side);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas)
    {
        if(canvas==offscreen){
            super.onDraw(offscreen);
        }
        else if (markedUp || markedDown) {
            float x = getWidth();
            float y = getHeight();
            if(x==0 || y==0) return;
            Bitmap bitmap=Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            offscreen=new Canvas(bitmap);
            super.draw(offscreen);

            if (markedUp) {
                //Our offscreen image uses the dimensions of the view rather than the canvas
                Paint paint = new Paint();
                int radius = 5;

                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.parseColor("#CD5C5C"));
                canvas.drawCircle(x/2, y/6, radius, paint);
            }
            if (markedDown) {
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