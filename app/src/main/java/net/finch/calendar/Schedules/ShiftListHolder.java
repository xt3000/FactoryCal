package net.finch.calendar.Schedules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import net.finch.calendar.R;

public class ShiftListHolder extends TreeNode.BaseNodeViewHolder<Shift> {
    public ShiftListHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, Shift value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.shift_item, null, false);

        TextView tvSdlName = view.findViewById(R.id.tv_item_sdlName);
        tvSdlName.setText(value.getSdlName());
        tvSdlName.setBackgroundColor(value.getColor());
        TextView tvShift = view.findViewById(R.id.tv_item_shift);
        tvShift.setText(String.valueOf(value.getShiftName()));
        tvShift.setBackgroundColor(value.getColor());
        return view;
    }
}
