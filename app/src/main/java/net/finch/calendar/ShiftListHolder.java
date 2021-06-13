package net.finch.calendar;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import net.finch.calendar.Schedules.Shift;

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
        tvShift.setText(String.valueOf(value.getShift()));
        tvShift.setBackgroundColor(value.getColor());
//        CardView cvShiftIyem = view.findViewById(R.id.cv_shiftItem);
//        cvShiftIyem.setCardBackgroundColor(value.getColor());

        return view;
    }
}
