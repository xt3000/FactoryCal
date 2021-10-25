package net.finch.calendar;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.transition.ChangeBounds;
import androidx.transition.Fade;
import androidx.transition.Scene;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static net.finch.calendar.CalendarVM.TAG;

public class SliderBehaviorCallback extends BottomSheetBehavior.BottomSheetCallback {
    FloatingActionButton afab;
    FrameLayout flFabMenu;
    CalendarVM model;
    LinearLayout llListInfo;
    AppBarLayout appBar;

    SliderBehaviorCallback() {
        this.afab = MainActivity.getContext().findViewById(R.id.afab_add);
        this.flFabMenu = MainActivity.getContext().findViewById(R.id.fl_fabMenu_root);
        this.llListInfo = MainActivity.getContext().findViewById(R.id.ll_markList);
        this.appBar = MainActivity.getContext().findViewById(R.id.main_appbar);

        this.model = MainActivity.getCalendarVM();
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        if(newState == BottomSheetBehavior.STATE_HIDDEN) model.setSliderState(false);
//        Log.d(TAG, "onStateChanged: newState"+newState);
        if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
            llListInfo.setNestedScrollingEnabled(false);
            llListInfo.setFocusable(false);
            llListInfo.setFocusableInTouchMode(false);
        }
        else llListInfo.setNestedScrollingEnabled(true);

//        if (newState == BottomSheetBehavior.STATE_COLLAPSED) appBar.setExpanded(false, true);
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//        Log.d(TAG, "onSlide: offset = "+slideOffset);

        float hiddenOffset = -0.8f;
        float idx = (slideOffset-hiddenOffset)/0.5f;

        // HIDDEN FAB
        if (slideOffset < -0.9) {
            afab.setVisibility(View.GONE);
            flFabMenu.setVisibility(View.GONE);
        }
        // VISIBLE FAB
        else {
            if (afab.getVisibility() != View.VISIBLE) afab.setVisibility(View.VISIBLE);
            if (flFabMenu.getVisibility() != View.VISIBLE) flFabMenu.setVisibility(View.VISIBLE);
        }

        // CLOSE MENU
        if (slideOffset < 0 && OnAddFABClickListener.isMenuOn) afab.callOnClick();

        // COLLAPSE STARTED
        if (slideOffset < -0.3f) {
            afab.animate().alpha(idx).scaleX(idx).scaleY(idx).setDuration(0).start();
        }
        // NOT COLLAPSE
        else {
            afab.animate().alpha(1f).scaleX(1).scaleY(1).setDuration(0).start();
        }

        appBar.setExpanded(!(slideOffset > -1), true);
    }


}
