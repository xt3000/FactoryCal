package net.finch.calendar.Dialogs;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import net.finch.calendar.MainActivity;
import net.finch.calendar.MenuHolder;
import net.finch.calendar.R;
import net.finch.calendar.SdlEditorActivity;

import java.io.StringWriter;
import java.util.ArrayList;

import static net.finch.calendar.CalendarVM.TAG;

public class PopupMenu implements TreeNode.TreeNodeClickListener {
    private final MainActivity context = (MainActivity) MainActivity.getContext();
    private final int layout = R.layout.popup_menu;

    private final String[] menuList = {"Добавить свой график", "Настройки","О приложении"};

    private View view;
    private  PopupWindow pw;


    public PopupMenu(View view) {
        this.view = view;
    }


    public PopupWindow show() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        final View popupView = inflater.inflate(layout, null, false);
        final FrameLayout viewGroup = popupView.findViewById(R.id.ll_mainMenu);


        TreeNode menuRoot = TreeNode.root();
//        menuRoot.setClickListener(this);
        for (String item : menuList) {
            menuRoot.addChild(new TreeNode(item).setViewHolder(new MenuHolder(context)).setClickListener(this));
        }
        AndroidTreeView treeView = new AndroidTreeView(context, menuRoot);
        viewGroup.addView(treeView.getView());

        pw = new PopupWindow(popupView, -2, -2, true);
        pw.showAsDropDown(view, 0, 6, Gravity.END);
        pw.update();

        return pw;
    }


//  Menu item Click
    @Override
    public void onClick(TreeNode node, Object value) {
        if (value.equals(menuList[0])) {
            pw.dismiss();
            Intent intent = new Intent(context, SdlEditorActivity.class);
            context.startActivity(intent);
        }
    }
}
