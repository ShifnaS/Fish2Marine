package com.smacon.fish2marine;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.smacon.f2mlibrary.CheckBox;
import com.smacon.f2mlibrary.CustomToast;
import com.smacon.f2mlibrary.Edittext.MaterialEditText;
import com.smacon.f2mlibrary.Progress.AVLoadingIndicatorView;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aiswarya on 13/11/17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,TextWatcher,Animation.AnimationListener {
    private SharedPreferences sPreferences;
    private SqliteHelper helper;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String PREF_NAME = "prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASS = "password";
    MaterialEditText mUsename, mPassword;
    TextView mSignup,mForgotPassword;
    CheckBox mRemember;
    Button mButtonLogin;
    ToggleButton mpwdtoggle;
    ImageView login_gif;
    AVLoadingIndicatorView indicator;
    private Config mConfig;
    Handler mHandler;
    private ProgressDialog progress;
    //google
    private static final int RC_SIGN_IN = 234; //a constant for detecting the login intent result
    private static final String TAG = "1111111"; //Tag for the logs optional
    GoogleSignInClient mGoogleSignInClient; //creating a GoogleSignInClient object
    FirebaseAuth mAuth;     //And also a Firebase Auth object

    //facebook
    private LoginButton loginButton;
    private CallbackManager callbackManager,mCallbackManager;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        helper = new SqliteHelper(getApplicationContext(), "Fish2Marine", null, 5);
        sPreferences = getSharedPreferences("Fish2Marine", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mHandler = new Handler();
        mConfig = new Config(getApplicationContext());
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(getApplicationContext(),"Cancel",Toast.LENGTH_SHORT).show();
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                // ...
            }
        });

        //first we intialized the FirebaseAuth object
        mAuth = FirebaseAuth.getInstance();

        //Then we need a GoogleSignInOptions object
        //And we need to build it as below
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Now we will attach a click listener to the sign_in_button
        //and inside onClick() method we are calling the signIn() method that will open
        //google sign in intent
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

       /* if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
        }*/
        InitIdView();
    }
    private void InitIdView(){

        login_gif=(ImageView)findViewById(R.id.login_gif);
        Glide.with(getApplicationContext())
                .load(R.drawable.login_gif_background)
                .into(login_gif);
        mUsename = (MaterialEditText) findViewById(R.id.username);
        mPassword = (MaterialEditText) findViewById(R.id.password);
        mRemember = (CheckBox) findViewById(R.id.rememberpwd);
        indicator = (AVLoadingIndicatorView) findViewById(R.id.indicator);
        indicator.setVisibility(View.INVISIBLE);
        mButtonLogin = (Button) findViewById(R.id.buttonLogin);
        mSignup=(TextView)findViewById(R.id.signup);
        mForgotPassword=(TextView)findViewById(R.id.forgotpassword);
        mpwdtoggle=(ToggleButton)findViewById(R.id.mpwdtoggle);

        mpwdtoggle.setChecked(true);
        mButtonLogin.setOnClickListener(this);



        if(sharedPreferences.getBoolean(KEY_REMEMBER, false))
            mRemember.setChecked(true);
        else
            mRemember.setChecked(false);

        mUsename.setText(sharedPreferences.getString(KEY_USERNAME,""));
        mPassword.setText(sharedPreferences.getString(KEY_PASS,""));

        mUsename.addTextChangedListener(this);
        mPassword.addTextChangedListener(this);
        mRemember.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onChange(boolean checked) {
                managePrefs();
            }
        });
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                LoginActivity.this.startActivity(intent);
                finish();
            }
        });
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                LoginActivity.this.startActivity(intent);
                finish();
            }
        });

        mpwdtoggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else{
                    mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }
            }
        });

    }

   /* //facebook
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }*/

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showProgress();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String fullname=user.getDisplayName();
                            String[] nameparts = fullname.split("\\s+");
                            String firstname = nameparts[0];
                            String lastname = nameparts[1];

                            if (mConfig.isOnline(LoginActivity.this)) {
                                UserSocialLoginTask userSocialLoginTask = new UserSocialLoginTask(user.getEmail(),"",firstname,lastname,user.getUid(),"facebook");
                                userSocialLoginTask.execute((Void) null);
                            } else {

                                CustomToast.error(getApplicationContext(),"No Internet Connection.").show();
                            }

                            Toast.makeText(LoginActivity.this, "User Signed In"+user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            //  startActivity(new Intent(LoginActivity.this, GoogleLoginProfileActivity.class));
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showProgress();
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            String fullname=user.getDisplayName();
                            String[] nameparts = fullname.split("\\s+");
                            String firstname = nameparts[0];
                            String lastname = nameparts[1];

                            if (mConfig.isOnline(LoginActivity.this)) {
                                UserSocialLoginTask userSocialLoginTask = new UserSocialLoginTask(user.getEmail(),"",firstname,lastname,user.getUid(),"google");
                                userSocialLoginTask.execute((Void) null);
                            } else {

                                CustomToast.error(getApplicationContext(),"No Internet Connection.").show();
                            }

                            Toast.makeText(LoginActivity.this, "User Signed In"+user.getDisplayName(), Toast.LENGTH_SHORT).show();
                          //  startActivity(new Intent(LoginActivity.this, GoogleLoginProfileActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            CustomToast.error(LoginActivity.this, "Authentication failed.").show();

                        }

                    }
                });
    }

    //this method is called on click
    private void signIn() {
        //getting the google signin intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void showProgress(){
        progress=new ProgressDialog(LoginActivity.this);
        progress.setMessage("Loading..");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
    }

    @Override
    public void onClick(View v) {
        int mId = v.getId();
        switch (mId) {
            case R.id.buttonLogin:
                buttonGone();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(mUsename.getText().toString().trim().equals("")) {

                            buttonVisible();
                            mUsename.setError("Field cannot be blank");

                        }else if(mUsename.getText().toString().trim().length() < 3) {

                            buttonVisible();
                            mUsename.setError("Please enter valid username");

                        } else if(mPassword.getText().toString().trim().equals("")) {

                            buttonVisible();
                            mPassword.setError("Field cannot be blank");

                        } else if(mPassword.getText().toString().trim().length() < 2) {

                            buttonVisible();
                            mPassword.setError("Please enter valid password");

                        } else {

                            if (mConfig.isOnline(LoginActivity.this)) {
                                UserLoginTask userLoginTask = new UserLoginTask(mUsename.getText().toString().trim(),
                                        mPassword.getText().toString().trim());
                                userLoginTask.execute((Void) null);
                            } else {

                                buttonVisible();
                                CustomToast.error(getApplicationContext(),"No Internet Connection.").show();
                            }
                        }
                    }
                }, 200);
                break;
        }
    }

    public class UserSocialLoginTask extends AsyncTask<Void, String, String> {

        JSONObject jsondata;
        private String mEmail,mMobile,mFirstname,mLastname,mUserid,mMode;

        UserSocialLoginTask(String email, String mobile,String firstname, String lastmane,
                            String userid, String mode) {
            mEmail = email;
            mMobile = mobile;
            mFirstname = firstname;
            mLastname = lastmane;
            mUserid = userid;
            mMode = mode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            helper.Delete_admin_details();
        }

        @Override
        protected String doInBackground(Void... params) {

            String Status = "";
            HttpOperations httpOperations = new HttpOperations(getBaseContext());
            StringBuilder result = httpOperations.doSocialLogin(mEmail,mMobile,mFirstname,mLastname,mUserid,mMode);
            Log.d("111111", "API__SOCIAL_LOGIN " + result);
            try {

                JSONObject jsonObj = new JSONObject(result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(2))) {
                        Status = "404";
                    }else if (jsonObj.getString("status").equals(String.valueOf(1))) {
                        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
                        JSONObject jsonObj1 = jsonObj.getJSONObject("data");
                        for (int i = 0; i < jsonObj1.length(); i++) {

                            HashMap<String, String> map;
                            map = new HashMap<String, String>();
                            map.put("customerId", jsonObj1.getString("customerId").trim());
                            map.put("customerName", jsonObj1.getString("customerName").trim());
                            map.put("mobile", jsonObj1.getString("mobile").trim());
                            map.put("email", jsonObj1.getString("email").trim());
                            fillMaps.add(map);

                        }
                        helper.Insert_admin_details(fillMaps);
                        final List<HashMap<String, String>> Data_Item;
                        Data_Item = helper.getadmindetails();
                        mConfig.savePreferences(getApplicationContext(),"ID",Data_Item.get(0).get("admin_id"));
                        mConfig.savePreferences(getApplicationContext(),"NAME",Data_Item.get(0).get("admin_name"));
                        Log.d("1111111",Data_Item.get(0).get("admin_id"));
                        Log.d("1111111",Data_Item.get(0).get("admin_name"));
                        Status="200";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Status = "400";
            }catch (NullPointerException e) {
                e.printStackTrace();
                Status = "401";
            }catch (Exception e) {
                e.printStackTrace();
                Status = "400";
            }
            return Status;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result.equals("404")){
                progress.dismiss();
                CustomToast.error(getApplicationContext(),"Invalid Username or Password").show();
                //buttonVisible();
            }else if(result.equals("401")){
                //buttonVisible();
                progress.dismiss();
                CustomToast.error(getApplicationContext(),"No Internet Connection.").show();
            }else if(result.equals("400")){
                //buttonVisible();
                progress.dismiss();
                CustomToast.info(getApplicationContext(),"Please try again").show();
            }else if(result.equals("200")){
                progress.dismiss();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences.Editor editor = sPreferences.edit();
                                editor.remove("DeliveryCenter_ID");
                                editor.remove("CartCount");
                                editor.remove("Location");
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this,NavigationDrawerActivity.class);
                                intent.putExtra("PAGE","LOGIN");
                                startActivity(intent);
                                /*overridePendingTransition(R.anim.bottom_up,
                                        android.R.anim.fade_out);*/
                                finish();
                            }
                        }, 1000);
                    }
                }, 1000);
            }
        }
    }
    
    public class UserLoginTask extends AsyncTask<Void, String, String> {

        JSONObject jsondata;
        private String mUname, mPwd;

        UserLoginTask(String username, String password) {
            mUname = username;
            mPwd = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mConfig.savePreferences(getApplicationContext(),"signinpwd",
                    mPwd.toString().trim());
            mConfig.savePreferences(getApplicationContext(),"signinuname",
                    mUname.toString().trim());

            helper.Delete_admin_details();
        }

        @Override
        protected String doInBackground(Void... params) {

            String Status = "";
            HttpOperations httpOperations = new HttpOperations(getBaseContext());
            StringBuilder result = httpOperations.doLogin(mUname, mPwd);
            Log.d("111111", "API_LOGIN_RESPONSE " + result);
            try {

                JSONObject jsonObj = new JSONObject(result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(2))) {
                        Status = "404";
                    }else if (jsonObj.getString("status").equals(String.valueOf(1))) {
                        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
                        JSONObject jsonObj1 = jsonObj.getJSONObject("data");
                        for (int i = 0; i < jsonObj1.length(); i++) {

                            HashMap<String, String> map;
                            map = new HashMap<String, String>();
                            map.put("customerId", jsonObj1.getString("customerId").trim());
                            map.put("customerName", jsonObj1.getString("customerName").trim());
                            map.put("mobile", jsonObj1.getString("mobile").trim());
                            map.put("email", jsonObj1.getString("email").trim());
                            fillMaps.add(map);

                        }
                        helper.Insert_admin_details(fillMaps);
                        final List<HashMap<String, String>> Data_Item;
                        Data_Item = helper.getadmindetails();
                        mConfig.savePreferences(getApplicationContext(),"ID",Data_Item.get(0).get("admin_id"));
                        mConfig.savePreferences(getApplicationContext(),"NAME",Data_Item.get(0).get("admin_name"));
                        Log.d("1111111",Data_Item.get(0).get("admin_id"));
                        Log.d("1111111",Data_Item.get(0).get("admin_name"));
                        Status="200";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Status = "400";
            }catch (NullPointerException e) {
                e.printStackTrace();
                Status = "401";
            }catch (Exception e) {
                e.printStackTrace();
                Status = "400";
            }
            return Status;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("404")){
                CustomToast.error(getApplicationContext(),"Invalid Username or Password").show();
                buttonVisible();
            }else if(result.equals("401")){
                buttonVisible();
                CustomToast.error(getApplicationContext(),"No Internet Connection.").show();
            }else if(result.equals("400")){
                buttonVisible();
                CustomToast.info(getApplicationContext(),"Please try again").show();
            }else if(result.equals("200")){
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences.Editor editor = sPreferences.edit();
                                editor.remove("DeliveryCenter_ID");
                                editor.remove("CartCount");
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this,NavigationDrawerActivity.class);
                                intent.putExtra("PAGE","HOME");
                                startActivity(intent);
                                /*overridePendingTransition(R.anim.bottom_up,
                                        android.R.anim.fade_out);*/

                                finish();
                            }
                        }, 1000);
                    }
                }, 1000);
            }
        }
    }

    private void managePrefs(){
        if(mRemember.isChecked()){
            editor.putString(KEY_USERNAME, mUsename.getText().toString().trim());
            editor.putString(KEY_PASS, mPassword.getText().toString().trim());
            editor.putBoolean(KEY_REMEMBER, true);
            editor.apply();
        }else{
            editor.putBoolean(KEY_REMEMBER, false);
            editor.remove(KEY_PASS);//editor.putString(KEY_PASS,"");
            editor.remove(KEY_USERNAME);//editor.putString(KEY_USERNAME, "");
            editor.apply();
        }
    }
    private void buttonVisible(){
        Animation animFadeIn;
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                android.R.anim.fade_in);
        animFadeIn.setAnimationListener(this);

        mButtonLogin.setVisibility(View.VISIBLE);
        mButtonLogin.startAnimation(animFadeIn);
        mButtonLogin.setClickable(true);
        indicator.setVisibility(View.INVISIBLE);

    }

    private void buttonGone(){

        Animation animFadeIn;
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                android.R.anim.fade_in);
        animFadeIn.setAnimationListener(this);

        indicator.setVisibility(View.VISIBLE);
        indicator.startAnimation(animFadeIn);

        mButtonLogin.setClickable(false);
        mButtonLogin.setVisibility(View.GONE);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}

