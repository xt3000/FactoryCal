package net.finch.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

public class InfoListHolder extends TreeNode.BaseNodeViewHolder<InfoListItem> {
    public InfoListHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, InfoListItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.info_item, null, false);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_infoTime);
        tvTime.setText(value.time);
        TextView tvInfo = (TextView) view.findViewById(R.id.tv_infoDesc);
        tvInfo.setText(value.info);

        return view;
    }
}
