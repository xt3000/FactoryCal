package net.finch.calendar.Marks;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import net.finch.calendar.CalendarVM;
import net.finch.calendar.Dialogs.PopupDel;
import net.finch.calendar.MainActivity;
import net.finch.calendar.Marks.Mark;
import net.finch.calendar.R;

import static net.finch.calendar.CalendarVM.TAG;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MarkListHolder extends TreeNode.BaseNodeViewHolder<Mark> implements View.OnClickListener {
    public MarkListHolder(Context context) {
        super(context);
    }

    ImageView ivMenu;
    ImageView ivbDel;
    CalendarVM model;

    @Override
    public View createNodeView(TreeNode node, Mark value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.mark_item, null, false);

        TextView sqlId = view.findViewById(R.id.tv_mark_sqlId);
        sqlId.setText(""+value.getDB_id());

        TextView tvTime = view.findViewById(R.id.tv_item_markTime);
        tvTime.setText(value.getTime());
        TextView tvInfo = view.findViewById(R.id.tv_item_markDesc);
        tvInfo.setText(value.getInfo());

        ivMenu = view.findViewById(R.id.ivb_markMenu);
        ivMenu.setOnClickListener(this);

        ivbDel = view.findViewById(R.id.iv_btn_markDel);
        ivbDel.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ivb_markMenu:
                onMenuBtnClick(v);
                break;
            case (R.id.iv_btn_markDel):
                View menu = (View) v.getParent();
                TextView mrkId = menu.findViewById(R.id.tv_mark_sqlId);
                onSdlDelBtnClick(Integer.parseInt(mrkId.getText().toString()));
                break;
            case R.id.iv_btn_sdlEdit:
                onSdlEditBtnClick();
                break;
        }
    }

    public void onMenuBtnClick(View v) {
        ConstraintLayout vg = (ConstraintLayout) v.getParent().getParent();
        View item = vg.findViewById(R.id.ll_markItem);
        View menu = vg.getViewById(R.id.ll_markItemMenu);

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

    private void onSdlDelBtnClick(int sqlId) {
        model = ViewModelProviders.of((MainActivity)MainActivity.getContext()).get(CalendarVM.class);
        model.setSliderState(false);
        new PopupDel(PopupDel.MRK_DEL, sqlId);
    }

    private void onSdlEditBtnClick() {

    }
}
