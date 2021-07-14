package net.finch.calendar.Schedules;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import net.finch.calendar.CalendarVM;
import net.finch.calendar.Dialogs.PopupDel;
import net.finch.calendar.Dialogs.PopupEdit;
import net.finch.calendar.MainActivity;
import net.finch.calendar.R;

import static net.finch.calendar.CalendarVM.TAG;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ShiftListHolder extends TreeNode.BaseNodeViewHolder<Shift> implements View.OnClickListener {
    View view;
    ImageView ivbMenu;
//    ImageView ivbDel;
//    ImageView ivbEdit;

    CalendarVM model;

    public ShiftListHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, Shift value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.shift_item, null, false);

        Log.d(TAG, "createNodeView: sql_ID = "+value.getDb_id());
        TextView sqlId = view.findViewById(R.id.tv_sdl_sqlId);
        sqlId.setText(""+value.getDb_id());

        TextView tvSdlName = view.findViewById(R.id.tv_item_sdlName);
        tvSdlName.setText(value.getSdlName());
//        tvSdlName.setBackgroundColor(value.getColor());
        TextView tvShift = view.findViewById(R.id.tv_item_shift);
        tvShift.setText(String.valueOf(value.getShiftName()));
        tvShift.setBackgroundColor(value.getColor());

        ivbMenu = view.findViewById(R.id.ivb_shiftMenu);
        ivbMenu.setOnClickListener(this);

        view.findViewById(R.id.iv_btn_sdlDel).setOnClickListener(this);
//        ivbDel.setOnClickListener(this);

        view.findViewById(R.id.iv_btn_sdlEdit).setOnClickListener(this);
//        ivbEdit.setOnClickListener(this);

        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.ivb_shiftMenu) onMenuBtnClick(v);
        else {
            View menu = (View) v.getParent();
            TextView tvSqlId = menu.findViewById(R.id.tv_sdl_sqlId);
            int sqlId = Integer.parseInt(tvSqlId.getText().toString());


            if (vid == R.id.iv_btn_sdlDel) onSdlDelBtnClick(sqlId);
            else if (vid == R.id.iv_btn_sdlEdit) onSdlEditBtnClick(sqlId);
        }
        
    }

//    public int dpToPx(int dp) {
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
//    }

    public void onMenuBtnClick(View v) {
        ConstraintLayout vg = (ConstraintLayout) v.getParent().getParent();
        View item = vg.findViewById(R.id.ll_shiftItem);
        View menu = vg.getViewById(R.id.ll_shiftItemMenu);

        TransitionManager.beginDelayedTransition(vg);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) item.getLayoutParams();

        if (params.getMarginEnd() > 0) {
            params.width = 0;
            Log.d(TAG, "onClick: with = "+params.rightMargin);
            params.rightMargin = 0;
        }
        else {
            Log.d(TAG, "onClick: margin = "+params.rightMargin);
            params.width = item.getWidth();
            params.rightMargin = (menu.getWidth()+20)*2;
        }

        item.setLayoutParams(params);
    }

    public void onSdlDelBtnClick(int sqlId) {
        model = ViewModelProviders.of((MainActivity)MainActivity.getContext()).get(CalendarVM.class);
        model.setSliderState(false);
        new PopupDel(PopupDel.SDL_DEL, sqlId);
    }

    public void onSdlEditBtnClick(int sqlId) {
        new PopupEdit(PopupEdit.SCHEDULE, sqlId);
    }
}
