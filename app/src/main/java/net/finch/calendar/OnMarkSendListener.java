package net.finch.calendar;

import android.arch.lifecycle.ViewModelProviders;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OnMarkSendListener implements TextView.OnEditorActionListener {

    protected MainActivity ma;
    CalendarVM model;

    OnMarkSendListener() {
        this.ma = (MainActivity) MainActivity.getContext();
        model = ViewModelProviders.of(ma).get(CalendarVM.class);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            saveMark();
            return true;
        }

        return false;
    }

    void saveMark() {
        MainActivity.hideKeyboard(MainActivity.getContext());
        model.setSliderState(false);
        Toast.makeText(MainActivity.getContext(), "Mark Saved", Toast.LENGTH_SHORT).show();
        // TODO: *************
    }
}
