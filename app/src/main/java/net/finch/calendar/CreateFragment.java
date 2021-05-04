package net.finch.calendar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateFragment extends Fragment implements TextView.OnEditorActionListener, View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PAGE = "ARG_PAGE";

    // TODO: Rename and change types of parameters
    TextInputEditText etMarkNote;
    TextView tvSliderTitle;
    String text;
    DBHelper db;

    private int pageParam;
    private int markLayout = R.layout.fragment_mark_create;
    private int scheduleLayout = R.layout.fragment_schedule_create;
    private int id;

    View view;
    CalendarVM model;
    LiveData<Boolean> SSdata;



    public CreateFragment() {
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
    public static CreateFragment newInstance(int param1) {
        CreateFragment fragment = new CreateFragment();
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
        model = ViewModelProviders.of(getActivity()).get(CalendarVM.class);

        if (pageParam == 0) view = markView(inflater, container);
        else view = scheduleView(inflater, container);



        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.hideKeyboard(MainActivity.getContext());
    }

    private View markView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(markLayout, container, false);
        Button btnMarkConfirm = v.findViewById(R.id.btn_markConfirm);
		btnMarkConfirm.setOnClickListener(this);

        etMarkNote = v.findViewById(R.id.et_markNote);
		etMarkNote.setOnEditorActionListener(this);

        SSdata = model.getSStateLiveData();
        SSdata.observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                etMarkNote.clearFocus();
                etMarkNote.setText(null);
                MainActivity.hideKeyboard(getActivity());

            }
        });
        return v;
    }

    private View scheduleView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(scheduleLayout, container, false);


        return v;
    }

    @Override
    public void onClick(View view) {
        saveMark();
        Log.d(CalendarVM.TAG, "onClick: ");
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
        tvSliderTitle = MainActivity.getContext().findViewById(R.id.tv_slider_title);

        String[] date = tvSliderTitle.getText().toString().split("\\.");
        int d = Integer.parseInt(date[0]);
        int m = Integer.parseInt(date[1]);
        int y = Integer.parseInt(date[2]);
        db = new DBHelper(MainActivity.getContext());
        db.saveDayMark(y, m-1, d, text);
        model.getFODLiveData();
        model.updInfoList();
        model.setSliderState(true);
//        Toast.makeText(MainActivity.getContext(), "Mark Saved :" +d+" "+m+" "+y, Toast.LENGTH_SHORT).show();
    }
}