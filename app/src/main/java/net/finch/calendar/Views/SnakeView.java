package net.finch.calendar.Views;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.snackbar.Snackbar;
import net.finch.calendar.R;

public class SnakeView {
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_INFO = 1;
    public static final int TYPE_ERROR = 2;

    public static final int[] ICONS = {R.drawable.ic_round_check_24, R.drawable.ic_outline_info_24, R.drawable.ic_round_clear_24};
    public static final int[] TEXTS = {R.string.snake_success, R.string.snake_info, R.string.snake_err};
    public static final int[] COLORS = {R.color.colorSucces, R.color.colorInfo, R.color.colorError};

    public static Snackbar make(View view, int resIcon, CharSequence text, int color) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Snackbar sbar = Snackbar.make(view, "", 4000);

        @SuppressLint("InflateParams") View customView = activity.getLayoutInflater().inflate(R.layout.snake_bar, null);

        ((ImageView) customView.findViewById(R.id.snake_iv_icon)).setImageResource(resIcon);
        ((TextView) customView.findViewById(R.id.snake_tv_text)).setText(text);
        ((CardView) customView.findViewById(R.id.snake_cv_background)).setCardBackgroundColor(activity.getColor(color));

        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) sbar.getView();
        snackbarLayout.removeAllViews();
        snackbarLayout.setPadding(0, 0, 0, 0);
        snackbarLayout.setBackgroundColor(Color.TRANSPARENT);

        snackbarLayout.addView(customView, 0);

        return sbar;
    }

    public static Snackbar make(View view, int type, int txtRes) {
        return make(view, ICONS[type], view.getContext().getString(txtRes), COLORS[type]);
    }

    public static Snackbar make(View view, int type, CharSequence text) {
        return make(view, ICONS[type], text, COLORS[type]);
    }
}
