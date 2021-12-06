package net.finch.calendar.Marks;

//import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Build;
//import android.support.annotation.RequiresApi;
//import android.support.constraint.ConstraintLayout;
//import android.support.transition.TransitionManager;
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
import net.finch.calendar.Dialogs.PopupMarkEdit;
import net.finch.calendar.Dialogs.PopupWarning;
import net.finch.calendar.MainActivity;
import net.finch.calendar.R;
import net.finch.calendar.Utils;

import org.json.JSONException;

import static net.finch.calendar.CalendarVM.TAG;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MarkListHolder extends TreeNode.BaseNodeViewHolder<Mark> implements View.OnClickListener {
    private static View openedView = null;
    private boolean isLost;

    ImageView ivMenu, ivbDel, ivbEdit;
    CalendarVM model = MainActivity.getCalendarVM();


    public MarkListHolder(Context context, boolean isLost) {
        super(context);
        this.isLost = isLost;
    }



    @Override
    public View createNodeView(TreeNode node, Mark value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.mark_item, null, false);

        TextView sqlId = view.findViewById(R.id.tv_mark_sqlId);
        sqlId.setText(String.valueOf(value.getDB_id()));

        TextView tvTime = view.findViewById(R.id.tv_item_markTime);
        tvTime.setText(value.getTime());
        TextView tvInfo = view.findViewById(R.id.tv_item_markDesc);
        tvInfo.setText(value.getInfo());

        if (isLost) view.findViewById(R.id.rl_markItem).setBackgroundColor(context.getColor(R.color.bg_bottomHeader));

        ivMenu = view.findViewById(R.id.ivb_markMenu);
        ivMenu.setOnClickListener(this);

        ivbDel = view.findViewById(R.id.iv_btn_markDel);
        ivbDel.setOnClickListener(this);

        ivbEdit = view.findViewById(R.id.iv_btn_markEdit);
        ivbEdit.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == (R.id.ivb_markMenu)) onMenuBtnClick(v);
        else {
            View menu = (View) v.getParent();
            TextView mrkId = menu.findViewById(R.id.tv_mark_sqlId);
            int id =  Integer.parseInt(mrkId.getText().toString());

            if (vid == (R.id.iv_btn_markDel)) {
                Log.d(TAG, "onClick: btn_markDel");
                onSdlDelBtnClick(id);
            }else  if (vid == (R.id.iv_btn_markEdit)) {
                Log.d(TAG, "onClick: btn_markEdit");
                onMarkEditBtnClick(id);
            }
        }
    }

    public void onMenuBtnClick(View v) {
        if (openedView !=null) {
            ConstraintLayout vgOpen = (ConstraintLayout) openedView.getParent().getParent();
            View contentOpen = vgOpen.findViewById(R.id.ll_markItem_content);
            TransitionManager.beginDelayedTransition(vgOpen);
            ConstraintLayout.LayoutParams paramsOpen = (ConstraintLayout.LayoutParams) contentOpen.getLayoutParams();
            paramsOpen.width = 0;
            paramsOpen.rightMargin = 0;
            contentOpen.setLayoutParams(paramsOpen);
        }

        ConstraintLayout vg = (ConstraintLayout) v.getParent().getParent();
        View item = vg.findViewById(R.id.ll_markItem_content);
//        View menu = vg.getViewById(R.id.ll_markItem_menu);

        TransitionManager.beginDelayedTransition(vg);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) item.getLayoutParams();

        if (v.equals(openedView)) {
            openedView = null;
        }
        else {
            openedView = v;
            Log.d(TAG, "onClick: margin = "+params.rightMargin);
            params.width = item.getWidth();
            params.rightMargin = (int) Utils.dpToPx(context, 103f)*2;
        }

        item.setLayoutParams(params);
    }

    private void onSdlDelBtnClick(int sqlId) {
        PopupWarning pwarn = new PopupWarning(context, context.getText(R.string.del_calMark_text).toString());
        pwarn.setBgColor(PopupWarning.COLOR_ERROR);
        pwarn.setIcon(PopupWarning.ICON_DELETE);
        pwarn.setOnPositiveClickListener("", ()-> {
            new DBMarks(MainActivity.getContext()).delete(sqlId);
            model.getFODLiveData(null);
//            model.updInfoList();
        });
        pwarn.setOnNegativeClickListener("", ()->{});
    }

    private void onMarkEditBtnClick(int sqlId) {
        Log.d(TAG, "onMarkEditBtnClick: ");
        new PopupMarkEdit(context, sqlId);
    }


}
