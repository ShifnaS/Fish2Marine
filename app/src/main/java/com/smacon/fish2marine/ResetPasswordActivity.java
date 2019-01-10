package com.smacon.fish2marine;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.smacon.f2mlibrary.CustomEditText;
import com.smacon.f2mlibrary.CustomToast;
import com.smacon.f2mlibrary.Edittext.MaterialEditText;
import com.smacon.f2mlibrary.Progress.AVLoadingIndicatorView;
import com.smacon.fish2marine.HelperClass.Utilities;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialEditText otp,newpassword,confirmpassword;
    private Button setpassword;
    TextView resendotp,message;
    private ImageView back;
    Dialog dialog;
    AVLoadingIndicatorView loading;
    private Intent intent;
    private String CustomerID="",Email="",Mobile="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_password_reset);
        intent = getIntent();
        CustomerID=intent.getExtras().getString("CUSTOMER_ID");
        Email=intent.getExtras().getString("EMAIL");
        Mobile=intent.getExtras().getString("MOBILE");
        InitIdView();
    }

    private void InitIdView(){

        otp = (MaterialEditText)findViewById(R.id.otp);
        newpassword = (MaterialEditText)findViewById(R.id.newpassword);
        confirmpassword = (MaterialEditText)findViewById(R.id.confirmpassword);
        message = (TextView) findViewById(R.id.message);
        message.setText("We have sent an OTP to your registered mobile number"+ Mobile);

        setpassword = (Button) findViewById(R.id.setpassword);
        resendotp = (TextView)findViewById(R.id.resendotp);
        back = (ImageView) findViewById(R.id.back);
        dialog = new Dialog(ResetPasswordActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_progress);
        dialog.setCanceledOnTouchOutside(false);
        loading = (AVLoadingIndicatorView) dialog.findViewById(R.id.loading);
        setpassword.setOnClickListener(this);
        back.setOnClickListener(this);
        resendotp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int buttonId=v.getId();
        switch (buttonId){
            case R.id.back:
                onBackPressed();
                break;
            case R.id.setpassword:
                if (loading.getVisibility() == View.VISIBLE) {
                    CustomToast.info(getApplicationContext(), "Please wait while we process your request").show();
                } else {
                    if (otp.getText().toString().trim().equals("")) {
                        otp.setError("Enter OTP");
                    } else if (newpassword.getText().toString().trim().equals("")) {
                        newpassword.setError("Field cannot be blank");
                    } else if (confirmpassword.getText().toString().trim().equals("")) {
                        confirmpassword.setError("Field cannot be blank");
                    } else if (!confirmpassword.getText().toString().trim().equals(newpassword.getText().toString().trim())) {
                        confirmpassword.setError("Password do not match");
                    } else {
                        ResetPassword();
                    }
                }
                break;
            case R.id.resendotp:
                ResendOTP("mobile");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ResetPasswordActivity.this,ForgotPasswordActivity.class);
        startActivity(intent);
        finish();
    }
    private void ResetPassword(){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            LoadResetPasswordInitiate mLoadResetPasswordInitiate = new LoadResetPasswordInitiate(
                    Email,
                    CustomerID,
                    otp.getText().toString().trim(),
                    newpassword.getText().toString().trim());
            mLoadResetPasswordInitiate.execute((Void) null);
        }else {
            CustomToast.error(getApplicationContext(),"No Internet Connection.").show();
        }
    }

    public class LoadResetPasswordInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mEmail,mCustomerid,mOtp,mPassword;

        LoadResetPasswordInitiate(String email, String customerid,String otp,String password) {
            mEmail = email;
            mCustomerid = customerid;
            mOtp=otp;
            mPassword=password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
            loading.setVisibility(View.VISIBLE);
            //mRegister.setVisibility(View.INVISIBLE);
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doResetpassword(mEmail, mCustomerid,mOtp,mPassword);
            Log.d("111111111","FORGOT PASS REQUEST: "+mEmail+" "+mCustomerid+" "+mOtp+" "+mPassword);
            Log.d("111111111","FORGOT PASS RESULT: "+result);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);

            // mRegister.setVisibility(View.VISIBLE);
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(2))) {
                        dialog.dismiss();
                        CustomToast.error(getApplicationContext(),jsonObj.getString("message")).show();
                    }else if (jsonObj.getString("status").equals(String.valueOf(1))) {
                        dialog.dismiss();
                        CustomToast.success(getApplicationContext(),"Your Password Changed").show();
                        Intent intent = new Intent(ResetPasswordActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
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
    private void ResendOTP(String type){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            LoadResendOTPInitiate mLoadResendOTPInitiate = new LoadResendOTPInitiate(
                    Mobile,type);
            mLoadResendOTPInitiate.execute((Void) null);
        }else {
            CustomToast.error(getApplicationContext(),"No Internet Connection.").show();
        }
    }

    public class LoadResendOTPInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mMobile,mtype;

        LoadResendOTPInitiate(String mobile, String type) {
            mMobile = mobile;
            mtype = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
            loading.setVisibility(View.VISIBLE);
            //mRegister.setVisibility(View.INVISIBLE);
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getApplicationContext());
            StringBuilder result = httpOperations.doResendOTP(CustomerID,mMobile, mtype);
            Log.d("111111111","FORGOT PASS REQUEST: "+mMobile+" "+mtype);
            Log.d("111111111","FORGOT PASS RESULT: "+result);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(2))) {
                        dialog.dismiss();
                        CustomToast.info(getApplicationContext(),jsonObj.getString("message")).show();
                    }else if (jsonObj.getString("status").equals(String.valueOf(1))) {
                        dialog.dismiss();
                        CustomToast.success(getApplicationContext(),"OTP sent to your mobile").show();
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