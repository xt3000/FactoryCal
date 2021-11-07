package net.finch.calendar.Dialogs;

import android.content.Context;
import android.util.Log;
import android.widget.PopupWindow;

import net.finch.calendar.Marks.DBMarks;
import net.finch.calendar.Marks.Mark;
import net.finch.calendar.R;
import net.finch.calendar.Time;

import org.json.JSONException;

import java.util.Objects;

import static net.finch.calendar.CalendarVM.TAG;

public class PopupMarkEdit extends PopupAdd{
    public PopupMarkEdit(Context ctx, int sqlId) throws JSONException {
        super(ctx, PopupAdd.MARK, sqlId);
        Log.d(TAG, "PopupMarkEdit: ");
    }

    @Override
    protected void layoutSettings(PopupWindow pw) throws JSONException {
        super.layoutSettings(pw);

        DBMarks db = new DBMarks(activity);
        Mark mrk = db.readMark(sqlId);
        tvAddTime.setText(mrk.getTime());
        etMarkNote.setText(mrk.getInfo());

        btnMarkConfirm.setOnClickListener((v)->{
            if (v.getId() == (R.id.btn_markConfirm)) {
                mrk.setInfo(Objects.requireNonNull(etMarkNote.getText()).toString());
                mrk.setTime(tvAddTime.getText().toString());

                boolean isSaved = db.update(mrk);
                Log.d(TAG, "layoutSettings: mrkSaved - "+isSaved);
                try {
                    model.getFODLiveData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                model.updInfoList();
                model.setSliderState(true);
            }
            pw.dismiss();
        });
    }

}
