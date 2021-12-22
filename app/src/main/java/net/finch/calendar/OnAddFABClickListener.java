package net.finch.calendar;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.transition.Fade;
import androidx.transition.Scene;
import androidx.transition.Slide;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OnAddFABClickListener implements View.OnClickListener {

    FrameLayout flFabMenu;
    private final FloatingActionButton fabMark;
    private final FloatingActionButton fabSdl;
    private  FloatingActionButton addFAB;

    private final Scene scOn;
    private final Scene scOff;

    private final Animation rotateOn;
    private final Animation rotateOff;

    protected static boolean isMenuOn = false;


    OnAddFABClickListener() {
        rotateOn = AnimationUtils.loadAnimation(MainActivity.getContext(), R.anim.rotate_on);
        rotateOff = AnimationUtils.loadAnimation(MainActivity.getContext(), R.anim.rotate_off);
        isMenuOn = false;

        flFabMenu = MainActivity.getContext().findViewById(R.id.fl_fabMenu);
        fabMark = MainActivity.getContext().findViewById(R.id.fab_mark);
        fabSdl= MainActivity.getContext().findViewById(R.id.fab_sdl);
        ViewGroup root = MainActivity.getContext().findViewById(R.id.fl_fabMenu_root);

        scOn = Scene.getSceneForLayout(root, R.layout.fab_layout_on, MainActivity.getContext());
        scOff = Scene.getSceneForLayout(root, R.layout.fab_layout_off, MainActivity.getContext());

    }


    @Override
    public void onClick(View view) {
//        Log.d(TAG, "onFABClick: ");
        addFAB = (FloatingActionButton) view;
        fabAddMenuClick();

    }


    public void fabAddMenuClick() {
//        Log.d(TAG, "fabAddMenu: isRotate = "+ isMenuOn);
        if (!isMenuOn) isMenuOn = menuOn();
        else isMenuOn = menuOff();
    }

    private boolean menuOn() {
        addFAB.startAnimation(rotateOn);
        fabSdl.setClickable(true);
        fabMark.setClickable(true);
        TransitionManager.go(scOn, tSetOn());
        return true;
    }

    private boolean menuOff() {
        addFAB.startAnimation(rotateOff);
        fabSdl.setClickable(false);
        fabMark.setClickable(false);
        TransitionManager.go(scOff, tSetOff());
        return false;
    }

    private TransitionSet tSetOn() {
        TransitionSet tSet = getTransitionSet();
        tSet.addTransition(new Fade().setStartDelay(tSet.getDuration()/3));
        tSet.addTransition(new Slide(Gravity.TOP));
        return tSet;
    }

    private TransitionSet tSetOff() {
        TransitionSet tSet = getTransitionSet();
        tSet.addTransition(new Fade());
        tSet.addTransition(new Slide(Gravity.TOP).setStartDelay(tSet.getDuration()/3));
        return tSet;
    }

    private TransitionSet getTransitionSet() {
        TransitionSet tSet = new TransitionSet();
        tSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
        tSet.setDuration(300);
        tSet.setInterpolator(new DecelerateInterpolator());
        return tSet;
    }
}
