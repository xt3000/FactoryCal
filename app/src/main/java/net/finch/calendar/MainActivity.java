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
import android.widget.LinearLayout;

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
	private boolean pageChanged = false;

	ConstraintLayout cl_bottomContainer;
	Toolbar toolbar;
	FloatingActionButton fabAdd, fabAddMark, fabAddSdl;
	OnAddFABClickListener fabClickListener;
	LinearLayout sliderLayout;
	BottomSheetBehavior sliderBehavior;
	LinearLayout llSdlInfoList, llMarkInfoList;
	Button btnMarkConfirm;
	TextInputEditText etMarkNote;

	CalendarVM model;
	LiveData<ArrayList<DayInfo>> FODdata;
	LiveData<DayInfo> dayInfoListData;
	LiveData<Boolean> SSdata;
	ArrayList<DayInfo> frameOfDates;


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

		}
		return true;
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		if (model != null) {
		model.getFODLiveData(pageOffset);
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
		
		toolbar = findViewById(R.id.main_toolbar);
		if(toolbar != null) setSupportActionBar(toolbar);

		Objects.requireNonNull(getSupportActionBar()).setTitle("Factory Calendar");

		fabClickListener = new OnAddFABClickListener();

		fabAdd = findViewById(R.id.afab_add);
		fabAdd.setOnClickListener(fabClickListener);

		fabAddMark = findViewById(R.id.fab_mark);
		fabAddMark.setOnClickListener(this);

		fabAddSdl = findViewById(R.id.fab_sdl);
		fabAddSdl.setOnClickListener(this);


// *** ViewPager2 ***
		ViewPager2 pager = findViewById(R.id.calendar_pager);
		CalendarPagerAdapter pagerAdapter = new CalendarPagerAdapter(this);

		pager.setAdapter(pagerAdapter);
		pager.setOffscreenPageLimit(1);
		pager.setCurrentItem(CalendarPagerAdapter.START_PAGE, false);

		pagerAdapter.setOnBtnClickListener(new CalendarPagerAdapter.OnBtnClickListener() {
			@Override
			public void onPrevClick() {
				pager.setCurrentItem(pager.getCurrentItem()-1, true);
			}

			@Override
			public void onNextClick() {
				pager.setCurrentItem(pager.getCurrentItem()+1, true);
			}
		});

		pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
			@Override
			public void onPageSelected(int position) {
				pageOffset = position-CalendarPagerAdapter.START_PAGE;
				pageChanged = true;
				model.getFODLiveData(pageOffset);
			}
		});

//*******************

		//***  ViewModel  ***

		sliderLayout = findViewById(R.id.main_ll_bottom_sheet);					//bottom_sheet
		sliderBehavior = BottomSheetBehavior.from(sliderLayout);
		sliderBehavior.addBottomSheetCallback(new SliderBehaviorCallback());

//		llListInfo = findViewById(R.id.ll_infoList);
		llSdlInfoList = findViewById(R.id.main_bottom_ll_sdllist);
		llMarkInfoList = findViewById(R.id.main_bottom_ll_marklist);
		cl_bottomContainer = findViewById(R.id.cl_bottomContainer);
		btnMarkConfirm = findViewById(R.id.btn_markConfirm);
		etMarkNote = findViewById(R.id.et_markNote);


//  *** FRAME OF DATE LD Observer ***  //
		FODdata = model.getFODLiveData(pageOffset);
		FODdata.observe(this, fod -> {
			frameOfDates = fod;
			if (!pageChanged) pagerAdapter.notifyDataSetChanged();
			else pageChanged = false;
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
