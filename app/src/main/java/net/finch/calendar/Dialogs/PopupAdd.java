package net.finch.calendar.Dialogs;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import net.finch.calendar.CalendarVM;
import net.finch.calendar.MainActivity;
import net.finch.calendar.Marks.DBMarks;
import net.finch.calendar.ParseDate;
import net.finch.calendar.R;
import net.finch.calendar.SDLEditor.SdlEditorActivity;
import net.finch.calendar.Schedules.DBSchedules;
import net.finch.calendar.Schedules.Schedule;
import net.finch.calendar.Settings.SDLSettings;
import net.finch.calendar.Time;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;


public class PopupAdd extends PopupView implements TextView.OnEditorActionListener, View.OnClickListener {
    public final static int MARK = R.layout.popup_mark_add;
    public final static int SCHEDULE = R.layout.popup_schedule_add;
    private final int rootId;
    protected AppCompatActivity activity;
    protected final int layout;
    protected int sqlId;
    private PopupWindow pw;
    private final Calendar mTime = new GregorianCalendar();
    protected Button btnMarkConfirm;
    protected TextView tvAddTime;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    protected TextInputEditText etMarkNote;
    protected Spinner spinner;
    private String headerDate;
    protected CalendarVM model;
    protected String sdlSelected;
    protected CheckBox chbSdlPrime, chbNewDay;
    private ArrayList<Schedule> sdlList = new ArrayList<>();




    public PopupAdd(Context ctx, int layout) {
        super(ctx, layout);
        this.activity = (AppCompatActivity) ctx;
        this.layout = layout;

        if(activity.getClass().equals(MainActivity.class)) rootId = MainActivity.ROOT_ID;
        else rootId = SdlEditorActivity.ROOT_ID;
        init(rootId);
    }

    public PopupAdd(Context ctx, int layout, int sqlId) {
        super(ctx, layout);
        this.activity = (AppCompatActivity) ctx;
        this.layout = layout;
        this.sqlId = sqlId;

        if(activity.getClass().equals(MainActivity.class)) rootId = MainActivity.ROOT_ID;
        else rootId = SdlEditorActivity.ROOT_ID;
        init(rootId);

    }

    private  void init(int rootId) {
        try {
            sdlList = new SDLSettings(activity).getSdlArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        headerDate = ((TextView) activity.findViewById(R.id.tv_slider_title)).getText().toString();
        model = MainActivity.getCalendarVM();
        pw = super.show(rootId);
        layoutSettings(pw);
    }

    @Override
    protected void layoutSettings(PopupWindow pw) {
        super.layoutSettings(pw);

        chbNewDay = pwView.findViewById(R.id.chb_newDayStart);

        TextView tvHeader = pwView.findViewById(R.id.tv_popupHeader);
        tvHeader.setText(headerDate);

        if (layout == MARK) {
            btnMarkConfirm = pwView.findViewById(R.id.btn_markConfirm);
            btnMarkConfirm.setOnClickListener(this);

            etMarkNote = pwView.findViewById(R.id.et_markNote);
            etMarkNote.setOnEditorActionListener(this);

            tvAddTime = pwView.findViewById(R.id.tv_addTime);
            tvAddTime.setOnClickListener(this);
            setTimeListener();
        }else {
            spinner = pwView.findViewById(R.id.sp_sdlAdd);
            chbSdlPrime = pwView.findViewById(R.id.chb_SdlPrime);
            Button btnSdlSave = pwView.findViewById(R.id.btn_sdlSave);
            btnSdlSave.setOnClickListener(this);
            setSpinnerAdapter();

        }
    }

    protected void setSpinnerAdapter() {


        final ArrayList<String> sdlNames = getSdlNames(sdlList);
        sdlNames.add(activity.getResources().getString(R.string.sdl_add_new));
        ArrayAdapter<String> sdlSpinnerAdapter = new ArrayAdapter<>(activity, R.layout.sdl_spiner_tvitem, sdlNames);
        spinner.setAdapter(sdlSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                if (pos == sdlNames.size()-1) {
                    pw.dismiss();
                    activity.startActivity(new Intent(activity, SdlEditorActivity.class));
                }
                sdlSelected = sdlNames.get(pos);
//                sdlPosId = pos;
//                Log.d(CalendarVM.TAG, "onItemSelected: itemID = "+pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                Log.d(TAG, "onNothingSelected: ");
            }
        });
    }


    public ArrayList<String> getSdlNames(ArrayList<Schedule> sdlArray) {
        ArrayList<String> list = new ArrayList<>();

        for (Schedule sdl : sdlArray) {
            list.add(sdl.getName());
        }
        return list;
    }



    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

        return false;
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case (R.id.btn_markConfirm):
//                Log.d(CalendarVM.TAG, "onClick: btn_markConfirm");
                    saveMark();
                pw.dismiss();
                break;
            case (R.id.tv_addTime):
//                Log.d(CalendarVM.TAG, "onClick: setTime");
                new TimePickerDialog(activity, timeSetListener,
                        mTime.get(Calendar.HOUR_OF_DAY),
                        mTime.get(Calendar.MINUTE), true)
                        .show();
                break;
            case (R.id.btn_sdlSave):
//                Log.d(TAG, "onClick: btn_sdlSave");
                saveSdl();
                pw.dismiss();
        }
    }




    private void setTimeListener() {
        timeSetListener = (view, hourOfDay, minute) -> {
            mTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mTime.set(Calendar.MINUTE, minute);

            tvAddTime.setText(DateUtils.formatDateTime(activity,
                    mTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
        };
    }

    private void saveMark() {
        String text = Objects.requireNonNull(etMarkNote.getText()).toString();
        int t = Time.toInt(tvAddTime.getText().toString());
        ParseDate pd = new ParseDate(headerDate);
        DBMarks dbMarks = new DBMarks(activity);

        dbMarks.save(pd.getY(), pd.getM(), pd.getD(), t, text);
        model.getFODLiveData(null);
//        model.updInfoList();
//        model.setSliderState(true);
    }

    protected void saveSdl() {
        Schedule sdl = getSdlByName(sdlSelected);
        DBSchedules dbSdl = new DBSchedules(activity);
        ParseDate pd = new ParseDate(headerDate);
        if (chbNewDay.getVisibility() == View.VISIBLE && !chbNewDay.isChecked()) dbSdl.save(sdl.getName(), sdl.getSdl(), chbSdlPrime.isChecked());
            else dbSdl.save(pd.getY(), pd.getM(), pd.getD(), sdl.getName(), sdl.getSdl(), chbSdlPrime.isChecked());

        model.getFODLiveData(null);
    }

    private Schedule getSdlByName(String sdlSelected) {
        if (sdlSelected != null) {
            for (Schedule sdl : sdlList) {
                if (sdlSelected.equals(sdl.getName())) return sdl;
            }
        }
        return null;
    }

}
