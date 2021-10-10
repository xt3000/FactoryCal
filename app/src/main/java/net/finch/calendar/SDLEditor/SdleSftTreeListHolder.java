//package net.finch.calendar.SDLEditor;
//
//import android.annotation.SuppressLint;
////import android.app.Activity;
////import android.arch.lifecycle.LifecycleOwner;
////import android.arch.lifecycle.LiveData;
////import android.arch.lifecycle.Observer;
////import android.arch.lifecycle.ViewModelProviders;
//import android.content.Context;
//import android.os.Build;
////import android.support.annotation.Nullable;
////import android.support.annotation.RequiresApi;
////import android.support.v4.app.FragmentActivity;
////import android.support.v7.app.AppCompatActivity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//import androidx.annotation.RequiresApi;
//import androidx.cardview.widget.CardView;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.Observer;
//
//import com.unnamed.b.atv.model.TreeNode;
//
//import net.finch.calendar.Dialogs.ColorPiker;
//import net.finch.calendar.R;
//import net.finch.calendar.Schedules.Shift;
//
//import java.util.Map;
//
//public class SdleSftTreeListHolder extends TreeNode.BaseNodeViewHolder<String> implements View.OnClickListener {
//    View view;
//    TextView tvSft;
//    TextView tvNum;
//    ImageView ivbColor;
//    ImageView ivbDel;
//    CardView cvBgColor;
//
//    SdleVM colorsModel;
//    LiveData<Map<String, Integer>> colorsLD;
//
//    public SdleSftTreeListHolder(Context context) {
//        super(context);
//    }
//
//    @SuppressLint("InflateParams")
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    public View createNodeView(final TreeNode node, final String value) {
//        final LayoutInflater inflater = LayoutInflater.from(context);
//        view = inflater.inflate(R.layout.sdle_shiftlist_item, null, false);
//        tvSft = view.findViewById(R.id.sdle_sftList_item_sftText);
//        tvNum = view.findViewById(R.id.sdle_sftList_item_tv_num);
//        ivbColor = view.findViewById(R.id.sdle_sftList_item_ivb_color);
//        ivbDel = view.findViewById(R.id.sdle_sftList_item_ivb_del);
//        cvBgColor = view.findViewById(R.id.sdle_sftList_item_cv_background);
//
//        ivbColor.setOnClickListener(this);
//        ivbDel.setOnClickListener(this);
//        tvSft.setOnClickListener(this);
//
//        colorsModel = SdlEditorActivity.getSdleVM();
//        colorsLD = colorsModel.getColorsLD(null);
//        colorsLD.observe((SdlEditorActivity)context, new Observer<Map<String, Integer>>() {
//            @Override
//            public void onChanged(@Nullable Map<String, Integer> colorMap) {
//                setValues(value, colorMap.get(value), node);
//            }
//        });
//
//        return view;
//    }
//
//
//
//    private void setValues(String sft, int color, TreeNode node) {
//        tvSft.setText(Shift.shiftNameOf(sft.charAt(0)));
//        tvNum.setText(String.valueOf(node.getId()));
//        cvBgColor.setCardBackgroundColor(color);
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.sdle_sftList_item_ivb_color:
//                ColorPiker.Bilder(context, "U", colorsModel.getCurrentSftColor("U"));
//                break;
//            case R.id.sdle_sftList_item_sftText:
//                nextSft();
//                break;
//            case R.id.sdle_sftList_item_ivb_del:
//                break;
//        }
//    }
//
//    private void nextSft() {
//    }
//}
