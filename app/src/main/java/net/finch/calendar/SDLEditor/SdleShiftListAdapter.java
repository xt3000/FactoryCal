package net.finch.calendar.SDLEditor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import net.finch.calendar.R;
import net.finch.calendar.Schedules.Schedule;
import net.finch.calendar.Schedules.Shift;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static net.finch.calendar.CalendarVM.TAG;

public class SdleShiftListAdapter extends RecyclerView.Adapter<SdleShiftListAdapter.ViewHolder> {
    private final Context ctx;
    private static ArrayList<String> sftList;
    private static Map<String, Integer> colorsMap;
    private static SdleVM model = SdlEditorActivity.getSdleVM();
    private static String sdlName;
//    private static boolean sdlChanged;


    public SdleShiftListAdapter(@NonNull Context ctx, Schedule sdl) {
        SdleShiftListAdapter.sftList = sdl.getSftList();
        SdleShiftListAdapter.sdlName = sdl.getName();
        this.ctx = ctx;
//        sdlChanged = false;
    }

/////// ***  ViewHolder *** ///////
    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSftName;
        private FrameLayout flBG;

        private SdleVM holderModel = SdlEditorActivity.getSdleVM();
        private LiveData<Map<String, Integer>> colorsLD;


        public ViewHolder(final Context ctx, @NonNull View itemView) {
            super(itemView);

            colorsLD = holderModel.getColorsLD(null);

            tvSftName = itemView.findViewById(R.id.sdle_sftList_item_sftText);
            flBG = itemView.findViewById(R.id.sdle_sftList_item_fl_background);

            tvSftName.setOnClickListener(v -> nextSft());
            Log.d(TAG, "ViewHolder: !!!!!!!!!!!!!!!");
            colorsLD.observe((AppCompatActivity)ctx, cMap -> {
                Log.d(TAG, "ViewHoldercolor: !!!!!!!!!!!!!!!");
                colorsMap = cMap;
                String sftName = tvSftName.getText().toString();
                if (!sftName.isEmpty()) {
                    Character s = Shift.shiftCharOf(sftName);
                    if (s != null) flBG.setBackgroundColor(colorsMap.get(s.toString()));
                }

            });
        }

/////// ***** NEXT SHIFT *****  //////////
        private void nextSft() {

            String sft = Objects.requireNonNull(Shift.shiftCharOf(tvSftName.getText().toString())).toString();
            Integer pos = getAdapterPosition();
            Log.d(TAG, "nextSft: sft = "+sft);

            if (pos != null) {
                String nextSft = Schedule.getNextSft(sft);
                sftList.set(pos, nextSft);
                modelNotify();

                tvSftName.setText(Shift.shiftNameOf(nextSft.charAt(0)));
                flBG.setBackgroundColor(colorsMap.get(nextSft));
                SdlEditorActivity.isChanged(true);
//                sdlChanged = true;
            }
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
        String sft = sftList.get(position);
        holder.tvSftName.setText(Shift.shiftNameOf(sft.charAt(0)));
        holder.flBG.setBackgroundColor(colorsMap.get(sft));
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
    public void addItem(String newSft) {
        if (newSft.length() == 1) {
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
        for (String sft : sftList) {
            sdlSB.append(sft);
        }
        return  new Schedule(sdlName, sdlSB.toString(), colorsMap);
    }


    public static void modelNotify() {
        model.setSfts(getSchedule());
    }
}
