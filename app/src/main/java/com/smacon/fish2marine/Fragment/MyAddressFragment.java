package com.smacon.fish2marine.Fragment;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.smacon.f2mlibrary.Button.FloatingActionButton;
import com.smacon.f2mlibrary.CustomCheckBox;
import com.smacon.f2mlibrary.CustomEditText;
import com.smacon.f2mlibrary.CustomToast;
import com.smacon.f2mlibrary.Progress.AVLoadingIndicatorView;
import com.smacon.f2mlibrary.Switcher.Switcher;
import com.smacon.fish2marine.AdapterClass.MyAddressAdapter;
import com.smacon.fish2marine.HelperClass.AddressListItem;
import com.smacon.fish2marine.HelperClass.MessageConstants;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.HelperClass.Utilities;
import com.smacon.fish2marine.R;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class MyAddressFragment extends Fragment implements View.OnClickListener{
    private SharedPreferences sPreferences;
    private SqliteHelper helper;
    private Config mConfig;
    List<HashMap<String, String>> SQLData_Item ;
    String CustomerID = "";
    private Switcher switcher;
    private RecyclerView mrecyclerview;
    private FloatingActionButton fab_add_address;

    private TextView mTitle,error_label_retry, empty_label_retry;
    ArrayList<AddressListItem> dataItem;
    ImageView back;
    private static MyAddressFragment addressActivity;
    private Typeface tf;
    AVLoadingIndicatorView indicator;
    Button btn_add;
    MyAddressAdapter myAddressAdapter;
    public static final MyAddressFragment getInstance(){
        return addressActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.myaddress_fragment, container, false);
        helper = new SqliteHelper(getActivity(), "Fish2Marine", null, 5);
        sPreferences = getActivity().getSharedPreferences("Fish2Marine", MODE_PRIVATE);
        mConfig = new Config(getActivity());
        addressActivity = this;
        InitIdView(rootView);
        return rootView;
    }
    private void InitIdView(View rootview){
        SQLData_Item = helper.getadmindetails();
        CustomerID=SQLData_Item.get(0).get("admin_id");
        Log.d("1111221", "Customer ID "+CustomerID);
        switcher = new Switcher.Builder(getActivity())
                .addContentView( rootview.findViewById(R.id.mrecyclerview))
                .addErrorView( rootview.findViewById(R.id.error_view))
                .addProgressView( rootview.findViewById(R.id.progress_view))
                .setErrorLabel((TextView) rootview.findViewById(R.id.error_label))
                .setEmptyLabel((TextView) rootview.findViewById(R.id.empty_label))
                .addEmptyView( rootview.findViewById(R.id.empty_view))
                .build();
        tf = Typeface.createFromAsset(getResources().getAssets(),"rounded_font.otf");
        mrecyclerview = ((RecyclerView) rootview.findViewById(R.id.mrecyclerview));
        mrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        fab_add_address = (FloatingActionButton) rootview.findViewById(R.id.fab_add_address);
        error_label_retry = ((TextView)  rootview.findViewById(R.id.error_label_retry));
        empty_label_retry = ((TextView) rootview.findViewById(R.id.empty_label_retry));
        error_label_retry.setOnClickListener(this);
        empty_label_retry.setOnClickListener(this);
        fab_add_address.setOnClickListener(this);
        dataItem = new ArrayList<>();
        InitGetAddressData();

    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId) {
            case R.id.error_label_retry:
                InitGetAddressData();
                break;
            case R.id.empty_label_retry:
                InitGetAddressData();
                break;
            case R.id.fab_add_address:
                showAddDialog();
                break;
        }
    }

    private void InitGetAddressData(){
        Config mConfig = new Config(getActivity());
        if(mConfig.isOnline(getActivity())){
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
            //helper.Delete_timeslot_details();
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getActivity());
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
                        switcher.showContentView();
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
                            myAddressAdapter = new MyAddressAdapter(getActivity(), dataItem);
                            mrecyclerview.setAdapter(myAddressAdapter);
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

        final Dialog dialog = new Dialog(getActivity());
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
        Utilities.setDialogParamsWrapContent(getActivity(),dialog);

    }

    private void processAddAddress(String customerid,String fname,String lname,String company,String add1,String add2,
                                   String city,String zip,String phone,String country,String state,String default_bill,String default_ship,Dialog dialog){
        Config mConfig = new Config(getActivity());
        if(mConfig.isOnline(getActivity())){
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
            CustomToast.error(getActivity(),"No Internet Connection.").show();
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

            HttpOperations httpOperations = new HttpOperations(getActivity());
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
                        CustomToast.success(getActivity(),"Address added succesfully").show();
                        mdialog.dismiss();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                CustomToast.info(getActivity(),"Please Try Again").show();
            } catch (NullPointerException e) {
                CustomToast.error(getActivity(),"No Internet Connection.").show();
            } catch (Exception e) {
                CustomToast.info(getActivity(),"Please Try Again").show();
            }
        }
    }
    public void refresh(){
        InitGetAddressData();
    }

}