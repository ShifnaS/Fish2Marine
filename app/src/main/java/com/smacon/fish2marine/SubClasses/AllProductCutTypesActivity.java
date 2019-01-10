package com.smacon.fish2marine.SubClasses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.smacon.f2mlibrary.CustomToast;
import com.smacon.f2mlibrary.Switcher.Switcher;
import com.smacon.fish2marine.AdapterClass.CutTypesAdapter;
import com.smacon.fish2marine.HelperClass.ProductListItem;
import com.smacon.fish2marine.ProductViewActivity;
import com.smacon.fish2marine.R;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by smacon on 3/1/18.
 */

public class AllProductCutTypesActivity extends AppCompatActivity implements View.OnClickListener{
    private String mCustomerID,mProductId,mFromPageName,mQuantity;
    private SharedPreferences sPreferences;
    private Config mConfig;
    private RecyclerView mrecyclerview;
    ImageView close;
    private Switcher switcher;
    GridLayoutManager mLayoutManager;
    private TextView error_label_retry, empty_label_retry;
    ArrayList<ProductListItem> dataItem;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuttype_dialog);
        sPreferences = getSharedPreferences("Fish2Marine", MODE_PRIVATE);
        mConfig = new Config(getApplicationContext());
        intent = getIntent();
        mProductId=intent.getExtras().getString("Product_ID");
        mQuantity=intent.getExtras().getString("Quantity");
        mFromPageName = intent.getExtras().getString("FROM");
        InitIdView();
    }
    private void InitIdView(){
        switcher = new Switcher.Builder(getApplicationContext())
                .addErrorView(findViewById(R.id.error_view))
                .addProgressView(findViewById(R.id.progress_view))
                .addContentView(findViewById(R.id.mrecyclerview))
                .setErrorLabel((TextView)findViewById(R.id.error_label))
                .setEmptyLabel((TextView) findViewById(R.id.empty_label))
                .addEmptyView(findViewById(R.id.empty_view))
                .build();
        close = (ImageView) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Close_view();
            }
        });
        error_label_retry = ((TextView) findViewById(R.id.error_label_retry));
        empty_label_retry = ((TextView)findViewById(R.id.empty_label_retry));
        mrecyclerview = ((RecyclerView)findViewById(R.id.mrecyclerview));
        error_label_retry.setOnClickListener(this);
        empty_label_retry.setOnClickListener(this);
        dataItem = new ArrayList<>();
        initRecyclerView();
        CutTypeSpinner(mProductId);
    }
    private void initRecyclerView() {
        mrecyclerview.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        mrecyclerview.setLayoutManager(mLayoutManager);
    }
    private void CutTypeSpinner(String ProductID){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            LoadCutTypeSpinnerInitiate mLoadCutTypeSpinnerInitiate = new LoadCutTypeSpinnerInitiate
                    (ProductID);
            mLoadCutTypeSpinnerInitiate.execute((Void) null);
        }else {
            CustomToast.error(getApplicationContext(),"No Internet Connection.").show();
        }
    }
    @Override
    public void onClick(View v) {
        int buttonId = v.getId();
        switch (buttonId){
            case R.id.error_label_retry:
                CutTypeSpinner(mProductId);
                break;
            case R.id.empty_label_retry:
                CutTypeSpinner(mProductId);
                break;


        }
    }
    public class LoadCutTypeSpinnerInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {
        private String mProductID;
        LoadCutTypeSpinnerInitiate(String Product_ID) {
            mProductID = Product_ID;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switcher.showProgressView();
        }
        @Override
        protected StringBuilder doInBackground(Void... params) {
            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doCutTypesList(mProductID);
            Log.d("111111", "API_CUTTYPE_RESPONSE " + result);
            return result;
        }
        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(1))) {
                        switcher.showContentView();
                        if(jsonObj.has("data")) {
                            JSONObject jsonObj1 = jsonObj.getJSONObject("data");
                            if (jsonObj1.has("cutTypes")) {
                                JSONArray feedArray1 = jsonObj1.getJSONArray("cutTypes");
                                for (int i = 0; i < feedArray1.length(); i++) {
                                    ProductListItem item1 = new ProductListItem();
                                    JSONObject feedObj2 = (JSONObject) feedArray1.get(i);
                                    Log.d("111111", "API_CUTTYPE_RESPONSE " + feedObj2.getString("label"));
                                    item1.setcuttype_label(feedObj2.getString("label"));
                                    item1.setcuttype_value(feedObj2.getString("value"));
                                    item1.setcuttype_imageurl(feedObj2.getString("ImageUrl"));
                                    dataItem.add(item1);
                                    Log.d("111111", "API_CUTTYPE_RESPONSE " + dataItem.size());
                                }
                                CutTypesAdapter mSpinnerAdapter = new CutTypesAdapter(AllProductCutTypesActivity.this, dataItem,mProductID,mQuantity);
                                mrecyclerview.setAdapter(mSpinnerAdapter);
                            }
                        }
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
    private void Close_view(){
        Intent intent = new Intent(AllProductCutTypesActivity.this, ProductViewActivity.class);
        Log.d("11111111shared",sPreferences.getString("CategoryName",""));
        Log.d("11111111shared",sPreferences.getString("CategoryID",""));
        intent.putExtra("ID",sPreferences.getString("CategoryID",""));
        intent.putExtra("NAME",sPreferences.getString("CategoryName",""));
        intent.putExtra("PAGE","FROM_CUTTYPE");
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,
                R.anim.bottom_down);
        finish();
    }
}
