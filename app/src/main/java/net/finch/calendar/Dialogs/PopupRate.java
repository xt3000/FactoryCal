package net.finch.calendar.Dialogs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

// *** RATE NOW ***
        (pwView.findViewById(R.id.btn_rate_now)).setOnClickListener(v -> {
            pw.dismiss();
            new HSettings(ctx).noRate();
            // TODO: Go to PlayMarket URL...
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(
                    ctx.getString(R.string.market_url)));
            intent.setPackage("com.android.vending");
            ctx.startActivity(intent);
        });

// *** RATE LATER ***
        (pwView.findViewById(R.id.btn_rate_later)).setOnClickListener(v -> {
            pw.dismiss();
            ((AppCompatActivity)ctx).finish();
        });

// *** RATE NEVER ***
        (pwView.findViewById(R.id.btn_rate_never)).setOnClickListener(v -> {
            new HSettings(ctx).noRate();
            pw.dismiss();
            ((AppCompatActivity)ctx).finish();
        });
    }
}
