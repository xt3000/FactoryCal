package net.finch.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

public class AppBarBehavior extends AppBarLayout.Behavior {
    private boolean locked = true;

    public AppBarBehavior() {
        super();
    }

    public AppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, @NonNull View directTargetChild, View target, int nestedScrollAxes, int type) {
        if (locked) return false;       // Lock when content scrolled
        else return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type);
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, @NonNull MotionEvent ev) {
        if (locked) return false;       // Lock when appBar swiped
        else return super.onTouchEvent(parent, child, ev);
    }

    public void lock(boolean locked) {
        this.locked = locked;
    }
}
