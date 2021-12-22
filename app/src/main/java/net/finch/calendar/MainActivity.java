package net.finch.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import net.finch.calendar.Dialogs.PopupAbout;
import net.finch.calendar.Dialogs.PopupAdd;
import net.finch.calendar.Dialogs.PopupRate;
import net.finch.calendar.Marks.MarkListHolder;
import net.finch.calendar.SDLEditor.SdlEditorActivity;
import net.finch.calendar.Schedules.ShiftListHolder;
import net.finch.calendar.Settings.HSettings;
import net.finch.calendar.Views.SnakeView;
import java.util.ArrayList;
import java.util.Map;

import static net.finch.calendar.CalendarVM.TAG;

public class MainActivity extends AppCompatActivity implements OnClickListener {
	@SuppressLint("StaticFieldLeak")
	public static MainActivity instance;
	public final static int ROOT_ID = R.id.main_layout;
	public static int pageOffset = 0;

	private Toolbar toolbar;
	private OnAddFABClickListener fabClickListener;
	private TextView tvSliderTitle;
	private LinearLayout sliderLayout;
	private BottomSheetBehavior<View> sliderBehavior;
	private LinearLayout llSdlInfoList, llMarkInfoList;
	private CalendarPagerAdapter pagerAdapter;
	private ViewPager2 pager;
	private FrameLayout flAds;

	private CalendarVM model;
	protected ArrayList<DayInfo> frameOfDates = new ArrayList<>();

	private InterstitialAd mInterstitialAd;
	public static Billing billingObj;
	private HSettings hset;



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		Log.d(TAG, "onKeyDown: keycode = "+keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (hset.needShowRate()) new PopupRate(this);
			else finish();
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case (R.id.main_menu_0):
				if (pager != null) pager.setCurrentItem(CalendarPagerAdapter.START_PAGE, true);
				break;
			case (R.id.main_menu_1):
				if (mInterstitialAd != null && hset.needShowADS()) showInterAds();
				else startSDLEditor();
				break;
			case (R.id.main_menu_2):
				billingObj.getPro();
				break;
			case (R.id.main_menu_2_5):
				hset.noRate();
				// TODO: Go to PlayMarket URL...
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(
						getString(R.string.market_url)));
				intent.setPackage("com.android.vending");
				startActivity(intent);
				break;
			case (R.id.main_menu_3):
				new PopupAbout(this).setOnNoAdsClickListener(() -> {
					Log.d(TAG, "onOptionsItemSelected: GET PRO");
					billingObj.getPro();
				});
		}
		return true;
	}


	@Override
	protected void onDestroy() {
		hset.countAdd(HSettings.ROC);
		super.onDestroy();
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		billingObj.queryPurchases();
		Log.d(TAG, "onPostResume: ");

		if (model != null) {
		model.getFODLiveData(null);
		model.updInfoList();
		model.proState(hset.isPro());
		}
		setADS();
	}


	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
    	instance = this;
    	model = getCalendarVM();
		hset = new HSettings(this);

        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setViews();
		setADS();
		billingObj = new Billing(this);
		billingObj.gpsStart();

		//***  ViewModel  ***
		setObservers();

    }

    private void setViews() {
		toolbar = findViewById(R.id.main_toolbar);
		if(toolbar != null) setSupportActionBar(toolbar);

		flAds = findViewById(R.id.fl_adView);

		fabClickListener = new OnAddFABClickListener();
		FloatingActionButton fabAdd = findViewById(R.id.afab_add);
		fabAdd.setOnClickListener(fabClickListener);

		FloatingActionButton fabAddMark = findViewById(R.id.fab_mark);
		fabAddMark.setOnClickListener(this);

		FloatingActionButton fabAddSdl = findViewById(R.id.fab_sdl);
		fabAddSdl.setOnClickListener(this);

		llSdlInfoList = findViewById(R.id.main_bottom_ll_sdllist);
		llMarkInfoList = findViewById(R.id.main_bottom_ll_marklist);
		tvSliderTitle = findViewById(R.id.tv_slider_title);
		sliderLayout = findViewById(R.id.main_ll_bottom_sheet);					//bottom_sheet
		sliderBehavior = BottomSheetBehavior.from(sliderLayout);
		sliderBehavior.addBottomSheetCallback(new SliderBehaviorCallback());

// *** ViewPager2 ***
		pager = findViewById(R.id.calendar_pager);
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

		ImageButton ibtnPrevious = findViewById(R.id.calendar_ibtn_previous);
		ibtnPrevious.setOnClickListener((v)-> pager.setCurrentItem(pager.getCurrentItem()-1, true));

		ImageButton ibtnNext = findViewById(R.id.calendar_ibtn_next);
		ibtnNext.setOnClickListener((v)-> pager.setCurrentItem(pager.getCurrentItem()+1, true));
	}

	private void setObservers() {
//  *** BILLING MESSAGE LD Observe ***  //
		LiveData<Map<String, Integer>> billingMSG = model.billingMsg(null,null);
		billingMSG.observe(this, msgMap -> {
			if (msgMap.get(Billing.MSG_TXTRES) != null) {
				Integer type = msgMap.get(Billing.MSG_TYPE);
				Integer msg = msgMap.get(Billing.MSG_TYPE);
				if (type!=null && msg!=null)
					SnakeView.make(
						findViewById(R.id.main_layout),
						type,
						msg
				).show();
			}

		});

//  *** PRO STATE LD Observe ***  //
		LiveData<Boolean> proStateLD = model.proState(hset.isPro());
		proStateLD.observe(this, proState -> {
			Log.i(TAG, "setObservers: proState "+proState);
			if (hset.isPro() != proState) {
				Log.i(TAG, "setObservers: proState Changed");
				hset.setPro(proState);
				if (proState) purchaseSuccess();
				else setADS();
			}
		});

//  *** FRAME OF DATE LD Observe ***  //
		LiveData<ArrayList<DayInfo>> FODdata = model.getFODLiveData(null);
		FODdata.observe(this, fod -> {
			frameOfDates = fod;
			model.updInfoList();
			if (model.selectedDayId != null) {
				tvSliderTitle.setText(fod.get(model.selectedDayId).getFullDateString());
				pagerAdapter.notifyDataSetChanged();
			}
		});


//  *** SLIDER STATE LD Observe ***  //
		LiveData<Boolean> SSdata = model.getSStateLiveData();
		SSdata.observe(this, sliderState -> {
			hideKeyboard(MainActivity.this);
			if(sliderState) {
				sliderBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
			}
			else {
				sliderBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
			}
		});


//  *** DAY INFO LD Observe ***  //
		LiveData<DayInfo> dayInfoListData = model.getDayInfoLiveData();
		dayInfoListData.observe(this, dayInfo -> {
			if (dayInfo == null) return;
			llSdlInfoList.removeAllViews();
			llMarkInfoList.removeAllViews();

			if(dayInfo.getShiftList().size() != 0) {
				TreeNode sdlListRoot = TreeNode.root();
				sdlListRoot.addChild(new TreeNode(new MainBottomChapterObject(R.drawable.ic_calendar_sdl, getString(R.string.main_bottom_sftlist_name))).setViewHolder(new MainListHolder(instance)));
				for (int i=0; i<dayInfo.getShiftList().size(); i++) {
					sdlListRoot.addChild(new TreeNode(dayInfo.getShiftList().get(i)).setViewHolder(new ShiftListHolder(MainActivity.this, (i==(dayInfo.getShiftList().size()-1)))));
				}
				AndroidTreeView treeView = new AndroidTreeView(MainActivity.this, sdlListRoot);
				llSdlInfoList.addView(treeView.getView());
			}


			if(dayInfo.getMarkList().size() != 0) {
				TreeNode markListRoot = TreeNode.root();
				markListRoot.addChild(new TreeNode(new MainBottomChapterObject(R.drawable.ic_mark, getString(R.string.main_bottom_mrklist_name))).setViewHolder(new MainListHolder(instance)));
				for (int i=0; i<dayInfo.getMarkList().size(); i++) {
					markListRoot.addChild(new TreeNode(dayInfo.getMarkList().get(i)).setViewHolder(new MarkListHolder(MainActivity.this, (i==(dayInfo.getMarkList().size()-1)))));
				}
				AndroidTreeView treeView = new AndroidTreeView(MainActivity.this, markListRoot);
				llMarkInfoList.addView(treeView.getView());
			}
//			model.setSliderState(true);
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
//		Log.d(TAG, "onFABClick: ");
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




	private void setADS() {
		if (!hset.isPro()) {
			MobileAds.initialize(this, initializationStatus -> {
			});
			setBannerAds();
			setInterAds();
		}else {
			flAds = findViewById(R.id.fl_adView);
			flAds.setVisibility(View.VISIBLE);
			flAds.removeAllViews();
		}
	}

	private void setBannerAds() {
		AdView adView = new AdView(this);
		adView.setBackgroundColor(getColor(R.color.colorPrimaryDark_2));
		adView.setAdUnitId(getString(R.string.ads_baner_id));

		flAds.setVisibility(View.VISIBLE);
		flAds.removeAllViews();
		flAds.addView(adView);

		AdRequest adRequest = new AdRequest.Builder().build();
		AdSize adSize = Utils.getAdSize(this);
		adView.setAdSize(adSize);
		adView.loadAd(adRequest);
	}

	private void setInterAds() {
		// preLoad ADS
		AdRequest adRequest = new AdRequest.Builder().build();
		InterstitialAd.load(this,getString(R.string.ads_inter_id), adRequest,
				new InterstitialAdLoadCallback() {
					@Override
					public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
						// ADS загружено.
						mInterstitialAd = interstitialAd;
						Log.i(TAG, "onAdLoaded");

						// setCallback ADS
						mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
							@Override
							public void onAdDismissedFullScreenContent() {
								// Полноэкранный ADS закрыт.
								Log.d("TAG", "The ad was dismissed.");
								startSDLEditor();
							}

							@Override
							public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
								// Ошибка отображения полноэкранного ADS.
								Log.d("TAG", "The ad failed to show.");
								startSDLEditor();
							}

							@Override
							public void onAdShowedFullScreenContent() {
								// Полноэкранный ADS отображается.
								// Обязательно установите для ссылки значение null, чтобы не отображать ее второй раз.
								mInterstitialAd = null;
								Log.d("TAG", "The ad was shown.");
							}
						});
					}

					@Override
					public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
						// Ошибка загрузки ADS
						Log.i(TAG, loadAdError.getMessage());
						mInterstitialAd = null;
					}
				});
	}

	private void showInterAds() {
		hset.resetADSCounts();
		mInterstitialAd.show(this);
	}

	private void startSDLEditor() {
		Intent intent = new Intent(this, SdlEditorActivity.class);
		startActivity(intent);
	}

	public void purchaseSuccess() {               // Покупка совершена
		Log.d(TAG, "purchaseSuccess: ");
		model.billingMsg(R.string.purchase_success, SnakeView.TYPE_SUCCESS);

		flAds = findViewById(R.id.fl_adView);
		flAds.getChildAt(0).setVisibility(View.GONE);
		flAds.invalidate();
	}
}
