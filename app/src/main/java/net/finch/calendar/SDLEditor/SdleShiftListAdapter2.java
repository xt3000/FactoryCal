package net.finch.calendar.SDLEditor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class SdleShiftListAdapter2 extends RecyclerView.Adapter<SdleShiftListAdapter2.ViewHolder> {
    private final Context ctx;
    private static ArrayList<String> sftList;
    private static Map<String, Integer> colorsMap;
    private static SdleVM model = SdlEditorActivity.getSdleVM();
    private static String sdlName;
    private static boolean sdlChanged;


    public SdleShiftListAdapter2(@NonNull Context ctx, Schedule sdl) {
        SdleShiftListAdapter2.sftList = sdl.getSftList();
        SdleShiftListAdapter2.sdlName = sdl.getName();
        this.ctx = ctx;
        sdlChanged = false;
    }

/////// ***  ViewHolder *** ///////
    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSftName;
        private ConstraintLayout clBG;

        private SdleVM holderModel = SdlEditorActivity.getSdleVM();
        private LiveData<Map<String, Integer>> colorsLD;


        public ViewHolder(final Context ctx, @NonNull View itemView) {
            super(itemView);

            colorsLD = holderModel.getColorsLD(null);

            tvSftName = itemView.findViewById(R.id.sdle_sftList_item_sftText);
            clBG = itemView.findViewById(R.id.sdle_sftList_item_cl_background);

            tvSftName.setOnClickListener(v -> nextSft());
            Log.d(TAG, "ViewHolder: !!!!!!!!!!!!!!!");
            colorsLD.observe((AppCompatActivity)ctx, cMap -> {
                Log.d(TAG, "ViewHoldercolor: !!!!!!!!!!!!!!!");
                colorsMap = cMap;
                String sftName = tvSftName.getText().toString();
                if (!sftName.isEmpty()) {
                    Character s = Shift.shiftCharOf(sftName);
                    if (s != null) clBG.setBackgroundColor(colorsMap.get(s.toString()));
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
                clBG.setBackgroundColor(colorsMap.get(nextSft));
                sdlChanged = true;
            }
        }
    }
/////// _____  ViewHolder _____ ///////







    @NonNull
    @Override
    public SdleShiftListAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sdle_shiftlist_item,  parent, false);

        return new ViewHolder(ctx, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String sft = sftList.get(position);
        holder.tvSftName.setText(Shift.shiftNameOf(sft.charAt(0)));
        holder.clBG.setBackgroundColor(colorsMap.get(sft));
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
        sdlChanged = true;
    }

/////// ***** DELETE *****  //////////
    public void onRemoveItem(int position) {
        sftList.remove(position);
        notifyItemRemoved(position);
        modelNotify();
        sdlChanged = true;
    }

/////// ***** ADD *****  //////////
    public void addItem(String newSft) {
        if (newSft.length() == 1) {
            sftList.add(newSft);
            notifyItemInserted(getItemCount()-1);
            modelNotify();
            sdlChanged = true;
        }
    }

/////// ***** CLEAR *****  //////////
    public void clear() {
        sftList = new ArrayList<>();
        notifyDataSetChanged();
        modelNotify();
        sdlChanged = true;
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

    public boolean isSdlChanged() {
        return sdlChanged;
    }
}
