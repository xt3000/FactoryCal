package net.finch.calendar.SDLEditor;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unnamed.b.atv.model.TreeNode;

import static net.finch.calendar.CalendarVM.TAG;

import net.finch.calendar.CalendarVM;
import net.finch.calendar.Dialogs.PopupSdlSave;
import net.finch.calendar.R;
import net.finch.calendar.Schedules.Schedule;

import java.util.ArrayList;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SdlEditorActivity extends AppCompatActivity implements View.OnClickListener {
    private static SdlEditorActivity instance;
    public static final int ROOT_ID = R.id.activity_sdl_editor;

    SdleVM sdleModel;
    LiveData<ArrayList<SdleListObj>> sdlLd;

    ImageButton btnAddSft;
    ImageButton btnClear;
    ImageButton btnSave;
    RecyclerView rvSdl;

    ArrayList<SdleListObj> listObjs;
    SdleListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdl_editor);


        btnAddSft = findViewById(R.id.sdle_btn_addSft);
        btnAddSft.setOnClickListener(this);

        btnSave = findViewById(R.id.sdle_btn_save);
        btnSave.setOnClickListener(this);

        btnClear = findViewById(R.id.sdle_btn_clear);
        btnClear.setOnClickListener(this);

        rvSdl = findViewById(R.id.sdle_rv_list);
        rvSdl.setLayoutManager(new LinearLayoutManager(this));

        sdleModel = getSdleVM();

        sdlLd = sdleModel.getSdlLD(null);
        sdlLd.observe(this, new Observer<ArrayList<SdleListObj>>() {
            @Override
            public void onChanged(ArrayList<SdleListObj> lObjs) {
                if (listObjs == null) {
                    adapter = new SdleListAdapter(SdlEditorActivity.this, lObjs);
                    initMovement();
                }
                listObjs = lObjs;
            }
        });



    }

    private void initMovement() {
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                adapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                hendeOnSwipe(viewHolder.getAdapterPosition());
                adapter.onRemoveItem(viewHolder.getAdapterPosition());
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                Log.d(TAG, "clearView: ");

                super.clearView(recyclerView, viewHolder);
            }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvSdl);
        rvSdl.setAdapter(adapter);
    }

    private void hendeOnSwipe(final int pos) {
        final boolean[] del = {false};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы уверены, что хотите удалить смену")
                .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        del[0] = true;
                        adapter.onRemoveItem(pos);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (!del[0]) adapter.notifyDataSetChanged();
                    }
                })
                .show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sdle_btn_addSft:
                Log.d(TAG, "SDLE_onClick: add");
                adapter.addItem("U");
                break;
            case R.id.sdle_btn_clear:
                Log.d(TAG, "SDLE_onClick: clear");
                adapter.clear();
                break;
            case R.id.sdle_btn_save:
                Log.d(TAG, "SDLE_onClick: save");
                saveSft(adapter.getSchedule());
                break;
        }
    }

    private void saveSft(Schedule sdl) {
        if (sdl.getSdl().length() < 1) {
            emptySdl();
            return;
        }
        new PopupSdlSave(this, sdl);
    }

    private void emptySdl() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Добавьте смены в график перед сохранением!")
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public static SdleVM getSdleVM() {
        return new ViewModelProvider(instance, new ViewModelProvider.NewInstanceFactory()).get(SdleVM.class);
    }

    private void deleteItem(View rowView, final int position) {
        Animation anim = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right);
        anim.setDuration(300);
        rowView.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (adapter.getItemCount() == 0) return;
//                if (myDataSource.size() == 0) {
//                    addEmptyView(); // adding empty view instead of the RecyclerView
//                    return;
//                }
                adapter.onRemoveItem(position); //Remove the current content from the array
//                myRVAdapter.notifyDataSetChanged(); //Refresh list
            }

        }, anim.getDuration());
    }
}