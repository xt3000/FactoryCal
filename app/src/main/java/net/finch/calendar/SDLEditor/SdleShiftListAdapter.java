//package net.finch.calendar.SDLEditor;
//
//import android.content.Context;
//import android.os.Build;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.Observer;
//import androidx.recyclerview.widget.RecyclerView;
//
//import net.finch.calendar.Dialogs.ColorPiker;
//import net.finch.calendar.R;
//import net.finch.calendar.Schedules.Schedule;
//import net.finch.calendar.Schedules.Shift;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
//import static net.finch.calendar.CalendarVM.TAG;
//
//@RequiresApi(api = Build.VERSION_CODES.N)
//public class SdleShiftListAdapter extends RecyclerView.Adapter<SdleShiftListAdapter.ViewHolder> {
//    private static ArrayList<SdleListObj> sftList;
//    private static Map<String, Integer> colorsMap;
//
//    private final SdleVM model;
//    private LiveData<Map<String, Integer>> colorsLD;
//    private LiveData<ArrayList<SdleListObj>> sdlLD;
//
//    private final Context ctx;
//    private int cnt;
//
//    public SdleShiftListAdapter(@NonNull Context ctx, ArrayList<SdleListObj> sftList) {
//        this.sftList = sftList;
//        this.ctx = ctx;
//
//        model = SdleEditorActivity_v2.getSdleVM();
//        colorsLD = model.getColorsLD(null);
//        sdlLD = model.getSdlLD(sftList);
//    }
//
//
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView tvNum, tvSft;
//        ImageView ivbDel, ivbColor;
//        ConstraintLayout clBG;
//
//        private final SdleVM model = SdleEditorActivity_v2.getSdleVM();
//        private LiveData<ArrayList<SdleListObj>> sdlLD;
//
//        public ViewHolder(final Context ctx, @NonNull View itemView) {
//            super(itemView);
//
//            setIsRecyclable(false);
//
//            tvNum = itemView.findViewById(R.id.sdle_sftList_item_tv_num);
//            tvSft = itemView.findViewById(R.id.sdle_sftList_item_sftText);
//            ivbDel = itemView.findViewById(R.id.sdle_sftList_item_ivb_del);
//            ivbColor = itemView.findViewById(R.id.sdle_sftList_item_ivb_color);
//            clBG = itemView.findViewById(R.id.sdle_sftList_item_cl_background);
//
//            tvSft.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    nextSft();
//                }
//            });
//            ivbColor.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String sft = Shift.shiftCharOf(tvSft.getText().toString()).toString();
//                    ColorPiker.Bilder(ctx, sft, colorsMap.get(sft));
//                }
//            });
//
//            ivbDel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//        }
//
//        private void nextSft() {
//            String sft = Objects.requireNonNull(Shift.shiftCharOf(tvSft.getText().toString())).toString();
//            Integer pos = getTruePos(getAdapterPosition());
//            Log.d(TAG, "nextSft: AdapterPos="+pos);
//
//            if (pos != null) {
//                SdleListObj obj = sftList.get(pos);
//                obj.setShift(Schedule.getNextSft(sft));
//                sftList.set(pos, obj);
//                model.setSdl(sftList);
//            }
//
//        }
//    }
//
//
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View view = inflater.inflate(R.layout.sdle_shiftlist_item,  parent, false);
//
//        return new ViewHolder(ctx, view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
//        Log.d(TAG, "onBindViewHolder: with payloads!!!!!!!!!!!!!!!!");
//
//        super.onBindViewHolder(holder, position, payloads);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
//        Log.d(TAG, "onBindViewHolder: ---> pos = "+position);
//        holder.setIsRecyclable(false);
//
//        if (position == 0) {
//            sftList = getTrueList();
//            SdleListObj obj = sftList.get(position);
//            obj.setPosition(position);
//            sftList.set(position, obj);
//            model.setSdl(sftList);
//        }
//
//        colorsLD.observe((AppCompatActivity)ctx, new Observer<Map<String, Integer>>() {
//            @Override
//            public void onChanged(Map<String, Integer> cMap) {
//                Log.d(TAG, "onChanged: colorMap");
//
//                colorsMap = cMap;
//                String s = sftList.get(position).getShift();
//                if (s != null && colorsMap.get(s) != null)
//                    holder.clBG.setBackgroundColor(colorsMap.get(s));
//            }
//        });
//
//        sdlLD.observe((AppCompatActivity)ctx, new Observer<ArrayList<SdleListObj>>() {
//            @Override
//            public void onChanged(ArrayList<SdleListObj> sList) {
//                Log.d(TAG, "onChanged: sdlList");
//
//                sftList = sList;
////                printList(sList, "sList / listPos = "+position);
//                if (position >= sList.size()) {
////                    Log.d(TAG, "onChanged: > remove Obs pos "+position);
//                    sdlLD.removeObservers((AppCompatActivity)ctx);
//                    return;
//                }
//
//
//                SdleListObj sft = sList.get(position);
//                if (sft.getPosition() != null) {
//                    holder.tvNum.setText(String.valueOf(sft.getPosition()+1));
//                    holder.tvSft.setText(Shift.shiftNameOf(sft.getShift().charAt(0)));
//                    String s = sft.getShift();
//                    if (s != null && colorsMap.get(s) != null)
//                        holder.clBG.setBackgroundColor(colorsMap.get(s));
//                }
//            }
//        });
//
//        holder.tvNum.setTag(sftList.get(position).getId());
//        holder.tvSft.setText(Shift.shiftNameOf(sftList.get(position).getShift().charAt(0)));
//
//    }
//
//    @Override
//    public int getItemCount() {
//        cnt = getTrueCount();
//        Log.d(TAG, "getItemCount > "+cnt);
//        return cnt;
//    }
//
//// ***   Change position of item    ***
//    public void onRowMoved(int fromPos, int toPos) {
//        Log.d(TAG, "onRowMoved: *******************************");
////        printList(sftList, "onRowMoved:  >");
//        Log.d(TAG, "onRowMoved: "+fromPos+" > "+toPos);
//        SdleListObj fromSft;
//        SdleListObj toSft;
//        if (fromPos < toPos) {      // перемещение вниз
//            for (int i=fromPos; i<toPos; i++) {
//                Integer trueFrom = getTruePos(i);
//                Integer trueTo = getTruePos(i+1);
//                Log.d(TAG, "onRowMoved: "+trueFrom+" > "+trueTo);
//                if (trueFrom!=null && trueTo!=null) {
//                    fromSft = sftList.get(trueFrom);
//                    toSft = sftList.get(trueTo);
//
//                    fromSft.setPosition(fromSft.getPosition()+1);
//                    sftList.set(trueFrom, fromSft);
//
//                    toSft.setPosition(toSft.getPosition()-1);
//                    sftList.set(trueTo, toSft);
//                }
//            }
//        }else {                     // перемещение вверх
//            for (int i=fromPos; i>toPos; i--) {
//                Integer trueFrom = getTruePos(i);
//                Integer trueTo = getTruePos(i-1);
//                Log.d(TAG, "onRowMoved: *true* "+trueFrom+" > "+trueTo);
//                if (trueFrom!=null && trueTo!=null) {
//                    fromSft = sftList.get(trueFrom);
//                    toSft = sftList.get(trueTo);
//
//                    fromSft.setPosition(fromSft.getPosition()-1);
//                    sftList.set(trueFrom, fromSft);
//
//                    toSft.setPosition(toSft.getPosition()+1);
//                    sftList.set(trueTo, toSft);
//                }
//            }
//        }
//
////        printList(sftList, "onRowMoved:  >");
//        model.setSdl(sftList);
//
//        Log.d(TAG, "notyfyMoved()");
//        notifyItemMoved(fromPos, toPos);
//        notifyItemChanged(fromPos);
//        notifyItemChanged(toPos);
//    }
//
//
//
//    // ***   Remove Item   ***
//    public void onRemoveItem(Integer pos) {
//        Log.d(TAG, "onRemoveItem: *******************");
////        printList(sftList, "sftList");
//        Integer truePos = getTruePos(pos);
//        if (truePos == null) {
//            Log.d(TAG, "onRemoveItem: ERR:NullPointerException > pos - "+pos);
//            return;
//        }
//        SdleListObj obj = sftList.get(truePos);
//        obj.setPosition(null);
//        sftList.set(truePos, obj);
//        for (int i=pos+1; i<sftList.size(); i++) {
//            Integer p = getTruePos(i);
////            Log.d(TAG, "onRemoveItem: i="+i+"; pos="+p);
//            if (p != null) {
//                obj = sftList.get(p);
//                obj.setPosition(obj.getPosition()-1);
//                sftList.set(p, obj);
////                Log.d(TAG, "                  > sft(p).pos="+sftList.get(p).getPosition());
//            }
//
//        }
//
////        printList(sftList, "sftList");
//        model.setSdl(sftList);
//        notifyItemRemoved(pos);
////        notifyDataSetChanged();
//
//        Log.d(TAG, "onRemoveItem: ///////////////////////////////");
//    }
//
//    private void printList(ArrayList<SdleListObj> sftList, String tag) {
//        Log.d(TAG, "printList ("+tag+"): ");
//        for (int i=0; i<sftList.size(); i++) {
//            Log.d(tag, "pos = "+i + "  id = " + sftList.get(i).getId()+" / truePos = "+sftList.get(i).getPosition());
//        }
//    }
//
//    //***   Add Item   ***
//    public void addItem(String newSft) {
//        if (newSft.length() == 1) {
////            sftList = getTrueList();
//            sftList.add(new SdleListObj(model.getNewId(), newSft, getItemCount()));
//            model.setSdl(sftList);
////            notifyDataSetChanged();
//            Log.d(TAG, "addItem: getitemcount: "+(getItemCount()-1)+" / sftlist.size: "+sftList.size()+" / cnt: "+cnt);
//            notifyItemInserted(getItemCount()-1);
//
//        }
//
//    }
//
//    private static Integer getTruePos(Integer pos) {
//        for (int i=0; i<sftList.size(); i++) {
//            if (pos.equals(sftList.get(i).getPosition())) return i;
//        }
//
//        return null;
//    }
//
//    private int getTrueCount() {
//        int count = 0;
//        for (SdleListObj obj : sftList) {
//            if (obj.getPosition() != null) count++;
//        }
//        return count;
//    }
//
//    public ArrayList<SdleListObj> getTrueList() {
//        for (int i = 0; i < sftList.size(); i++) {
//            if (sftList.get(i).getPosition() == null) sftList.remove(i);
//        }
//
//        ArrayList<SdleListObj> newList = new ArrayList<>();
//        for (int i = 0; i < sftList.size(); i++) {
//            for (int c = 0; c < sftList.size(); c++) {
//                if (sftList.get(c).getPosition() == i) {
//                    newList.add(sftList.get(c));
//                    break;
//                }
//            }
//        }
//        return newList;
//    }
//
//    public void clear() {
//        for (SdleListObj sft : sftList) {
//            if (sft.getPosition() !=null) onRemoveItem(sft.getPosition());
//        }
//        sftList = new ArrayList<>();
//        model.setSdl(sftList);
//    }
//
//    public Schedule getSchedule() {
//        StringBuilder sdl = new StringBuilder();
//        ArrayList<SdleListObj> list = getTrueList();
//        for (SdleListObj obj : list) {
//            sdl.append(obj.getShift());
//        }
//        return  new Schedule("", sdl.toString(), colorsMap);
//    }
//}
