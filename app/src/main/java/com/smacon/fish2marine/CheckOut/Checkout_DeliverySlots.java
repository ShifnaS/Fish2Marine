package com.smacon.fish2marine.CheckOut;

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

import com.smacon.f2mlibrary.CustomToast;
import com.smacon.fish2marine.AdapterClass.DateAdapter;
import com.smacon.fish2marine.HelperClass.CartListItem;
import com.smacon.fish2marine.HelperClass.ProductListItem;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.R;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class Checkout_DeliverySlots extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences sPreferences;
    private SqliteHelper helper;
    private Config mConfig;
    List<HashMap<String, String>> SQLData_Item ;
    String CustomerID = "";
    private RecyclerView mrecyclerviewDate;

    ArrayList<ProductListItem> dataItem;
    ImageView back;
    Button btn_checkout;
    LinearLayout sub_layout,main_layout;
    DateAdapter myCartAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_deliveryslot1);
        helper = new SqliteHelper(getApplicationContext(), "Fish2Marine", null, 5);
        sPreferences = getApplicationContext().getSharedPreferences("Fish2Marine", MODE_PRIVATE);
        mConfig = new Config(getApplicationContext());
        InitIdView();
    }
    private void InitIdView(){

        SQLData_Item = helper.getadmindetails();
        CustomerID=SQLData_Item.get(0).get("admin_id");
        Log.d("1111221", "Customer ID "+CustomerID);
        mrecyclerviewDate = ((RecyclerView) findViewById(R.id.mrecyclerview));
        mrecyclerviewDate.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        dataItem = new ArrayList<>();
        InitGetSlotData();
    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId) {


        }
    }

    private void InitGetSlotData(){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            LoadDeliveryslotInitiate mLoadDeliveryslotInitiate = new LoadDeliveryslotInitiate(CustomerID);
            mLoadDeliveryslotInitiate.execute((Void) null);
        }else {
            CustomToast.error(getApplicationContext(),"No Internet Connection.").show();
        }
    }

    public class LoadDeliveryslotInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mId;
        LoadDeliveryslotInitiate(String ID) {
            mId=ID;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
                        Log.d("111111", "here0 ");
                        JSONObject jsonObj1 = jsonObj0.getJSONObject("data");
                        if(jsonObj1.has("summary")) {
                            JSONObject jsonObj2 = jsonObj1.getJSONObject("summary");
                            if(jsonObj2.has("slots")){
                                Log.d("111111", "here4 ");
                                JSONObject jsonObj3 = jsonObj2.getJSONObject("slots");
                                CartListItem item = new CartListItem();
                                Iterator<String> keys = jsonObj3.keys();
                                Log.d("111111", "here5 "+jsonObj3);
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    Log.d("11111list key", key);
                                    item.setProductName(key);
                                    if(jsonObj3.get(key) instanceof JSONArray) {
                                        JSONArray feedArray2 = jsonObj3.getJSONArray(key);
                                        for (int i = 0; i < feedArray2.length(); i++) {
                                            JSONObject feedObj1 = (JSONObject) feedArray2.get(i);
                                            String slot = feedObj1.getString("slot");
                                            Log.d("111111", "here8 " + slot);
                                            item.setAmount(slot);

                                        }
                                        /*JSONObject innerJObject = jObject.getJSONObject(key);
                                        String id = innerJObject.getString("id");
                                        String name = innerJObject.getString("name");
                                        String points = innerJObject.getString("points");
                                        String ranking = innerJObject.getString("ranking");
                                        String tour = innerJObject.getString("tour");
                                        String lastUpdate = innerJObject.gettString("lastUpdate");*/

                                    }
                                    myCartAdapter = new DateAdapter(Checkout_DeliverySlots.this, dataItem);
                                    mrecyclerviewDate.setAdapter(myCartAdapter);
                                }
                            }
                        }

                    }else {
                        CustomToast.info(getApplicationContext(),"Empty").show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                CustomToast.info(getApplicationContext(),"Please Try Again").show();
            } catch (NullPointerException e) {
                CustomToast.error(getApplicationContext(),"No Internet Connection.").show();
            } catch (Exception e) {
                CustomToast.info(getApplicationContext(),"Please Try Again").show();
            }
        }

    }
    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }
}