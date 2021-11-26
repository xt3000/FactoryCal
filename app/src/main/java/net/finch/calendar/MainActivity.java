package net.finch.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
//import android.arch.lifecycle.LiveData;
//import android.arch.lifecycle.Observer;
//import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.annotation.RequiresApi;
//import android.support.constraint.ConstraintLayout;
//import android.support.constraint.ConstraintSet;
//import android.support.design.widget.BottomSheetBehavior;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.TextInputEditText;
//import android.support.v7.app.AppCompatActivity;
////import android.support.v7app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import net.finch.calendar.Dialogs.PopupAbout;
import net.finch.calendar.Dialogs.PopupAdd;
import net.finch.calendar.Marks.MarkListHolder;
import net.finch.calendar.SDLEditor.SdlEditorActivity;
import net.finch.calendar.Schedules.ShiftListHolder;

import java.util.ArrayList;
import java.util.Objects;

import static net.finch.calendar.CalendarVM.TAG;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity implements OnClickListener {
	@SuppressLint("StaticFieldLeak")
	public static MainActivity instance;
	public final static int ROOT_ID = R.id.main_layout;
	public static int pageOffset = 0;

	private Toolbar toolbar;
	private FloatingActionButton fabAdd, fabAddMark, fabAddSdl;
	private OnAddFABClickListener fabClickListener;
	private TextView tvSliderTitle;
	private LinearLayout sliderLayout;
	private BottomSheetBehavior<View> sliderBehavior;
	private LinearLayout llSdlInfoList, llMarkInfoList;
	private CalendarPagerAdapter pagerAdapter;
	private ImageButton ibtnPrevious, ibtnNext;

	private CalendarVM model;
	private LiveData<ArrayList<DayInfo>> FODdata;
	private LiveData<DayInfo> dayInfoListData;
	private LiveData<Boolean> SSdata;
	protected ArrayList<DayInfo> frameOfDates = new ArrayList<>();



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case (R.id.main_menu_1):
				Intent intent = new Intent(this, SdlEditorActivity.class);
				startActivity(intent);
				break;
			case (R.id.main_menu_2):
				break;
			case (R.id.main_menu_3):
				new PopupAbout(this);

		}
		return true;
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		if (model != null) {
		model.getFODLiveData(null);
		}

		if (model != null) model.updInfoList();
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
    	instance = this;
    	model = getCalendarVM();
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setViews();


		//***  ViewModel  ***
		llSdlInfoList = findViewById(R.id.main_bottom_ll_sdllist);
		llMarkInfoList = findViewById(R.id.main_bottom_ll_marklist);
		tvSliderTitle = findViewById(R.id.tv_slider_title);
		sliderLayout = findViewById(R.id.main_ll_bottom_sheet);					//bottom_sheet
		sliderBehavior = BottomSheetBehavior.from(sliderLayout);
		sliderBehavior.addBottomSheetCallback(new SliderBehaviorCallback());



//  *** FRAME OF DATE LD Observer ***  //
		FODdata = model.getFODLiveData(null);
		FODdata.observe(this, fod -> {
			Log.d(TAG, "onFODdata Changed:");
			frameOfDates = fod;
			model.updInfoList();
			if (model.selectedDayId != null) {
				tvSliderTitle.setText(fod.get(model.selectedDayId).getFullDateString());
				pagerAdapter.notifyDataSetChanged();
			}
		});


//  *** SLIDER STATE LD Observer ***  //
		SSdata = model.getSStateLiveData();
		SSdata.observe(this, sliderState -> {
			hideKeyboard(MainActivity.this);
			if(sliderState) {
				sliderBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
			}
			else {
				sliderBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
			}
		});


//  *** DAY INFO LD Observer ***  //
		dayInfoListData = model.getDayInfoLiveData();
		dayInfoListData.observe(this, dayInfo -> {
			if (dayInfo == null) return;
			llSdlInfoList.removeAllViews();
			llMarkInfoList.removeAllViews();

			if(dayInfo.getShiftList().size() != 0) {
				TreeNode sdlListRoot = TreeNode.root();
				sdlListRoot.addChild(new TreeNode(new MainBottomChapterObject(android.R.drawable.ic_menu_today, "Смены")).setViewHolder(new MainListHolder(instance)));
				for (int i=0; i<dayInfo.getShiftList().size(); i++) {
					sdlListRoot.addChild(new TreeNode(dayInfo.getShiftList().get(i)).setViewHolder(new ShiftListHolder(MainActivity.this, (i==(dayInfo.getShiftList().size()-1)))));
				}
				AndroidTreeView treeView = new AndroidTreeView(MainActivity.this, sdlListRoot);
				llSdlInfoList.addView(treeView.getView());
			}


			if(dayInfo.getMarkList().size() != 0) {
				TreeNode markListRoot = TreeNode.root();
				markListRoot.addChild(new TreeNode(new MainBottomChapterObject(R.drawable.ic_baseline_create_24, "Заметки")).setViewHolder(new MainListHolder(instance)));
				for (int i=0; i<dayInfo.getMarkList().size(); i++) {
					markListRoot.addChild(new TreeNode(dayInfo.getMarkList().get(i)).setViewHolder(new MarkListHolder(MainActivity.this, (i==(dayInfo.getMarkList().size()-1)))));
				}
				AndroidTreeView treeView = new AndroidTreeView(MainActivity.this, markListRoot);
				llMarkInfoList.addView(treeView.getView());
			}
		});


		sliderLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				sliderLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) sliderLayout.getLayoutParams();

				params.height = sliderLayout.getMeasuredHeight() - toolbar.getMeasuredHeight();
				sliderLayout.setLayoutParams(params);
			}
		});
    }

    private void setViews() {
		toolbar = findViewById(R.id.main_toolbar);
		if(toolbar != null) setSupportActionBar(toolbar);

//		Objects.requireNonNull(getSupportActionBar()).setTitle("Factory Calendar");

		fabClickListener = new OnAddFABClickListener();

		fabAdd = findViewById(R.id.afab_add);
		fabAdd.setOnClickListener(fabClickListener);

		fabAddMark = findViewById(R.id.fab_mark);
		fabAddMark.setOnClickListener(this);

		fabAddSdl = findViewById(R.id.fab_sdl);
		fabAddSdl.setOnClickListener(this);






// *** ViewPager2 ***
		ViewPager2 pager = findViewById(R.id.calendar_pager);
		pagerAdapter = new CalendarPagerAdapter();

		pager.setAdapter(pagerAdapter);
		pager.setOffscreenPageLimit(1);
		pager.setCurrentItem(CalendarPagerAdapter.START_PAGE, false);
		pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
			@Override
			public void onPageSelected(int position) {
				pageOffset = position-CalendarPagerAdapter.START_PAGE;
			}
		});

		ibtnPrevious = findViewById(R.id.calendar_ibtn_previous);
		ibtnPrevious.setOnClickListener((v)-> {
			pager.setCurrentItem(pager.getCurrentItem()-1, true);
		});

		ibtnNext = findViewById(R.id.calendar_ibtn_next);
		ibtnNext.setOnClickListener((v)-> {
			pager.setCurrentItem(pager.getCurrentItem()+1, true);
		});
	}



	public static Activity getContext() {
    	return instance;
	}

	public static CalendarVM getCalendarVM() {
		return new ViewModelProvider(instance, new ViewModelProvider.NewInstanceFactory()).get(CalendarVM.class);
	}

	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		View view = activity.getCurrentFocus();
		if (view == null) {
			view = new View(activity);
		}
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public void onClick(View view) {
		Log.d(TAG, "onFABClick: ");
		switch (view.getId()) {
			case (R.id.fab_mark):
				new PopupAdd(this, PopupAdd.MARK);
				fabClickListener.fabAddMenuClick();
				break;
			case (R.id.fab_sdl):
				new PopupAdd(this, PopupAdd.SCHEDULE);
				fabClickListener.fabAddMenuClick();
				break;
		}

	}



}
