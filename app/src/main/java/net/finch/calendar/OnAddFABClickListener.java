package net.finch.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
//import android.support.design.widget.FloatingActionButton;
//import android.support.transition.ChangeBounds;
//import android.support.transition.Fade;
//import android.support.transition.Scene;
//import android.support.transition.TransitionManager;
//import android.support.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.transition.ChangeBounds;
import androidx.transition.Fade;
import androidx.transition.Scene;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static net.finch.calendar.CalendarVM.TAG;

public class OnAddFABClickListener implements View.OnClickListener {

    ConstraintLayout flFabMenu;
    @SuppressLint("StaticFieldLeak")
    private FloatingActionButton fabMark;
    @SuppressLint("StaticFieldLeak")
    private  FloatingActionButton fabSdl;
    private  FloatingActionButton addFAB;

    private  Scene scOn;
    private Scene scOff;
    private TransitionSet tSet;

    private  Animation rotateOn;
    private  Animation rotateOff;

    protected static boolean isMenuOn = false;


    OnAddFABClickListener() {
        rotateOn = AnimationUtils.loadAnimation(MainActivity.getContext(), R.anim.rotate_on);
        rotateOff = AnimationUtils.loadAnimation(MainActivity.getContext(), R.anim.rotate_off);
        isMenuOn = false;

        flFabMenu = MainActivity.getContext().findViewById(R.id.fl_fabMenu);
        fabMark = MainActivity.getContext().findViewById(R.id.fab_mark);
        fabSdl= MainActivity.getContext().findViewById(R.id.fab_sdl);
        ViewGroup root = MainActivity.getContext().findViewById(R.id.fl_fabMenu_root);

        tSet = new TransitionSet();
        tSet.addTransition(new Fade()).addTransition(new ChangeBounds());
        tSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
        tSet.setDuration(200);
        tSet.setInterpolator(new DecelerateInterpolator());

        scOn = Scene.getSceneForLayout(root, R.layout.fab_layout_on, MainActivity.getContext());
        scOff = Scene.getSceneForLayout(root, R.layout.fab_layout_off, MainActivity.getContext());

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Log.d(TAG, "onFABClick: ");
        addFAB = (FloatingActionButton) view;
        fabAddMenuClick();

    }


    public void fabAddMenuClick() {
        Log.d(TAG, "fabAddMenu: isRotate = "+ isMenuOn);
        if (!isMenuOn) isMenuOn = menuOn();
        else isMenuOn = menuOff();
    }

    private boolean menuOn() {
        addFAB.startAnimation(rotateOn);
        fabSdl.setClickable(true);
        fabMark.setClickable(true);
        TransitionManager.go(scOn, tSet);
        return true;
    }

    private boolean menuOff() {
        addFAB.startAnimation(rotateOff);
        fabSdl.setClickable(false);
        fabMark.setClickable(false);
        TransitionManager.go(scOff, tSet);
        return false;
    }
}
