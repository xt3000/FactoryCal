package net.finch.calendar.Dialogs;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import net.finch.calendar.CalendarVM;
import net.finch.calendar.MainActivity;
import net.finch.calendar.Marks.DBMarks;
import net.finch.calendar.ParseDate;
import net.finch.calendar.R;
import net.finch.calendar.Schedules.DBSchedules;
import net.finch.calendar.Schedules.Schedule;
import net.finch.calendar.Schedules.ScheduleArray;
import net.finch.calendar.Time;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static net.finch.calendar.CalendarVM.TAG;

@RequiresApi(api = Build.VERSION_CODES.M)
public class PopupAdd extends PopupView implements TextView.OnEditorActionListener, View.OnClickListener {
    public final static int MARK = R.layout.popup_mark_create;
    public final static int SCHEDULE = R.layout.popup_schedule_create;

    protected final MainActivity context = (MainActivity) MainActivity.getContext();
    protected final int layout;
    protected int sqlId;

    private PopupWindow pw;
    private final Calendar mTime = new GregorianCalendar();
    private TextView tvAddTime;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private TextInputEditText etMarkNote;
    protected Spinner spinner;

    private String headerDate;

    private CalendarVM model;

    protected int sdlPosId;
    private CheckBox chbSdlPrime;
    private final ScheduleArray sdlList = new ScheduleArray();

    public PopupAdd(int layout) {
        super(layout);
        this.layout = layout;
        init();
    }

    public PopupAdd(int layout, int sqlId) {
        super(layout);
        this.layout = layout;
        this.sqlId = sqlId;
        init();

    }

    private  void init() {
        headerDate = ((TextView) context.findViewById(R.id.tv_slider_title)).getText().toString();
        model = ViewModelProviders.of(MainActivity.instance).get(CalendarVM.class);
        pw = super.show();
        layoutSettings(pw.getContentView());
    }


    protected void layoutSettings(View popupView) {
        TextView tvHeader = popupView.findViewById(R.id.tv_popupHeader);
        tvHeader.setText(headerDate);

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
            chbSdlPrime = popupView.findViewById(R.id.chb_SdlPrime);
            Button btnSdlSave = popupView.findViewById(R.id.btn_sdlSave);
            btnSdlSave.setOnClickListener(this);
            ReadSDLs();
            setSpinnerAdapter();

        }
    }

    protected void setSpinnerAdapter() {

        ArrayAdapter<String> sdlSpinnerAdapter = new ArrayAdapter<String>(context, R.layout.sdl_list_tvitem, sdlList.getNames());
        spinner.setAdapter(sdlSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                sdlPosId = pos;
                Log.d(CalendarVM.TAG, "onItemSelected: itemID = "+pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void ReadSDLs() {
        //TODO: Create SDL List : ПЕРЕПИСАТЬ
        sdlList.add(new Schedule("График1", context.getString(R.string.schedule1)));
        sdlList.add(new Schedule("График2", context.getString(R.string.schedule2)));
        sdlList.add(new Schedule("График3", context.getString(R.string.schedule1)));
        sdlList.add(new Schedule("График4", context.getString(R.string.schedule2)));
        sdlList.add(new Schedule("График5", context.getString(R.string.schedule1)));
        sdlList.add(new Schedule("График6", context.getString(R.string.schedule2)));
        //*********************
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

    private void saveMark() {
        String text = etMarkNote.getText().toString();
        int t = Time.toInt(tvAddTime.getText().toString());
        ParseDate pd = new ParseDate(headerDate);
        DBMarks dbMarks = new DBMarks(context);

        dbMarks.save(pd.getY(), pd.getM(), pd.getD(), t, text);
        model.getFODLiveData();
        model.updInfoList();
        model.setSliderState(true);
    }

    private void saveSdl() {
        Schedule sdl = sdlList.get(sdlPosId);
        Log.d(CalendarVM.TAG, "onItemSelected: save  "+sdl.getName()+" ("+sdl.getSdl()+")");
        DBSchedules dbSdl = new DBSchedules(context);
        ParseDate pd = new ParseDate(headerDate);
        dbSdl.save(pd.getY(), pd.getM(), pd.getD(), sdl.getName(), sdl.getSdl(), chbSdlPrime.isChecked());
        model.getFODLiveData();
    }

}
