package com.smacon.fish2marine;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.smacon.f2mlibrary.CustomCheckBox;
import com.smacon.f2mlibrary.CustomEditText;
import com.smacon.f2mlibrary.CustomToast;
import com.smacon.fish2marine.Fragment.MyProfileFragment;
import com.smacon.fish2marine.HelperClass.MessageConstants;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.HelperClass.Utilities;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class EditProfileActivity extends AppCompatActivity {
    private Intent intent;
    private String mFirstName,mLastName,mEmail,mMobile,mChecked;
    List<HashMap<String, String>> SQLData_Item ;
    private SqliteHelper helper;
    String CustomerID = "";
    private CustomEditText edt_fname_profile,edt_lname_profile,edt_email_profile,edt_mobile_profile,
            edt_current_password,edt_new_password,edt_new_confirm_password;
    private TextInputLayout txt_input_fname_profile,txt_input_lname_profile,txt_input_email_profile,
            txt_input_mobile_profile,txt_input_current_password,txt_input_new_password,
            txt_input_new_confirm_password;
    ToggleButton mpwdtoggle,mpwdtoggle1,mpwdtoggle2;
    private CustomCheckBox chk_change_password;
    private ImageView mback;
    private Button btn_save;
    private LinearLayout lay_change_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile_edit);
        helper = new SqliteHelper(getApplicationContext(), "Fish2Marine", null, 5);
        intent = getIntent();
        mFirstName = intent.getExtras().getString("FIRSTNAME");
        mLastName=intent.getExtras().getString("LASTNAME");
        mEmail = intent.getExtras().getString("EMAIL");
        mMobile=intent.getExtras().getString("MOBILE");
        mChecked=intent.getExtras().getString("CHECKED");
        SQLData_Item = helper.getadmindetails();
        CustomerID=SQLData_Item.get(0).get("admin_id");
        InitIdView();
    }
    private void InitIdView(){
        edt_fname_profile = (CustomEditText) findViewById(R.id.edt_fname_profile);
        edt_lname_profile = (CustomEditText) findViewById(R.id.edt_lname_profile);
        edt_email_profile = (CustomEditText) findViewById(R.id.edt_email_profile);
        edt_mobile_profile = (CustomEditText) findViewById(R.id.edt_mobile_profile);
        edt_current_password = (CustomEditText) findViewById(R.id.edt_current_password);
        edt_new_password = (CustomEditText) findViewById(R.id.edt_new_password);
        edt_new_confirm_password = (CustomEditText) findViewById(R.id.edt_new_confirm_password);

        txt_input_fname_profile = (TextInputLayout) findViewById(R.id.txt_input_fname_profile);
        txt_input_lname_profile = (TextInputLayout) findViewById(R.id.txt_input_lname_profile);
        txt_input_email_profile = (TextInputLayout) findViewById(R.id.txt_input_email_profile);
        txt_input_mobile_profile = (TextInputLayout) findViewById(R.id.txt_input_mobile_profile);
        txt_input_current_password = (TextInputLayout) findViewById(R.id.txt_input_current_password);
        txt_input_new_password = (TextInputLayout) findViewById(R.id.txt_input_new_password);
        txt_input_new_confirm_password = (TextInputLayout) findViewById(R.id.txt_input_new_confirm_password);

        mpwdtoggle1 = (ToggleButton) findViewById(R.id.mpwdtoggle1);
        mpwdtoggle2 = (ToggleButton) findViewById(R.id.mpwdtoggle2);
        mpwdtoggle = (ToggleButton) findViewById(R.id.mpwdtoggle);

        mback=(ImageView)findViewById(R.id.back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        mpwdtoggle.setChecked(true);
        mpwdtoggle1.setChecked(true);
        mpwdtoggle2.setChecked(true);
        chk_change_password = (CustomCheckBox) findViewById(R.id.chk_change_password);

        btn_save = (Button) findViewById(R.id.btn_save);
        lay_change_password = (LinearLayout) findViewById(R.id.lay_change_password);

        setProfileValues();
        setListeners();
    }
    private void setProfileValues(){

        edt_fname_profile.setText(mFirstName);
        edt_lname_profile.setText(mLastName);
        edt_email_profile.setText(mEmail);
        edt_mobile_profile.setText(mMobile);
        if(mChecked.equals("TRUE")){
            chk_change_password.setChecked(true);
        }
        else {
            chk_change_password.setChecked(false);
        }

    }
    private void setListeners() {

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  processSaveChanges();
            }
        });

        chk_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lay_change_password.getVisibility() == View.VISIBLE) {
                    lay_change_password.setVisibility(View.GONE);
                } else {
                    lay_change_password.setVisibility(View.VISIBLE);
                }

            }
        });

        mpwdtoggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    edt_current_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    edt_current_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }
            }
        });
        mpwdtoggle1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    edt_new_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    edt_new_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }
            }
        });
        mpwdtoggle2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    edt_new_confirm_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    edt_new_confirm_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }
            }
        });
    }

    private void processSaveChanges(){

        String fname = Utilities.getTrimText(edt_fname_profile);
        String lname = Utilities.getTrimText(edt_lname_profile);
        String email = Utilities.getTrimText(edt_email_profile);
        String phone = Utilities.getTrimText(edt_mobile_profile);
        String cur_pass = Utilities.getTrimText(edt_current_password);
        String new_pass = Utilities.getTrimText(edt_new_password);
        String new_confirm_pass = Utilities.getTrimText(edt_new_confirm_password);

        if (fname.length() > 0){

            edt_fname_profile.setError(null);

            if (lname.length() > 0){

                edt_lname_profile.setError(null);

                if (email.length() > 0 && Utilities.isValidEmail(email)){

                    edt_email_profile.setError(null);

                    if (phone.length() > 0 && Utilities.isValidPhone(phone)){

                        edt_mobile_profile.setError(null);

                        if (chk_change_password.isChecked()){

                            if (cur_pass.length() > 0){

                                edt_current_password.setError(null);

                                if (new_pass.length() > 0){
                                    edt_new_password.setError(null);

                                    if (new_confirm_pass.length() > 0){
                                        edt_new_confirm_password.setError(null);

                                        if(new_pass.equals(new_confirm_pass)) {

                                            edt_new_confirm_password.setError(null);

                                            processEditProfile(CustomerID,fname,lname,email,phone,"1",cur_pass,new_pass);

                                        }
                                        else {

                                            edt_new_confirm_password.setError(MessageConstants.PASSWORDS_NOT_MATCHING);
                                            edt_new_confirm_password.requestFocus();
                                        }

                                    }
                                    else {

                                        edt_new_confirm_password.setError(MessageConstants.FILL_THIS_FIELD);
                                        edt_new_confirm_password.requestFocus();
                                    }
                                }
                                else {

                                    edt_new_password.setError(MessageConstants.FILL_THIS_FIELD);
                                    edt_new_password.requestFocus();
                                }

                            }
                            else{
                                edt_current_password.setError(MessageConstants.FILL_THIS_FIELD);
                                edt_current_password.requestFocus();
                            }

                        }
                        else{

                            processEditProfile(CustomerID,fname,lname,email,phone,"2","","");
                        }
                    }
                    else{
                        edt_mobile_profile.setError(MessageConstants.INVALID_PHONE);
                        edt_mobile_profile.requestFocus();
                    }
                }
                else{
                    edt_email_profile.setError(MessageConstants.INVALID_EMAIL);
                    edt_email_profile.requestFocus();
                }
            }
            else{
                edt_lname_profile.setError(MessageConstants.FILL_THIS_FIELD);
                edt_lname_profile.requestFocus();
            }
        }
        else{
            edt_fname_profile.setError(MessageConstants.FILL_THIS_FIELD);
            edt_fname_profile.requestFocus();
        }
    }

    private void processEditProfile(String customerid,String fname,String lname,String email,String mobile,
                                   String resetpassword,String oldpassword,String newpassword){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            EditProfileInitiate mEditProfileInitiate = new EditProfileInitiate(
                    customerid.trim(),
                    fname.trim(),
                    lname.trim(),
                    email.trim(),
                    mobile.trim(),
                    resetpassword.trim(),
                    oldpassword.trim(),
                    newpassword.trim());
            mEditProfileInitiate.execute((Void) null);
        }else {
            CustomToast.error(getApplicationContext(),"No Internet Connection.").show();
        }
    }

    public class EditProfileInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mcustomerid,mfirstname,mlastname,memail,
                mmobile,mreset,moldpass,mnewpass;


        EditProfileInitiate(String customerid,String fname,String lname,String email,String mobile,
                            String resetpassword,String oldpassword,String newpassword) {

            mcustomerid=customerid;
            mfirstname = fname;
            mlastname = lname;
            memail = email;
            mmobile = mobile;
            mreset = resetpassword;
            moldpass = oldpassword;
            mnewpass = newpassword;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

          /*  indicator.setVisibility(View.VISIBLE);
            btn_add.setVisibility(View.INVISIBLE);*/
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doEditProfile(mcustomerid,mfirstname, mlastname,
                    memail,mmobile,mreset,moldpass,mnewpass);
            Log.d("111111111","PASSING: id: "+mcustomerid+" fname: "+mfirstname+" lname: "+mlastname+
                    " email: "+memail+" mobile: "+mmobile+" reset: "+mreset+" old: "+moldpass+" new: "+mnewpass);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
           /* indicator.setVisibility(View.GONE);
            btn_add.setVisibility(View.VISIBLE);*/
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                Log.d("111111111",result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(2))) {
                        //mEmail.setText("");
                         CustomToast.error(getApplicationContext(),jsonObj.getString("message")).show();
                    }else if (jsonObj.getString("status").equals(String.valueOf(1))) {
                        back();
                        CustomToast.success(getApplicationContext(),"Updated succesfully").show();

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
    public void back(){
       /* Intent intent = new Intent(getApplicationContext(),NavigationDrawerActivity.class);
        intent.putExtra("PROFILE","");
        startActivity(intent);*/
      // NavigationDrawerActivity.getInstance().onItemSelected(5);
    }

}