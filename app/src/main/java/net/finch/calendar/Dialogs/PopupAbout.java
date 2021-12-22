package net.finch.calendar.Dialogs;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.finch.calendar.MainActivity;
import net.finch.calendar.R;


public class PopupAbout extends PopupView implements View.OnClickListener {
    final static int LAYOUT_ID_ABOUT = R.layout.popup_about;
    Context ctx;
    PopupWindow pw;
    private OnNoAdsClickListener noAdsClickListener;

    public PopupAbout(Context ctx) {
        super(ctx, LAYOUT_ID_ABOUT);
        this.ctx = ctx;
        pw = super.show(MainActivity.ROOT_ID);
        layoutSettings(pw);
    }

    @Override
    protected void layoutSettings(PopupWindow pw) {
        super.layoutSettings(pw);

        // TODO: BTN_NO_ADS listener
    }



    public void setOnNoAdsClickListener(OnNoAdsClickListener noAdsClickListener) {
        this.noAdsClickListener = noAdsClickListener;
        Button btnNoAds = pwView.findViewById(R.id.btn_no_ads);
        btnNoAds.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_no_ads && noAdsClickListener != null)
            noAdsClickListener.onClick();
        pw.dismiss();
    }

    public interface OnNoAdsClickListener {
        void onClick();

    }
}
