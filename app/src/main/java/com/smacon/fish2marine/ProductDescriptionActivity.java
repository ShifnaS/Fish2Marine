package com.smacon.fish2marine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.smacon.f2mlibrary.Badge;
import com.smacon.f2mlibrary.Button.FloatingActionButton;
import com.smacon.f2mlibrary.CustomToast;
import com.smacon.f2mlibrary.Switcher.Switcher;
import com.smacon.fish2marine.AdapterClass.HomeViewPagerAdapter;
import com.smacon.fish2marine.AdapterClass.SpinnerAdapter;
import com.smacon.fish2marine.HelperClass.ProductListItem;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by smacon on 3/1/18.
 */

public class ProductDescriptionActivity extends AppCompatActivity implements View.OnClickListener{
    private String mProductId,mProductName;
    Toolbar toolbar;
    private Switcher switcher;
    private SqliteHelper helper;
    private Config mConfig;
    List<HashMap<String, String>> SQLData_Item ;
    String CustomerID = "";
    private SharedPreferences sPreferences;

    private TextView error_label_retry, empty_label_retry, mTitle,
            txt_product_name,txt_product_code,txt_product_stock,txt_product_price,
            txt_product_special_price,txt_ordered_qty,txt_cleaned_qty,txt_product_description,txt_sold_by;
    private ImageView opendrawer;
    Button addtocart;
    FrameLayout maincontent,subcontent;
    LinearLayout layout_specialPrice,layout_spinner;
    String CuttypeApplicable="";
    private Badge mBadge;
    ArrayList<ProductListItem> dataItem,spinnerdataItem;
    private Intent intent;
    FloatingActionButton share;

    HomeViewPagerAdapter mviewPagerAdapter;
    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    ImageView Plus,Minus;
    TextView Quantity,choose;
    String mSpinnerItem = "",ShareURL="";
    private Spinner spinner;
    String OrderQty,AfterQty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_description);
        helper = new SqliteHelper(getApplicationContext(), "Fish2Marine", null, 5);
        sPreferences = getApplicationContext().getSharedPreferences("Fish2Marine", MODE_PRIVATE);
        mConfig = new Config(getApplicationContext());
        intent = getIntent();
        mProductName = intent.getExtras().getString("NAME");
        mProductId=intent.getExtras().getString("ID");
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

        SQLData_Item = helper.getadmindetails();
        CustomerID=SQLData_Item.get(0).get("admin_id");
        Log.d("1111221", "Customer ID "+CustomerID);

        mTitle = ((TextView) findViewById(R.id.mTitle));
        mTitle.setText(mProductName);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // ((AppCompatActivity) getApplicationContext()).setSupportActionBar(toolbar);

        opendrawer = ((ImageView) findViewById(R.id.opendrawer));
        maincontent = ((FrameLayout)findViewById(R.id.main_content));
        subcontent=((FrameLayout)findViewById(R.id.sub_layout));

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);

        txt_product_name = ((TextView) findViewById(R.id.txt_product_name));
        choose = ((TextView) findViewById(R.id.choose));
        txt_product_code = ((TextView) findViewById(R.id.txt_product_code));
        txt_product_stock = ((TextView) findViewById(R.id.txt_product_stock));
        txt_product_price = ((TextView) findViewById(R.id.txt_product_price));
        layout_specialPrice=(LinearLayout)findViewById(R.id.layout_specialPrice);
        txt_product_special_price = ((TextView) findViewById(R.id.txt_product_special_price));
        txt_ordered_qty= ((TextView) findViewById(R.id.txt_ordered_qty));
        txt_cleaned_qty= ((TextView) findViewById(R.id.txt_cleaned_qty));
        txt_product_description= ((TextView) findViewById(R.id.txt_product_description));
        txt_sold_by=((TextView) findViewById(R.id.txt_sold_by));
        addtocart=(Button)findViewById(R.id.addtocart);
        Plus = (ImageView) findViewById(R.id.img_plus);
        Minus = (ImageView) findViewById(R.id.img_minus);
        Quantity = (TextView) findViewById(R.id.txt_quantity);
        share=(FloatingActionButton)findViewById(R.id.share);

        layout_spinner=(LinearLayout)findViewById(R.id.layout_spinner);
        spinner = (Spinner)findViewById(R.id.spinner);

        error_label_retry = ((TextView) findViewById(R.id.error_label_retry));
        empty_label_retry = ((TextView)findViewById(R.id.empty_label_retry));

        error_label_retry.setOnClickListener(this);
        empty_label_retry.setOnClickListener(this);
        addtocart.setOnClickListener(this);
        opendrawer.setOnClickListener(this);

        Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(Quantity.getText().toString()) + 1;
                Quantity.setText(String.valueOf(number));
                Double orderqty=Double.parseDouble(OrderQty)*Double.parseDouble(Quantity.getText().toString());
                if(orderqty>=1000){
                    Double value=orderqty/1000;
                    txt_ordered_qty.setText("Order Qty: "+value+"kg");
                }
                else {
                    txt_ordered_qty.setText("Order Qty: "+orderqty+"gm");
                }

                Double afterqty=Double.parseDouble(AfterQty)*Double.parseDouble(Quantity.getText().toString());
                if(afterqty>=1000){
                    Double value=afterqty/1000;
                    txt_cleaned_qty.setText("After Cleaning: "+value+"kg");
                }
                else {
                    txt_cleaned_qty.setText("After Cleaning: "+afterqty+"gm");
                }

            }
        });

        Minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(Quantity.getText().toString()) >= 2) {
                    int number = Integer.parseInt(Quantity.getText().toString()) - 1;
                    Quantity.setText(String.valueOf(number));
                }
                Double orderqty=Double.parseDouble(OrderQty)*Double.parseDouble(Quantity.getText().toString());
                if(orderqty>=1000){
                    Double value=orderqty/1000;
                    txt_ordered_qty.setText("Order Qty: "+value+"kg");
                }
                else {
                    txt_ordered_qty.setText("Order Qty: "+orderqty+"gm");
                }

                Double afterqty=Double.parseDouble(AfterQty)*Double.parseDouble(Quantity.getText().toString());
                if(afterqty>=1000){
                    Double value=afterqty/1000;
                    txt_cleaned_qty.setText("After Cleaning: "+value+"kg");
                }
                else {
                    txt_cleaned_qty.setText("After Cleaning: "+afterqty+"gm");
                }

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTextUrl();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mSpinnerItem = spinnerdataItem.get(i).getcuttype_value();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        dataItem = new ArrayList<>();


        InitGetData();

    }

    private void InitGetData(){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            LoadProductDescriptionInitiate mLoadProductDescriptionInitiate = new LoadProductDescriptionInitiate(mProductId);
            mLoadProductDescriptionInitiate.execute((Void) null);
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
                onBackPressed();
                break;
            case R.id.addtocart:
                if(sPreferences.getString("DeliveryCenter_ID","").equals("")){
                    CustomToast.error(v.getContext(), "Please Choose ur location"+sPreferences.getString("DeliveryCenter_ID","")).show();
                }
                else {
                    AddToCart(Quantity.getText().toString(), mProductId, mSpinnerItem);
                }
                break;

        }
    }
    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Fish2Marine");
        share.putExtra(Intent.EXTRA_TEXT, "Get more at Fish2Marine! Register with Fish2Marine and explore more fish.\n"+ShareURL);
        startActivity(Intent.createChooser(share, "Share link!"));

    }
    public class LoadProductDescriptionInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {
        private String mId;
        LoadProductDescriptionInitiate(String id) {
            mId = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switcher.showProgressView();
            spinnerdataItem=new ArrayList<>();

        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doProductDescription(mId);
            Log.d("1111111", "API_PRODUCT_DESC " + result);
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
                        if(jsonObj1.has("productData")) {
                            JSONArray feedArray1 = jsonObj1.getJSONArray("productData");
                            for (int i = 0; i < feedArray1.length(); i++) {
                                JSONObject feedObj1 = (JSONObject) feedArray1.get(i);
                                ShareURL=feedObj1.getString("urlkey");
                                txt_product_name.setText(feedObj1.getString("name")+" - "+feedObj1.getString("nameInMalayalam"));
                                txt_product_code.setText("SKU# : "+feedObj1.getString("sku"));
                                txt_product_stock.setVisibility(View.GONE);
                                if (feedObj1.getString("inStock").equals("")||feedObj1.getString("inStock").equals("0")){
                                    txt_product_stock.setText("Out of Stock");
                                    txt_product_stock.setTextColor(getResources().getColor(R.color.red));
                                }
                                else {
                                    txt_product_stock.setText("In Stock: "+feedObj1.getString("inStock"));
                                    txt_product_stock.setTextColor(getResources().getColor(R.color.colorAccentsecond));
                                }
                                txt_product_price.setText(""+feedObj1.getString("price"));
                                if (feedObj1.getString("specialPrice").equals("")||feedObj1.getString("specialPrice").equals("0")){
                                    layout_specialPrice.setVisibility(View.GONE);
                                }
                                else {
                                    txt_product_price.setPaintFlags(
                                            txt_product_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    txt_product_special_price.setText("Rs. "+feedObj1.getString("specialPrice"));
                                }
                                txt_ordered_qty.setText("Ordered Qty: "+feedObj1.getString("beforeCleaning")+"g");
                                txt_cleaned_qty.setText("After Cleaning: "+feedObj1.getString("afterCleaning")+"g");
                                OrderQty=feedObj1.getString("beforeCleaning");
                                AfterQty=feedObj1.getString("afterCleaning");
                                txt_product_description.setText(feedObj1.getString("description"));
                                if (feedObj1.getString("seller").equals("")){
                                    txt_sold_by.setVisibility(View.GONE);
                                }
                                else {
                                    txt_sold_by.setText(feedObj1.getString("seller"));
                                }
                                if (feedObj1.has("galleryImages")){
                                    JSONArray feedArray2 = feedObj1.getJSONArray("galleryImages");
                                    for (int j = 0; j < feedArray2.length(); j++) {
                                        ProductListItem item = new ProductListItem();
                                        JSONObject feedObj2 = (JSONObject) feedArray2.get(j);
                                        item.set_sliderimage(feedObj2.getString("imageurl"));
                                        dataItem.add(item);
                                    }
                                    mviewPagerAdapter = new HomeViewPagerAdapter(getApplicationContext(),dataItem);
                                    viewPager.setAdapter(mviewPagerAdapter);
                                    initviewpager();
                                }
                                CuttypeApplicable=feedObj1.getString("cutTypeApplicable").toString();
                                if(feedObj1.getString("cutTypeApplicable").equals("2")){
                                    layout_spinner.setVisibility(View.GONE);
                                    choose.setVisibility(View.GONE);
                                }
                                else {
                                    if (feedObj1.has("cutTypes")){
                                        JSONArray feedArray2 = feedObj1.getJSONArray("cutTypes");
                                        for (int k = 0; k < feedArray2.length(); k++) {
                                            ProductListItem item1 = new ProductListItem();
                                            JSONObject feedObj2 = (JSONObject) feedArray2.get(k);
                                            Log.d("1111221", "API ");
                                            item1.setcuttype_label(feedObj2.getString("label"));
                                            item1.setcuttype_value(feedObj2.getString("value"));
                                            item1.setcuttype_imageurl(feedObj2.getString("ImageUrl"));
                                            spinnerdataItem.add(item1);
                                        }
                                        SpinnerAdapter mSpinnerAdapter = new SpinnerAdapter(getApplicationContext(),
                                                spinnerdataItem);
                                        spinner.setAdapter(mSpinnerAdapter);
                                    }
                                }
                            }
                            //Log.d("1111221", "API "+dataItem.size());
                            //mTitle.setText("");
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
    private void initviewpager(){
        dotscount = mviewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++){

            dots[i] = new ImageView(getApplicationContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    public boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private void AddToCart(String Quantity,String ProductID,String Cuttype){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            LoadAddToCartInitiate mLoadAddToCartInitiate = new LoadAddToCartInitiate(
                    CustomerID,ProductID,Cuttype,Quantity);

            mLoadAddToCartInitiate.execute((Void) null);
        }else {
            CustomToast.error(getApplicationContext(),"No Internet Connection.").show();
        }
    }

    public class LoadAddToCartInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mCustomerID,mProductID,mCutype,mQty;



        LoadAddToCartInitiate(String Customer_ID, String Product_ID, String CutType,String Qty) {
            mCustomerID = Customer_ID;
            mProductID = Product_ID;
            mCutype = CutType;
            mQty = Qty;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            helper.Delete_cartcount();
            switcher.showProgressView();
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            Log.d("111111", "PASSING VALUE " + mCustomerID+" "+mProductID+" "+mCutype+" "+mQty);
            StringBuilder result = httpOperations.doAddToCart(mCustomerID, mProductID, mCutype,mQty);
            Log.d("111111111",result.toString());
            return result;

        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(2))) {
                        switcher.showContentView();
                        Log.d("11111111","here0");
                        CustomToast.info(getApplicationContext(),jsonObj.getString("message").toString()).show();
                    }else if (jsonObj.getString("status").equals(String.valueOf(1))) {
                        switcher.showContentView();
                        Log.d("11111111","here1");

                        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
                        JSONObject jsonObj1 = jsonObj.getJSONObject("data");
                        for (int i = 0; i < jsonObj1.length(); i++) {
                            Log.d("11111111","here2");

                            HashMap<String, String> map;
                            map = new HashMap<String, String>();
                            map.put("itemsCount", jsonObj1.getString("itemsCount").trim());
                            fillMaps.add(map);
                        }
                        Log.d("11111111","here3");

                        mConfig.savePreferences(getApplicationContext(),"CartID",jsonObj1.getString("id").trim());
                        Log.d("111111111",sPreferences.getString("CartID",""));
                        helper.Insert_Count(fillMaps);
                        final List<HashMap<String, String>> Data_Item;;
                        Data_Item = helper.getCount();
                        Log.d("1111111112","Cart Count "+Data_Item.get(0).get("cartcount"));
                        mConfig.savePreferences(getApplicationContext(),"CartCount",Data_Item.get(0).get("cartcount"));
                        updatecartcount();
                        Log.d("111111111",sPreferences.getString("CartCount",""));

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
    private void updatecartcount(){
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("data_changed"));
        NavigationDrawerActivity.getInstance().updateCartCount();
       /* Intent intent = new Intent(mContext, ProductViewActivity.class);
        Log.d("11111111shared",sPreferences.getString("CategoryName",""));
        Log.d("11111111shared",sPreferences.getString("CategoryID",""));
        intent.putExtra("ID",sPreferences.getString("CategoryID",""));
        intent.putExtra("NAME",sPreferences.getString("CategoryName",""));
        intent.putExtra("PAGE","SUBPAGE");
       // mContext.getWindow().setExitTransition(null);
        mContext.startActivity(intent);*/
        CustomToast.success(getApplicationContext(), "Item successfully added to cart").show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProductDescriptionActivity.this,NavigationDrawerActivity.class);
        intent.putExtra("PAGE","HOME");
        startActivity(intent);
        finish();
    }
}
