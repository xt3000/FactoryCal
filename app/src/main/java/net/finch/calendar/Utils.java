package net.finch.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import com.google.android.gms.ads.AdSize;


public class Utils {
    public static float dpToPx(Context ctx, float dp) {
        Resources r = ctx.getResources();
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
    }

    public static AdSize getAdSize(Context context) {
        float widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
        float density = Resources.getSystem().getDisplayMetrics().density;
        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }
}
