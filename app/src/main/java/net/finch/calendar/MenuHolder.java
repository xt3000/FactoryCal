package net.finch.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

public class MenuHolder extends TreeNode.BaseNodeViewHolder<String> {
    public MenuHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, String value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.menu_item, null, false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tvItem = view.findViewById(R.id.tv_menu_item);
        tvItem.setText(value);
        tvItem.setLayoutParams(params);

        return view;
    }

}
