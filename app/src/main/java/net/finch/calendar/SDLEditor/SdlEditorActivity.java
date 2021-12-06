package net.finch.calendar.SDLEditor;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.finch.calendar.Dialogs.ColorPiker;
import net.finch.calendar.Dialogs.PopupSDLEHelp;
import net.finch.calendar.Dialogs.PopupSdlCreate;
import net.finch.calendar.Dialogs.PopupWarning;
import net.finch.calendar.R;
import net.finch.calendar.Schedules.Schedule;
import net.finch.calendar.Settings.SDLSettings;
import net.finch.calendar.Utils;
import net.finch.calendar.Views.SnakeView;

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
    private boolean MODE, keyboardState, needSlide = false;
    private static boolean isChanged = false;

    private SdleVM sdleModel;
    private LiveData<Schedule> sftsLD;
    private LiveData<ArrayList<Schedule>> sdlsListLD;
    private LiveData<Map<String, Integer>> colorLD;
    private LiveData<Boolean> editorModeLD;

    private Schedule sdl;
    private ArrayList<Schedule> sdlList;
    private Map<String, Integer> colorMap;

    private SdleSdlListAdapter sdlAdapter;
    private SdleShiftListAdapter sftAdapter;

    private FloatingActionButton fab;
    private ConstraintLayout cvBottomSheet;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Menu menu;
    private Toolbar toolbar;
    private LinearLayout llInfo;
    private TextView tvInfoLine1, tvInfoLine2;
    private RecyclerView rv;
    private Map<String, FrameLayout> flMap;
    private MaterialButton btnDefColors;
    private CoordinatorLayout clWindow;



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (MODE) {
                if (sftAdapter != null) {
                    if (isChanged) {
                        PopupWarning pwarn = new PopupWarning(this, this.getText(R.string.sdle_notSavedBack));
                        pwarn.setBgColor(PopupWarning.COLOR_ERROR);
                        pwarn.setIcon(PopupWarning.ICON_INFO);
                        pwarn.setOnPositiveClickListener("", ()-> {
                            sdleModel.setEditorMode(sdlMODE);
                            sdleModel.getSdlsListLD();
                            isChanged(false);
                        });
                        pwarn.setOnNegativeClickListener("", ()->{});
                    }else {
                        sdleModel.setEditorMode(sdlMODE);
                        sdleModel.getSdlsListLD();
                        isChanged(false);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(0,KeyEvent.KEYCODE_BACK));
                break;
            case (R.id.menu_sdls_help):
                new PopupSDLEHelp(this, PopupSDLEHelp.layoutId_sdl);
                break;
            case (R.id.menu_sft_help):
                new PopupSDLEHelp(this, PopupSDLEHelp.layoutId_sft);
                break;
            case (R.id.menu_sft_clear):
                PopupWarning pw = new PopupWarning(instance, "Вы уверены что хотите очистить этот график");
                pw.setBgColor(PopupWarning.COLOR_ERROR);
                pw.setIcon(PopupWarning.ICON_X);
                pw.setOnPositiveClickListener("", ()->sftAdapter.clear());
                pw.setOnNegativeClickListener("", ()->{});

                break;
            case (R.id.menu_sft_save):
                Schedule sdlToSave = SdleShiftListAdapter.getSchedule();
                if (sftAdapter!=null)  {
                    if (sdlToSave.getSdl().length() < 1) {
//                        try {
//                            PopupWarning pw = new PopupWarning(instance, "Добавьте смены в график перед сохранением!");
//                            pw.setOnPositiveClickListener("", ()->{});
//                        }catch (JSONException e) {}
                        SnakeView.make(fab,
                                SnakeView.ICONS[SnakeView.TYPE_INFO],
                                "Добавьте смены в график перед сохранением!",
                                SnakeView.COLORS[SnakeView.TYPE_INFO])
                                .show();

                        break;
                    }else {
                        try {
                            SDLSettings set = new SDLSettings(this);
                            sdlToSave.setId(set.getNewSdlId());
                            set.saveSchedule(sdlToSave);
                        } catch (JSONException e) {
                            Spanned txt = Html.fromHtml("Произошла ошибка! График \"<b>"+sdlToSave.getName()+"\"</b> НЕ сохранён.", Html.FROM_HTML_MODE_COMPACT);
                            SnakeView.make(fab,
                                    SnakeView.ICONS[SnakeView.TYPE_ERROR],
                                    txt,
                                    SnakeView.COLORS[SnakeView.TYPE_ERROR]).show();
                        }finally {
                            Spanned txt = Html.fromHtml("График \"<b>"+sdlToSave.getName()+"\"</b> успешно сохранён!", Html.FROM_HTML_MODE_COMPACT);
                            SnakeView.make(fab,
                                    SnakeView.ICONS[SnakeView.TYPE_SUCCESS],
                                    txt,
                                    SnakeView.COLORS[SnakeView.TYPE_SUCCESS]).show();
                        }
                        isChanged(false);
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


        final View activityRootView = findViewById(R.id.activity_sdle_v2);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
            if (heightDiff > Utils.dpToPx(instance, 200)) { // if more than 200 dp, it's probably a keyboard...
                Log.d(TAG, "onGlobalLayout: keyboard ON");
                keyboardState = true;
            }
            else {
                Log.d(TAG, "onGlobalLayout: keyboard OFF");
                keyboardState = false;
                if (needSlide) {
                    needSlide = false;
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottomSheetBehavior.setHideable(false);
                }
            }
        });




////  **** MODE SWITCHER ****  ////
        editorModeLD = sdleModel.getEditorModeLD();
        editorModeLD.observe(this, mode -> {
            Log.d(TAG, "Observe: MODE LD => "+mode);
            MODE = mode;
            /// SCHEDULE MODE
            if (!MODE) {
                bottomSheetBehavior.setHideable(true);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                sdleModel.getSdlsListLD();
                if (menu != null) {
                    menu.clear();
                    getMenuInflater().inflate(R.menu.sdle_sdl_menu, menu);
                }
            }
            /// SHIFT MODE
            else {
                if (!keyboardState) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottomSheetBehavior.setHideable(false);
                }else {
                    needSlide = true;
                }

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
            sdlCreateSet();
        });




////  **** SHIFTS EDITOR MODE ****  ////
        sftsLD = sdleModel.getSftsLD(null);
        sftsLD.observe(this, s -> {
            Log.d(TAG, "Observe: SFT LD");
            sdl = s;
            sftEditSet();
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
//
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
        fab = findViewById(R.id.sdle_fab);
        fab.setOnClickListener(view -> {
            if (!MODE) {
                new PopupSdlCreate(instance, new Schedule("", ""));
            }else {
                if (sftAdapter.getItemCount() == 365) SnakeView.make(fab,
                        SnakeView.ICONS[SnakeView.TYPE_ERROR],
                        "Добавлено 365 смен. График заполнен!",
                        SnakeView.COLORS[SnakeView.TYPE_ERROR]).show();
                else sftAdapter.addItem("W");

            }
        });
    }

    private void setViews() {
        clWindow = findViewById(R.id.activity_sdle_v2);
        toolbar = findViewById(R.id.sdle_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }


        cvBottomSheet = findViewById(R.id.sdle_cl_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(cvBottomSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.d(TAG, "onStateChanged: STATE = "+newState);
                if (rv!=null){
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) rv.setPadding(0,0,0,0);
                    else rv.setPadding(0,0,0, getResources().getDimensionPixelSize(R.dimen.bottom_sheet_collapse_height));
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
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

    private void sdlCreateSet() {
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
                        PopupWarning pwarn = new PopupWarning(instance, "Вы уверены что хотите удалить график \""+sdl.getName()+"\"?");
                        pwarn.setBgColor(PopupWarning.COLOR_ERROR);
                        pwarn.setIcon(PopupWarning.ICON_DELETE);
                        pwarn.setOnPositiveClickListener("", ()->{
                            new SDLSettings(instance).removeSchedule(sdl);
                            sdleModel.getSdlsListLD();
                        });
                        pwarn.setOnNegativeClickListener("", ()->{});
                    }

                    @Override
                    public void onEditClick(Schedule sdl) {
                        sdleModel.setEditorMode(sftMODE);
                        sdleModel.setSfts(sdl);
                    }
                });
                LinearLayout.LayoutParams rvParams = (LinearLayout.LayoutParams) rv.getLayoutParams();
                rvParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
                rv.setLayoutParams(rvParams);
                rv.setLayoutManager(new LinearLayoutManager(this));
                rv.setAdapter(sdlAdapter);
            }else {
                sdlAdapter.notifySdlsChanged(sdlList);
            }



        }
    }

    private void sftEditSet() {
        if (MODE) {
            toolbar.setTitle(R.string.sdle_sft_title);
            sdleModel.getColorsLD(sdl.getMapSdlColors());
            if (!sdl.getSdl().equals("")) {
                llInfo.setVisibility(View.INVISIBLE);
            }else {
                llInfo.setVisibility(View.VISIBLE);
                tvInfoLine1.setText("Этот график пуст.");
                tvInfoLine2.setText("Нажмите \"+\", чтобы добавить смены.");
            }
            sdlAdapter = null;
            if (sftAdapter == null) {
                sftAdapter = new SdleShiftListAdapter(instance, sdl);

                LinearLayout.LayoutParams rvParams = (LinearLayout.LayoutParams) rv.getLayoutParams();
                rvParams.width = (int) Utils.dpToPx(this, 371f);
                rv.setLayoutParams(rvParams);
                rv.setLayoutManager(new GridLayoutManager(this, 7));
                rv.setAdapter(sftAdapter);
            }
        }
    }



/// Actyviti UTILS
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static SdleVM getSdleVM() {
        return new ViewModelProvider(instance, new ViewModelProvider.NewInstanceFactory()).get(SdleVM.class);
    }

    public static AppCompatActivity getInstance() {
        return instance;
    }

    public static void isChanged(boolean changed) {
        isChanged = changed;
    }
}