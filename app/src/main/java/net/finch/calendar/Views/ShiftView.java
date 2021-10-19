package net.finch.calendar.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import net.finch.calendar.R;
import net.finch.calendar.Utils;

public class ShiftView extends CardView {
    public ShiftView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public ShiftView(@NonNull Context context, int bgColor) {
        super(context);
        int hw = (int)Utils.dpToPx(context, 14f);
        int m  = (int)Utils.dpToPx(context, 1f);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(hw, hw);
        params.setMargins(m, 0, m, 0);
        params.gravity = Gravity.CENTER;

        setLayoutParams(params);
        setCardBackgroundColor(bgColor);
        setCardElevation(0);
        setRadius((int)Utils.dpToPx(context, 4));
        setForeground(context.getDrawable(R.drawable.fg_trans_outline));
    }




}
