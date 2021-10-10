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
import net.finch.calendar.Dialogs.PopupWarning;
import net.finch.calendar.MainActivity;
import net.finch.calendar.R;
import net.finch.calendar.Utils;

import org.json.JSONException;

import static net.finch.calendar.CalendarVM.TAG;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MarkListHolder extends TreeNode.BaseNodeViewHolder<Mark> implements View.OnClickListener {
    private static View openedView = null;

    ImageView ivMenu;
    ImageView ivbDel;
    CalendarVM model = MainActivity.getCalendarVM();


    public MarkListHolder(Context context) {
        super(context);
    }



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
                try {
                    onSdlDelBtnClick(Integer.parseInt(mrkId.getText().toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.iv_btn_sdlEdit:
                onSdlEditBtnClick();
                break;
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
            params.rightMargin = (int) Utils.dpToPx(context, 84f)*2;
        }

        item.setLayoutParams(params);
    }

    private void onSdlDelBtnClick(int sqlId) throws JSONException {
        PopupWarning pwarn = new PopupWarning(context, context.getText(R.string.del_calMark_text).toString());
        pwarn.setOnPositiveClickListener("", ()-> {
            new DBMarks(MainActivity.getContext()).delete(sqlId);
            model.getFODLiveData();
            model.updInfoList();
        });
        pwarn.setOnNegativeClickListener("", ()->{});
    }

    private void onSdlEditBtnClick() {

    }
}
