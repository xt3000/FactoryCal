package net.finch.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import static net.finch.calendar.CalendarVM.TAG;


public class ToolBarBehavior extends AppBarLayout.ScrollingViewBehavior {

    private static final float UNDEFINED = Float.MAX_VALUE;
    private float childStartY = UNDEFINED;

    public ToolBarBehavior() {
    }

    public ToolBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return getBottomSheetBehavior(dependency) != null;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        super.onDependentViewChanged(parent, child, dependency);
        AppBarLayout appBar = (AppBarLayout) child;

        BottomSheetBehavior bottomSheetBehavior = getBottomSheetBehavior(dependency);
        if (bottomSheetBehavior != null) {
            float slideOffset = getSlideOffset(parent, dependency, bottomSheetBehavior);
            if (slideOffset > -1) appBar.setExpanded(false,true);
            else appBar.setExpanded(true,true);

            if (slideOffset > 0) {
//                Log.d(TAG, "onDependentViewChanged: ");
//                appBar.setExpanded(false,true);


                child.setAlpha(1 - slideOffset);

                if (childStartY == UNDEFINED) {
                    childStartY = child.getY();
                }
                int childHeight = child.getMeasuredHeight();
                float childY = childStartY - (childHeight * slideOffset);
                child.setY(childY);
            }
        }
        return true;
    }

    private float getSlideOffset(CoordinatorLayout parent, View dependency, BottomSheetBehavior bottomSheetBehavior) {
        int parentHeight = parent.getMeasuredHeight();
        float sheetY = dependency.getY();
        int peekHeight = bottomSheetBehavior.getPeekHeight();
        int sheetHeight = dependency.getHeight();
        float collapseY = parentHeight - peekHeight;
        float expandY = parentHeight - sheetHeight;
        float deltaY = collapseY - expandY;

        return (parentHeight - peekHeight - sheetY) / deltaY;
    }

    @Nullable
    private BottomSheetBehavior getBottomSheetBehavior(@NonNull View view) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (behavior instanceof BottomSheetBehavior) {
            return (BottomSheetBehavior) behavior;
        }
        return null;
    }
}
