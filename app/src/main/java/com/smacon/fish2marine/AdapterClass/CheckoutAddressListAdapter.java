package com.smacon.fish2marine.AdapterClass;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smacon.f2mlibrary.CustomCheckBox;
import com.smacon.f2mlibrary.CustomEditText;
import com.smacon.f2mlibrary.CustomTextView;
import com.smacon.f2mlibrary.CustomToast;
import com.smacon.f2mlibrary.Progress.AVLoadingIndicatorView;
import com.smacon.fish2marine.CheckOut.Checkout_SetAddress;
import com.smacon.fish2marine.HelperClass.AddressListItem;
import com.smacon.fish2marine.HelperClass.CartListItem;
import com.smacon.fish2marine.HelperClass.MessageConstants;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.HelperClass.Utilities;
import com.smacon.fish2marine.R;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Aiswarya on 09/05/2018.
 */

public class CheckoutAddressListAdapter extends RecyclerView.Adapter<CheckoutAddressListAdapter.ViewHolder>  {
    private SharedPreferences sPreferences;
    private SqliteHelper helper;
    private Config mConfig;
    private Context mContext;
    private ArrayList<AddressListItem> mListItem;
    private AddressListItem item;
    private String Customer_ID;
    AVLoadingIndicatorView indicator;
    ArrayList<CartListItem> cuttype_dataItem;
    Animation animFadein;
    Button btn_add;
    List<HashMap<String, String>> SQLData_Item;

    public CheckoutAddressListAdapter(Context context, ArrayList<AddressListItem> listitem) {
        this.mContext = context;
        this.mListItem = listitem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_checkoutaddress, viewGroup, false);
        helper = new SqliteHelper(mContext, "Fish2Marine", null, 5);
        mConfig = new Config(mContext);
        sPreferences = mContext.getSharedPreferences("Fish2Marine", MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mListItem.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView txt_address_name,txt_company,txt_street,
                txt_city,txt_state_pin,txt_country,txt_phone,txt_default_address,txt_heading;
        private LinearLayout layout;
        private ImageView edit,delete;
        private FrameLayout layout_indicator;

        ViewHolder(final View itemView) {
            super(itemView);
            this.edit = (ImageView) itemView.findViewById(R.id.edit);
            this.delete = (ImageView) itemView.findViewById(R.id.delete);
            this.txt_address_name = (CustomTextView) itemView.findViewById(R.id.txt_address_name);
            this.txt_company = (CustomTextView) itemView.findViewById(R.id.txt_company);
            this.txt_street = (CustomTextView) itemView.findViewById(R.id.txt_street);
            this.txt_city = (CustomTextView) itemView.findViewById(R.id.txt_city);
            this.txt_state_pin = (CustomTextView) itemView.findViewById(R.id.txt_state_pin);
            this.txt_country = (CustomTextView) itemView.findViewById(R.id.txt_country);
            this.txt_phone = (CustomTextView) itemView.findViewById(R.id.txt_phone);
            this.txt_default_address = (CustomTextView) itemView.findViewById(R.id.txt_default_address);

            this.layout = (LinearLayout) itemView.findViewById(R.id.layout);
            this.layout_indicator = (FrameLayout) itemView.findViewById(R.id.layout_indicator);
        }

    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        item = mListItem.get(position);
        SQLData_Item = helper.getadmindetails();
        Customer_ID=SQLData_Item.get(0).get("admin_id");

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CustomToast.success(mContext, "Address deleted position "+holder.getAdapterPosition() ).show();
            }
        });
        holder.edit.setVisibility(View.GONE);
        holder.delete.setVisibility(View.GONE);
        holder.txt_address_name.setText(item.getFirstname()+" "+item.getLastname()+"/"+item.getAddress_id());
        holder.txt_company.setText(item.getCompany());
        holder.txt_street.setText(item.getStreet1()+","+item.getStreet2());
        holder.txt_city.setText(item.getCity());
        holder.txt_state_pin.setText(item.getState()+","+item.getPostcode());
        holder.txt_country.setText(item.getCountry());
        holder.txt_phone.setText(item.getTelephone());
        if(item.getIs_default_billing().equals("1")||item.getIs_default_shipping().equals("1")){
            holder.txt_default_address.setVisibility(View.VISIBLE);
        }

    }

    private void showAddDialog(final String addressid, final int position, final ViewHolder holder) {

        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_add_address);
        // setDialogInputLayoutTF(dialog);
        TextView txt_heading = (TextView) dialog.findViewById(R.id.txt_heading);
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

        edt_fname_add.setText(mListItem.get(position).getFirstname());
        edt_lname_add.setText(mListItem.get(position).getLastname());
        edt_company_add.setText(mListItem.get(position).getCompany());
        edt_phone_add.setText(mListItem.get(position).getTelephone());
        edt_add1_add.setText(mListItem.get(position).getStreet1());
        edt_add2_add.setText(mListItem.get(position).getStreet2());
        edt_city_add.setText(mListItem.get(position).getCity());
        edt_state_add.setText(mListItem.get(position).getState());
        edt_zip_add.setText(mListItem.get(position).getPostcode());
        edt_country_add.setText(mListItem.get(position).getCountry());

        if(mListItem.get(position).getIs_default_shipping().equals("1")) chk_default_shipping.setChecked(true);
        else chk_default_shipping.setChecked(false);
        btn_add = (Button) dialog.findViewById(R.id.btn_add);
        txt_heading.setText("Edit Address");
        btn_add.setText("Save");


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

                                            processEditAddress(Customer_ID,fname,lname,company,add1,add2,
                                                    city,zip,phone,country,state,String.valueOf(default_add),String.valueOf(default_add),addressid,dialog,holder);
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
        Utilities.setDialogParamsWrapContent(mContext,dialog);

    }

    private void processEditAddress(String customerid,String fname,String lname,String company,String add1,String add2,
                                   String city,String zip,String phone,String country,String state,String default_bill,String default_ship,String addressid,Dialog dialog,ViewHolder holder){
        Config mConfig = new Config(mContext);
        if(mConfig.isOnline(mContext)){
            EditAddressInitiate mEditAddressInitiate = new EditAddressInitiate(
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
                    default_bill.trim(),default_ship.trim(),
                    addressid,
                    dialog,holder);
            mEditAddressInitiate.execute((Void) null);
        }else {
            CustomToast.error(mContext,"No Internet Connection.").show();
        }
    }
    public class EditAddressInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mcustomerid,mfirstname,mlastname,mcompany,
                mphone,madd1,madd2,mcity,mstate,mzip,mcountry,mdefaultbill,mdefaultship,maddressid;
        Dialog mdialog;
        ViewHolder mholder;

        EditAddressInitiate(String customerid,String firstname, String lastname, String company,
                           String add1,String add2,String city,String zip,String phone,String country,String state,String defaultbill,String defaultship,String addressid,Dialog dialog,ViewHolder holder) {

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
            maddressid=addressid;
            mdialog=dialog;
            mholder=holder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            indicator.setVisibility(View.VISIBLE);
            btn_add.setVisibility(View.INVISIBLE);
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(mContext);
            StringBuilder result = httpOperations.doAddAddress(mcustomerid,mfirstname, mlastname,
                    mcompany,madd1,madd2,mcity,mzip,mphone,mcountry,mstate,mdefaultship,mdefaultbill,"2",maddressid);
            Log.d("111111111","PASSING: id: "+mcustomerid+" fname: "+mfirstname+" lname: "+mlastname+
                    " company: "+mcompany+" address: "+madd1+" "+madd2+" city: "+mcity+" zip: "+mzip+" phone: "+mphone+
                    " country: "+mcountry+" state: "+mstate+" defaultship: "+mdefaultship+" defaultbill: "+mdefaultbill+" mode: 2 "+" addressid :"+maddressid);
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
                    }else if (jsonObj.getString("status").equals(String.valueOf(3))) {
                       // refresh(mholder);
                        Checkout_SetAddress.getInstance().refresh();
                        mdialog.dismiss();
                        CustomToast.success(mContext,"Address Updated succesfully").show();

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                CustomToast.info(mContext,"Please Try Again").show();
            } catch (NullPointerException e) {
                CustomToast.error(mContext,"No Internet Connection.").show();
            } catch (Exception e) {
                CustomToast.info(mContext,"Please Try Again").show();
            }
        }
    }

    private void DeleteAddress(String AddressID,ViewHolder holder){

        Config mConfig = new Config(mContext);
        if(mConfig.isOnline(mContext)){
            DeleteAddressInitiate mDeleteAddressInitiate = new DeleteAddressInitiate
                    (AddressID,holder);
            mDeleteAddressInitiate.execute((Void) null);
        }else {
            CustomToast.error(mContext,"No Internet Connection.").show();
        }
    }

    public class DeleteAddressInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mAddressID;
        ViewHolder mholder;

        DeleteAddressInitiate(String Address_ID,ViewHolder holder) {
            mAddressID=Address_ID;
            mholder=holder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mholder.layout.setVisibility(View.INVISIBLE);
            mholder.layout_indicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(mContext);
            Log.d("111111", "PASSING VALUE " + mAddressID);
            StringBuilder result = httpOperations.doDeleteAddress(mAddressID);
            Log.d("111111", "PASSING VALUE " + mAddressID);
            Log.d("111111", "API_DELETE_RESPONSE " + result);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(2))) {
                        mholder.layout.setVisibility(View.VISIBLE);
                        mholder.layout_indicator.setVisibility(View.GONE);
                        CustomToast.info(mContext,jsonObj.getString("message").toString()).show();
                    }else if (jsonObj.getString("status").equals(String.valueOf(1))) {
                         //Checkout_SetAddress.getInstance().refresh();
                         CustomToast.success(mContext, "Address deleted").show();
                        update(mholder);

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                CustomToast.info(mContext,"Please Try Again").show();
            } catch (NullPointerException e) {
                CustomToast.error(mContext,"No Internet Connection.").show();
            } catch (Exception e) {
                CustomToast.info(mContext,"Please Try Again").show();
            }
        }
    }

    private void update(ViewHolder holder){
       /* LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent("data_changed"));
        NavigationDrawerActivity.getInstance().updateCartCount();*/

        holder.layout.setVisibility(View.VISIBLE);
        holder.layout_indicator.setVisibility(View.GONE);
        //CustomToast.success(mContext, "Address deleted position "+holder.getAdapterPosition() ).show();
        mListItem.remove(holder.getAdapterPosition());
        notifyDataSetChanged();
        notifyItemRemoved(holder.getAdapterPosition());
        notifyItemRangeChanged(holder.getAdapterPosition(), mListItem.size());
        CustomToast.success(mContext, "Address deleted").show();
    }
 /*   private void refresh(ViewHolder holder){
       notifyItemChanged(holder.getAdapterPosition());
       notifyDataSetChanged();
       CustomToast.success(mContext, "Address deleted").show();
    }
*/
}
