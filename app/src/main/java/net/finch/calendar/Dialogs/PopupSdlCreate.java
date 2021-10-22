package net.finch.calendar.Dialogs;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import net.finch.calendar.MainActivity;
import net.finch.calendar.R;
import net.finch.calendar.SDLEditor.SdlEditorActivity;
import net.finch.calendar.SDLEditor.SdleVM;
import net.finch.calendar.Schedules.Schedule;
import net.finch.calendar.Settings.SDLSettings;

import org.json.JSONException;

import java.util.ArrayList;

import static net.finch.calendar.CalendarVM.TAG;


@RequiresApi(api = Build.VERSION_CODES.N)
public class PopupSdlCreate extends PopupView {
    final private static int layout = R.layout.popup_sdl_create;
    private final int rootId;

    SdleVM sdleModel = SdlEditorActivity.getSdleVM();

    Context ctx;
    AppCompatActivity activity;
    Schedule sdl;

    PopupWindow pw;
    TextView tvInfo;

    ArrayList<String> sdlNamesArr;


    public PopupSdlCreate(Context ctx, Schedule sdl) throws JSONException {
        super(ctx, layout);

        this.ctx = ctx;
        this.activity = (AppCompatActivity) ctx;
        this.sdl = sdl;

        if(activity.getClass().equals(MainActivity.class)) rootId = MainActivity.ROOT_ID;
        else rootId = SdlEditorActivity.ROOT_ID;
        pw = super.show(rootId);
        layoutSettings(pw);
    }


    @Override
    protected void layoutSettings(PopupWindow pw) throws JSONException {
        super.layoutSettings(pw);

        tvInfo = pwView.findViewById(R.id.popup_save_tv_info);
        sdlNamesArr = new SDLSettings(activity).getSdlNames();

        TextView tvHeader = pwView.findViewById(R.id.tv_popupHeader);
        tvHeader.setText("Создание графика");

        final EditText etName = pwView.findViewById(R.id.popupSave_et_name);

        Button btnCancel = pwView.findViewById(R.id.btn_save_cancel);
        btnCancel.setOnClickListener(v -> pw.dismiss());

        Button btnSave = pwView.findViewById(R.id.btn_save_confirm);
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            if (name.equals("")) {
                setWarningInfo("Поле не должно быть пустым!");
                return;
            }else if (isUsesName(name)) {
                setWarningInfo("Такой график уже существует!");
                return;
            }
            pw.dismiss();
            sdl.setName(name);
            sdleModel.setEditorMode(SdlEditorActivity.sftMODE);
            sdleModel.getSftsLD(sdl);

            sdleModel.getSdlsListLD();

//

        });
    }

    private void setWarningInfo(String info) {
        Log.d(TAG, "setWarningInfo: "+info);
        tvInfo.setText(info);
        tvInfo.setVisibility(View.VISIBLE);
    }

    private Boolean isUsesName(String name) {
        if (sdlNamesArr != null) {
            for (String n : sdlNamesArr) {
                if (n.equals(name)) return true;
            }
        }

        return false;
    }
}
