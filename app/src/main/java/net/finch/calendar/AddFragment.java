package net.finch.calendar;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
//import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import net.finch.calendar.Marks.DBMarks;
import net.finch.calendar.Schedules.DBSchedules;
import net.finch.calendar.Schedules.Schedule;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment implements TextView.OnEditorActionListener, View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PAGE = "ARG_PAGE";

    // TODO: Rename and change types of parameters
    private TextInputEditText etMarkNote;
    private TextView tvSliderTitle;
    private TextView tvAddTime;
    private String text;
    private DBMarks dbMarks;
    private Calendar mTime = new GregorianCalendar();
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private Button btnSdlSave;

    private int pageParam;
    private int markLayout = R.layout.popup_mark_create;
    private int scheduleLayout = R.layout.popup_schedule_create;
    private int sdlId;
//    private String[] sdlNamesList = {"График1", "График2", "График3", "График4"};
    private ScheduleArray sdlList = new ScheduleArray();
    private ArrayAdapter<String> sdlSpinnerAdapter;
//    private SdlSpinnerAdapter sdlSpinnerAdapter;

    private View view;
    private CalendarVM model;
    private LiveData<Boolean> SSdata;




    public AddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MarkCreateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance(int param1) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageParam = getArguments().getInt(ARG_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        model = ViewModelProviders.of(MainActivity.instance).get(CalendarVM.class);     //  getActivity()
        tvSliderTitle = getActivity().findViewById(R.id.tv_slider_title);

        if (pageParam == 0) view = markView(inflater, container);
        else view = scheduleView(inflater, container);



        return view;
    }





//**** Create MarkView Fragment ****
    private View markView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(markLayout, container, false);
        Button btnMarkConfirm = v.findViewById(R.id.btn_markConfirm);
		btnMarkConfirm.setOnClickListener(this);

        etMarkNote = v.findViewById(R.id.et_markNote);
		etMarkNote.setOnEditorActionListener(this);

		tvAddTime = v.findViewById(R.id.tv_addTime);
		tvAddTime.setOnClickListener(this);
        setTimeListener();

        SSdata = model.getSStateLiveData();
        SSdata.observe(MainActivity.instance, new Observer<Boolean>() {         //  getActivity()
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                etMarkNote.clearFocus();
                etMarkNote.setText(null);
                MainActivity.hideKeyboard(getActivity());

            }
        });
        return v;
    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_markConfirm:
                Log.d(CalendarVM.TAG, "onClick: btn_markConfirm");
                saveMark();
                break;
            case R.id.tv_addTime:
                Log.d(CalendarVM.TAG, "onClick_setTime: ");
                new TimePickerDialog(getActivity(), timeSetListener,
                        mTime.get(Calendar.HOUR_OF_DAY),
                        mTime.get(Calendar.MINUTE), true)
                        .show();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            saveMark();
            Log.d(CalendarVM.TAG, "onEditorAction: " + actionId);
            return true;
        }

        return false;
    }

    void saveMark() {
        text = etMarkNote.getText().toString();
        MainActivity.hideKeyboard(MainActivity.getContext());
        etMarkNote.setText(null);

        int t = Time.toInt(tvAddTime.getText().toString());

        ParseDate pd = new ParseDate(tvSliderTitle.getText().toString());
        dbMarks = new DBMarks(getActivity());
        dbMarks.saveDayMark(pd.getY(), pd.getM(), pd.getD(), t, text);
        model.getFODLiveData();
        model.updInfoList();
        model.setSliderState(true);
    }

    private void setTimeListener() {
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mTime.set(Calendar.MINUTE, minute);

                tvAddTime.setText(DateUtils.formatDateTime(getActivity(),
                        mTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
            }
        };
    }


    //**** Create ScheduleView Fragment ****
    private View scheduleView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(scheduleLayout, container, false);

//        sdlList.add(new Schedule("График1", getString(R.string.schedule1), true));
//        sdlList.add(new Schedule("График2", getString(R.string.schedule2), false));
//        sdlList.add(new Schedule("График3", getString(R.string.schedule1)));
//        sdlList.add(new Schedule("График4", getString(R.string.schedule2)));
//        sdlList.add(new Schedule("График5", getString(R.string.schedule1)));
//        sdlList.add(new Schedule("График6", getString(R.string.schedule2)));

        Spinner spinner = v.findViewById(R.id.sp_sdlAdd);
        btnSdlSave = v.findViewById(R.id.btn_sdlSave);

        sdlSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.sdl_list_tvitem, sdlList.getNames());
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

        btnSdlSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Schedule sdl = sdlList.get(sdlId);
                Log.d(CalendarVM.TAG, "onItemSelected: save  "+sdl.getName()+" ("+sdl.getSdl()+")");
                DBSchedules dbSdl = new DBSchedules(getActivity());
                ParseDate pd = new ParseDate(tvSliderTitle.getText().toString());
                dbSdl.saveSchedule(pd.getY(), pd.getM(), pd.getD(), sdl.getName(), sdl.getSdl(),false);
            }
        });

        return v;
    }





    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.hideKeyboard(MainActivity.getContext());
    }


}