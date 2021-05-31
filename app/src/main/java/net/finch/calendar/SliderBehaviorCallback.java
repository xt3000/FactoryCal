package net.finch.calendar;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.ChangeBounds;
import android.support.transition.Fade;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.getbase.floatingactionbutton.AddFloatingActionButton;

public class SliderBehaviorCallback extends BottomSheetBehavior.BottomSheetCallback {
    AddFloatingActionButton afab;
    FrameLayout flFabMenu;
    FrameLayout.LayoutParams lpFAB_mark;
    FrameLayout.LayoutParams lpFAB_sdl;
    FloatingActionButton fabMark;
    FloatingActionButton fabSdl;
    CalendarVM model;
    TransitionSet tSet;
    Scene scOff;

    SliderBehaviorCallback() {

        this.afab = MainActivity.getContext().findViewById(R.id.afab_add);
        this.flFabMenu = MainActivity.getContext().findViewById(R.id.fl_fabMenu_root);
        this.fabMark = MainActivity.getContext().findViewById(R.id.fab_mark);
        this.fabSdl = MainActivity.getContext().findViewById(R.id.fab_sdl);

        lpFAB_sdl = (FrameLayout.LayoutParams) fabSdl.getLayoutParams();
        this.model = ViewModelProviders.of(MainActivity.instance).get(CalendarVM.class);
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        if(newState == BottomSheetBehavior.STATE_COLLAPSED) model.setSliderState(false);
        Log.d(CalendarVM.TAG, "onStateChanged: newState"+newState);
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        float idx = (slideOffset-0.2f)/0.5f;
        if (slideOffset < 0.2f) {
            afab.setVisibility(View.GONE);
            afab.clearAnimation();
            flFabMenu.setVisibility(View.GONE);
            flFabMenu.clearAnimation();

            if (OnAddFABClickListener.isMenuOn) {
                ViewGroup root = MainActivity.getContext().findViewById(R.id.fl_fabMenu_root);
                tSet = new TransitionSet();
                tSet.addTransition(new Fade()).addTransition(new ChangeBounds());
                tSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
                tSet.setDuration(300);
                tSet.setInterpolator(new AccelerateDecelerateInterpolator());

                scOff = Scene.getSceneForLayout(root, R.layout.fab_layout_off, MainActivity.getContext());
                TransitionManager.go(scOff, tSet);

                OnAddFABClickListener.isMenuOn = false;
            }
        }
        else {
            afab.setVisibility(View.VISIBLE);
            flFabMenu.setVisibility(View.VISIBLE);

        }

        if (slideOffset < 0.7f) {
            afab.animate().alpha(idx).scaleX(idx).scaleY(idx).setDuration(0).start();
            flFabMenu.animate().alpha(idx).translationX((1-idx)*200).translationY((1-idx)*200).scaleX(idx).scaleY(idx).setDuration(0).start();
        }
        else {
            afab.animate().alpha(1f).scaleX(1).scaleY(1).setDuration(0).start();
            flFabMenu.animate().alpha(1f).translationX(0).translationY(0).scaleX(1).scaleY(1).setDuration(0).start();
        }

    }


}
