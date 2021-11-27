package net.finch.calendar.Dialogs;

import android.content.Context;
import android.widget.Button;
import android.widget.PopupWindow;

import net.finch.calendar.R;
import net.finch.calendar.SDLEditor.SdlEditorActivity;

public class PopupSDLEHelp extends PopupView {
    public final static int layoutId_sdl = R.layout.popup_sdle_sdl_help;
    public final static int layoutId_sft = R.layout.popup_sdle_sft_help;

    public PopupSDLEHelp(Context ctx, int layoutId) {
        super(ctx, layoutId);
        PopupWindow pw = super.show(SdlEditorActivity.ROOT_ID);
        layoutSettings(pw);
    }

    @Override
    protected void layoutSettings(PopupWindow pw) {
        super.layoutSettings(pw);

        Button btnOk = pwView.findViewById(R.id.popup_btn_ok);
        if (btnOk != null) btnOk.setOnClickListener(v -> {pw.dismiss();});
    }
}
