package com.smacon.fish2marine.AdapterClass;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.smacon.f2mlibrary.CheckBox;
import com.smacon.f2mlibrary.CustomEditText;
import com.smacon.fish2marine.R;

/**
 * Created by smacon on 30/1/17.
 */

public final class AddressActivity extends AppCompatActivity {

    private ListView list_view;
    private FloatingActionButton fab_add_address;

    private Dialog loaderDialog;
   // private Addresses addresses;
    private static AddressActivity addressActivity;
    private Typeface tf;
    //private Countries countries;
    private String country_code;

    public static final AddressActivity getInstance(){
        return addressActivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.myaddress_fragment);

        addressActivity = this;

      //  list_view = (ListView) findViewById(R.id.list_view);

        fab_add_address = (FloatingActionButton) findViewById(R.id.fab_add_address);

        init();
    }

    private void init(){

        tf = Typeface.createFromAsset(getResources().getAssets(),"rounded_font.otf");

        initLoaderDialog();

        setListeners();
    }

    public void initLoaderDialog(){

      //  loaderDialog = Utilities.getLoaderDialog(this);

    }

    private void setListeners(){

        fab_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
    }

    private void showAddDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(R.layout.dialog_add_address);

        setDialogInputLayoutTF(dialog);

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

        final CheckBox chk_default_shipping = (CheckBox) dialog.findViewById(R.id.chk_default_shipping);

        Button btn_add = (Button) dialog.findViewById(R.id.btn_add);

        edt_country_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showCountriesDialog(edt_country_add);
            }
        });
    }

       /* btn_add.setOnClickListener(new View.OnClickListener() {
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

                                            //Toast.makeText(getApplicationContext(),"refresh",Toast.LENGTH_SHORT);
                                            if(chk_default_shipping.isChecked()) default_add = 1;

                                            processAddAddress(fname,lname,company,phone,add1,add2,
                                                    city,state,zip,country,String.valueOf(default_add));

                                            dialog.dismiss();
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
        Utilities.setDialogParamsWrapContent(this,dialog);

    }*/



    private void setDialogInputLayoutTF(Dialog dialog){

        TextInputLayout txt_input_country_add = (TextInputLayout) dialog.findViewById(R.id.txt_input_country_add);
        TextInputLayout txt_input_zip_add = (TextInputLayout) dialog.findViewById(R.id.txt_input_zip_add);
        TextInputLayout txt_input_state_add = (TextInputLayout) dialog.findViewById(R.id.txt_input_state_add);
        TextInputLayout txt_input_city_add = (TextInputLayout) dialog.findViewById(R.id.txt_input_city_add);
        TextInputLayout txt_input_add2_add = (TextInputLayout) dialog.findViewById(R.id.txt_input_add2_add);
        TextInputLayout txt_input_add1_add = (TextInputLayout) dialog.findViewById(R.id.txt_input_add1_add);
        TextInputLayout txt_input_phone_add = (TextInputLayout) dialog.findViewById(R.id.txt_input_phone_add);
        TextInputLayout txt_input_company_add = (TextInputLayout) dialog.findViewById(R.id.txt_input_company_add);
        TextInputLayout txt_input_lname_add = (TextInputLayout) dialog.findViewById(R.id.txt_input_lname_add);
        TextInputLayout txt_input_fname_add = (TextInputLayout) dialog.findViewById(R.id.txt_input_fname_add);

        txt_input_fname_add.setTypeface(tf);
        txt_input_lname_add.setTypeface(tf);
        txt_input_company_add.setTypeface(tf);
        txt_input_phone_add.setTypeface(tf);
        txt_input_add1_add.setTypeface(tf);
        txt_input_add2_add.setTypeface(tf);
        txt_input_city_add.setTypeface(tf);
        txt_input_state_add.setTypeface(tf);
        txt_input_zip_add.setTypeface(tf);
        txt_input_country_add.setTypeface(tf);

    }


    private void processAddAddress(String fname,String lname,String company,String phone, String add1,
                                   String add2, String city, String state, String zip, String country,
                                   String default_add){


        if(loaderDialog != null )
            loaderDialog.show();

        else{
            initLoaderDialog();
            loaderDialog.show();
        }

        try{

          /*  AddressListItem address = new AddressListItem();
            List<String> street = new ArrayList<>();
            street.add(add1);
            if (add2.length() > 0) street.add(add2);
            address.setFirstname(fname);
            address.setLastname(lname);
            address.setCompany(company);
            address.setTelephone(phone);
            address.setStreet(street);
            address.setCity(city);
            address.setRegion(state);
            address.setPostcode(zip);
            address.setCountryName(country);
            address.setRegion_id("0");
            address.setCountry_id(country_code);
            address.setIs_default_billing(default_add);
            address.setIs_default_shipping(default_add);
            NetworksUtil networksUtil = NetworksUtil.getInstance();
            networksUtil.addHeader(PreferenceConstants.TOKEN, Preferences.
                    getInstance(this).getString(PreferenceConstants.TOKEN,""));
            networksUtil.doPostRequest(this, UrlUtil.getAddressesURL(),
                    Utilities.getGson().toJson(address), addAddressCallback);*/
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }



   /* private void loadAddress(){

        if(loaderDialog != null ){

           // if (loaderDialog.isShowing()) loaderDialog.dismiss();
            loaderDialog.show();
        }

        else{
            initLoaderDialog();
            loaderDialog.show();
        }


        NetworksUtil networksUtil = NetworksUtil.getInstance();
        networksUtil.addHeader(PreferenceConstants.TOKEN, Preferences.
                getInstance(this).getString(PreferenceConstants.TOKEN,""));

        networksUtil.doGetRequest(this, UrlUtil.getAddressesURL(),
                addressCallback);
    }*/

   /* NetworkCallback addAddressCallback = new NetworkCallback() {

        @Override
        public void onResponse(boolean isSuccess, int responseCode, final String networkMessage,
                               String response) throws IOException {

            if (loaderDialog.isShowing()) loaderDialog.dismiss();

            if (isSuccess){

//                Log.i(Utilities.getTag(AddressActivity.this),response);

                final AddressResponse addAddressResponse = Utilities.getGson().fromJson(response,
                        AddressResponse.class);
                Log.d("111Address111111",addAddressResponse.getData().toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(AddressActivity.this,
                                addAddressResponse.getData().getMessage(), Toast.LENGTH_LONG).show();
                        refreshList();

                    }

                });


            }
            else{
                if (response.startsWith("{")) {
                    final ErrorResponse errorResponse = Utilities.getGson().fromJson(response,ErrorResponse.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddressActivity.this,
                                    errorResponse.getData().getMessages().getError().
                                            get(0).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(AddressActivity.this,
                                    networkMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }

        @Override
        public void onNoNetwork() {

            if (loaderDialog.isShowing()) loaderDialog.dismiss();
            Toast.makeText(AddressActivity.this, MessageConstants.NO_NETWORK_CONNECTION , Toast.LENGTH_LONG).show();

        }
    };*/

   /* NetworkCallback addressCallback = new NetworkCallback() {
        @Override
        public void onResponse(boolean isSuccess, int responseCode, final String networkMessage,
                               String response) throws IOException {

            if (loaderDialog.isShowing()) loaderDialog.dismiss();

            if (isSuccess){
//                Log.i(Utilities.getTag(MainActivity.this),response);

                addresses = Utilities.getGson().fromJson(response,Addresses.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        setAdapter();
                    }
                });

            }
            else{
                if (response.startsWith("{")) {
                    final ErrorResponse errorResponse = Utilities.getGson().fromJson(response,ErrorResponse.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddressActivity.this,
                                    errorResponse.getData().getMessages().getError().
                                            get(0).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(AddressActivity.this,
                                    networkMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }

        @Override
        public void onNoNetwork() {

            if (loaderDialog.isShowing()) loaderDialog.dismiss();
            Toast.makeText(AddressActivity.this, MessageConstants.NO_NETWORK_CONNECTION , Toast.LENGTH_LONG).show();

        }
    };*/

   /* private void setAdapter(){

        if (addresses != null && addresses.getData().size() > 0) {

            CustomAddressListAdapter adp = new CustomAddressListAdapter(this,getArrayList(),
                    addresses);

            Log.i(Utilities.getTag(this),getArrayList().toString());
            list_view.setAdapter(adp);


        }
        else{
            Toast.makeText(AddressActivity.this, "No address found", Toast.LENGTH_SHORT).show();
        }
    }*/

   /* private ArrayList<HashMap<String,String>> getArrayList(){

        ArrayList<HashMap<String, String>> mapArrayList = new ArrayList<>();

        for (int i=0; i< addresses.getData().size(); i++){
            HashMap<String,String> map = new HashMap<>();

            AddressListItem address = addresses.getData().get(i);

            map.put(AddressConstants.ADDRESS_ID,address.getEntity_id());
            map.put(AddressConstants.ADDRESS_NAME,address.getFirstname().concat(" ").concat(address.getLastname()));
            map.put(AddressConstants.FULL_ADDRESS,getFullAddress(address));
            map.put(AddressConstants.IS_DEFAULT_SHIPPING,address.getIs_default_shipping());

            mapArrayList.add(map);
        }

        return mapArrayList;
    }*/

  /*  private String getFullAddress(AddressListItem address){

        String add = "";

        add += (address.getCompany() != null && address.getCompany().length() > 0)?
                address.getCompany().concat("\n"):"";

        for (int i=0;i<address.getStreet().size();i++){

            add += address.getStreet().get(i);
            if (i != address.getStreet().size()-1) add += ",";
        }

        add += "\n";

        add += (address.getCity() != null && address.getCity().length() > 0)?
                address.getCity().concat("\n"):"";

        add += (address.getRegion() != null && address.getRegion().length() > 0)?
                address.getRegion().concat(","):"";

        add += (address.getPostcode() != null && address.getPostcode().length() > 0)?
                address.getPostcode():"";

        add += "\n";

        add += (address.getCountryName() != null && address.getCountryName().length() > 0)?
                address.getCountryName().concat("\n"):"";

        add += (address.getTelephone() != null && address.getTelephone().length() > 0)?
                address.getTelephone():"";

        return add;

    }*/

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch(itemId) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {

        addressActivity = null;
        super.onDestroy();
    }

    public void refreshList(){

      //  loadAddress();

    }

    public Dialog getLoaderDialog(){
        return loaderDialog;
    }

}
