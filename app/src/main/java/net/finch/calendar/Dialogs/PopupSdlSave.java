package net.finch.calendar.Dialogs;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import net.finch.calendar.R;
import net.finch.calendar.SDLEditor.SdlEditorActivity;
import net.finch.calendar.Schedules.Schedule;
import net.finch.calendar.Settings;

import org.json.JSONException;


@RequiresApi(api = Build.VERSION_CODES.N)
public class PopupSdlSave extends PopupView {
    final private static int layout = R.layout.popup_sdl_save;

    Context ctx;
    AppCompatActivity activity;
    Schedule sdl;

    PopupWindow pw;


    public PopupSdlSave(Context ctx, Schedule sdl) {
        super(ctx, layout, SdlEditorActivity.ROOT_ID);

        this.ctx = ctx;
        this.activity = (AppCompatActivity) ctx;
        this.sdl = sdl;
        pw = super.show();
        layoutSettings(pw.getContentView());
    }

    private void layoutSettings(View pwView) {
        TextView tvHeader = pwView.findViewById(R.id.tv_popupHeader);
        tvHeader.setText("Сохранение графика");

        final EditText etName = pwView.findViewById(R.id.popupSave_et_name);

        Button btnCancel = pwView.findViewById(R.id.btn_save_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });

        Button btnSave = pwView.findViewById(R.id.btn_save_confirm);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdl.setName(etName.getText().toString());

                try {
                    new Settings(ctx).saveSchedule(sdl.serialize());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pw.dismiss();
            }
        });
    }

}
