package net.finch.calendar;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
{
	@SuppressLint("StaticFieldLeak")
	public static MainActivity instance;
	final boolean DEBUG = false;
	String debugTxt="";

	ConstraintSet cSet = new ConstraintSet();
	ConstraintLayout cl_bottomContainer;
	public  static ConstraintLayout.LayoutParams LLP_Visible = new ConstraintLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
	public static ConstraintLayout.LayoutParams LLP_Gone = new ConstraintLayout.LayoutParams(0,0);

	LinearLayout[] llWeaks = new LinearLayout[6];

	LinearLayout sliderLayout;
	BottomSheetBehavior sliderBehavior;

	LinearLayout llListInfo;
	LinearLayout llAdd;
	Scene sceneMark;
	Scene sceneSchedule;

	TabLayout tl_select;
	ViewPager vpager_add;


	DayView dv;
	TextView tvMonth;
	TextView tvYear;
	TextView tvDebag;
	TextView tvBtnAdd;
	Button btnMarkConfirm;
//	EditText etMarkNote;
	
//	int width;
	int count = 0;

	CalendarVM model;
	LiveData<ArrayList<DayInfo>> FODdata;
	LiveData<ArrayList<InfoListItem>> dayInfoListData;
	LiveData<Boolean> SSdata;
	TextInputEditText etMarkNote;
	RadioGroup rg;

	ArrayList<DayInfo> frameOfDates;

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
    	instance = this;
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
		Toolbar toolbar = findViewById(R.id.main_toolbar);
		if(toolbar != null){
			setSupportActionBar(toolbar);
		}

		Objects.requireNonNull(getSupportActionBar()).setTitle("Factory Calendar");
		getSupportActionBar().setSubtitle("Калкндарь потребления воды");

		tvBtnAdd = findViewById(R.id.tv_btnAdd);
		tvBtnAdd.setOnClickListener(new onAddClickListener());

		tl_select = findViewById(R.id.tl_select);
		vpager_add = findViewById(R.id.vpager_add);
		setupViewPager();

		//***TEST ViewModel***
		sliderLayout = findViewById(R.id.bottom_sheet);
		sliderBehavior = BottomSheetBehavior.from(sliderLayout);

		llListInfo = findViewById(R.id.ll_list);
		llAdd = findViewById(R.id.ll_Add);
		cl_bottomContainer = findViewById(R.id.cl_bottomContainer);
		sceneMark = Scene.getSceneForLayout(llAdd, R.layout.mark_settings, this);
		sceneSchedule = Scene.getSceneForLayout(llAdd, R.layout.schedule_settings, this);

		btnMarkConfirm = findViewById(R.id.btn_markConfirm);

		etMarkNote = findViewById(R.id.et_markNote);

		model = ViewModelProviders.of(this).get(CalendarVM.class);
		FODdata = model.getFODLiveData();
		FODdata.observe(this, new Observer<ArrayList<DayInfo>>() {
			@Override
			public void onChanged(@Nullable ArrayList<DayInfo> fod) {
				frameOfDates = fod;
				updFrame();
			}
		});

		SSdata = model.getSStateLiveData();
		SSdata.observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(Boolean sliderState) {
				hideKeyboard(MainActivity.this);
				if(sliderState) sliderBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
				else {
					sliderBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
					if (tvBtnAdd.getText().equals("-")) tvBtnAdd.callOnClick();
				}
			}
		});

		dayInfoListData = model.getDayInfoListLiveData();
		dayInfoListData.observe(this, new Observer<ArrayList<InfoListItem>>() {
			@Override
			public void onChanged(@Nullable ArrayList<InfoListItem> infoList) {
				assert infoList != null;
				Log.d(CalendarVM.TAG, "=> onInfoChanged: "+infoList.size());
				llListInfo.removeAllViews();
				TreeNode listRoot = TreeNode.root();
				if(infoList.size() == 0) {
					tvBtnAdd.setText("-");
					addLayout_setVisible(true);
				}else {
					tvBtnAdd.setText("+");
					addLayout_setVisible(false);
					for (InfoListItem ilItem : infoList) {
						listRoot.addChild(new TreeNode(new InfoListItem(ilItem.getTime(), ilItem.getInfo())).setViewHolder(new InfoListHolder(MainActivity.this)));
					}
				}



				AndroidTreeView treeView = new AndroidTreeView(MainActivity.this, listRoot);
				llListInfo.addView(treeView.getView());
			}
		});

		sliderBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {
				if(newState == BottomSheetBehavior.STATE_COLLAPSED) model.setSliderState(false);
			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {

			}
		});

		//***TEST***
			
		llWeaks[0] = findViewById(R.id.LL_w1);
		llWeaks[1] = findViewById(R.id.LL_w2);
		llWeaks[2] = findViewById(R.id.LL_w3);
		llWeaks[3] = findViewById(R.id.LL_w4);
		llWeaks[4] = findViewById(R.id.LL_w5);
		llWeaks[5] = findViewById(R.id.LL_w6);


		TextView tvNext = findViewById(R.id.tv_nextMonth);
		TextView tvPrev = findViewById(R.id.tv_prevMonth);
		tvDebag = findViewById(R.id.tv_debag);
		if (!DEBUG)
			tvDebag.setVisibility(View.GONE);
		
		tvMonth = findViewById(R.id.tv_month);
		tvYear = findViewById(R.id.tv_year);




		llWeaks[0].getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener(){

				@Override
				public void onGlobalLayout() {

					// Ensure you call it only once :
					llWeaks[0].getViewTreeObserver().removeOnGlobalLayoutListener(this);
					updFrame();
				}
		});
		
		/// Слушатель смены месяца
		OnClickListener onChengeMonth = new OnClickListener() {
			@SuppressLint("NonConstantResourceId")
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
					case R.id.tv_prevMonth:
						model.previousMonth();
						break;
					case R.id.tv_nextMonth:
						model.nextMonth();
						break;
				}
			}
		};
		
		tvNext.setOnClickListener(onChengeMonth);
		tvPrev.setOnClickListener(onChengeMonth);
		
    }
	
	void inflateWeak(LinearLayout ll) {
		
		for (int i=1; i<=7; i++) {
			dv = new DayView(this);
			if (count<frameOfDates.size()) 
				dv.setDayText(frameOfDates.get(count).getDateString());
			dv.setId(count);

			/// Выделение текущего месяца
			if (frameOfDates.get(count).getMonthOffset() == 0){
				dv.setTypeface(Typeface.DEFAULT_BOLD);
			}else dv.setTextColor(0x55808080);
			
			/// Выделение отмеченных дат
			if (frameOfDates.get(count).isMarked()) {
				dv.markedUp(true);
				dv.markedDown(true, 0xff48b3ff);
			}
				
			//debag
			debugTxt += "("+frameOfDates.get(count).getId()+")\n";
			
			/// Выделение сегодняшней даты
			Calendar now = model.nCal.getNow();
			if (now.get(Calendar.YEAR) == frameOfDates.get(count).getCalendar().get(Calendar.YEAR)
				&& now.get(Calendar.DAY_OF_YEAR) == frameOfDates.get(count).getCalendar().get(Calendar.DAY_OF_YEAR)
				&& frameOfDates.get(count).getMonthOffset() == 0) {
				dv.setBackground(getDrawable(R.drawable.circle));
			}
			
			/// Слушатель нажатия на дату
			dv.setOnLongClickListener(new OnDayClickListener());
			dv.setOnClickListener(new OnDayClickListener());

			tvDebag.setText(debugTxt);
			
			ll.addView(dv);
			count++;
		}
		
		if (count>=42) count=0;
	}

	void updFrame() {
		debugTxt = "";
		String year = model.nCal.getYear()+"г.";
		tvYear.setText(year);
		tvMonth.setText(Month.getString(model.nCal.getMonth()));
		for (int i=0; i<6; i++) {
			llWeaks[i].removeAllViews();
			inflateWeak(llWeaks[i]);
		}
	}

	protected void addLayout_setVisible(Boolean visible) {
		cSet.clone(cl_bottomContainer);
		cSet.clear(R.id.ll_list, ConstraintSet.TOP);
		if (visible) {
			cSet.connect(R.id.ll_list, ConstraintSet.TOP, R.id.ll_Add, ConstraintSet.BOTTOM);
		}else {
			cSet.connect(R.id.ll_list, ConstraintSet.TOP, R.id.ll_Add, ConstraintSet.TOP);
		}
		TransitionManager.beginDelayedTransition(cl_bottomContainer);
		cSet.applyTo(cl_bottomContainer);
	}


	private void setupViewPager() {
		vpager_add.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
		tl_select.setupWithViewPager(vpager_add);
	}

	

	public static Activity getContext() {
    	return instance;
	}

	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		View view = activity.getCurrentFocus();
		if (view == null) {
			view = new View(activity);
		}
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
}
