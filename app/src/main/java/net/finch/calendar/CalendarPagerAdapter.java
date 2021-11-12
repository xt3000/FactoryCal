package net.finch.calendar;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.finch.calendar.Views.CalendarLayout;

import java.util.ArrayList;

import static net.finch.calendar.CalendarVM.TAG;

public class CalendarPagerAdapter extends RecyclerView.Adapter<CalendarPagerAdapter.PagerVH> {
    protected final static int START_PAGE = 101;
    private final Context ctx;
    private OnBtnClickListener onBtnClickListener;
    private Handler handler;

    CalendarPagerAdapter(Context ctx) {
        this.ctx = ctx;
    }

//   VIEW HOLDER
    static  class PagerVH extends RecyclerView.ViewHolder {
    private final FrameLayout flCalendarLayout;
    private final TextView tvMonth, tvYear;
    private final ImageButton ibtnPrevious, ibtnNext;

    PagerVH(View itemView){
            super(itemView);
            flCalendarLayout = itemView.findViewById(R.id.fl_calendar);
            tvMonth = itemView.findViewById(R.id.calendar_tv_month);
            tvYear = itemView.findViewById(R.id.calendar_tv_year);
            ibtnPrevious = itemView.findViewById(R.id.calendar_ibtn_previous);
            ibtnNext = itemView.findViewById(R.id.calendar_ibtn_next);

            flCalendarLayout.addView(new CalendarLayout(itemView.getContext()));
        }
    }
/////////////////////



    @NonNull
    @Override
    public PagerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_layout_v2,  parent, false);

        return new PagerVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagerVH holder, int position) {


        // ADD DAYS
        CalendarLayout calendarLayout = (CalendarLayout) holder.flCalendarLayout.getChildAt(0);

// TODO: replace on new thread
        CalendarNavigator cNav = new CalendarNavigator(position-START_PAGE);
        handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            ArrayList<DayInfo> fod = cNav.frameOfDates();
            handler.post(() -> {
                calendarLayout.setDays(fod);
            });
        }).start();

//TODO ///////////////////////

        // SET MONTH
        holder.tvMonth.setText(Month.getString(cNav.getMonth()));

        // SET YEAR
        holder.tvYear.setText(String.valueOf(cNav.getYear()));

        // SET LISTENERS
        if (onBtnClickListener != null) {
            holder.ibtnPrevious.setOnClickListener((v)-> {
                onBtnClickListener.onPrevClick();
            });

            holder.ibtnNext.setOnClickListener((v)-> {
                onBtnClickListener.onNextClick();
            });
        }

    }

    @Override
    public int getItemCount() {
        return 200;
    }


    //// *****  CALLBACK  ***** ////
    public void setOnBtnClickListener(OnBtnClickListener onBtnClickListener) {
        this.onBtnClickListener = onBtnClickListener;
    }

    public interface OnBtnClickListener {
        void onPrevClick();
        void onNextClick();
    }

}
