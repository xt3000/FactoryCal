package net.finch.calendar;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static net.finch.calendar.CalendarVM.TAG;

public class Popup implements TextView.OnEditorActionListener, View.OnClickListener {
    final static int MARK = R.layout.popup_mark_create;
    final static int SHEDULE = R.layout.popup_schedule_create;

    private MainActivity context;
    private int layout;

    private PopupWindow pw;
    private Calendar mTime = new GregorianCalendar();
    private TextView tvAddTime;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private TextInputEditText etMarkNote;

    private TextView tvSliderTitle;

    private DBMarks dbMarks;
    private CalendarVM model;

    private int sdlId;
    private Spinner spinner;
    private Button btnSdlSave;
    private ScheduleArray sdlList = new ScheduleArray();
    private ArrayAdapter<String> sdlSpinnerAdapter;

    Popup(int layout) {
        this.context = (MainActivity) MainActivity.getContext();
        this.layout = layout;
        tvSliderTitle = context.findViewById(R.id.tv_slider_title);
        model = ViewModelProviders.of(MainActivity.instance).get(CalendarVM.class);
    }


    void show() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(layout, null, false);
        pw = new PopupWindow(popupView, 800, 600, true);
        pw.setAnimationStyle(R.style.popup_animations);
        pw.showAtLocation(context.findViewById(R.id.main_layout), Gravity.CENTER, 0, 0);

        if (layout == MARK) {
            Button btnMarkConfirm = popupView.findViewById(R.id.btn_markConfirm);
            btnMarkConfirm.setOnClickListener(this);

            etMarkNote = popupView.findViewById(R.id.et_markNote);
            etMarkNote.setOnEditorActionListener(this);

            tvAddTime = popupView.findViewById(R.id.tv_addTime);
            tvAddTime.setOnClickListener(this);
            setTimeListener();
        }else {
            spinner = popupView.findViewById(R.id.sp_sdlAdd);
            btnSdlSave = popupView.findViewById(R.id.btn_sdlSave);
            btnSdlSave.setOnClickListener(this);

            //TODO: Create SDL List
            sdlList.add(new Schedule("График1", context.getString(R.string.schedule1)));
            sdlList.add(new Schedule("График2", context.getString(R.string.schedule2)));
            sdlList.add(new Schedule("График3", context.getString(R.string.schedule1)));
            sdlList.add(new Schedule("График4", context.getString(R.string.schedule2)));
            sdlList.add(new Schedule("График5", context.getString(R.string.schedule1)));
            sdlList.add(new Schedule("График6", context.getString(R.string.schedule2)));
            //*********************

            sdlSpinnerAdapter = new ArrayAdapter<String>(context, R.layout.sdl_list_tvitem, sdlList.getNames());
            spinner.setAdapter(sdlSpinnerAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    sdlId = pos;
                    Log.d(CalendarVM.TAG, "onItemSelected: itemID = "+pos);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
    }



    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

        return false;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_markConfirm:
                Log.d(CalendarVM.TAG, "onClick: btn_markConfirm");
                saveMark();
                pw.dismiss();
                break;
            case R.id.tv_addTime:
                Log.d(CalendarVM.TAG, "onClick: setTime");
                new TimePickerDialog(context, timeSetListener,
                        mTime.get(Calendar.HOUR_OF_DAY),
                        mTime.get(Calendar.MINUTE), true)
                        .show();
                break;
            case R.id.btn_sdlSave:
                Log.d(TAG, "onClick: btn_sdlSave");
                saveSdl();
                pw.dismiss();
        }
    }




    private void setTimeListener() {
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mTime.set(Calendar.MINUTE, minute);

                tvAddTime.setText(DateUtils.formatDateTime(context,
                        mTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
            }
        };
    }

    void saveMark() {
        String text = etMarkNote.getText().toString();
        int t = Time.toInt(tvAddTime.getText().toString());
        ParseDate pd = new ParseDate(tvSliderTitle.getText().toString());
        dbMarks = new DBMarks(context);

        dbMarks.saveDayMark(pd.getY(), pd.getM(), pd.getD(), t, text);
        model.getFODLiveData();
        model.updInfoList();
        model.setSliderState(true);
    }

    private void saveSdl() {
        Schedule sdl = sdlList.get(sdlId);
        Log.d(CalendarVM.TAG, "onItemSelected: save  "+sdl.getName()+" ("+sdl.getSdl()+")");
        DBSchedule dbSdl = new DBSchedule(context);
        ParseDate pd = new ParseDate(tvSliderTitle.getText().toString());
        dbSdl.saveSchedule(pd.getY(), pd.getM(), pd.getD(), sdl.getName(), sdl.getSdl());
    }

}
