package net.finch.calendar.Dialogs;

import android.content.Context;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import net.finch.calendar.MainActivity;
import net.finch.calendar.R;
import net.finch.calendar.SDLEditor.SdlEditorActivity;
import net.finch.calendar.SDLEditor.SdleVM;
import net.finch.calendar.Schedules.Schedule;
import net.finch.calendar.Settings.SDLSettings;
import org.json.JSONException;
import java.util.ArrayList;


@RequiresApi(api = Build.VERSION_CODES.N)
public class PopupSdlCreate extends PopupView {
    final private static int layout = R.layout.popup_sdl_create;

    SdleVM sdleModel = SdlEditorActivity.getSdleVM();
    Context ctx;
    AppCompatActivity activity;
    Schedule sdl;
    PopupWindow pw;
    ArrayList<String> sdlNamesArr = new ArrayList<>();


    public PopupSdlCreate(Context ctx, Schedule sdl) {
        super(ctx, layout);

        this.ctx = ctx;
        this.activity = (AppCompatActivity) ctx;
        this.sdl = sdl;

        int rootId;
        if(activity.getClass().equals(MainActivity.class)) rootId = MainActivity.ROOT_ID;
        else rootId = SdlEditorActivity.ROOT_ID;
        pw = super.show(rootId);
        layoutSettings(pw);
    }


    @Override
    protected void layoutSettings(PopupWindow pw) {
        super.layoutSettings(pw);
        try {
            sdlNamesArr = new SDLSettings(activity).getSdlNames();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView tvHeader = pwView.findViewById(R.id.tv_popupHeader);
        tvHeader.setText(ctx.getString(R.string.popup_sdl_create));

        final EditText etName = pwView.findViewById(R.id.popup_create_et_name);
        final TextInputLayout tiLayout = pwView.findViewById(R.id.popup_create_textinput_layout);

        Button btnCancel = pwView.findViewById(R.id.btn_save_cancel);
        btnCancel.setOnClickListener(v -> pw.dismiss());

        Button btnSave = pwView.findViewById(R.id.btn_save_confirm);
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            if (name.equals("")) {
                tiLayout.setError(ctx.getText(R.string.err_empty_field));
                return;
            }else if (isUsesName(name)) {
                tiLayout.setError(ctx.getText(R.string.err_sdl_exists));
                return;
            }
            tiLayout.setError(null);
            pw.dismiss();
            sdl.setName(name);
            sdleModel.setEditorMode(SdlEditorActivity.sftMODE);
            sdleModel.getSftsLD(sdl);

            sdleModel.getSdlsListLD();

//

        });
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
