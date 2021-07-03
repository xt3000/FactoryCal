package net.finch.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import net.finch.calendar.Dialogs.PopupAdd;
import net.finch.calendar.Marks.Mark;
import net.finch.calendar.Marks.MarkListHolder;
import net.finch.calendar.Schedules.Shift;
import net.finch.calendar.Schedules.ShiftListHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static net.finch.calendar.CalendarVM.TAG;

public class MainActivity extends AppCompatActivity implements OnClickListener {
	@SuppressLint("StaticFieldLeak")
	public static MainActivity instance;
	final boolean DEBUG = false;
	String debugTxt="";

	ConstraintSet cSet = new ConstraintSet();
	ConstraintLayout cl_bottomContainer;

	FloatingActionButton fabAdd;
	FloatingActionButton fabAddMark;
	FloatingActionButton fabAddSdl;
	OnAddFABClickListener fabClickListener;

	LinearLayout[] llWeaks = new LinearLayout[6];

	LinearLayout sliderLayout;
	BottomSheetBehavior sliderBehavior;

	LinearLayout llListInfo;
	LinearLayout llAdd;

	DayView dv;
	TextView tvMonth;
	TextView tvYear;
	TextView tvDebag;
	Button btnMarkConfirm;

	int count = 0;

	CalendarVM model;
	LiveData<ArrayList<DayInfo>> FODdata;
	LiveData<DayInfo> dayInfoListData;
	LiveData<Boolean> SSdata;
	TextInputEditText etMarkNote;
	RadioGroup rg;

	ArrayList<DayInfo> frameOfDates;

	@RequiresApi(api = Build.VERSION_CODES.M)
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

		fabClickListener = new OnAddFABClickListener();

		fabAdd = findViewById(R.id.afab_add);
		fabAdd.setOnClickListener(fabClickListener);

		fabAddMark = findViewById(R.id.fab_mark);
		fabAddMark.setOnClickListener(this);

		fabAddSdl = findViewById(R.id.fab_sdl);
		fabAddSdl.setOnClickListener(this);


		//***TEST ViewModel***

		sliderLayout = findViewById(R.id.bottom_sheet);
		sliderBehavior = BottomSheetBehavior.from(sliderLayout);
		sliderBehavior.setBottomSheetCallback(new SliderBehaviorCallback());

		llListInfo = findViewById(R.id.ll_markList);
		llAdd = findViewById(R.id.ll_sdlList);
		cl_bottomContainer = findViewById(R.id.cl_bottomContainer);

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
				if(sliderState) {
					sliderBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
				}
				else {
					sliderBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
				}
			}
		});

		dayInfoListData = model.getDayInfoLiveData();
		dayInfoListData.observe(this, new Observer<DayInfo>() {
			@Override
			public void onChanged(@Nullable DayInfo dayInfo) {
				assert dayInfo != null;
//				Log.d(CalendarVM.TAG, "=> onInfoChanged: "+dayInfo.size());
				llListInfo.removeAllViews();

				TreeNode listRoot = TreeNode.root();
				if(dayInfo.getShiftList().size() != 0) {
					listRoot.addChild(new TreeNode("   Графики:"));
					for (Shift s : dayInfo.getShiftList()) {
						listRoot.addChild(new TreeNode(s).setViewHolder(new ShiftListHolder(MainActivity.this)));
					}
				}

				if(dayInfo.getMarkList().size() != 0) {
					listRoot.addChild(new TreeNode("\n   Пометки:"));
					for (Mark m : dayInfo.getMarkList()) {
						listRoot.addChild(new TreeNode(m).setViewHolder(new MarkListHolder(MainActivity.this)));
					}
				}

				AndroidTreeView treeView = new AndroidTreeView(MainActivity.this, listRoot);
				llListInfo.addView(treeView.getView());
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
			DayInfo dayInfo = frameOfDates.get(count);

			dv = new DayView(this);
			if (count<frameOfDates.size()) 
				dv.setDayText(dayInfo.getDateString());
			dv.setId(count);

			/// Выделение текущего месяца
			if (dayInfo.getMonthOffset() == 0){
				dv.setTypeface(Typeface.DEFAULT_BOLD);
			}else dv.setTextColor(0x55808080);
			
			/// Выделение отмеченных дат
			if (dayInfo.isMarked()) {
				dv.markedUp(true);
//				dv.markedDown(true, 0xff48b3ff);
			}

			///  Выделение смены графика
			if (
					dayInfo.isShifted()
					&& dayInfo.getShiftList().size() > 0
					&& dayInfo.getShiftList().get(0).isPrime()) {
				dv.markedDown(true, frameOfDates.get(count).getShiftList().get(0).getColor());
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
//			dv.setOnLongClickListener(new OnDayClickListener());
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

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	public void onClick(View view) {
		Log.d(TAG, "onFABClick: ");
		model.setSliderState(false);
		switch (view.getId()) {
			case R.id.fab_mark:
				new PopupAdd(PopupAdd.MARK);
				break;
			case R.id.fab_sdl:
				new PopupAdd(PopupAdd.SHEDULE);
				break;
		}
	}

}
