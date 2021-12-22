package net.finch.calendar.Dialogs;

import android.content.Context;
import android.widget.PopupWindow;
import net.finch.calendar.Marks.DBMarks;
import net.finch.calendar.Marks.Mark;
import net.finch.calendar.R;
import java.util.Objects;


public class PopupMarkEdit extends PopupAdd{
    public PopupMarkEdit(Context ctx, int sqlId) {
        super(ctx, PopupAdd.MARK, sqlId);
    }

    @Override
    protected void layoutSettings(PopupWindow pw) {
        super.layoutSettings(pw);

        DBMarks db = new DBMarks(activity);
        Mark mrk = db.readMark(sqlId);
        tvAddTime.setText(mrk.getTime());
        etMarkNote.setText(mrk.getInfo());

        btnMarkConfirm.setOnClickListener((v)->{
            if (v.getId() == (R.id.btn_markConfirm)) {
                mrk.setInfo(Objects.requireNonNull(etMarkNote.getText()).toString());
                mrk.setTime(tvAddTime.getText().toString());

                db.update(mrk);
                model.getFODLiveData(null);
//                model.updInfoList();
//                model.setSliderState(true);
            }
            pw.dismiss();
        });
    }

}
