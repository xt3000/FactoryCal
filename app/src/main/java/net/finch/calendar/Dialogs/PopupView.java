package net.finch.calendar.Dialogs;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import net.finch.calendar.R;

@RequiresApi(api = Build.VERSION_CODES.N)
public class PopupView {
    private final AppCompatActivity activity;
    private final int layoutId;
    private final int rootId;

    public PopupView(Context ctx, int layoutId, int rootId) {
        this.activity = (AppCompatActivity) ctx;
        this.layoutId = layoutId;
        this.rootId = rootId;
    }

    public PopupWindow show() {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View popupView = inflater.inflate(layoutId, null, false);
        PopupWindow pw = new PopupWindow(popupView, 800, 600, true);
        pw.setAnimationStyle(R.style.popup_animations);
        pw.showAtLocation(activity.findViewById(rootId), Gravity.CENTER, 0, 0);

        return pw;
    }
}
