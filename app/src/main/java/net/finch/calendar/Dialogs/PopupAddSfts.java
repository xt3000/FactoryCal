package net.finch.calendar.Dialogs;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import net.finch.calendar.R;
import net.finch.calendar.SDLEditor.SdlEditorActivity;

import static net.finch.calendar.CalendarVM.TAG;

public class PopupAddSfts extends PopupView {
    final private static int layout = R.layout.popup_sdle_addsfts;
    int maxNum;
    Context ctx;
    PopupWindow pw;
    private final PopupAddSfts.OnPositiveClickListener positiveClickListener;

    public PopupAddSfts(Context ctx, int maxNum, PopupAddSfts.OnPositiveClickListener positiveClickListener) {
        super(ctx, layout);
        this.maxNum = maxNum;
        this.ctx = ctx;
        this.positiveClickListener = positiveClickListener;

        pw = super.show(SdlEditorActivity.ROOT_ID);
        layoutSettings(pw);
    }

    @Override
    protected void layoutSettings(PopupWindow pw) {
        super.layoutSettings(pw);

        ((TextView)pwView.findViewById(R.id.popup_addsfts_tv_text)).setText(ctx.getString(R.string.popup_addsfts_text, maxNum));
        ((TextView)pwView.findViewById(R.id.tv_popupHeader)).setText(ctx.getString(R.string.popup_addsfts_header));

        final EditText etNum = pwView.findViewById(R.id.popup_addsfts_et_num);
        final TextInputLayout tiLayout = pwView.findViewById(R.id.popup_addsfts_textinput_layout);

        Button btnCancel = pwView.findViewById(R.id.btn_addsfts_cancel);
        btnCancel.setOnClickListener(v -> pw.dismiss());

        Button btnSave = pwView.findViewById(R.id.btn_addsfts_confirm);
        btnSave.setOnClickListener(v -> {
            String n = etNum.getText().toString();
            if (n.equals("")) n = "0";
            int num = Integer.parseInt(n);
            if (num > maxNum) {
                tiLayout.setError(ctx.getString(R.string.err_more_then_max, maxNum));
            } else {
                pw.dismiss();
                positiveClickListener.onClick(num);
            }
        });
    }


    //  CALLBACK  //
    public interface OnPositiveClickListener {
        void onClick(int num);
    }
}
