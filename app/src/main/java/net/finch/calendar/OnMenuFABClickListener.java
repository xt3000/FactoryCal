package net.finch.calendar;

import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import net.finch.calendar.Dialogs.PopupMenu;

public class OnMenuFABClickListener implements View.OnClickListener {
    private final MainActivity context = (MainActivity) MainActivity.getContext();

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_mainMenu){
            new PopupMenu(v).show();
        }
    }
}
