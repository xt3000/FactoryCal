package net.finch.calendar.Dialogs;

//import android.arch.lifecycle.ViewModelProviders;
//import android.database.sqlite.SQLiteOpenHelper;
import android.annotation.SuppressLint;
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
import net.finch.calendar.SDLEditor.SdlEditorActivity;
import net.finch.calendar.Schedules.DBSchedules;

import org.json.JSONException;

@RequiresApi(api = Build.VERSION_CODES.N)
public class PopupWarning extends PopupView implements View.OnClickListener {
    final static public int SDL_DEL = 0;
    final static public int MRK_DEL = 1;
    final private static int layoutDel = R.layout.popup_warning;
    final private int rootId;

    private AppCompatActivity activity;
    private int type;
    private int sqlId;
    private PopupWindow pw;
    private CalendarVM model;
    private DB[] db = {new DBSchedules(MainActivity.getContext()), new DBMarks(MainActivity.getContext())};

    private String headerDate;
    private TextView tvHeader, tvDelText;
    String text;
    private Button btnOk, btnCancel;

    private OnPositiveClickListener positiveClickListener;
    private OnNegativeClickListener negativeClickListener;

    public PopupWarning(Context ctx, String text) throws JSONException {
        super(ctx, layoutDel);
        this.text = text;
        this.activity = (AppCompatActivity) ctx;
        if(activity.getClass().equals(MainActivity.class)) rootId = MainActivity.ROOT_ID;
        else rootId = SdlEditorActivity.ROOT_ID;

        model = MainActivity.getCalendarVM();
//        headerDate = ((TextView) activity.findViewById(R.id.tv_slider_title)).getText().toString();
        pw = super.show(rootId);
        layoutSettings(pw);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void layoutSettings(PopupWindow pw) throws JSONException {
        super.layoutSettings(pw);

//        tvHeader = pwView.findViewById(R.id.tv_popupHeader);
//        tvHeader.setText(headerDate);

        tvDelText = pwView.findViewById(R.id.tv_popup_del_text);
        if (type == SDL_DEL) {

            tvDelText.setText(text);
        }
        else tvDelText.setText(text);

    }



    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.btn_del_confirm) {
//            db[type].delete(sqlId);
//        }
//        try {
//            model.getFODLiveData();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        if (v.getId() == R.id.btn_wern_confirm && positiveClickListener != null) {
            try {
                positiveClickListener.onClick();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        pw.dismiss();
    }



//// CALLBACK BUTTONS ////
    public void setOnPositiveClickListener(String btnText, OnPositiveClickListener positiveClickListener) {
        this.positiveClickListener = positiveClickListener;
        btnOk = pwView.findViewById(R.id.btn_wern_confirm);
        btnOk.setOnClickListener(this);
        if (!btnText.equals("")) btnOk.setText(btnText);
        btnOk.setVisibility(View.VISIBLE);
    }

    public void setOnNegativeClickListener(String btnText, OnNegativeClickListener negativeClickListener) {
        this.negativeClickListener = negativeClickListener;
        btnCancel = pwView.findViewById(R.id.btn_wern_cancel);
        btnCancel.setOnClickListener(this);
        if (!btnText.equals("")) btnCancel.setText(btnText);
        btnCancel.setVisibility(View.VISIBLE);
    }

    public interface OnPositiveClickListener {
        void onClick() throws JSONException;

    }

    public interface OnNegativeClickListener {
        void onClick();
    }
}