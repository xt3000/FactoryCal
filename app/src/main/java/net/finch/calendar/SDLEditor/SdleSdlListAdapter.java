package net.finch.calendar.SDLEditor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import net.finch.calendar.R;
import net.finch.calendar.Schedules.Schedule;

import java.util.ArrayList;

import static net.finch.calendar.CalendarVM.TAG;

public class SdleSdlListAdapter extends RecyclerView.Adapter<SdleSdlListAdapter.ViewHolder> {
    OnMenuClickListener onMenuClickListener;

    Context ctx;
    ArrayList<Schedule> sdlList;
    
    View menuOpenedView = null;

    public SdleSdlListAdapter(ArrayList<Schedule> sdlList) {
        this.ctx = ctx;
        this.sdlList = sdlList;
    }

    // *****  ViewHolder  ***** //
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSdlName;
        ConstraintLayout clContent, clItem;
        ImageButton ibtnDel, ibtnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            clItem = itemView.findViewById(R.id.sdle_sdl_rv_cl_item);
            clContent = itemView.findViewById(R.id.sdle_sdl_rv_cl_content);
            tvSdlName = itemView.findViewById(R.id.sdle_sdl_rv_tv_name);
            ibtnDel = itemView.findViewById(R.id.sdle_sdl_rv_ibtn_del);
            ibtnEdit = itemView.findViewById(R.id.sdle_sdl_rv_ibtn_edit);
        }
    }// *****  ViewHolder  ***** //



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sdle_item_v2,  parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvSdlName.setText(sdlList.get(position).getName());
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.clContent.getLayoutParams();
        if (params.getMarginStart() > 0) {
            Log.d(TAG, "onBindViewHolder: marginStart > 0; pos = "+position);
            TransitionManager.beginDelayedTransition(holder.clItem);
            params.width = 0;
            params.leftMargin = 0;
            holder.clContent.setLayoutParams(params);
        }
        holder.clContent.setOnClickListener(v -> {
            if (menuOpenedView !=null) {
                ConstraintLayout vgOpen = (ConstraintLayout) menuOpenedView.getParent();
                View contentOpen = vgOpen.findViewById(R.id.sdle_sdl_rv_cl_content);
                TransitionManager.beginDelayedTransition(vgOpen);
                ConstraintLayout.LayoutParams paramsOpen = (ConstraintLayout.LayoutParams) contentOpen.getLayoutParams();
                paramsOpen.width = 0;
                paramsOpen.leftMargin = 0;
                contentOpen.setLayoutParams(paramsOpen);
            }

            if (v.equals(menuOpenedView)) {
                menuOpenedView = null;
            }
            else {
                menuOpenedView = v;
                ConstraintLayout vg = (ConstraintLayout) v.getParent();
                View content = vg.findViewById(R.id.sdle_sdl_rv_cl_content);
                View menu = vg.getViewById(R.id.sdle_sdl_rv_cl_menu);
                TransitionManager.beginDelayedTransition(vg);
                ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) content.getLayoutParams();
                params1.width = content.getWidth();
                params1.leftMargin = (menu.getWidth()+48)*2;
                content.setLayoutParams(params1);
            }


        });

        holder.ibtnEdit.setOnClickListener(v -> {
            if (onMenuClickListener != null) onMenuClickListener.onEditClick(sdlList.get(position));
        });

        holder.ibtnDel.setOnClickListener(v -> {
            if (onMenuClickListener != null) onMenuClickListener.onDelClick(sdlList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return sdlList.size();
    }

    public void notifySdlsChanged(ArrayList<Schedule> sdlList) {
        this.sdlList = sdlList;
        notifyDataSetChanged();
    }


//// *****  CALLBACK  ***** ////
    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }

    public interface OnMenuClickListener {
        void onEditClick(Schedule sdl);
        void onDelClick(Schedule sdl);
    }


}