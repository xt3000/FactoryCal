package net.finch.calendar.Dialogs;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
//import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Build;
//import android.support.annotation.RequiresApi;
//import android.support.design.widget.TextInputEditText;
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

import androidx.annotation.RequiresApi;
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

import static net.finch.calendar.CalendarVM.TAG;

@RequiresApi(api = Build.VERSION_CODES.N)
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

    protected int sdlPosId;
    protected String sdlSelected;
    protected CheckBox chbSdlPrime, chbNewDay;
    private ArrayList<Schedule> sdlList = new ArrayList<>();




    public PopupAdd(Context ctx, int layout) throws JSONException {
        super(ctx, layout);
        this.activity = (AppCompatActivity) ctx;
        this.layout = layout;

        if(activity.getClass().equals(MainActivity.class)) rootId = MainActivity.ROOT_ID;
        else rootId = SdlEditorActivity.ROOT_ID;
        init(rootId);
    }

    public PopupAdd(Context ctx, int layout, int sqlId) throws JSONException {
        super(ctx, layout);
        this.activity = (AppCompatActivity) ctx;
        this.layout = layout;
        this.sqlId = sqlId;

        if(activity.getClass().equals(MainActivity.class)) rootId = MainActivity.ROOT_ID;
        else rootId = SdlEditorActivity.ROOT_ID;
        init(rootId);

    }

    private  void init(int rootId) throws JSONException {
        headerDate = ((TextView) activity.findViewById(R.id.tv_slider_title)).getText().toString();
        model = MainActivity.getCalendarVM();
        pw = super.show(rootId);
        layoutSettings(pw);
    }

    @Override
    protected void layoutSettings(PopupWindow pw) throws JSONException {
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
            ReadSDLs();
            setSpinnerAdapter();

        }
    }

    protected void setSpinnerAdapter() {
        final ArrayList<String> sdlNames = getSdlNames(sdlList);
        ArrayAdapter<String> sdlSpinnerAdapter = new ArrayAdapter<String>(activity, R.layout.sdl_spiner_tvitem, sdlNames);
        spinner.setAdapter(sdlSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                sdlSelected = sdlNames.get(pos);
//                sdlPosId = pos;
                Log.d(CalendarVM.TAG, "onItemSelected: itemID = "+pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "onNothingSelected: ");
            }
        });
    }

    private void ReadSDLs() throws JSONException {
        sdlList = new SDLSettings(activity).getSdlArray();
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
                new TimePickerDialog(activity, timeSetListener,
                        mTime.get(Calendar.HOUR_OF_DAY),
                        mTime.get(Calendar.MINUTE), true)
                        .show();
                break;
            case R.id.btn_sdlSave:
                Log.d(TAG, "onClick: btn_sdlSave");
                try {
                    saveSdl();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        try {
            model.getFODLiveData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        model.updInfoList();
        model.setSliderState(true);
    }

    protected void saveSdl() throws JSONException {
        Schedule sdl = getSdlByName(sdlSelected);
//        Schedule sdl = sdlList.get(sdlPosId); //  TODO: ERR:при PopupEdit всегда равна 0
//        Log.d(CalendarVM.TAG, "onItemSelected: pos = "+sdlPosId+" save  "+sdl.getName()+" ("+sdl.getSdl()+")");
        DBSchedules dbSdl = new DBSchedules(activity);
        ParseDate pd = new ParseDate(headerDate);
        Log.d(TAG, "saveSdl: prime = "+chbSdlPrime.isChecked());
        if (chbNewDay.getVisibility() == View.VISIBLE && !chbNewDay.isChecked()) dbSdl.save(sdl.getName(), sdl.getSdl(), chbSdlPrime.isChecked());
            else dbSdl.save(pd.getY(), pd.getM(), pd.getD(), sdl.getName(), sdl.getSdl(), chbSdlPrime.isChecked());



        model.getFODLiveData();
        model.updInfoList();
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
