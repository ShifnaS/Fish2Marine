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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smacon.f2mlibrary.Switcher.Switcher;
import com.smacon.fish2marine.AdapterClass.PlaceOrderItemAdapter;
import com.smacon.fish2marine.CCAvenuePayment.WebViewActivity;
import com.smacon.fish2marine.Constants.OrdersConstants;
import com.smacon.fish2marine.HelperClass.CartListItem;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class OrderSummaryActivity extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences sPreferences;
    private SqliteHelper helper;
    private Config mConfig;
    List<HashMap<String, String>> SQLData_Item ;
    String CustomerID = "",QuoteID;
    int RewardPoints;
    private Switcher switcher;
    private RecyclerView mrecyclerview;
    private TextView mTitle,error_label_retry, empty_label_retry,
            carttotal,itemcount,ordertotal,shipping,tax,discount,
            shippingmethod,deliverydate,deliveryslot,shipaddress;
    ArrayList<CartListItem> dataItem;
    ImageView back;
    Button placeorder;
    LinearLayout sub_layout,main_layout;
    PlaceOrderItemAdapter myCartAdapter;
    private Intent mIntent;
    String address_name="",address="",city="",state="",pincode="",country="",phone="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordersummary);
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
        mTitle.setText("Order Summary");

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
        shipping = ((TextView)  findViewById(R.id.Shipping));
        tax = ((TextView)  findViewById(R.id.Taxamount));
        discount = ((TextView) findViewById(R.id.Discount));
        ordertotal = ((TextView) findViewById(R.id.OrderTotal));
        itemcount = ((TextView)  findViewById(R.id.itemcount));

        shippingmethod = ((TextView) findViewById(R.id.shippingmethod));
        deliverydate = ((TextView) findViewById(R.id.deliverydate));
        deliveryslot=((TextView) findViewById(R.id.deliveryslot));
        shipaddress=((TextView) findViewById(R.id.shipaddress));

        mIntent = getIntent();
        QuoteID=mIntent.getExtras().getString("PLACEORDER_ID");
        carttotal.setText("Rs. "+mIntent.getExtras().getString("SUBTOTAL"));
        shipping.setText("Rs. "+mIntent.getExtras().getString("SHIPPING"));
        tax.setText("Rs. "+mIntent.getExtras().getString("TAX"));
        discount.setText("Rs. "+mIntent.getExtras().getString("DISCOUNT"));
        ordertotal.setText("Rs. "+mIntent.getExtras().getString("GRANDTOTAL"));
        itemcount.setText(mIntent.getExtras().getString("ITEMCOUNT")+" items in the cart");
        shippingmethod.setText("Shipping Method "+mIntent.getExtras().getString("SHIPPINGMETHOD"));
        deliverydate.setText("Delivery Date: "+mIntent.getExtras().getString("DELIVERYDATE"));
        deliveryslot.setText("Delivery Slot: "+mIntent.getExtras().getString("DELIVERYSLOT"));
        address_name=mIntent.getExtras().getString("FULLNAME");
        address=mIntent.getExtras().getString("ADDRESS");
        city=mIntent.getExtras().getString("CITY");
        state=mIntent.getExtras().getString("STATE");
        country=mIntent.getExtras().getString("COUNTRY");
        pincode=mIntent.getExtras().getString("PINCODE");
        phone=mIntent.getExtras().getString("PHONE");
        shipaddress.setText(address_name+",\n"+address+",\n"+city+",\n"+state+","+pincode+",\n"+country+",\n"+phone);
        placeorder=((Button) findViewById(R.id.placeorder));

        error_label_retry = ((TextView)  findViewById(R.id.error_label_retry));
        empty_label_retry = ((TextView) findViewById(R.id.empty_label_retry));
        error_label_retry.setOnClickListener(this);
        empty_label_retry.setOnClickListener(this);
        placeorder.setOnClickListener(this);
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
            case R.id.placeorder:
                    InitPlaceOrder();
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

    private void InitPlaceOrder(){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            PlaceOrderInitiate mPlaceOrderInitiate = new PlaceOrderInitiate(QuoteID);
            mPlaceOrderInitiate.execute((Void) null);
        }else {
            switcher.showErrorView("No Internet Connection");
        }
    }

    public class PlaceOrderInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mQuoteId;
        PlaceOrderInitiate(String ID) {
            mQuoteId=ID;
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
            StringBuilder result = httpOperations.doPlaceOrder(mQuoteId);
            Log.d("111111111","PASSING VALUE: QUOTE ID "+mQuoteId);
            Log.d("111111111", "RESULT "+result);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
                if(result.toString().contains("message")){
                    switcher.showErrorView("Could not place order");
                }
                else {
                    String OrderID = result.toString().replaceAll("\\W", "");
                   // OrderID = OrderID.replaceAll("\\s","");
                    Log.d("111111111", "ORDER ID "+OrderID);
                    InitDeliveryOptions(OrderID);
                }
        }
    }

    private void InitDeliveryOptions(String ID){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            DeliveryOptionsInitiate mDeliveryOptionsInitiate = new DeliveryOptionsInitiate(ID);
            mDeliveryOptionsInitiate.execute((Void) null);
        }else {
            switcher.showErrorView("No Internet Connection");
        }
    }

    public class DeliveryOptionsInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mId;
        DeliveryOptionsInitiate(String ID) {
            mId=ID;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // switcher.showProgressView();

        }
        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doGoToPayment(mId);
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
                       // switcher.showContentView();
                       // sub_layout.setVisibility(View.VISIBLE);
                        Log.d("111111", "here0 ");
                        JSONObject jsonObj1 = jsonObj0.getJSONObject("data");
                        if (jsonObj0.getString("method").equals("cashondelivery")){
                            Log.d("111111", "here1 ");
                            String orderid=jsonObj1.getString("orderid");
                            String ordernumber=jsonObj1.getString("order_number");
                            Intent intent = new Intent(OrderSummaryActivity.this,SuccessActivity.class);
                            intent.putExtra("PLACEORDER_ID",orderid);
                            intent.putExtra("SUBTOTAL",ordernumber);
                            startActivity(intent);
                        }else if (jsonObj0.getString("method").equals("ccavenuepay")){
                            Log.d("111111", "here2 ");
                            Intent i = new Intent(OrderSummaryActivity.this,WebViewActivity.class);
                            i.putExtra(OrdersConstants.ORDER_ID, jsonObj1.getString("orderid"));
                            i.putExtra(OrdersConstants.ORDER_NUMBER, jsonObj1.getString("order_number"));
                            i.putExtra(OrdersConstants.ACCESS_CODE,jsonObj1.getString("access_code"));
                            i.putExtra(OrdersConstants.MERCHANT_ID,jsonObj1.getString("merchant_id"));
                            i.putExtra(OrdersConstants.CURRENCY, jsonObj1.getString("currency"));
                            i.putExtra(OrdersConstants.ORDER_AMOUNT, jsonObj1.getString("order_amount"));
                            i.putExtra(OrdersConstants.RSA_URL, jsonObj1.getString("rsa_url"));
                            i.putExtra(OrdersConstants.SUCCESS_URL, jsonObj1.getString("success_url"));
                            i.putExtra(OrdersConstants.CANCEL_URL, jsonObj1.getString("cancel_url"));
                            i.putExtra(OrdersConstants.BILLING_NAME,address_name);
                            i.putExtra(OrdersConstants.BILLING_ZIP,pincode);
                            i.putExtra(OrdersConstants.BILLING_CITY,city);
                            i.putExtra(OrdersConstants.BILLING_STATE,state);
                            i.putExtra(OrdersConstants.BILLING_COUNTRY,country);
                            i.putExtra(OrdersConstants.BILLING_TEL,phone);
                            startActivity(i);
                        }else if (jsonObj0.getString("method").equals("banktransfer")){
                            Log.d("111111", "here3 ");
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
                            myCartAdapter = new PlaceOrderItemAdapter(OrderSummaryActivity.this, dataItem);
                            mrecyclerview.setAdapter(myCartAdapter);
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

        super.onBackPressed();

    }
}