package net.finch.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import net.finch.calendar.Marks.Mark;

public class MarkListHolder extends TreeNode.BaseNodeViewHolder<Mark> {
    public MarkListHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, Mark value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.mark_item, null, false);
        TextView tvTime = view.findViewById(R.id.tv_item_markTime);
        tvTime.setText(value.time);
        TextView tvInfo = view.findViewById(R.id.tv_item_markDesc);
        tvInfo.setText(value.info);

        return view;
    }
}
