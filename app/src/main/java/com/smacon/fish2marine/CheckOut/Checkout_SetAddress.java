package com.smacon.fish2marine.CheckOut;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smacon.f2mlibrary.Button.LiquidRadioButton.LiquidRadioButton;
import com.smacon.f2mlibrary.CustomCheckBox;
import com.smacon.f2mlibrary.CustomEditText;
import com.smacon.f2mlibrary.CustomTextView;
import com.smacon.f2mlibrary.CustomToast;
import com.smacon.f2mlibrary.Progress.AVLoadingIndicatorView;
import com.smacon.f2mlibrary.Switcher.Switcher;
import com.smacon.fish2marine.AdapterClass.CheckoutAddressListAdapter;
import com.smacon.fish2marine.AdapterClass.ShippingMethodListAdapter;
import com.smacon.fish2marine.HelperClass.AddressListItem;
import com.smacon.fish2marine.HelperClass.CartListItem;
import com.smacon.fish2marine.HelperClass.MessageConstants;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.HelperClass.Utilities;
import com.smacon.fish2marine.OrderSummaryActivity;
import com.smacon.fish2marine.R;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class Checkout_SetAddress extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences sPreferences;
    private SqliteHelper helper;
    private Config mConfig;
    List<HashMap<String, String>> SQLData_Item ;
    String CustomerID = "";
    private Switcher switcher;
    CustomTextView txt_new_address,txt_address_name,txt_company,txt_street,
            txt_city,txt_state_pin,txt_country,txt_phone;
    private TextView mTitle,error_label_retry, empty_label_retry;
    ArrayList<AddressListItem> dataItem;
    ArrayList<CartListItem> shippingdataItem;
    ImageView back,address_list;
    private static Checkout_SetAddress addressActivity;
    private Typeface tf;
    AVLoadingIndicatorView indicator;
    Button btn_add,next;
    LinearLayout sub_layout, main_layout, LinearLayoutMain,
            lay_shipping_method,lay_delivery_address,lay_delivery_slots,lay_payment_method;
    CheckoutAddressListAdapter mAddressAdapter;
    ShippingMethodListAdapter mShippingAdapter;
    RecyclerView mrecyclerview;
    ListView shippingListView;
    View ChildView ;
    String STEP="";
    FrameLayout shipping_indicator,deliveryslot_indicator,payment_indicator;
    int RecyclerViewItemPosition ;
    RadioGroup radiogroup,paymentradiogroup;
    private String delivery_slot_id="",delivery_slot_group="",delivery_isavailable="",delivery_slot = "",delivery_date = "",
            shipping_method_code="",shipping_method_title="",shipping_carrier_code="",paymentmethod_code="",paymentmethod_title="";
    public static final Checkout_SetAddress getInstance(){
        return addressActivity;
    }

    String SubTotal="",Tax="",Shipping="",Discount="",GrandTotal="",ItemCount="",FullAddress="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_address);
        helper = new SqliteHelper(getApplicationContext(), "Fish2Marine", null, 5);
        sPreferences = getApplicationContext().getSharedPreferences("Fish2Marine", MODE_PRIVATE);
        mConfig = new Config(getApplicationContext());
        addressActivity = this;
        InitIdView();
    }
    private void InitIdView(){
        SQLData_Item = helper.getadmindetails();
        CustomerID=SQLData_Item.get(0).get("admin_id");
        Log.d("1111221", "Customer ID "+CustomerID);
        switcher = new Switcher.Builder(getApplicationContext())
                .addContentView(findViewById(R.id.main_layout))
                .addErrorView(findViewById(R.id.error_view))
                .addProgressView(findViewById(R.id.progress_view))
                .setErrorLabel((TextView)findViewById(R.id.error_label))
                .setEmptyLabel((TextView)findViewById(R.id.empty_label))
                .addEmptyView(findViewById(R.id.empty_view))
                .build();
        tf = Typeface.createFromAsset(getResources().getAssets(),"rounded_font.otf");

        sub_layout=((LinearLayout)findViewById(R.id.sub_layout));
        main_layout=((LinearLayout)findViewById(R.id.main_layout));
        LinearLayoutMain=((LinearLayout)findViewById(R.id.LinearLayoutMain));
        lay_delivery_address=((LinearLayout)findViewById(R.id.lay_delivery_address));
        lay_shipping_method=((LinearLayout)findViewById(R.id.lay_shipping_method));
        lay_delivery_slots=((LinearLayout)findViewById(R.id.lay_delivery_slots));
        lay_payment_method=((LinearLayout)findViewById(R.id.lay_payment_method));

        shippingListView = (ListView) findViewById(R.id.shippingListView);
        txt_new_address = (CustomTextView) findViewById(R.id.txt_new_address);
        txt_address_name = (CustomTextView) findViewById(R.id.txt_address_name);
        txt_company = (CustomTextView) findViewById(R.id.txt_company);
        txt_street = (CustomTextView) findViewById(R.id.txt_street);
        txt_city = (CustomTextView) findViewById(R.id.txt_city);
        txt_state_pin = (CustomTextView) findViewById(R.id.txt_state_pin);
        txt_country = (CustomTextView) findViewById(R.id.txt_country);
        txt_phone = (CustomTextView) findViewById(R.id.txt_phone);
        address_list=(ImageView)findViewById(R.id.address_list);
        back=(ImageView)findViewById(R.id.back);
        next=(Button)findViewById(R.id.next);
        shipping_indicator=(FrameLayout)findViewById(R.id.shipping_indicator);
        deliveryslot_indicator=(FrameLayout)findViewById(R.id.deliveryslot_indicator);
        payment_indicator=(FrameLayout)findViewById(R.id.payment_indicator);
        error_label_retry = ((TextView)findViewById(R.id.error_label_retry));
        empty_label_retry = ((TextView)findViewById(R.id.empty_label_retry));
        radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        paymentradiogroup = (RadioGroup) findViewById(R.id.paymentradiogroup);
        shippingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shipping_carrier_code = shippingdataItem.get(position).getCarrier_code();
                shipping_method_code=shippingdataItem.get(position).getMethod_code();
                shipping_method_title=shippingdataItem.get(position).getCarrier_title();
            }
        });
        error_label_retry.setOnClickListener(this);
        empty_label_retry.setOnClickListener(this);
        txt_new_address.setOnClickListener(this);
        address_list.setOnClickListener(this);
        next.setVisibility(View.GONE);
        next.setOnClickListener(this);
        dataItem = new ArrayList<>();
        shippingdataItem = new ArrayList<>();
        InitSetAddress();

    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId) {
            case R.id.error_label_retry:
                InitSetAddress();
                break;
            case R.id.empty_label_retry:
                InitSetAddress();
                break;
            case R.id.address_list:
                PickAddress();
                InitPickAddress();
                break;
            case R.id.txt_new_address:
                showAddDialog();
                break;
            case R.id.back:
                onBackPressed();
                break;
            case R.id.next:
                if(STEP=="SHIPPING_METHOD"){
                    lay_delivery_address.setVisibility(View.GONE);
                    lay_shipping_method.setVisibility(View.GONE);
                    deliveryslot_indicator.setVisibility(View.VISIBLE);
                    lay_delivery_slots.setVisibility(View.VISIBLE);
                }
                else if(STEP=="PAYMENT_METHOD"){
                    if (!paymentmethod_code.equals("")){
                        InitPaymentMode();
                    }
                    else {
                        CustomToast.error(getApplicationContext(),"Please Choose a Payment method.").show();
                    }
                }
                else if (STEP.equals("DELIVERY_SLOT") && !STEP.equals("ADDRESS")){
                    if(!delivery_slot.equals("")) {
                        lay_delivery_slots.setVisibility(View.GONE);
                        lay_payment_method.setVisibility(View.VISIBLE);
                        try {
                            getAllValues();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        CustomToast.error(getApplicationContext(),"Please Choose a delivery Slot.").show();
                    }
                }
                else {
                    lay_delivery_address.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void PickAddress(){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_addresslist);
        mrecyclerview = (RecyclerView) dialog.findViewById(R.id.mrecyclerview);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        Utilities.setDialogParamsWrapContent(this,dialog);
        indicator = (AVLoadingIndicatorView) dialog.findViewById(R.id.indicator);
        dialog.show();
        mrecyclerview.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(Checkout_SetAddress.this, new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if(ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {

                    RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(ChildView);
                    Toast.makeText(Checkout_SetAddress.this, dataItem.get(RecyclerViewItemPosition).getFirstname(), Toast.LENGTH_SHORT).show();
                    txt_address_name.setText(dataItem.get(RecyclerViewItemPosition).getFirstname()+" "+
                            dataItem.get(RecyclerViewItemPosition).getLastname());
                    //item.setAddress_id(feedObj1.getString("id"));
                    if(dataItem.get(RecyclerViewItemPosition).getCompany().equals("")||dataItem.get(RecyclerViewItemPosition).getCompany().equals("null")){
                        txt_company.setVisibility(View.GONE);
                    }
                    else {
                        txt_company.setText(dataItem.get(RecyclerViewItemPosition).getCompany());
                    }
                    txt_street.setText(dataItem.get(RecyclerViewItemPosition).getStreet1()+","+dataItem.get(RecyclerViewItemPosition).getStreet2());
                    txt_city.setText(dataItem.get(RecyclerViewItemPosition).getCity());
                    txt_state_pin.setText(dataItem.get(RecyclerViewItemPosition).getState()+","+dataItem.get(RecyclerViewItemPosition).getPostcode());
                    txt_country.setText(dataItem.get(RecyclerViewItemPosition).getCountry());
                    txt_phone.setText(dataItem.get(RecyclerViewItemPosition).getTelephone());
                    dialog.dismiss();
                    InitShippingMethod(dataItem.get(RecyclerViewItemPosition).getAddress_id());

                }
                return false;
            }
            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

            }
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
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
                        deliveryslot_indicator.setVisibility(View.GONE);
                        JSONObject jsonObj1 = jsonObj0.getJSONObject("data");
                        if(jsonObj1.has("summary")) {
                            JSONObject jsonObj2 = jsonObj1.getJSONObject("summary");
                            if(jsonObj2.has("slots")){
                                JSONObject jsonObj3 = jsonObj2.getJSONObject("slots");
                                STEP="DELIVERY_SLOT";
                                CartListItem item = new CartListItem();
                                Iterator<String> keys = jsonObj3.keys();
                                while (keys.hasNext()) {
                                    final String key = keys.next();
                                    TextView txt = (TextView)getLayoutInflater().inflate(R.layout.template_textview, null);
                                    //TextView txt=new TextView(Checkout_SetAddress.this);
                                    txt.setText(key);
                                    // txt.setTextSize(18);
                                    //txt.setTextColor(getResources().getColor(R.color.colorGrayDark));
                                    radiogroup.addView(txt);
                                    item.setProductName(key);
                                    if(jsonObj3.get(key) instanceof JSONArray) {
                                        JSONArray feedArray2 = jsonObj3.getJSONArray(key);
                                        for (int i = 0; i < feedArray2.length(); i++) {
                                            JSONObject feedObj1 = (JSONObject) feedArray2.get(i);
                                            final String slot = feedObj1.getString("slot");
                                            final String is_available = feedObj1.getString("is_available");
                                            Log.d("111111", "is_available " + is_available);
                                            final String slot_id = feedObj1.getString("slot_id");
                                            final String slot_group = feedObj1.getString("slot_group");

                                            LiquidRadioButton rdbtn = (LiquidRadioButton) getLayoutInflater().inflate(R.layout.template_radiobutton, null);
                                            rdbtn.setText(slot);
                                            if (feedObj1.getString("is_available").toString().equals(1)){
                                                rdbtn.setTextColor(getResources().getColor(R.color.colorGrayDark));
                                            }
                                            else if(feedObj1.getString("is_available").toString().equals(0)) {
                                                rdbtn.setTextColor(getResources().getColor(R.color.red));
                                                rdbtn.setClickable(false);
                                            }
                                            rdbtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    delivery_slot = slot;
                                                    delivery_date=key;
                                                    //delivery_isavailable=is_available;
                                                    delivery_slot_id=slot_id;
                                                    delivery_slot_group=slot_group;
                                                    CustomToast.info(getApplicationContext(),"Slot is: "+delivery_slot+" "+delivery_date).show();
                                                }
                                            });

                                            radiogroup.addView(rdbtn);
                                           // Log.d("111111", "here8 " + slot);
                                            item.setAmount(slot);
                                        }
                                    }
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

    private void InitShippingMethod(String address_id){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            LoadShippingMethodInitiate mLoadShippingMethodInitiate = new LoadShippingMethodInitiate(address_id,sPreferences.getString("CartID",""));
            mLoadShippingMethodInitiate.execute((Void) null);
        }else {
            switcher.showErrorView("No Internet Connection");
        }
    }

    public class LoadShippingMethodInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mAddressId,mCartId;
        LoadShippingMethodInitiate(String AddressID,String CartID) {
            mAddressId=AddressID;
            mCartId=CartID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doShippingMethod(mAddressId,mCartId);
            Log.d("111111111","PASSING VALUE: ADDRESS ID "+mAddressId+" CART ID: "+mCartId);
            Log.d("111111111", "RESULT "+result);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONArray jsonArray=new JSONArray(result.toString());
                //JSONObject jsonObj0 = new JSONObject(result.toString());
                Log.d("111111111", "here "+jsonArray);
                STEP="SHIPPING_METHOD";
                shippingdataItem.clear();
                shipping_indicator.setVisibility(View.GONE);
                next.setText("NEXT");
                next.setVisibility(View.VISIBLE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    CartListItem items = new CartListItem();
                    JSONObject feedObj1 = (JSONObject) jsonArray.get(i);
                    Log.d("111111111", "here1 ");
                    if(i==0){
                        shipping_carrier_code=feedObj1.getString("carrier_code");
                        shipping_method_code=feedObj1.getString("method_code");
                        shipping_method_title=feedObj1.getString("carrier_title");
                    }
                    items.setCarrier_title(feedObj1.getString("carrier_title"));
                    items.setAmount(feedObj1.getString("amount"));
                    items.setMethod_code(feedObj1.getString("method_code"));
                    items.setCarrier_code(feedObj1.getString("carrier_code"));
                    shippingdataItem.add(items);
                    Log.d("111111111", "here2 ");
                }
                mShippingAdapter = new ShippingMethodListAdapter(getApplicationContext(), shippingdataItem);
                shippingListView.setAdapter(mShippingAdapter);

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

    private void InitPickAddress(){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            LoadPickAddressInitiate mLoadPickAddressInitiate = new LoadPickAddressInitiate(CustomerID);
            mLoadPickAddressInitiate.execute((Void) null);
        }else {
            switcher.showErrorView("No Internet Connection");
        }
    }

    public class LoadPickAddressInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mId;
        LoadPickAddressInitiate(String ID) {
            mId=ID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doMyAddressList(mId);
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
                        indicator.setVisibility(View.GONE);
                        Log.d("111111", "here0 ");
                        JSONArray feedArray1 = jsonObj0.getJSONArray("data");
                        Log.d("1111221", "API jsonObj1" + feedArray1);
                        dataItem.clear();
                        for (int i = 0; i < feedArray1.length(); i++) {
                            AddressListItem item = new AddressListItem();
                            JSONObject feedObj1 = (JSONObject) feedArray1.get(i);
                            item.setFirstname(feedObj1.getString("firstname"));
                            item.setLastname(feedObj1.getString("lastname"));
                            item.setAddress_id(feedObj1.getString("id"));
                            item.setCompany(feedObj1.getString("company"));
                            item.setStreet1(feedObj1.getString("street1"));
                            item.setStreet2(feedObj1.getString("street2"));
                            item.setCity(feedObj1.getString("city"));
                            item.setState(feedObj1.getString("state"));
                            item.setPostcode(feedObj1.getString("postcode"));
                            item.setCountry(feedObj1.getString("country"));
                            item.setTelephone(feedObj1.getString("phone"));
                            item.setIs_default_shipping(feedObj1.getString("isdefaultshipping"));
                            item.setIs_default_billing(feedObj1.getString("isdefaultbilling"));
                            dataItem.add(item);
                        }
                        mAddressAdapter = new CheckoutAddressListAdapter(getApplicationContext(), dataItem);
                        mrecyclerview.setAdapter(mAddressAdapter);
                    }else {
                        CustomToast.error(getApplicationContext(),"Status 2").show();
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

    private void InitSetAddress(){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            LoadMyAddressInitiate mLoadMyAddressInitiate = new LoadMyAddressInitiate(CustomerID);
            mLoadMyAddressInitiate.execute((Void) null);
        }else {
            switcher.showErrorView("No Internet Connection");
        }
    }

    public class LoadMyAddressInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mId;


        LoadMyAddressInitiate(String ID) {
            mId=ID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switcher.showProgressView();
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doMyAddressList(mId);
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
                        STEP="ADDRESS";
                        switcher.showContentView();
                        sub_layout.setVisibility(View.VISIBLE);
                        Log.d("111111", "here0 ");
                        JSONArray feedArray1 = jsonObj0.getJSONArray("data");
                        for (int i = 0; i < feedArray1.length(); i++) {
                            JSONObject feedObj1 = (JSONObject) feedArray1.get(i);
                            if(feedObj1.getString("isdefaultbilling").equals("1")) {
                                lay_shipping_method.setVisibility(View.VISIBLE);
                                shipping_indicator.setVisibility(View.VISIBLE);
                                InitShippingMethod(feedObj1.getString("id"));
                                InitGetSlotData();
                                txt_address_name.setText(feedObj1.getString("firstname")+" "+feedObj1.getString("lastname"));
                                //item.setAddress_id(feedObj1.getString("id"));
                                if(feedObj1.getString("company").equals("")||feedObj1.getString("company").equals("null")){
                                    txt_company.setVisibility(View.GONE);
                                }
                                else {
                                    txt_company.setText(feedObj1.getString("company"));
                                }
                                txt_street.setText(feedObj1.getString("street1")+","+feedObj1.getString("street2"));
                                txt_city.setText(feedObj1.getString("city"));
                                txt_state_pin.setText(feedObj1.getString("state")+","+feedObj1.getString("postcode"));
                                txt_country.setText(feedObj1.getString("country"));
                                txt_phone.setText(feedObj1.getString("phone"));
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

    private void showAddDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_add_address);
        // setDialogInputLayoutTF(dialog);
        final CustomEditText edt_fname_add = (CustomEditText) dialog.findViewById(R.id.edt_fname_add);
        final CustomEditText edt_lname_add = (CustomEditText) dialog.findViewById(R.id.edt_lname_add);
        final CustomEditText edt_company_add = (CustomEditText) dialog.findViewById(R.id.edt_company_add);
        final CustomEditText edt_phone_add = (CustomEditText) dialog.findViewById(R.id.edt_phone_add);
        final CustomEditText edt_add1_add = (CustomEditText) dialog.findViewById(R.id.edt_add1_add);
        final CustomEditText edt_add2_add = (CustomEditText) dialog.findViewById(R.id.edt_add2_add);
        final CustomEditText edt_city_add = (CustomEditText) dialog.findViewById(R.id.edt_city_add);
        final CustomEditText edt_state_add = (CustomEditText) dialog.findViewById(R.id.edt_state_add);
        final CustomEditText edt_zip_add = (CustomEditText) dialog.findViewById(R.id.edt_zip_add);
        final CustomEditText edt_country_add = (CustomEditText) dialog.findViewById(R.id.edt_country_add);
        indicator=(AVLoadingIndicatorView)dialog.findViewById(R.id.indicator);

        final CustomCheckBox chk_default_shipping = (CustomCheckBox) dialog.findViewById(R.id.chk_default_shipping);

        btn_add = (Button) dialog.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fname = Utilities.getTrimText(edt_fname_add);
                String lname = Utilities.getTrimText(edt_lname_add);
                String company = Utilities.getTrimText(edt_company_add);
                String phone = Utilities.getTrimText(edt_phone_add);
                String add1 = Utilities.getTrimText(edt_add1_add);
                String add2 = Utilities.getTrimText(edt_add2_add);
                String city = Utilities.getTrimText(edt_city_add);
                String state = Utilities.getTrimText(edt_state_add);
                String zip = Utilities.getTrimText(edt_zip_add);
                String country = Utilities.getTrimText(edt_country_add);

                if (fname.length() > 0){

                    edt_fname_add.setError(null);
                    if (lname.length() > 0){

                        edt_lname_add.setError(null);
                        if (phone.length() > 0 && Utilities.isValidPhone(phone)){

                            edt_phone_add.setError(null);
                            if (add1.length() > 0){

                                edt_add1_add.setError(null);
                                if (city.length() > 0){

                                    edt_city_add.setError(null);
                                    if (zip.length() > 0){

                                        edt_zip_add.setError(null);
                                        if (country.length() > 0){

                                            edt_country_add.setError(null);

                                            int default_add = 0;

                                            if(chk_default_shipping.isChecked()) default_add = 1;

                                            processAddAddress(CustomerID,fname,lname,company,add1,add2,
                                                    city,zip,phone,country,state,String.valueOf(default_add),String.valueOf(default_add),dialog);
                                            //dialog.dismiss();
                                        }
                                        else {

                                            edt_country_add.setError(MessageConstants.FILL_THIS_FIELD);
                                            edt_country_add.requestFocus();
                                        }
                                    }
                                    else {

                                        edt_zip_add.setError(MessageConstants.FILL_THIS_FIELD);
                                        edt_zip_add.requestFocus();
                                    }
                                }
                                else {

                                    edt_city_add.setError(MessageConstants.FILL_THIS_FIELD);
                                    edt_city_add.requestFocus();
                                }
                            }
                            else {

                                edt_add1_add.setError(MessageConstants.FILL_THIS_FIELD);
                                edt_add1_add.requestFocus();
                            }
                        }
                        else {

                            edt_phone_add.setError(MessageConstants.INVALID_PHONE);
                            edt_phone_add.requestFocus();
                        }
                    }
                    else {

                        edt_lname_add.setError(MessageConstants.FILL_THIS_FIELD);
                        edt_lname_add.requestFocus();
                    }
                }
                else{
                    edt_fname_add.setError(MessageConstants.FILL_THIS_FIELD);
                    edt_fname_add.requestFocus();
                }

            }
        });

        dialog.show();
        Utilities.setDialogParamsWrapContent(getApplicationContext(),dialog);

    }

    private void processAddAddress(String customerid,String fname,String lname,String company,String add1,String add2,
                                   String city,String zip,String phone,String country,String state,String default_bill,String default_ship,Dialog dialog){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            AddAddressInitiate mAddAddressInitiate = new AddAddressInitiate(
                    customerid.trim(),
                    fname.trim(),
                    lname.trim(),
                    company.trim(),
                    add1.trim(),
                    add2.trim(),
                    city.trim(),
                    zip.trim(),
                    phone.trim(),
                    country.trim(),
                    state.trim(),
                    default_bill.trim(),default_ship.trim(),dialog);
            mAddAddressInitiate.execute((Void) null);
        }else {
            CustomToast.error(getApplicationContext(),"No Internet Connection.").show();
        }
    }
    public class AddAddressInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mcustomerid,mfirstname,mlastname,mcompany,
                mphone,madd1,madd2,mcity,mstate,mzip,mcountry,mdefaultbill,mdefaultship;
        Dialog mdialog;

        AddAddressInitiate(String customerid,String firstname, String lastname, String company,
                           String add1,String add2,String city,String zip,String phone,String country,String state,String defaultbill,String defaultship,Dialog dialog) {

            mcustomerid=customerid;
            mfirstname = firstname;
            mlastname = lastname;
            mcompany = company;
            madd1 = add1;
            madd2 = add2;
            mcity = city;
            mzip = zip;
            mphone = phone;
            mcountry = country;
            mstate = state;
            mdefaultship=defaultship;
            mdefaultbill = defaultbill;
            mdialog=dialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            indicator.setVisibility(View.VISIBLE);
            btn_add.setVisibility(View.INVISIBLE);
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doAddAddress(mcustomerid,mfirstname, mlastname,
                    mcompany,madd1,madd2,mcity,mzip,mphone,mcountry,mstate,mdefaultship,mdefaultbill,"1","");
            Log.d("111111111","PASSING: id: "+mcustomerid+" fname: "+mfirstname+" lname: "+mlastname+
                    " company: "+mcompany+" address: "+madd1+" "+madd2+" city: "+mcity+" zip: "+mzip+" phone: "+mphone+
                    " country: "+mcountry+" state: "+mstate+" defaultship: "+mdefaultship+" defaultbill: "+mdefaultbill+" mode: 1 "+" addressid ");
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            indicator.setVisibility(View.GONE);
            btn_add.setVisibility(View.VISIBLE);
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                Log.d("111111111",result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(2))) {
                        //mEmail.setText("");
                        // CustomToast.info(getApplicationContext(),"User already exist with this Email ID").show();
                    }else if (jsonObj.getString("status").equals(String.valueOf(1))) {
                        refresh();
                        CustomToast.success(getApplicationContext(),"Address added succesfully").show();
                        mdialog.dismiss();
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
    public void refresh(){
        InitSetAddress();
    }

    public void getAllValues() throws ParseException {

        FullAddress=txt_address_name.getText().toString()+",\n"+txt_street.getText().toString()+",\n"
        +txt_city.getText().toString()+",\n"+txt_state_pin.getText().toString()+",\n"
        +"India"+",\n"+txt_phone.getText().toString();

        CustomToast.info(getApplicationContext(),"data "+FullAddress).show();

        String s3=txt_state_pin.getText().toString();
        String[] state_pin = s3.split("\\,"); // escape .
        String state = state_pin[0];
        String pin = state_pin[1];

        String s1=txt_street.getText().toString();
        String[] streetparts = s1.split("\\,"); // escape .
        String street1 = streetparts[0];
        String street2 = streetparts[1];

        List<String> street = new ArrayList<>();
        street.add(street1);
        if (street2.length() > 0) street.add(street2);
        JSONArray streetjsonAraay = new JSONArray(street);
        Log.d("1111111","Street String: "+streetjsonAraay);


        String s2=txt_address_name.getText().toString();
        String[] nameparts = s2.split("\\s+");
        String firstname = nameparts[0];
        String lastname = nameparts[1];

        List<HashMap<String, String>> shipping_fillMaps1 = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map1;
        map1 = new HashMap<String, String>();
        map1.put("countryId", "IN");
        map1.put("regionId", "512");
        map1.put("region", "Kerala");
        map1.put("street", streetjsonAraay.toString());
        map1.put("telephone", txt_phone.getText().toString());
        map1.put("postcode",pin);
        map1.put("city",txt_city.getText().toString());
        map1.put("firstname", firstname);
        map1.put("lastname", lastname);
        // map1.put("\"Hello\"",lastname);
        shipping_fillMaps1.add(0,map1);

        JSONArray shippingjsonAraay = new JSONArray(shipping_fillMaps1);
        String a1 = shippingjsonAraay.toString();
        a1 = a1.replace("[{", "{").replace("}]","}")
                .replace(":\"[",":[").replace("]\",","],")
                .replace("\\\"","\"");   ;
        Log.d("1111111", "Shipping: "+a1);


        List<HashMap<String, String>> billing_fillMaps2 = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map2;
        map2 = new HashMap<String, String>();
        map2.put("countryId", "IN");
        map2.put("regionId", "512");
        map2.put("region", "Kerala");
        map2.put("street", streetjsonAraay.toString());
        map2.put("telephone", txt_phone.getText().toString());
        map2.put("postcode",pin);
        map2.put("city",txt_city.getText().toString());
        map2.put("firstname", firstname);
        map2.put("lastname", lastname);
        map2.put("saveInAddressBook", "1");
        billing_fillMaps2.add(0,map2);

        JSONArray billingjsonAraay = new JSONArray(billing_fillMaps2);
        String a2 = billingjsonAraay.toString();
        a2 = a2.replace("[{", "{").replace("}]","}")
                .replace(":\"[",":[").replace("]\",","],")
                .replace("\\\"","\"");
        Log.d("11111111", "Billing: "+a2);

        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date MyDate = newDateFormat.parse(delivery_date);
        newDateFormat.applyPattern("EEEE, d MMM, yyyy");
        String deliverydate = newDateFormat.format(MyDate);
        Log.d("11111111", "Date: "+deliverydate);


        List<HashMap<String, String>> slotdata_fillMaps3 = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map3;
        map3 = new HashMap<String, String>();
        map3.put("slot_time", delivery_slot);
        map3.put("date", deliverydate);
        map3.put("slot_id", delivery_slot_id);
        map3.put("slot_group", delivery_slot_group);
        slotdata_fillMaps3.add(0,map3);

        JSONArray slotdatajsonAraay = new JSONArray(slotdata_fillMaps3);
        String a3 = slotdatajsonAraay.toString();
        a3 = a3.replace("[{", "{").replace("}]","}")
                .replace("\"","\\\"");
        Log.d("11111111", "Slot data: "+a3);

        List<HashMap<String, String>> extension_fillMaps4 = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map4;
        map4 = new HashMap<String, String>();
        map4.put("slot_data", a3);
        extension_fillMaps4.add(0,map4);

        JSONArray extensionjsonAraay = new JSONArray(extension_fillMaps4);
        String a4 = extensionjsonAraay.toString();
        a4 = a4.replace("[{", "{").replace("}]","}")
                .replace("\\\\","\"");
        Log.d("11111111", "Extenions Attribute: "+a4);


        List<HashMap<String, String>> sub_fillMaps = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> sub_map;
        sub_map = new HashMap<String, String>();
        sub_map.put("\"shipping_address\"",a1);
        sub_map.put("\"billing_address\"",a2);
        sub_map.put("\"shipping_method_code\"","\""+shipping_method_code+"\"");
        sub_map.put("\"shipping_carrier_code\"","\""+shipping_carrier_code+"\"");
        sub_map.put("\"extension_attributes\"",a4);
        sub_fillMaps.add(0,sub_map);

        String str = sub_fillMaps.toString();
        str = str.replace("[{", "{").replace("}]","}")
                .replace("=",":")
                .replace("{\"\\","{\\")
                .replace("\"\\","\\");
        Log.d("1111111","final String: "+str);


        List<HashMap<String, String>> address_fillMaps5 = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map5;
        map5 = new HashMap<String, String>();
        map5.put("\"addressInformation\"", str);
        address_fillMaps5.add(0,map5);
        Log.d("1111111","Address information: "+address_fillMaps5);

        String laststring = address_fillMaps5.toString();
        laststring = laststring.replace("[{", "{").replace("}]","}")
                .replace("=",":");
        Log.d("1111111","final String: "+laststring);
        CustomToast.info(getApplicationContext(),"data "+laststring).show();

        InitShippingInformation(laststring);

    }

    private void InitShippingInformation(String address_id){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            LoadShippingInformationInitiate mLoadShippingInformationInitiate = new LoadShippingInformationInitiate(address_id,sPreferences.getString("CartID",""));
            mLoadShippingInformationInitiate.execute((Void) null);
        }else {
            switcher.showErrorView("No Internet Connection");
        }
    }

    public class LoadShippingInformationInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mAddressId,mCartId;
        LoadShippingInformationInitiate(String AddressID,String CartID) {
            mAddressId=AddressID;
            mCartId=CartID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doShippingInformation(mAddressId,mCartId);
            Log.d("111111111","PASSING VALUE: ADDRESS ID "+mAddressId+" CART ID: "+mCartId);
            Log.d("111111111", "RESULT "+result);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj0 = new JSONObject(result.toString());
                if (jsonObj0.has("payment_methods")) {
                    payment_indicator.setVisibility(View.GONE);
                    STEP="PAYMENT_METHOD";
                    next.setText("ORDER SUMMARY");
                    JSONArray feedArray1 = jsonObj0.getJSONArray("payment_methods");
                    Log.d("1111221", "API jsonObj1" + feedArray1);
                    for (int i = 0; i < feedArray1.length(); i++) {
                        AddressListItem item = new AddressListItem();
                        JSONObject feedObj1 = (JSONObject) feedArray1.get(i);
                        final String paymentCode = feedObj1.getString("code");
                        final String paymentTitle = feedObj1.getString("title");
                        LiquidRadioButton rdbtn = (LiquidRadioButton) getLayoutInflater().inflate(R.layout.template_radiobutton, null);
                        rdbtn.setText(paymentTitle);
                        rdbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                paymentmethod_code = paymentCode;
                                paymentmethod_title=paymentTitle;
                                CustomToast.info(getApplicationContext(),"Payment code is: "+paymentmethod_code+" "+paymentmethod_title).show();
                            }
                        });
                        paymentradiogroup.addView(rdbtn);

                    }
                }
                if(jsonObj0.has("totals")){
                    JSONObject jsonObj1 = jsonObj0.getJSONObject("totals");
                    Log.d("11111111","here1");
                    SubTotal=jsonObj1.getString("subtotal").toString().trim();
                    Shipping=jsonObj1.getString("shipping_amount").toString().trim();
                    Tax=jsonObj1.getString("tax_amount").toString().trim();
                    Discount=jsonObj1.getString("discount_amount").toString().trim();
                    GrandTotal=jsonObj1.getString("grand_total").toString().trim();
                    ItemCount=jsonObj1.getString("items_qty").toString().trim();
                    Log.d("11111111","here1");
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
    private void InitPaymentMode(){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            LoadPaymentModeInitiate mLoadPaymentModeInitiate = new LoadPaymentModeInitiate(paymentmethod_code);
            mLoadPaymentModeInitiate.execute((Void) null);
        }else {
            switcher.showErrorView("No Internet Connection");
        }
    }

    public class LoadPaymentModeInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mPayment;
        LoadPaymentModeInitiate(String payment) {
            mPayment=payment;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switcher.showProgressView();
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doPaymentMethod(CustomerID,mPayment);
            Log.d("111111111","PASSING VALUE: PAYMENT "+mPayment);
            Log.d("111111111", "RESULT "+result);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj0 = new JSONObject(result.toString());
                if (jsonObj0.has("id")) {
                    String id=jsonObj0.getString("id");
                    Log.d("11111111","ID"+id);
                    Intent intent = new Intent(Checkout_SetAddress.this,OrderSummaryActivity.class);
                    intent.putExtra("PLACEORDER_ID",id);
                    intent.putExtra("SUBTOTAL",SubTotal);
                    intent.putExtra("SHIPPING",Shipping);
                    intent.putExtra("TAX",Tax);
                    intent.putExtra("DISCOUNT",Discount);
                    intent.putExtra("GRANDTOTAL",GrandTotal);
                    intent.putExtra("ITEMCOUNT",ItemCount);
                    intent.putExtra("DELIVERYDATE",delivery_date);
                    intent.putExtra("DELIVERYSLOT",delivery_slot);
                    intent.putExtra("SHIPPINGMETHOD",shipping_method_title);
                    intent.putExtra("FULLNAME",txt_address_name.getText().toString());
                    intent.putExtra("ADDRESS",txt_street.getText().toString());
                    intent.putExtra("CITY",txt_city.getText().toString());
                    String s3=txt_state_pin.getText().toString();
                    String[] state_pin = s3.split("\\,"); // escape .
                    String state = state_pin[0];
                    String pin = state_pin[1];
                    intent.putExtra("STATE","Kerala");
                    intent.putExtra("PINCODE",pin);
                    intent.putExtra("COUNTRY","India");
                    intent.putExtra("PHONE",txt_phone.getText().toString());
                    startActivity(intent);
                    switcher.showContentView();
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