package com.smacon.fish2marine;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.smacon.f2mlibrary.CustomToast;
import com.smacon.f2mlibrary.Edittext.MaterialEditText;
import com.smacon.f2mlibrary.Progress.AVLoadingIndicatorView;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialEditText mFirstname,mLastname,mMobile,mEmail,mNewPassword,mConfirmpassword;
    private Button mRegister;
    private ImageView mClose;
    private AVLoadingIndicatorView loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        InitIdView();
    }

    private void InitIdView(){
        mFirstname = (MaterialEditText)findViewById(R.id.firstname);
        mLastname = (MaterialEditText)findViewById(R.id.lastname);
        mMobile = (MaterialEditText)findViewById(R.id.mobile);
        mEmail = (MaterialEditText)findViewById(R.id.email);
        mNewPassword = (MaterialEditText)findViewById(R.id.newpassword);
        mConfirmpassword = (MaterialEditText)findViewById(R.id.confirmpassword);
        mRegister = (Button) findViewById(R.id.register);
        mClose = (ImageView) findViewById(R.id.close);
        loading = (AVLoadingIndicatorView) findViewById(R.id.indicator);
        loading.setVisibility(View.GONE);
        mRegister.setOnClickListener(this);
        mClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int buttonId=v.getId();
        switch (buttonId){
            case R.id.close:
                if (loading.getVisibility() == View.VISIBLE) {
                    CustomToast.info(getApplicationContext(), "Please wait while we process your request").show();
                } else {
                    //close
                    onBackPressed();
                }
                break;
            case R.id.register:
                if (loading.getVisibility() == View.VISIBLE) {
                    CustomToast.info(getApplicationContext(), "Please wait while we process your request").show();
                } else {
                    if (mFirstname.getText().toString().trim().equals("")) {

                        mFirstname.setError("Field cannot be blank");
                    } else if (mLastname.getText().toString().trim().equals("")) {

                        mLastname.setError("Field cannot be blank");
                    } else if (mMobile.getText().toString().trim().equals("")) {

                        mMobile.setError("Field cannot be blank");
                    } else if (mEmail.getText().toString().trim().equals("")) {

                        mEmail.setError("Field cannot be blank");
                    } else if (mNewPassword.getText().toString().trim().equals("")) {

                        mNewPassword.setError("Field cannot be blank");
                    } else if (mConfirmpassword.getText().toString().trim().equals("")) {
                        mConfirmpassword.setError("Field cannot be blank");

                    } else if (!mConfirmpassword.getText().toString().trim().equals(mNewPassword.getText().toString().trim())) {
                        mConfirmpassword.setError("Password do not match");
                    } else {
                        UserRegistration();
                    }
                }
                break;

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUpActivity.this,ForgotPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    private void UserRegistration(){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            LoadUserRegistrationInitiate mLoadUserRegistrationInitiate = new LoadUserRegistrationInitiate(
                    mFirstname.getText().toString().trim(),
                    mLastname.getText().toString().trim(),
                    mMobile.getText().toString().trim(),
                    mEmail.getText().toString().trim(),
                    mNewPassword.getText().toString().trim());
            mLoadUserRegistrationInitiate.execute((Void) null);
        }else {
            CustomToast.error(getApplicationContext(),"No Internet Connection.").show();
        }
    }
    public class LoadUserRegistrationInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mfirstname,mlastname,mmobile,memail,mnewpassword;

        LoadUserRegistrationInitiate(String firstname, String lastname, String mobile,String email,String password) {
            mfirstname = firstname;
            mlastname = lastname;
            mmobile = mobile;
            memail = email;
            mnewpassword = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
            mRegister.setVisibility(View.INVISIBLE);
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doUserRegistration(mfirstname, mlastname, mmobile,memail,mnewpassword);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            loading.setVisibility(View.GONE);
            mRegister.setVisibility(View.VISIBLE);
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                Log.d("111111111",result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(3))) {
                        mEmail.setText("");
                        CustomToast.error(getApplicationContext(),jsonObj.getString("message")).show();
                    }else if (jsonObj.getString("status").equals(String.valueOf(2))) {
                        mNewPassword.setText("");
                        mConfirmpassword.setText("");
                        mNewPassword.setError(jsonObj.getString("message"));
                        //CustomToast.error(getApplicationContext(),jsonObj.getString("message")).show();
                    } else if (jsonObj.getString("status").equals(String.valueOf(1))) {
                        mFirstname.setText("");
                        mLastname.setText("");
                        mMobile.setText("");
                        mEmail.setText("");
                        mNewPassword.setText("");
                        mConfirmpassword.setText("");
                        CustomToast.success(getApplicationContext(),"You have successfully registered").show();
                        onBackPressed();
                        /*Intent intent = new Intent(SignUpActivity.this,EditProfileActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in,
                                R.anim.bottom_down);
                        finish();*/
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
}