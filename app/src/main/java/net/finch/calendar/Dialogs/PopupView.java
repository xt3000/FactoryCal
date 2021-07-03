package net.finch.calendar.Dialogs;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import net.finch.calendar.MainActivity;
import net.finch.calendar.R;

public class PopupView {
    private MainActivity context;
    private int layout;
    private PopupWindow pw;

    public PopupView(int layout) {
        this.context = (MainActivity) MainActivity.getContext();
        this.layout = layout;
    }

    public PopupWindow show() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(layout, null, false);
        pw = new PopupWindow(popupView, 800, 600, true);
        pw.setAnimationStyle(R.style.popup_animations);
        pw.showAtLocation(context.findViewById(R.id.main_layout), Gravity.CENTER, 0, 0);

        return pw;
    }
}
