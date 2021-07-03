package net.finch.calendar.Dialogs;

import android.os.Build;
import android.support.annotation.RequiresApi;

import net.finch.calendar.MainActivity;

public class PopupEdit {
    private MainActivity context;
    private int layout;

    PopupEdit(int layout) {
        this.context = (MainActivity) MainActivity.getContext();
        this.layout = layout;
    }
}
