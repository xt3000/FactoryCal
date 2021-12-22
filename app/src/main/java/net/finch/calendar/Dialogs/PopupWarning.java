package net.finch.calendar.Dialogs;


import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import net.finch.calendar.MainActivity;
import net.finch.calendar.R;
import net.finch.calendar.SDLEditor.SdlEditorActivity;
import org.json.JSONException;


public class PopupWarning extends PopupView implements View.OnClickListener {
    public static final int ICON_INFO = R.drawable.ic_outline_info_24;
    public static final int ICON_X = R.drawable.ic_round_clear_24;
    public static final int ICON_DELETE = R.drawable.ic_round_delete_forever_24;

    public static final int COLOR_INFO = R.color.colorInfo;
    public static final int COLOR_ERROR = R.color.colorError;

    final static public int SDL_DEL = 0;
    final static public int MRK_DEL = 1;
    final private static int layoutWarn = R.layout.popup_warning;

    private final AppCompatActivity activity;
    private final PopupWindow pw;

    private final CharSequence text;

    private OnPositiveClickListener positiveClickListener;
    private OnNegativeClickListener negativeClickListener;

    public PopupWarning(Context ctx, CharSequence text) {
        super(ctx, layoutWarn);
        this.text = text;
        this.activity = (AppCompatActivity) ctx;
        int rootId;
        if(activity.getClass().equals(MainActivity.class)) rootId = MainActivity.ROOT_ID;
        else rootId = SdlEditorActivity.ROOT_ID;

        pw = super.show(rootId);
        layoutSettings(pw);
    }


    @Override
    protected void layoutSettings(PopupWindow pw) {
        super.layoutSettings(pw);

        TextView tvDelText = pwView.findViewById(R.id.tv_popup_del_text);
//        if (type == SDL_DEL) {
//
//            tvDelText.setText(text);
//        }
//        else
            tvDelText.setText(text);

    }

    public void setBgColor(@ColorRes int colorResId) {
        ((CardView)pwView.findViewById(R.id.popup_cv_window)).setCardBackgroundColor(activity.getColor(colorResId));
    }

    public void setIcon(@DrawableRes int iconResId) {
        ((ImageView)pwView.findViewById(R.id.popupwarn_iv_icon)).setImageResource(iconResId);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_wern_confirm && positiveClickListener != null)
            positiveClickListener.onClick();
        pw.dismiss();
    }



//// CALLBACK BUTTONS ////
    public void setOnPositiveClickListener(String btnText, OnPositiveClickListener positiveClickListener) {
        this.positiveClickListener = positiveClickListener;
        Button btnOk = pwView.findViewById(R.id.btn_wern_confirm);
        btnOk.setOnClickListener(this);
        if (!btnText.equals("")) btnOk.setText(btnText);
        btnOk.setVisibility(View.VISIBLE);
    }

    public void setOnNegativeClickListener(String btnText, OnNegativeClickListener negativeClickListener) {
        this.negativeClickListener = negativeClickListener;
        Button btnCancel = pwView.findViewById(R.id.btn_wern_cancel);
        btnCancel.setOnClickListener(this);
        if (!btnText.equals("")) btnCancel.setText(btnText);
        btnCancel.setVisibility(View.VISIBLE);
    }

    public interface OnPositiveClickListener {
        void onClick();

    }

    public interface OnNegativeClickListener {
        void onClick();
    }
}
