package net.finch.calendar;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import net.finch.calendar.Views.SnakeView;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import static net.finch.calendar.CalendarVM.TAG;

public class Billing {
    public static final String MSG_TYPE = "type";
    public static final String MSG_TXTRES = "txt_res";
    private final AppCompatActivity activity;
    private final String SKU_PREMIUM =  "premium_upgrade";
    private final List<String> skuList = new ArrayList<>();
    private BillingClient billingClient = null;
    private SkuDetails skuNoAds;
    private String onVerify = "";
    CalendarVM model = MainActivity.getCalendarVM();




    public Billing(Context context) {
        this.activity = (AppCompatActivity) context;
        skuList.add(SKU_PREMIUM);

    }

    public void gpsStart() {
        if (billingClient != null) return;
        PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {     // (BillingResult billingResult, List<Purchase> purchases) -> {}
            // Этот слушатель получает обновления обо всех покупках в вашем приложении.
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
//                Log.d(TAG, "gpsStart: Покупка произведена успешно!");
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
//                Log.d(TAG, "gpsStart: Покупка отменена пользователем!");
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
//                Log.d(TAG, "gpsStart: Уже куплено!");
            } else {        // Обработайте любые другие коды ошибок.
//                Log.d(TAG, "gpsStart: Неудачная покупка / (CODE): "+billingResult.getResponseCode()+ " (MSG): "+billingResult.getDebugMessage());
                model.billingMsg(R.string.purchase_err, SnakeView.TYPE_ERROR);
            }
        };

        billingClient = BillingClient.newBuilder(activity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        connectToBilling();
    }


    private void connectToBilling() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // BillingClient готов. Вы можете запросить покупки.
//                    Log.d(TAG, "onBillingSetupFinished: ");
                    getSkuDetails();
                    queryPurchases();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Попробуйте перезапустить соединение при следующем запросе в Google Play, вызвав метод startConnection().
//                Log.d(TAG, "onBillingServiceDisconnected: ");
                connectToBilling();
            }
        });
    }


    private void handlePurchase(Purchase purchase) {
        // получить токен пользователя и токен продукта зашифровать и сохранить для проверки покупки
//        Log.d(TAG, "handlePurchase: Еть покупка =>  OrderId:" + purchase.getOrderId() + " state: "+purchase.getPurchaseState());
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
//                Log.d(TAG, "handlePurchase: PURCHASED  NOT ACKNOWLEDGE");
                verifyPurchase(purchase);
            } else if (purchase.isAcknowledged()){
//                Log.d(TAG, "handlePurchase: PURCHASED ACKNOWLEDGE");
                model.proState(true);

            }
        }else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            model.billingMsg(R.string.purchase_pedding, SnakeView.TYPE_INFO);
        } else {
//            Log.d(TAG, "handlePurchase: покупка отменена или не действительна");
            model.proState(false);
        }
    }



    private  void verifyPurchase(Purchase purchase) {
        if (onVerify.equals(purchase.getPurchaseToken())) return;
        onVerify = purchase.getPurchaseToken();
        String url = "https://us-central1-workcalendar-1206e.cloudfunctions.net/verifyPurchases?" +
                "purchaseToken=" + purchase.getPurchaseToken() + "&" +
                "purchaseTime=" + purchase.getPurchaseTime() + "&" +
                "orderId=" + purchase.getOrderId();
//        Log.d(TAG, "verifyPurchase: Запрос: "+url);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                (response) -> {      // Response.Listener<String>()
//                    Log.d(TAG, "verifyPurchase: Ответ: "+response);
                    try {
                        JSONObject pInfoFromServer = new JSONObject(response);
                        if (pInfoFromServer.getBoolean("isValid")) {
//                            Log.d(TAG, "verifyPurchase: isValid = "+true);
                            acknowledge(purchase);
                        }
                    }catch (Exception ignored) { }
                },
                (error) -> {      // Response.ErrorListener()
//                    Log.d(TAG, "verifyPurchase: ERR");
                    onVerify = "";
                });

        Volley.newRequestQueue(activity).add(stringRequest);

    }

    private void acknowledge(Purchase purchase) {
        AcknowledgePurchaseParams acknowledgePurchaseParams =
                AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            onVerify = "";
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                Log.d(TAG, "acknowledge: OK");
                model.proState(true);
            }   // else Log.d(TAG, "acknowledge: ERR");
        });
    }

    private void getSkuDetails() {
        SkuDetailsParams params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build();

        billingClient.querySkuDetailsAsync(params,
                (billingResult, skuDetailsList) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                            && skuDetailsList != null){
                        skuNoAds = skuDetailsList.get(0);

                    }
                });
    }


    public void getPro() {
        BillingFlowParams billingFlowParams = BillingFlowParams
                .newBuilder()
                .setSkuDetails(skuNoAds)
                .build();
        int responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();
        Log.d(TAG, "getSkuDetails: skuDetails = "+ skuNoAds + ";  responseCode = "+responseCode);
    }


    public void queryPurchases() {
//        Log.d(TAG, "queryPurchases: ");
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult, purchasesList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (purchasesList.size() == 0) {
//                    Log.d(TAG, "queryPurchases: Покупок не обнаружено");
                    model.proState(false);
                }
                for (Purchase purchase : purchasesList) {
                    handlePurchase(purchase);
                }
            }
        });
    }
}
