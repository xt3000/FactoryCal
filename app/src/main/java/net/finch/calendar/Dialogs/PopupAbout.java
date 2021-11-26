package net.finch.calendar.Dialogs;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.widget.PopupWindow;

import net.finch.calendar.MainActivity;
import net.finch.calendar.R;

import static net.finch.calendar.CalendarVM.TAG;


public class PopupAbout extends PopupView {
    final static int LAYOUT_ID_ABOUT = R.layout.popup_about;
    Context ctx;

    public PopupAbout(Context ctx) {
        super(ctx, LAYOUT_ID_ABOUT);
        this.ctx = ctx;
        PopupWindow pw = super.show(MainActivity.ROOT_ID);
        layoutSettings(pw);
    }

    @Override
    protected void layoutSettings(PopupWindow pw) {
        super.layoutSettings(pw);

    }
}
