package net.finch.calendar.SDLEditor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import net.finch.calendar.R;
import net.finch.calendar.Schedules.Schedule;
import net.finch.calendar.Schedules.Shift;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import static net.finch.calendar.Schedules.Shift.sftChr;


public class SdleShiftListAdapter extends RecyclerView.Adapter<SdleShiftListAdapter.ViewHolder> {
    private final Context ctx;
    private static ArrayList<Integer> sftList;
    private static Map<String, Integer> colorsMap;
    private static final SdleVM model = SdlEditorActivity.getSdleVM();
    private static String sdlName;


    public SdleShiftListAdapter(@NonNull Context ctx, Schedule sdl) {
        SdleShiftListAdapter.sftList = sdl.getSftIdList();
        SdleShiftListAdapter.sdlName = sdl.getName();
        this.ctx = ctx;
    }

/////// ***  ViewHolder *** ///////
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvSftName;
        private final FrameLayout flBG;
        private final Context ctx;


    public ViewHolder(final Context ctx, @NonNull View itemView) {
            super(itemView);
            this.ctx = ctx;

            SdleVM holderModel = SdlEditorActivity.getSdleVM();
            LiveData<Map<String, Integer>> colorsLD = holderModel.getColorsLD(null);

            tvSftName = itemView.findViewById(R.id.sdle_sftList_item_sftText);
            flBG = itemView.findViewById(R.id.sdle_sftList_item_fl_background);

            tvSftName.setOnClickListener(v -> nextSft());
//            Log.d(TAG, "ViewHolder: !!!!!!!!!!!!!!!");
            colorsLD.observe((AppCompatActivity)ctx, cMap -> {
//                Log.d(TAG, "ViewHoldercolor: !!!!!!!!!!!!!!!");
                colorsMap = cMap;
                Integer sftId = (Integer) tvSftName.getTag();
                if (sftId != null && sftId<6) {
                    String s = sftChr[sftId].toString();
                    Integer c = colorsMap.get(s);
                    if (c != null) flBG.setBackgroundColor(c);
                }

            });
        }

/////// ***** NEXT SHIFT *****  //////////
        private void nextSft() {
            int pos = getAdapterPosition();
            int sftId = sftList.get(pos);
            if (sftId > 4) sftId = 0;
            else sftId++;

            sftList.set(pos, sftId);
            modelNotify();

            String nextSft = ctx.getString(Shift.sftRes[sftId]);
            tvSftName.setText(nextSft);
            tvSftName.setTag(sftId);
            Integer c = colorsMap.get(sftChr[sftId].toString());
            if (c!=null)flBG.setBackgroundColor(c);
            SdlEditorActivity.isChanged(true);
//                sdlChanged = true;
        }
    }
/////// _____  ViewHolder _____ ///////







    @NonNull
    @Override
    public SdleShiftListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sdle_shiftlist_item,  parent, false);

        return new ViewHolder(ctx, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int sftId = sftList.get(position);
        holder.tvSftName.setText(ctx.getString(Shift.sftRes[sftId]));
        holder.tvSftName.setTag(sftId);
        Integer c = colorsMap.get(sftChr[sftId].toString());
        if (c!=null) holder.flBG.setBackgroundColor(c);
    }

    @Override
    public int getItemCount() {
        return sftList.size();
    }





/////// ***** MOVE *****  //////////
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(sftList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(sftList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        modelNotify();
        SdlEditorActivity.isChanged(true);
//        sdlChanged = true;
    }

/////// ***** DELETE *****  //////////
    public void onRemoveItem(int position) {
        sftList.remove(position);
        notifyItemRemoved(position);
        modelNotify();
        SdlEditorActivity.isChanged(true);
//        sdlChanged = true;
    }

/////// ***** ADD *****  //////////
    public void addItem(int newSft) {
        if (newSft < 6) {
            sftList.add(newSft);
            notifyItemInserted(getItemCount()-1);
            modelNotify();
            SdlEditorActivity.isChanged(true);
//            sdlChanged = true;
        }
    }

/////// ***** CLEAR *****  //////////
    public void clear() {
        sftList = new ArrayList<>();
        notifyDataSetChanged();
        modelNotify();
        SdlEditorActivity.isChanged(true);
//        sdlChanged = true;
    }

/////// ***** GET SCHEDULE *****  //////////
    public static Schedule getSchedule() {
        StringBuilder sdlSB = new StringBuilder();
        for (int sftId : sftList) {
            sdlSB.append(sftChr[sftId]);
        }
        return  new Schedule(sdlName, sdlSB.toString(), colorsMap);
    }


    public static void modelNotify() {
        model.setSfts(getSchedule());
    }
}
