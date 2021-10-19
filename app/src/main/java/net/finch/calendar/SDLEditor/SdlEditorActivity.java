package net.finch.calendar.SDLEditor;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.finch.calendar.Dialogs.ColorPiker;
import net.finch.calendar.Dialogs.PopupSdlCreate;
import net.finch.calendar.Dialogs.PopupWarning;
import net.finch.calendar.R;
import net.finch.calendar.Schedules.Schedule;
import net.finch.calendar.Settings.SDLSettings;
import net.finch.calendar.Utils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.finch.calendar.CalendarVM.TAG;

public class SdlEditorActivity extends AppCompatActivity {
    private static AppCompatActivity instance;
    public static final int ROOT_ID = R.id.activity_sdle_v2;

    public static final boolean sdlMODE = false;
    public static final boolean sftMODE = true;
    private boolean MODE;

    SdleVM sdleModel;
    LiveData<Schedule> sftsLD;
    LiveData<ArrayList<Schedule>> sdlsListLD;
    LiveData<Map<String, Integer>> colorLD;
    LiveData<Boolean> editorModeLD;

    private Schedule sdl;
    private ArrayList<Schedule> sdlList;
    private Map<String, Integer> colorMap;

    private SdleSdlListAdapter sdlAdapter;
    private SdleShiftListAdapter2 sftAdapter;

    ConstraintLayout cvBottomSheet;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Menu menu;
    private Toolbar toolbar;
    private LinearLayout llInfo;
    private TextView tvInfoLine1, tvInfoLine2;
    private RecyclerView rv;
    private Map<String, FrameLayout> flMap;
    private MaterialButton btnDefColors;



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (MODE) {
                if (sftAdapter != null) {
                    if (sftAdapter.isSdlChanged()) {
                        try {
                            PopupWarning pwarn = new PopupWarning(this, this.getText(R.string.sdle_notSavedBack).toString());
                            pwarn.setOnPositiveClickListener("", ()-> {
//                                MODE = sdlMODE;
                                sdleModel.setEditorMode(sdlMODE);
                                sdleModel.getSdlsListLD();
                            });
                            pwarn.setOnNegativeClickListener("", ()->{});
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        sdleModel.setEditorMode(sdlMODE);
                        sdleModel.getSdlsListLD();
                    }
                }

            }else finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        if (!MODE) getMenuInflater().inflate(R.menu.sdle_sdl_menu, menu);
        else getMenuInflater().inflate(R.menu.sdle_sft_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(0,KeyEvent.KEYCODE_BACK));
            case R.id.menu_sdls_help:
                break;
            case R.id.menu_sft_help:
                break;
            case R.id.menu_sft_clear:
                sftAdapter.clear();
                break;
            case R.id.menu_sft_save:
                Schedule sdlToSave = SdleShiftListAdapter2.getSchedule();
                if (sftAdapter!=null)  {
                    if (sdlToSave.getSdl().length() < 1) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Добавьте смены в график перед сохранением!")
                                .setPositiveButton("Оk", (dialog, which) -> {
                                })
                                .show();
                        break;
                    }else {
                        try {
                            SDLSettings set = new SDLSettings(this);
                            sdlToSave.setId(set.getNewSdlId());
                            set.saveSchedule(sdlToSave);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sdleModel.setEditorMode(sdlMODE);

                    }


                    Log.d(TAG, "onOptionsItemSelected: newSDL = "+ sdlToSave.getSdl());
                }
                break;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdle_editor_v2);
        initCvMap();
        setViews();
        setFAB();
        sdleModel = getSdleVM();




////  **** MODE SWITCHER ****  ////
        editorModeLD = sdleModel.getEditorModeLD();
        editorModeLD.observe(this, mode -> {
            Log.d(TAG, "Observe: MODE LD => "+mode);
            MODE = mode;
            if (!MODE) {
                bottomSheetBehavior.setHideable(true);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                sdleModel.getSdlsListLD();
                if (menu != null) {
                    menu.clear();
                    getMenuInflater().inflate(R.menu.sdle_sdl_menu, menu);
                }
            }else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior.setHideable(false);

                if (menu != null) {
                    menu.clear();
                    getMenuInflater().inflate(R.menu.sdle_sft_menu, menu);
                }
            }
        });




////  **** SCHEDULE CREATOR MODE ****  ////
        sdlsListLD = sdleModel.getSdlsListLD();
        sdlsListLD.observe(this, schedules -> {
            Log.d(TAG, "Observe: SDLS LD");
            sdlList = schedules;
            if (!MODE) {
                toolbar.setTitle(R.string.sdle_sdl_title);

                if (sdlList.size() > 0) {
                    llInfo.setVisibility(View.GONE);
                }else {
                    llInfo.setVisibility(View.VISIBLE);
                    tvInfoLine1.setText("У вас нет графиков.");
                    tvInfoLine2.setText("Нажмите \"+\", чтобы создать новый.");
                }
                sftAdapter = null;
                if (sdlAdapter == null) {
                    sdlAdapter = new SdleSdlListAdapter(this, sdlList);
                    sdlAdapter.setOnMenuClickListener(new SdleSdlListAdapter.OnMenuClickListener() {
                        @Override
                        public void onDelClick(Schedule sdl) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(instance);
                            builder.setMessage("Вы уверены что хотите удалить график \""+sdl.getName()+"\"?")
                                    .setPositiveButton("Оk", (dialog, which) -> {
                                        try {
                                            new SDLSettings(instance).removeSchedule(sdl);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        sdleModel.getSdlsListLD();
                                    })
                                    .setNegativeButton("Cancel", (dialog, which) -> {})
                                    .show();
                        }

                        @Override
                        public void onEditClick(Schedule sdl) {
                            sdleModel.setEditorMode(sftMODE);
                            sdleModel.setSfts(sdl);
                        }
                    });
                    FrameLayout.LayoutParams rvParams = (FrameLayout.LayoutParams) rv.getLayoutParams();
                    rvParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
                    rv.setLayoutParams(rvParams);
                    rv.setLayoutManager(new LinearLayoutManager(this));
                    rv.setAdapter(sdlAdapter);
                }else {
                    sdlAdapter.notifySdlsChanged(sdlList);
                }



            }
        });




////  **** SHIFTS EDITOR MODE ****  ////
        sftsLD = sdleModel.getSftsLD(null);
        sftsLD.observe(this, s -> {
            Log.d(TAG, "Observe: SFT LD");
            sdl = s;
            if (MODE) {
                toolbar.setTitle(R.string.sdle_sft_title);
                sdleModel.getColorsLD(sdl.getMapSdlColors());
                if (!sdl.getSdl().equals("")) {
                    llInfo.setVisibility(View.GONE);
                }else {
                    llInfo.setVisibility(View.VISIBLE);
                    tvInfoLine1.setText("Этот график пуст.");
                    tvInfoLine2.setText("Нажмите \"+\", чтобы добавить смены.");
                }
                sdlAdapter = null;
                if (sftAdapter == null) {
                    sftAdapter = new SdleShiftListAdapter2(instance, sdl);

                    FrameLayout.LayoutParams rvParams = (FrameLayout.LayoutParams) rv.getLayoutParams();
                    rvParams.width = (int) Utils.dpToPx(this, 371f);
                    rv.setLayoutParams(rvParams);
                    rv.setLayoutManager(new GridLayoutManager(this, 7));
                    rv.setAdapter(sftAdapter);
                }
            }
        });





////  **** COLOR EDITOR ****  ////
        colorLD = sdleModel.getColorsLD(null);
        colorLD.observe(this, colMap -> {
            colorMap = colMap;
            Log.d(TAG, "onChange Color: !!!!!!!!!!!");
            for (String sft : colorMap.keySet()) {
                if (colorMap.get(sft) != null)
                    Objects.requireNonNull(flMap.get(sft)).setBackgroundTintList(ColorStateList.valueOf(colorMap.get(sft)));
            }
        });

    }





    private void initCvMap() {
        flMap = new HashMap<>();
        flMap.put("U", (FrameLayout)findViewById(R.id.sdle_bsheet_fl_sft_U));
        flMap.put("D", (FrameLayout)findViewById(R.id.sdle_bsheet_fl_sft_D));
        flMap.put("N", (FrameLayout)findViewById(R.id.sdle_bsheet_fl_sft_N));
        flMap.put("S", (FrameLayout)findViewById(R.id.sdle_bsheet_fl_sft_S));
        flMap.put("V", (FrameLayout)findViewById(R.id.sdle_bsheet_fl_sft_V));
        flMap.put("W", (FrameLayout)findViewById(R.id.sdle_bsheet_fl_sft_W));
    }

    private void setFAB() {
        FloatingActionButton fab = findViewById(R.id.sdle_fab);
        fab.setOnClickListener(view -> {
            if (!MODE) {
                try {
                    new PopupSdlCreate(instance, new Schedule("", ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                sftAdapter.addItem("W");
            }
        });
    }

    private void setViews() {
        toolbar = findViewById(R.id.sdle_toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);
        toolbar.setSubtitleTextAppearance(this, R.style.TextAppearance_MaterialComponents_Subtitle2);

        cvBottomSheet = findViewById(R.id.sdle_cl_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(cvBottomSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (rv!=null){
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) rv.setPadding(0,0,0,0);
                    else rv.setPadding(0,0,0, getResources().getDimensionPixelSize(R.dimen.bottom_sheet_collapse_height));
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        llInfo = findViewById(R.id.sdle_ll_info_text);
        tvInfoLine1 = findViewById(R.id.sdle_info_text_tvLine_1);
        tvInfoLine2 = findViewById(R.id.sdle_info_text_tvLine_2);
        btnDefColors = findViewById(R.id.sdle_bsheet_btn_def_sft_colors);
        btnDefColors.setOnClickListener((v)->{
            sdleModel.getColorsLD(Schedule.getDefaultShiftColors());
        });

        rv = findViewById(R.id.sdle_rv);
        initRvMovement();

        for (String sft : flMap.keySet()) {
            FrameLayout cv = flMap.get(sft);
            if (cv!=null){
                cv.setTag(sft);
                cv.setOnClickListener((v)->{
                    FrameLayout cView = (FrameLayout) v;
                    ColorPiker.Bilder(this, cView.getTag().toString(), cView.getBackgroundTintList().getDefaultColor());
                });
            }

        }
    }


    private void initRvMovement() {
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (rv.getLayoutManager() instanceof GridLayoutManager) {
                    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
                    int swipeFlags = ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }else return makeMovementFlags(0, 0);

            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                sftAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                sftAdapter.onRemoveItem(viewHolder.getAdapterPosition());
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
        touchHelper.attachToRecyclerView(rv);


    }


    public static SdleVM getSdleVM() {
        return new ViewModelProvider(instance, new ViewModelProvider.NewInstanceFactory()).get(SdleVM.class);
    }

    public static AppCompatActivity getInstance() {
        return instance;
    }
}