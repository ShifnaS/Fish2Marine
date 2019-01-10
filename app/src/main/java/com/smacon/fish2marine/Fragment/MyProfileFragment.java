package com.smacon.fish2marine.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smacon.f2mlibrary.Button.FloatingActionButton;
import com.smacon.f2mlibrary.Switcher.Switcher;
import com.smacon.fish2marine.EditProfileActivity;
import com.smacon.fish2marine.HelperClass.AllListItem;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.R;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aiswarya on 09/05/2018.
 */

public class MyProfileFragment extends Fragment implements View.OnClickListener{

    private SharedPreferences sPreferences;
    private SqliteHelper helper;
    private Config mConfig;
    List<HashMap<String, String>> SQLData_Item ;
    String CustomerID = "";
    private Switcher switcher;
    private TextView error_label_retry, empty_label_retry,name,firstname,lastname,email,mobile,address,mobile_heading,company;
    LinearLayout sub_layout;
    FloatingActionButton fab_edit;
    ArrayList<AllListItem> dataItem;
    int start = 0;
    private static MyProfileFragment profilefragment;
    public static final MyProfileFragment getInstance(){
        return profilefragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.myprofile_fragment, container, false);
        helper = new SqliteHelper(getActivity(), "Fish2Marine", null, 5);
        sPreferences = getActivity().getSharedPreferences("Fish2Marine", getActivity().MODE_PRIVATE);
        profilefragment = this;
        InitIdView(rootView);
        return rootView;
    }

    private void InitIdView(View rootView){
        switcher = new Switcher.Builder(getActivity())
                .addContentView(rootView.findViewById(R.id.main_layout))
                .addErrorView(rootView.findViewById(R.id.error_view))
                .addProgressView(rootView.findViewById(R.id.progress_view))
                .setErrorLabel((TextView) rootView.findViewById(R.id.error_label))
                .setEmptyLabel((TextView) rootView.findViewById(R.id.empty_label))
                .addEmptyView(rootView.findViewById(R.id.empty_view))
                .build();
        sub_layout=((LinearLayout)rootView.findViewById(R.id.sub_layout));

        SQLData_Item = helper.getadmindetails();
        CustomerID=SQLData_Item.get(0).get("admin_id");

        error_label_retry = ((TextView) rootView.findViewById(R.id.error_label_retry));
        empty_label_retry = ((TextView)rootView.findViewById(R.id.empty_label_retry));

        firstname = ((TextView) rootView.findViewById(R.id.firstname));
        lastname = ((TextView) rootView.findViewById(R.id.lastname));
        name = ((TextView) rootView.findViewById(R.id.name));
        email = ((TextView)rootView.findViewById(R.id.email));
        mobile = ((TextView) rootView.findViewById(R.id.mobile));
        fab_edit=((FloatingActionButton)rootView.findViewById(R.id.fab_edit));
        mobile_heading = ((TextView) rootView.findViewById(R.id.mobile_heading));
        company = ((TextView)rootView.findViewById(R.id.company));
        address = ((TextView)rootView.findViewById(R.id.address));
        error_label_retry.setOnClickListener(this);
        empty_label_retry.setOnClickListener(this);
        fab_edit.setOnClickListener(this);
        dataItem = new ArrayList<>();
        InitGetData();
    }

    private void InitGetData(){
        Config mConfig = new Config(getActivity());
        if(mConfig.isOnline(getActivity())){
            LoadTypeListInitiate mLoadTypeListInitiate = new LoadTypeListInitiate(CustomerID);
            mLoadTypeListInitiate.execute((Void) null);
        }else {
            switcher.showErrorView("No Internet Connection");
        }
    }

    @Override
    public void onClick(View view) {

        int buttonId = view.getId();
        switch (buttonId){

            case R.id.error_label_retry:
                InitGetData();
                break;
            case R.id.empty_label_retry:
                InitGetData();
                break;
            case R.id.fab_edit:
                Intent intent = new Intent(getActivity(),EditProfileActivity.class);
                intent.putExtra("FIRSTNAME",firstname.getText().toString());
                intent.putExtra("LASTNAME",lastname.getText().toString());
                intent.putExtra("EMAIL",email.getText().toString());
                intent.putExtra("MOBILE",mobile.getText().toString());
                intent.putExtra("CHECKED","FALSE");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.bottom_up,
                        android.R.anim.fade_out);
                getActivity().finish();
                break;
        }
    }

    public class LoadTypeListInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mId;
        LoadTypeListInitiate(String id) {
            mId = id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switcher.showProgressView();
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {
            HttpOperations httpOperations = new HttpOperations(getActivity());
            StringBuilder result = httpOperations.doMyProfile(mId);
            Log.d("111111111","PASSING VALUE: CUSTOMER ID "+mId);
            Log.d("111111111", "RESULT "+result);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(1))) {
                        switcher.showContentView();
                        sub_layout.setVisibility(View.VISIBLE);
                        fab_edit.setVisibility(View.VISIBLE);
                        JSONObject jsonObj1 = jsonObj.getJSONObject("data");
                        name.setText(jsonObj1.getString("firstname").toString()+" "+jsonObj1.getString("lastname").toString());
                        firstname.setText(jsonObj1.getString("firstname").toString());
                        lastname.setText(jsonObj1.getString("lastname").toString());
                        email.setText(jsonObj1.getString("email").toString());
                        if(jsonObj1.getString("mobile").toString().equals("")||jsonObj1.getString("mobile").toString().equals("null")){
                            mobile.setText("");
                            mobile_heading.setVisibility(View.GONE);
                            mobile.setVisibility(View.GONE);
                        }
                        else {
                            mobile.setText(jsonObj1.getString("mobile").toString());
                        }

                        if (jsonObj1.has("defaultbilling")) {
                            Log.d("11111111","here");
                            if(!jsonObj1.getString("defaultbilling").equals("")) {
                                Log.d("11111111","here1");
                                JSONObject jsonObj2 = jsonObj1.getJSONObject("defaultbilling");
                                if (jsonObj2.length() != 0) {
                                    if (jsonObj2.getString("company").toString().equals("") || jsonObj2.getString("company").toString().equals("null")) {
                                        company.setVisibility(View.GONE);
                                    } else {
                                        company.setText(jsonObj2.getString("company").toString());
                                    }
                                    Log.d("111111111", "RESULT " + jsonObj2.length());

                                    address.setText(String.format("%s, %s \n%s \n%s, %s \n%s",
                                            jsonObj2.getString("street1").toString(), jsonObj2.getString("street2").toString(),
                                            jsonObj2.getString("city").toString(),
                                            jsonObj2.getString("state").toString(), jsonObj2.getString("postcode").toString(),
                                            jsonObj2.getString("country").toString()));
                                } else {
                                    address.setText("No Address");
                                }

                            }
                            else {
                                Log.d("11111111","here2");
                                address.setText("No Address");
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

    public boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
