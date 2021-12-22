package net.finch.calendar;

import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SliderBehaviorCallback extends BottomSheetBehavior.BottomSheetCallback {
    FloatingActionButton afab;
    FrameLayout flFabMenu;
    CalendarVM model;
    ConstraintLayout llContent;
    AppBarLayout appBar;

    SliderBehaviorCallback() {
        this.afab = MainActivity.getContext().findViewById(R.id.afab_add);
        this.flFabMenu = MainActivity.getContext().findViewById(R.id.fl_fabMenu_root);
        this.llContent = MainActivity.getContext().findViewById(R.id.main_ll_content);
        this.appBar = MainActivity.getContext().findViewById(R.id.main_appbar);

        this.model = MainActivity.getCalendarVM();
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        if(newState == BottomSheetBehavior.STATE_HIDDEN) model.setSliderState(false);
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

        // COLLAPSE & EXPENDED APPBAR
        appBar.setExpanded(!(slideOffset > -1), true);

        // SET ALPHA TO CONTENT
        if (slideOffset >= 0) llContent.setAlpha((float)Math.pow((1-slideOffset), 4));
    }

}
