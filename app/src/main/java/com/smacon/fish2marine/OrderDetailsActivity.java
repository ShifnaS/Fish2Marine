package com.smacon.fish2marine;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.smacon.f2mlibrary.Badge;
import com.smacon.f2mlibrary.Switcher.Switcher;
import com.smacon.fish2marine.AdapterClass.HomeViewPagerAdapter;
import com.smacon.fish2marine.AdapterClass.OrderItemDetailsAdapter;
import com.smacon.fish2marine.AdapterClass.SpinnerAdapter;
import com.smacon.fish2marine.HelperClass.ProductListItem;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by smacon on 3/1/18.
 */

public class OrderDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private String mOrderId;
    Toolbar toolbar;
    private Switcher switcher;
    private TextView error_label_retry, empty_label_retry, mTitle,
            txt_order_date,txt_order_item_count,txt_order_grand_total,txt_address_name,
            txt_order_deliverydate,txt_order_slot,txt_full_address,txt_order_number,
            txt_order_status,txt_order_shipping,txt_order_payment,txt_thankyou;
    private ImageView opendrawer;
    FrameLayout maincontent,subcontent;
    ArrayList<ProductListItem> dataItem;
    private Intent intent;
    RecyclerView mrecyclerview;
    OrderItemDetailsAdapter mOrderItemDetailsAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        intent = getIntent();
        mOrderId=intent.getExtras().getString("ORDER_ID");
        InitIdView();
    }
    private void InitIdView(){
        switcher = new Switcher.Builder(getApplicationContext())
                .addErrorView(findViewById(R.id.error_view))
                .addProgressView(findViewById(R.id.progress_view))
                .addContentView(findViewById(R.id.main_content))
                .setErrorLabel((TextView)findViewById(R.id.error_label))
                .setEmptyLabel((TextView) findViewById(R.id.empty_label))
                .addEmptyView(findViewById(R.id.empty_view))
                .build();

        opendrawer = ((ImageView) findViewById(R.id.opendrawer));
        maincontent = ((FrameLayout)findViewById(R.id.main_content));
        subcontent=((FrameLayout)findViewById(R.id.sub_layout));

        mTitle = ((TextView) findViewById(R.id.mTitle));
        mTitle.setText(mOrderId);

        txt_order_date = ((TextView) findViewById(R.id.txt_order_date));
        txt_order_item_count = ((TextView) findViewById(R.id.txt_order_item_count));
        txt_order_grand_total = ((TextView) findViewById(R.id.txt_order_grand_total));
        txt_address_name = ((TextView) findViewById(R.id.txt_address_name));
        txt_full_address = ((TextView) findViewById(R.id.txt_full_address));
        txt_order_number= ((TextView) findViewById(R.id.txt_order_number));
        txt_order_number.setText(mOrderId);
        txt_order_status= ((TextView) findViewById(R.id.txt_order_status));
        txt_order_shipping= ((TextView) findViewById(R.id.txt_order_shipping));
        txt_order_payment=((TextView) findViewById(R.id.txt_order_payment));
        txt_order_deliverydate= ((TextView) findViewById(R.id.txt_order_deliverydate));
        txt_order_slot=((TextView) findViewById(R.id.txt_order_slot));
        txt_thankyou=(TextView)findViewById(R.id.txt_thankyou);
        mrecyclerview = ((RecyclerView)findViewById(R.id.mrecyclerview));
        
        error_label_retry = ((TextView) findViewById(R.id.error_label_retry));
        empty_label_retry = ((TextView)findViewById(R.id.empty_label_retry));
        error_label_retry.setOnClickListener(this);
        empty_label_retry.setOnClickListener(this);
        opendrawer.setOnClickListener(this);
        
        dataItem = new ArrayList<>();
        InitGetData();
        mrecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    private void InitGetData(){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            LoadOrderDetailsInitiate mLoadOrderDetailsInitiate = new LoadOrderDetailsInitiate(mOrderId);
            mLoadOrderDetailsInitiate.execute((Void) null);
        }else {
            switcher.showErrorView("No Internet Connection");
        }
    }

    @Override
    public void onClick(View v) {
        int buttonId = v.getId();
        switch (buttonId){

            case R.id.error_label_retry:
                InitGetData();
                break;
            case R.id.empty_label_retry:
                InitGetData();
                break;
            case R.id.opendrawer:
                // mListener.OpenDrawer();
                break;
        }
    }
    public class LoadOrderDetailsInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mId;
        LoadOrderDetailsInitiate(String id) {
            mId = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switcher.showProgressView();
            
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doOrderDetails(mId);
            Log.d("1111111", "API_ORDER_DETAILS " + result);
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
                        subcontent.setVisibility(View.VISIBLE);
                        JSONObject jsonObj1 = jsonObj0.getJSONObject("data");
                        if(jsonObj1.has("items")) {
                            JSONArray feedArray1 = jsonObj1.getJSONArray("items");
                            for (int i = 0; i < feedArray1.length(); i++) {
                                ProductListItem item = new ProductListItem();
                                JSONObject feedObj1 = (JSONObject) feedArray1.get(i);
                                item.setAllproduct_name(feedObj1.getString("name"));
                                item.setAllproduct_Othername(feedObj1.getString("sku"));
                                item.setAllproduct_price(feedObj1.getString("price"));
                                item.setallorder_qty(feedObj1.getString("qty"));
                                item.setallcleaned_qty(feedObj1.getString("grossWeight"));
                                item.setAllproduct_image(feedObj1.getString("imagepath"));
                                if (feedObj1.has("itemOptions")){
                                    JSONArray feedArray2 = feedObj1.getJSONArray("itemOptions");
                                    for (int j = 0; j < feedArray2.length(); j++) {

                                        JSONObject feedObj2 = (JSONObject) feedArray2.get(j);
                                        item.setAllproduct_id(feedObj2.getString("value"));
                                        dataItem.add(item);
                                    }

                                }
                                mOrderItemDetailsAdapter = new OrderItemDetailsAdapter(getApplicationContext(),dataItem);
                                mrecyclerview.setAdapter(mOrderItemDetailsAdapter);
                            }
                        }
                        txt_order_status.setText(jsonObj1.getString("orderStatus"));
                        txt_order_date.setText(jsonObj1.getString("orderDate"));
                        txt_order_shipping.setText(jsonObj1.getString("shippingMethod"));
                        txt_order_payment.setText(jsonObj1.getString("paymentMethod"));
                        txt_order_item_count.setText(jsonObj1.getString("totalqty"));
                        txt_order_grand_total.setText("Rs. "+jsonObj1.getString("grand_total"));
                        Log.d("1111111","here");
                        if(jsonObj1.has("delivaryInfo")){
                            JSONObject jsonObj2 = jsonObj1.getJSONObject("delivaryInfo");
                            txt_order_deliverydate.setText(jsonObj2.getString("delivaryDate"));
                            txt_order_slot.setText(jsonObj2.getString("delivaryTime"));
                        }
                        Log.d("1111111","here");

                        if(jsonObj1.has("shippingAddress")){
                            JSONObject jsonObj2 = jsonObj1.getJSONObject("shippingAddress");
                            txt_address_name.setText(jsonObj2.getString("name"));
                            /*jsonObj2.getString("city");
                            jsonObj2.getString("region");
                            jsonObj2.getString("postcode");
                            jsonObj2.getString("phone");*/
                            Log.d("1111111","here1");
                            String street=jsonObj2.getString("street");
                            street=street.replaceAll("\"","")
                                    .replaceAll("\\[","").replaceAll("\\]","");
                            Log.d("11111111","street"+street);
                            String address=street+",\n"+jsonObj2.getString("city")+",\n"+
                            jsonObj2.getString("region")+","+jsonObj2.getString("postcode")+",\n"+"India"+",\n"+
                            jsonObj2.getString("phone");
                            txt_full_address.setText(address);

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
   
}
