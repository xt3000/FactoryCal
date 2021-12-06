package net.finch.calendar.Schedules;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.transition.TransitionManager;

import com.unnamed.b.atv.model.TreeNode;

import net.finch.calendar.CalendarVM;
import net.finch.calendar.Dialogs.PopupWarning;
import net.finch.calendar.Dialogs.PopupSdlEdit;
import net.finch.calendar.MainActivity;
import net.finch.calendar.R;
import net.finch.calendar.Utils;

import static net.finch.calendar.CalendarVM.TAG;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ShiftListHolder extends TreeNode.BaseNodeViewHolder<Shift> implements View.OnClickListener {
    private boolean isLost;

    @SuppressLint("StaticFieldLeak")
    private static View openedView = null;

    CalendarVM model = MainActivity.getCalendarVM();

    public ShiftListHolder(Context context, boolean isLost) {
        super(context);
        this.isLost = isLost;
    }

    @Override
    public View createNodeView(TreeNode node, Shift value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.shift_item, null, false);

        Log.d(TAG, "createNodeView: sql_ID = "+value.getDb_id());
        TextView sqlId = view.findViewById(R.id.tv_sdl_sqlId);
        sqlId.setText(""+value.getDb_id());

        TextView tvSdlName = view.findViewById(R.id.tv_item_sdlName);
        tvSdlName.setText(value.getSdlName());
        TextView tvShift = view.findViewById(R.id.tv_item_shift);
        tvShift.setText(String.valueOf(value.getShiftName()));
        tvShift.setBackgroundColor(value.getColor());

        ImageView ivbMenu = view.findViewById(R.id.ivb_shiftMenu);
        ivbMenu.setOnClickListener(this);
        if (isLost) view.findViewById(R.id.ll_shiftItem_menu).setBackgroundColor(context.getColor(R.color.bg_bottomHeader));

        view.findViewById(R.id.iv_btn_sdlDel).setOnClickListener(this);
        view.findViewById(R.id.iv_btn_sdlEdit).setOnClickListener(this);

        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.ivb_shiftMenu) onMenuBtnClick(v);
        else {
            View parent = (View) v.getParent().getParent();
            TextView tvSqlId = parent.findViewById(R.id.tv_sdl_sqlId);
            int sqlId = Integer.parseInt(tvSqlId.getText().toString());



            if (vid == R.id.iv_btn_sdlDel) {
                onSdlDelBtnClick(sqlId);
            }
            else if (vid == R.id.iv_btn_sdlEdit) {
                onSdlEditBtnClick(sqlId);
            }
        }
        
    }

    public void onMenuBtnClick(View v) {
        if (openedView !=null) {
            ConstraintLayout vgOpen = (ConstraintLayout) openedView.getParent().getParent();
            View contentOpen = vgOpen.findViewById(R.id.ll_shiftItem_content);
            TransitionManager.beginDelayedTransition(vgOpen);
            ConstraintLayout.LayoutParams paramsOpen = (ConstraintLayout.LayoutParams) contentOpen.getLayoutParams();
            paramsOpen.width = 0;
            paramsOpen.rightMargin = 0;
            contentOpen.setLayoutParams(paramsOpen);
        }

        ConstraintLayout vg = (ConstraintLayout) v.getParent().getParent();
        View item = vg.findViewById(R.id.ll_shiftItem_content);

        TransitionManager.beginDelayedTransition(vg);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) item.getLayoutParams();

        if (v.equals(openedView)) {
            openedView = null;
        }
        else {
            openedView = v;
            Log.d(TAG, "onClick: margin = "+params.rightMargin);
            params.width = item.getWidth();
            params.rightMargin = (int)Utils.dpToPx(context, 103f)*2;
        }

        item.setLayoutParams(params);
    }

    public void onSdlDelBtnClick(int sqlId) {
        String name = new DBSchedules(context).readSdlName(sqlId);
        PopupWarning pwarn = new PopupWarning(context, context.getText(R.string.del_calSdl_text_1)+name+context.getText(R.string.del_calSdl_text_2));
        pwarn.setBgColor(PopupWarning.COLOR_ERROR);
        pwarn.setIcon(PopupWarning.ICON_DELETE);
        pwarn.setOnPositiveClickListener("", ()-> {
            new DBSchedules(MainActivity.getContext()).delete(sqlId);
            model.getFODLiveData(null);
        });
        pwarn.setOnNegativeClickListener("", ()-> {});
    }

    public void onSdlEditBtnClick(int sqlId) {
        new PopupSdlEdit(context, sqlId);
    }
}
