package net.finch.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

public class MainListHolder extends TreeNode.BaseNodeViewHolder<MainBottomChapterObject> {

    public MainListHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, MainBottomChapterObject value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.main_bottom_listinfo_chapter, null, false);
        TextView tv = view.findViewById(R.id.main_bottom_listinfo_tv_chapter);
        tv.setText(value.getText());
        ImageView iv = view.findViewById(R.id.main_bottom_listinfo_iv_chapter);
        iv.setImageResource(value.getImgRes());
        return view;
    }

}
