package com.smacon.fish2marine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smacon.f2mlibrary.CustomToast;
import com.smacon.f2mlibrary.MaterialDialog.Effectstype;
import com.smacon.f2mlibrary.MaterialDialog.NiftyDialogBuilder;
import com.smacon.f2mlibrary.Switcher.Switcher;
import com.smacon.fish2marine.AdapterClass.MyCartAdapter;
import com.smacon.fish2marine.CheckOut.Checkout_SetAddress;
import com.smacon.fish2marine.HelperClass.CartListItem;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class MyCartActivity extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences sPreferences;
    private SqliteHelper helper;
    private Config mConfig;
    List<HashMap<String, String>> SQLData_Item ;
    String CustomerID = "";
    int RewardPoints;
    private Switcher switcher;
    private RecyclerView mrecyclerview;
    private TextView mTitle,error_label_retry, empty_label_retry,carttotal,applyrewards,applycoupon,itemcount,subtotal,shipping,tax,grandtotal;
    ArrayList<CartListItem> dataItem;
    ImageView back;
    Button btn_checkout;
    LinearLayout sub_layout,main_layout;
    MyCartAdapter myCartAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycart);
        helper = new SqliteHelper(getApplicationContext(), "Fish2Marine", null, 5);
        sPreferences = getApplicationContext().getSharedPreferences("Fish2Marine", MODE_PRIVATE);
        mConfig = new Config(getApplicationContext());
        InitIdView();
    }
    private void InitIdView(){
        SQLData_Item = helper.getadmindetails();
        CustomerID=SQLData_Item.get(0).get("admin_id");
        Log.d("1111221", "Customer ID "+CustomerID);

        mTitle=(TextView)findViewById(R.id.mTitle);
        mTitle.setText("My Cart");

        switcher = new Switcher.Builder(getApplicationContext())
                .addContentView(findViewById(R.id.main_layout))
                .addErrorView( findViewById(R.id.error_view))
                .addProgressView( findViewById(R.id.progress_view))
                .setErrorLabel((TextView) findViewById(R.id.error_label))
                .setEmptyLabel((TextView) findViewById(R.id.empty_label))
                .addEmptyView( findViewById(R.id.empty_view))
                .build();
        sub_layout=((LinearLayout)findViewById(R.id.sub_layout));
        main_layout=((LinearLayout)findViewById(R.id.main_layout));
        mrecyclerview = ((RecyclerView) findViewById(R.id.mrecyclerview));
        mrecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        carttotal = ((TextView)  findViewById(R.id.CartTotal));
        applycoupon = ((TextView) findViewById(R.id.applycoupon));
        applyrewards = ((TextView) findViewById(R.id.applyrewards));
        btn_checkout=((Button) findViewById(R.id.btn_checkout));
        itemcount = ((TextView)  findViewById(R.id.itemcount));
        subtotal = ((TextView) findViewById(R.id.subtotal));
        shipping = ((TextView)  findViewById(R.id.shipping));
        tax = ((TextView) findViewById(R.id.tax));
        grandtotal = ((TextView) findViewById(R.id.grandtotal));

        error_label_retry = ((TextView)  findViewById(R.id.error_label_retry));
        empty_label_retry = ((TextView) findViewById(R.id.empty_label_retry));
        error_label_retry.setOnClickListener(this);
        empty_label_retry.setOnClickListener(this);
        btn_checkout.setOnClickListener(this);
        applycoupon.setOnClickListener(this);
        applyrewards.setOnClickListener(this);
        back=(ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

        dataItem = new ArrayList<>();
        InitGetCartData();

    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId) {

            case R.id.error_label_retry:
                InitGetCartData();
                break;
            case R.id.empty_label_retry:
                InitGetCartData();
                break;
            case R.id.back:
                onBackPressed();
                break;
            case R.id.btn_checkout:
                Intent intent = new Intent(MyCartActivity.this,Checkout_SetAddress.class);
                startActivity(intent);
                break;
            case R.id.applycoupon:
                final NiftyDialogBuilder coupondialog=NiftyDialogBuilder.getInstance(this);
                coupondialog
                        .withTitle("Apply Coupon")
                        .withTitleColor("#FFFFFF")
                        .withDividerColor("#11000000")
                        .withDialogColor("#E53935")
                        .withIcon(getResources().getDrawable(R.drawable.ic_basket))
                        .isCancelableOnTouchOutside(true)
                        .withDuration(600)
                        .withEffect(Effectstype.Slidetop)
                        .withButton1Text("Apply")
                        // .withButton2Text("Cancel")
                        .setCustomView(R.layout.dialog_applycoupon,getApplicationContext())
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final EditText discountcoupon=(EditText)coupondialog.findViewById(R.id.discountcoupon);
                                if(discountcoupon.getText().toString().equals("")){
                                    //comment.setError("Comment should not be blank");
                                    CustomToast.error(v.getContext(), "Coupon should not be blank", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    coupondialog.dismiss();
                                    InitApplyCoupon(discountcoupon.getText().toString());
                                }
                            }
                        })
                        /*.setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogBuilder.dismiss();
                            }
                        })*/
                        .show();
                break;
            case R.id.applyrewards:
                final NiftyDialogBuilder rewarddialog=NiftyDialogBuilder.getInstance(this);
                rewarddialog
                        .withTitle("Apply Rewards")
                        .withTitleColor("#FFFFFF")
                        .withDividerColor("#11000000")
                        .withDialogColor("#E53935")
                        .withIcon(getResources().getDrawable(R.drawable.ic_basket))
                        .isCancelableOnTouchOutside(true)
                        .withDuration(600)
                        .withEffect(Effectstype.Slidetop)
                        .withButton1Text("Apply")
                        // .withButton2Text("Cancel")
                        .setCustomView(R.layout.dialog_applyrewards,getApplicationContext())
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final EditText rewards=(EditText)rewarddialog.findViewById(R.id.rewardpoints);

                                if(rewards.getText().toString().equals("")){
                                    //comment.setError("Comment should not be blank");
                                    CustomToast.error(v.getContext(), "Please enter Reward Points", Toast.LENGTH_SHORT).show();
                                }
                                else if(Integer.parseInt(rewards.getText().toString())>=RewardPoints){
                                    CustomToast.error(v.getContext(), "You have only "+RewardPoints+" Reward Points", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    rewarddialog.dismiss();
                                    InitRewards(rewards.getText().toString());
                                }
                            }
                        })
                       /* .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogBuilder.dismiss();
                            }
                        })*/
                        .show();
                break;
        }
    }

    private void InitGetCartData(){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            LoadMyCartInitiate mLoadMyCartInitiate = new LoadMyCartInitiate(CustomerID);
            mLoadMyCartInitiate.execute((Void) null);
        }else {
            switcher.showErrorView("No Internet Connection");
        }
    }

    private void InitApplyCoupon(String CouponCode){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            ApplyCouponInitiate mApplyCouponInitiate = new ApplyCouponInitiate(CustomerID,CouponCode);
            mApplyCouponInitiate.execute((Void) null);
        }else {
            switcher.showErrorView("No Internet Connection");
        }
    }

    private void InitRewards(String RewardPoints){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            ApplyRewardsInitiate mApplyRewardsInitiate = new ApplyRewardsInitiate(CustomerID,RewardPoints);
            mApplyRewardsInitiate.execute((Void) null);
        }else {
            switcher.showErrorView("No Internet Connection");
        }
    }
    public class ApplyRewardsInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mId,mRewards;
        ApplyRewardsInitiate(String ID,String Rewards) {
            mId=ID;
            mRewards=Rewards;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switcher.showProgressView();
            //helper.Delete_timeslot_details();
        }
        @Override
        protected StringBuilder doInBackground(Void... params) {
            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doApplyRewardPoints(mId,mRewards);
            Log.d("111111111","PASSING VALUE: CUSTOMER ID "+mId+"Rewards: "+mRewards);
            Log.d("111111111", "RESULT "+result);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj0 = new JSONObject(result.toString());
                if (jsonObj0.has("status")) {
                    if (jsonObj0.getString("status").equals(String.valueOf(1))) {
                        switcher.showContentView();
                        sub_layout.setVisibility(View.VISIBLE);
                        Log.d("111111", "here0 ");
                        JSONObject jsonObj1 = jsonObj0.getJSONObject("data");
                    }else {
                        switcher.showEmptyView();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                switcher.showErrorView("Please Try Again");
            } catch (NullPointerException e) {
                switcher.showErrorView("No Internet Connection");
            } catch (Exception e) {
                switcher.showErrorView("Please Try Again");
            }
        }

    }
    public class ApplyCouponInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mId,mCouponCode;
        ApplyCouponInitiate(String ID,String Coupon) {
            mId=ID;
            mCouponCode=Coupon;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switcher.showProgressView();
            //helper.Delete_timeslot_details();
        }
        @Override
        protected StringBuilder doInBackground(Void... params) {
            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doApplyCoupon(mId,mCouponCode);
            Log.d("111111111","PASSING VALUE: CUSTOMER ID "+mId+"Coupon: "+mCouponCode);
            Log.d("111111111", "RESULT "+result);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj0 = new JSONObject(result.toString());
                if (jsonObj0.has("status")) {
                    if (jsonObj0.getString("status").equals(String.valueOf(1))) {
                        switcher.showContentView();
                        sub_layout.setVisibility(View.VISIBLE);
                        Log.d("111111", "here0 ");
                        JSONObject jsonObj1 = jsonObj0.getJSONObject("data");
                    }else {
                        switcher.showEmptyView();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                switcher.showErrorView("Please Try Again");
            } catch (NullPointerException e) {
                switcher.showErrorView("No Internet Connection");
            } catch (Exception e) {
                switcher.showErrorView("Please Try Again");
            }
        }

    }

    public class LoadMyCartInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mId;


        LoadMyCartInitiate(String ID) {
            mId=ID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switcher.showProgressView();
            //helper.Delete_timeslot_details();
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doMyCartList(mId);
            Log.d("111111111","PASSING VALUE: CUSTOMER ID "+mId);
            Log.d("111111111", "RESULT "+result);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj0 = new JSONObject(result.toString());
                if (jsonObj0.has("status")) {
                    if (jsonObj0.getString("status").equals(String.valueOf(1))) {
                        switcher.showContentView();
                        sub_layout.setVisibility(View.VISIBLE);
                        Log.d("111111", "here0 ");
                        JSONObject jsonObj1 = jsonObj0.getJSONObject("data");
                        if(jsonObj1.has("items")) {
                            JSONArray feedArray1 = jsonObj1.getJSONArray("items");
                            Log.d("1111221", "API jsonObj1" + feedArray1);
                            for (int i = 0; i < feedArray1.length(); i++) {
                                CartListItem item = new CartListItem();
                                JSONObject feedObj1 = (JSONObject) feedArray1.get(i);
                                item.setProductId(feedObj1.getString("productId"));
                                item.setProductImage(feedObj1.getString("imagepath"));
                                item.setProductName(feedObj1.getString("productname")+"/"+feedObj1.getString("itemId"));
                                Log.d("111111", "here3 "+feedObj1.getString("productname"));
                                item.setOtherName(feedObj1.getString("nameInMalayalam"));
                                item.setQuantity(feedObj1.getString("qty"));
                                item.setNetQty(feedObj1.getDouble("netQty"));
                                item.setPrice(feedObj1.getString("price"));
                                item.setItemsTotal(feedObj1.getString("itemsTotal"));
                                item.setItemId(feedObj1.getString("itemId"));
                                item.setCutTypeApplicable(feedObj1.getString("cutTypeApplicable"));
                                item.setCutType(feedObj1.getString("cutType"));
                                item.setBeforeCleaning(feedObj1.getDouble("beforeCleaning"));
                                item.setAfterCleaning(feedObj1.getDouble("afterCleaning"));
                                item.setSoldBy(feedObj1.getString("soldby"));
                                dataItem.add(item);
                            }
                            myCartAdapter = new MyCartAdapter(MyCartActivity.this, dataItem);
                            mrecyclerview.setAdapter(myCartAdapter);
                        }
                        if(jsonObj1.has("summary")) {
                            JSONObject jsonObj2 = jsonObj1.getJSONObject("summary");
                            Log.d("111111", "here3 "+jsonObj2.getString("cartSubtotal"));
                            carttotal.setText(jsonObj2.getString("cartSubtotal"));
                            subtotal.setText(jsonObj2.getString("cartSubtotal"));
                            itemcount.setText(jsonObj2.getString("itemsCount"));
                            shipping.setText(jsonObj2.getString("shipping"));
                            tax.setText(jsonObj2.getString("tax"));
                            grandtotal.setText(jsonObj2.getString("grandTotal"));
                            RewardPoints=jsonObj2.getInt("rewardbalancepoints");
                            if(jsonObj2.has("slots")){
                                Log.d("111111", "here4 ");
                                JSONObject jsonObj3 = jsonObj2.getJSONObject("slots");
                                Iterator<String> keys = jsonObj3.keys();
                                Log.d("111111", "here5 "+jsonObj3);
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    Log.d("11111list key", key);
                                    if(jsonObj3.get(key) instanceof JSONArray) {
                                        JSONArray feedArray2 = jsonObj3.getJSONArray(key);
                                        for (int i = 0; i < feedArray2.length(); i++) {
                                            JSONObject feedObj1 = (JSONObject) feedArray2.get(i);
                                            String slot = feedObj1.getString("slot");
                                            Log.d("111111", "here8 " + slot);

                                        }
                                        /*JSONObject innerJObject = jObject.getJSONObject(key);
                                        String id = innerJObject.getString("id");
                                        String name = innerJObject.getString("name");
                                        String points = innerJObject.getString("points");
                                        String ranking = innerJObject.getString("ranking");
                                        String tour = innerJObject.getString("tour");
                                        String lastUpdate = innerJObject.gettString("lastUpdate");*/

                                    }
                                }
                            }
                        }

                    }else {
                        switcher.showEmptyView();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                switcher.showErrorView("Please Try Again");
            } catch (NullPointerException e) {
                switcher.showErrorView("No Internet Connection");
            } catch (Exception e) {
                switcher.showErrorView("Please Try Again");
            }
        }

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MyCartActivity.this,NavigationDrawerActivity.class);
        intent.putExtra("PAGE","HOME");
        startActivity(intent);
        finish();
    }
}