package net.finch.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class Utils {
    public static float dpToPx(Context ctx, float dp) {
        Resources r = ctx.getResources();
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
    }
}
