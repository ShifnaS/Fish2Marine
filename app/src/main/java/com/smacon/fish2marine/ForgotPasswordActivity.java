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
import android.widget.EditText;
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

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private CustomEditText email_mobile;
    private TextView submit;
    private ImageView close;
    Dialog dialog;
    AVLoadingIndicatorView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        InitIdView();
    }

    private void InitIdView(){

        email_mobile = (CustomEditText)findViewById(R.id.email_mobile);
        submit = (TextView) findViewById(R.id.submit);
        close = (ImageView) findViewById(R.id.close);
        dialog = new Dialog(ForgotPasswordActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_progress);
        dialog.setCanceledOnTouchOutside(false);
        loading = (AVLoadingIndicatorView) dialog.findViewById(R.id.loading);
        submit.setOnClickListener(this);
        close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int buttonId=v.getId();
        switch (buttonId){
            case R.id.close:
                //close
                Close_view();
                break;
            case R.id.submit:
                String phoneoremail = Utilities.getTrimText(email_mobile);
                if (loading.getVisibility() == View.VISIBLE) {
                    CustomToast.info(getApplicationContext(), "Please wait while we process your request").show();
                } else {
                    if (email_mobile.getText().toString().trim().equals("")) {
                        CustomToast.error(getApplicationContext(),"Field cannot be blank").show();
                    }
                    else {
                        if (phoneoremail.length() > 0 && Utilities.isValidPhone(phoneoremail)){
                            ForgotPassword("mobile");
                        }
                        else if(phoneoremail.length() > 0 && Utilities.isValidEmail(phoneoremail)){
                            ForgotPassword("email");
                        }
                        else {
                            CustomToast.error(getApplicationContext(),"Enter a valid Email/Mobile").show();
                        }

                    }
                }
                break;

        }
    }
    private void Close_view(){
        Intent intent = new Intent(ForgotPasswordActivity.this,LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,
                R.anim.bottom_down);
        finish();
    }
    private void ForgotPassword(String type){
        Config mConfig = new Config(getApplicationContext());
        if(mConfig.isOnline(getApplicationContext())){
            LoadForgotPasswordInitiate mLoadForgotPasswordInitiate = new LoadForgotPasswordInitiate(
                    email_mobile.getText().toString().trim(),type);
            mLoadForgotPasswordInitiate.execute((Void) null);
        }else {
            CustomToast.error(getApplicationContext(),"No Internet Connection.").show();
        }
    }
    public class LoadForgotPasswordInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mEmailMobile,mtype;

        LoadForgotPasswordInitiate(String emailORmobile, String type) {
            mEmailMobile = emailORmobile;
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
            StringBuilder result = httpOperations.doForgotpassword(mEmailMobile, mtype);
            Log.d("111111111","FORGOT PASS REQUEST: "+mEmailMobile+" "+mtype);
            Log.d("111111111","FORGOT PASS RESULT: "+result);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            loading.setVisibility(View.GONE);
            // mRegister.setVisibility(View.VISIBLE);
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(2))) {
                        email_mobile.setText("");
                        dialog.dismiss();
                        CustomToast.info(getApplicationContext(),"Invalid Email/Mobile").show();
                    }else if (jsonObj.getString("status").equals(String.valueOf(3))) {
                        dialog.dismiss();
                        CustomToast.success(getApplicationContext(),"The link to reset your password has been sent to your email").show();
                        Intent intent = new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in,
                                R.anim.bottom_down);
                        finish();
                    }
                    else if(jsonObj.getString("status").equals(String.valueOf(1))){
                        if (jsonObj.has("data")){
                            JSONObject jsonObj1 = jsonObj.getJSONObject("data");
                            String customerid=jsonObj1.getString("customerid");
                            String email=jsonObj1.getString("email");
                            String mobile=jsonObj1.getString("mobile");
                            Log.d("111111111",email+customerid+mobile);
                            dialog.dismiss();
                            CustomToast.success(getApplicationContext(),"OTP send").show();
                            Intent intent = new Intent(ForgotPasswordActivity.this,ResetPasswordActivity.class);
                            intent.putExtra("CUSTOMER_ID",customerid);
                            intent.putExtra("EMAIL",email);
                            intent.putExtra("MOBILE",mobile);
                            startActivity(intent);
                            finish();
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
}