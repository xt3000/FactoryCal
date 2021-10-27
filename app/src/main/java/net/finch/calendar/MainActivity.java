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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import net.finch.calendar.Dialogs.PopupAdd;
import net.finch.calendar.Marks.Mark;
import net.finch.calendar.Marks.MarkListHolder;
import net.finch.calendar.SDLEditor.SdlEditorActivity;
import net.finch.calendar.Schedules.Shift;
import net.finch.calendar.Schedules.ShiftListHolder;
import net.finch.calendar.Views.DayView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static net.finch.calendar.CalendarVM.TAG;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity implements OnClickListener {
	@SuppressLint("StaticFieldLeak")
	public static MainActivity instance;
	public final static int ROOT_ID = R.id.main_layout;
	final boolean DEBUG = false;
	String debugTxt="";

	ConstraintLayout cl_bottomContainer;
	Toolbar toolbar;

	FloatingActionButton fabAdd;
	FloatingActionButton fabAddMark;
	FloatingActionButton fabAddSdl;
	OnAddFABClickListener fabClickListener;

	LinearLayout[] llWeaks = new LinearLayout[6];

	LinearLayout sliderLayout;
	BottomSheetBehavior sliderBehavior;

	LinearLayout llListInfo, llSdlInfoList, llMarkInfoList;
//	LinearLayout llAdd;

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
			try {
				model.getFODLiveData();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (model != null) model.updInfoList();
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
    	instance = this;
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


		//***  ViewModel  ***

		sliderLayout = findViewById(R.id.main_ll_bottom_sheet);					//bottom_sheet
		sliderBehavior = BottomSheetBehavior.from(sliderLayout);
		sliderBehavior.addBottomSheetCallback(new SliderBehaviorCallback());

		llListInfo = findViewById(R.id.ll_infoList);
		llSdlInfoList = findViewById(R.id.main_bottom_ll_sdllist);
		llMarkInfoList = findViewById(R.id.main_bottom_ll_marklist);

		cl_bottomContainer = findViewById(R.id.cl_bottomContainer);

		btnMarkConfirm = findViewById(R.id.btn_markConfirm);

		etMarkNote = findViewById(R.id.et_markNote);

		model = getCalendarVM();
		try {
			FODdata = model.getFODLiveData();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		FODdata.observe(this, fod -> {
			Log.d(TAG, "onChanged: FOD LD");
			frameOfDates = fod;
			updFrame();
		});

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

		dayInfoListData = model.getDayInfoLiveData();
		dayInfoListData.observe(this, dayInfo -> {

//				Log.d(CalendarVM.TAG, "=> onInfoChanged: "+dayInfo.size());
			if (dayInfo == null) return;
//			llListInfo.removeAllViews();
			llSdlInfoList.removeAllViews();
			llMarkInfoList.removeAllViews();

			if(dayInfo.getShiftList().size() != 0) {
				TreeNode sdlListRoot = TreeNode.root();
				sdlListRoot.addChild(new TreeNode(new MainBottomChapterObject(android.R.drawable.ic_menu_today, "Смены")).setViewHolder(new MainListHolder(instance)));
				for (int i=0; i<dayInfo.getShiftList().size(); i++) {
					sdlListRoot.addChild(new TreeNode(dayInfo.getShiftList().get(i)).setViewHolder(new ShiftListHolder(MainActivity.this, (i==(dayInfo.getShiftList().size()-1)))));
				}
//				sdlListRoot.addChild(new TreeNode("   "));
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


		sliderLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				sliderLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) sliderLayout.getLayoutParams();

				params.height = sliderLayout.getMeasuredHeight() - toolbar.getMeasuredHeight();
				sliderLayout.setLayoutParams(params);
			}
		});

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
						try {
							model.previousMonth();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						break;
					case R.id.tv_nextMonth:
						try {
							model.nextMonth();
						} catch (JSONException e) {
							e.printStackTrace();
						}
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
				dv.setTypeface(ResourcesCompat.getFont(this, R.font.open_sans_semibold));
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
//		model.setSliderState(false);
		try {
			switch (view.getId()) {
				case R.id.fab_mark:
					new PopupAdd(this, PopupAdd.MARK);
					fabClickListener.fabAddMenuClick();
					break;
				case R.id.fab_sdl:
					new PopupAdd(this, PopupAdd.SCHEDULE);
					fabClickListener.fabAddMenuClick();
					break;
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}

	}



}
