package net.finch.calendar;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;

public class OnDayClickListener implements View.OnLongClickListener, View.OnClickListener {

    protected MainActivity ma;

    TextView tvSliderTitle;
    CalendarVM model;
    LiveData<ArrayList<DayInfo>> FODdata;
    ArrayList<DayInfo> fod;
    DBHelper db;
    DayInfo di;
    TreeNode listRoot;

    OnDayClickListener() {
        this.ma = (MainActivity) MainActivity.getContext();
        model = ViewModelProviders.of(ma).get(CalendarVM.class);
    }

    @Override
    public boolean onLongClick(View v) {
        model.setSliderState(false);
        db = new DBHelper(ma);
        DayInfo day = ma.frameOfDates.get(v.getId());
        boolean marked = day.isMarked();

        int y = day.getYear();
        int m = day.getMonth();
        int d = day.getDate();

        if(!marked) {
            db.saveDayMark(y, m, d, "");
        }else {
            db.deleteDayMark(y, m, d);
        }
        model.update();

        return true;
    }




    @Override
    public void onClick(View v) {
        AndroidTreeView treeView = null;
        LinearLayout.LayoutParams lpVisible = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lpGone = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        LinearLayout llAdd = ma.findViewById(R.id.ll_Add);
        TextView tvAdd = ma.findViewById(R.id.tv_btnAdd);
        LinearLayout llListInfo = ma.findViewById(R.id.ll_list);

        int id = v.getId();
        DayInfo di = ma.frameOfDates.get(id);
        tvSliderTitle = ma.findViewById(R.id.tv_slider_title);
        llListInfo.removeAllViews();

        if(!di.isMarked()) {
            tvAdd.setText("-");
            llAdd.setLayoutParams(lpVisible);
            // TODO: see MARK create slider
        }else {
            tvAdd.setText("+");
            llAdd.setLayoutParams(lpGone);
            // TODO: see MARK info slider

            listRoot = TreeNode.root();
            for (String info : di.getInfoList()) {
                InfoListItem infoItem = new InfoListItem(info);
                listRoot.addChild(new TreeNode(new InfoListItem(info)).setViewHolder(new InfoListHolder(ma)));
            }

            treeView = new AndroidTreeView(ma, listRoot);
            llListInfo.addView(treeView.getView());

        }

        tvSliderTitle.setText(ma.frameOfDates.get(id).getFullDateString());

        model.setSliderState(true);
    }

}
