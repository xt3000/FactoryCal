package net.finch.calendar;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class onAddClickListener implements View.OnClickListener {
    protected LinearLayout.LayoutParams lpVisible = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    protected LinearLayout.LayoutParams lpGone = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
    protected MainActivity ma = (MainActivity) MainActivity.getContext();

    @Override
    public void onClick(View view) {
        LinearLayout llAdd = ma.findViewById(R.id.ll_Add);
        TextView tvAdd = (TextView) view;

        if (tvAdd.getText().equals("+")) {
            tvAdd.setText("-");
            llAdd.setLayoutParams(lpVisible);
        } else {
            tvAdd.setText("+");
            llAdd.setLayoutParams(lpGone);
        }



    }
}
