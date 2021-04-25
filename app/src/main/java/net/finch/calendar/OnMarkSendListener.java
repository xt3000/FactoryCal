package net.finch.calendar;

import android.arch.lifecycle.ViewModelProviders;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OnMarkSendListener implements TextView.OnEditorActionListener, View.OnClickListener {

    protected MainActivity ma;
    CalendarVM model;
    EditText et;
    TextView tvSliderTitle;
    String text;
    DBHelper db;


    OnMarkSendListener() {
        this.ma = (MainActivity) MainActivity.getContext();
        model = ViewModelProviders.of(ma).get(CalendarVM.class);
        et = ma.findViewById(R.id.et_markNote);
        tvSliderTitle = ma.findViewById(R.id.tv_slider_title);
    }


    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            saveMark();
            return true;
        }

        Toast.makeText(MainActivity.getContext(), "actionId: " + actionId, Toast.LENGTH_SHORT).show();

        return false;
    }

    @Override
    public void onClick(View view) {
        saveMark();
    }


    void saveMark() {
        text = et.getText().toString();
        MainActivity.hideKeyboard(MainActivity.getContext());
        model.setSliderState(false);
//        Toast.makeText(MainActivity.getContext(), "Mark Saved: " + text, Toast.LENGTH_SHORT).show();

        String[] date = tvSliderTitle.getText().toString().split("\\.");
        int d = Integer.parseInt(date[0]);
        int m = Integer.parseInt(date[1]);
        int y = Integer.parseInt(date[2]);
        db = new DBHelper(ma);
        db.saveDayMark(y, m-1, d, text);
        model.update();
        Toast.makeText(MainActivity.getContext(), "Mark Saved :" +d+" "+m+" "+y, Toast.LENGTH_SHORT).show();
    }


}
