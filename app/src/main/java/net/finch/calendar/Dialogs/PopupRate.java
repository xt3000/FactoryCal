package net.finch.calendar.Dialogs;

import android.content.Context;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import net.finch.calendar.MainActivity;
import net.finch.calendar.R;
import net.finch.calendar.Settings.HSettings;

public class PopupRate extends PopupView {

    final static int LAYOUT_ID_ABOUT = R.layout.popup_rate;
    Context ctx;

    public PopupRate(Context ctx) {
        super(ctx, LAYOUT_ID_ABOUT);
        this.ctx = ctx;
        PopupWindow pw = super.show(MainActivity.ROOT_ID);
        layoutSettings(pw);
    }

    @Override
    protected void layoutSettings(PopupWindow pw) {
//        super.layoutSettings(pw);
        pwView = pw.getContentView();



// *** RATE NOW ***
        (pwView.findViewById(R.id.btn_rate_now)).setOnClickListener(v -> {
            pw.dismiss();
            new HSettings(ctx).rdcZero();
            // TODO: Go to PlayMarket URL...
        });

// *** RATE LATER ***
        (pwView.findViewById(R.id.btn_rate_later)).setOnClickListener(v -> {
            pw.dismiss();
            ((AppCompatActivity)ctx).finish();
        });

// *** RATE NEVER ***
        (pwView.findViewById(R.id.btn_rate_never)).setOnClickListener(v -> {
            new HSettings(ctx).rdcZero();
            pw.dismiss();
            ((AppCompatActivity)ctx).finish();
        });
    }
}
