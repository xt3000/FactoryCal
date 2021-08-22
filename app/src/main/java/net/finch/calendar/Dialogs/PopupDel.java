package net.finch.calendar.Dialogs;

//import android.arch.lifecycle.ViewModelProviders;
//import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.os.Build;
//import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import net.finch.calendar.CalendarVM;
import net.finch.calendar.DB;
import net.finch.calendar.MainActivity;
import net.finch.calendar.Marks.DBMarks;
import net.finch.calendar.R;
import net.finch.calendar.Schedules.DBSchedules;

@RequiresApi(api = Build.VERSION_CODES.N)
public class PopupDel extends PopupView implements View.OnClickListener {
    final static public int SDL_DEL = 0;
    final static public int MRK_DEL = 1;
    final private static int layoutDel = R.layout.popup_delete;

    private AppCompatActivity activity;
    private int type;
    private int sqlId;
    private PopupWindow pw;
    private CalendarVM model;
    private DB[] db = {new DBSchedules(activity), new DBMarks(activity)};

    private String headerDate;
    private String sdlName;
    private TextView tvHeader;
    private TextView tvDelText;
    private Button btnDel;

    public PopupDel(Context ctx, int type, int sqlId) {
        super(ctx, layoutDel, MainActivity.ROOT_ID);
        this.activity = (AppCompatActivity) ctx;
        this.type = type;
        this.sqlId = sqlId;

        model = MainActivity.getCalendarVM();
        headerDate = ((TextView) activity.findViewById(R.id.tv_slider_title)).getText().toString();
        pw = super.show();
        layoutSettings(pw.getContentView());
    }

    private void layoutSettings(View popupView) {
        tvHeader = popupView.findViewById(R.id.tv_popupHeader);
        tvHeader.setText(headerDate);

        tvDelText = popupView.findViewById(R.id.tv_popup_del_text);
        if (type == SDL_DEL) {
            String name = new DBSchedules(activity).readSdlName(sqlId);
            tvDelText.setText("Вы уверены, что хотите удалить график \""+name+"\" из календаря?");
        }
        else tvDelText.setText("Вы уверены, что хотите удалить это событие из календаря?");

        btnDel = popupView.findViewById(R.id.btn_del_confirm);
        btnDel.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_del_confirm) {
            db[type].delete(sqlId);
        }
        model.getFODLiveData();
        pw.dismiss();
    }
}
