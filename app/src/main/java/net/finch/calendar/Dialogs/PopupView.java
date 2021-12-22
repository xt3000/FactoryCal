package net.finch.calendar.Dialogs;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import net.finch.calendar.R;


public class PopupView {
    private final AppCompatActivity activity;
    private final int layoutId;
    protected View pwView;

    public PopupView(Context ctx, int layoutId) {
        this.activity = (AppCompatActivity) ctx;
        this.layoutId = layoutId;
    }

    public PopupWindow show(int rootId) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View popupView = inflater.inflate(layoutId, null, false);
        PopupWindow pw = new PopupWindow(popupView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT, true);
        pw.setAnimationStyle(R.style.popup_animations);
        pw.showAtLocation(activity.findViewById(rootId), Gravity.CENTER, 0, 0);

        CardView cvWindow = popupView.findViewById(R.id.popup_cv_window);
        cvWindow.setClickable(true);
        Animation windowUp = AnimationUtils.loadAnimation(activity, R.anim.popup_translate_up);
        cvWindow.startAnimation(windowUp);

        return pw;
    }

    protected void layoutSettings(PopupWindow pw) {
        pwView = pw.getContentView();
        FrameLayout flDarkBG = pwView.findViewById(R.id.popup_bg_dark);
        flDarkBG.setOnClickListener(v -> pw.dismiss());
    }
}
