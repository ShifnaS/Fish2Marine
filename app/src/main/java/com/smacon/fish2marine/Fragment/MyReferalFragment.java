package com.smacon.fish2marine.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smacon.f2mlibrary.Button.FloatingActionButton;
import com.smacon.f2mlibrary.CustomEditText;
import com.smacon.f2mlibrary.CustomToast;
import com.smacon.f2mlibrary.Progress.AVLoadingIndicatorView;
import com.smacon.f2mlibrary.Switcher.Switcher;
import com.smacon.fish2marine.AdapterClass.MyReferralsAdapter;
import com.smacon.fish2marine.AdapterClass.MyRewardsAdapter;
import com.smacon.fish2marine.HelperClass.AllListItem;
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

/**
 * Created by Aiswarya on 09/05/2018.
 */

public class MyReferalFragment extends Fragment implements View.OnClickListener{

    private SharedPreferences sPreferences;
    private SqliteHelper helper;
    private Config mConfig;
    List<HashMap<String, String>> SQLData_Item ;
    String CustomerID = "";
    private Switcher switcher;
    private RecyclerView mrecyclerview;
    private TextView error_label_retry, empty_label_retry,referral_link,txt_share;
    LinearLayout sub_layout,referrallistheading;
    GridLayoutManager mLayoutManager;
    MyReferralsAdapter myReferralsAdapter;
    FloatingActionButton invite;
    AVLoadingIndicatorView indicator;
    BottomSheetDialog mBottomSheetDialog;
    Button send;
    FrameLayout container;
    CardView listcardview;
    ArrayList<AllListItem> dataItem;
    int start = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.myreferal_fragment, container, false);
        helper = new SqliteHelper(getActivity(), "Fish2Marine", null, 5);
        sPreferences = getActivity().getSharedPreferences("Fish2Marine", getActivity().MODE_PRIVATE);

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
        container=(FrameLayout)rootView.findViewById(R.id.container);
        sub_layout=((LinearLayout)rootView.findViewById(R.id.sub_layout));
        mrecyclerview = ((RecyclerView)rootView.findViewById(R.id.mrecyclerview));
        SQLData_Item = helper.getadmindetails();
        CustomerID=SQLData_Item.get(0).get("admin_id");
        referral_link=((TextView) rootView.findViewById(R.id.referral_link));
        listcardview=((CardView) rootView.findViewById(R.id.listcardview));
        error_label_retry = ((TextView) rootView.findViewById(R.id.error_label_retry));
        empty_label_retry = ((TextView)rootView.findViewById(R.id.empty_label_retry));
        invite=(FloatingActionButton)rootView.findViewById(R.id.invite);
        txt_share = ((TextView)rootView.findViewById(R.id.txt_share));
        error_label_retry.setOnClickListener(this);
        empty_label_retry.setOnClickListener(this);
        txt_share.setOnClickListener(this);
        invite.setOnClickListener(this);

        initRecyclerView();

        dataItem = new ArrayList<>();
        InitGetData();
    }

    private void InitGetData(){
        Config mConfig = new Config(getActivity());
        if(mConfig.isOnline(getActivity())){
            LoadReferralListInitiate mLoadReferralListInitiate = new LoadReferralListInitiate(CustomerID);
            mLoadReferralListInitiate.execute((Void) null);
        }else {
            switcher.showErrorView("No Internet Connection");
        }
    }

    private void InitInvite(String name,String email,String message){
        Config mConfig = new Config(getActivity());
        if(mConfig.isOnline(getActivity())){
            LoadReferralInviteInitiate mLoadReferralInviteInitiate = new LoadReferralInviteInitiate(CustomerID,name,email,message);
            mLoadReferralInviteInitiate.execute((Void) null);
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
            case R.id.txt_share:
                shareTextUrl();
                break;
            case R.id.invite:
                mBottomSheetDialog = new BottomSheetDialog(getActivity());
                View sheetView = getActivity().getLayoutInflater().inflate(R.layout.bottomsheet_invitefriends, null);
                mBottomSheetDialog.setContentView(sheetView);
                mBottomSheetDialog.show();
                final CustomEditText name = (CustomEditText) sheetView.findViewById(R.id.name);
                final CustomEditText email = (CustomEditText) sheetView.findViewById(R.id.email);
                final EditText message = (EditText) sheetView.findViewById(R.id.message);
                indicator=(AVLoadingIndicatorView)sheetView.findViewById(R.id.indicator);
                send = (Button) sheetView.findViewById(R.id.send);

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(name.getText().toString().equals("")){
                            CustomToast.error(getActivity(),"Field cannot be blank").show();
                        }
                        else  if(email.getText().toString().equals("")){
                            CustomToast.error(getActivity(),"Field cannot be blank").show();
                        }
                        else if (!email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                            CustomToast.error(getActivity(),"Enter a valid Email").show();
                        }
                        else  if(message.getText().toString().equals("")){
                            CustomToast.error(getActivity(),"Field cannot be blank").show();
                        }
                        else {
                            InitInvite(name.getText().toString(),email.getText().toString(),message.getText().toString());
                           // mBottomSheetDialog.dismiss();
                        }
                       // CustomToast.info(getActivity(),"This is edit"+name.getText().toString()).show();
                        //mBottomSheetDialog.dismiss();
                    }
                });


                break;
        }
    }

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Fish2Marine");
        share.putExtra(Intent.EXTRA_TEXT, "Get more at Fish2Marine! Register with Fish2Marine and get Reward Points.\n"+referral_link.getText().toString());
        startActivity(Intent.createChooser(share, "Share link!"));

    }

    private void initRecyclerView() {
        mrecyclerview.setHasFixedSize(true);

        if (isTablet(getActivity())) {
            mLayoutManager = new GridLayoutManager(getActivity(), 1);
        } else {
            mLayoutManager = new GridLayoutManager(getActivity(), 1);
        }
        mrecyclerview.setLayoutManager(mLayoutManager);
    }

    public class LoadReferralInviteInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mId,mName,mEmail,mMessage;

        LoadReferralInviteInitiate(String id,String name,String email,String message) {
            mId = id;
            mName=name;
            mEmail=email;
            mMessage=message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            send.setVisibility(View.GONE);
            indicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(getActivity());
            StringBuilder result = httpOperations.doReferralInvite(mId,mName,mEmail,mMessage);
            Log.d("111111111","PASSING VALUE: "+mId+" "+mName+" "+mEmail+" "+mMessage);
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
                        indicator.setVisibility(View.GONE);
                        CustomToast.success(getActivity(),"Success").show();
                        mBottomSheetDialog.dismiss();
                        Fragment newFragment = new MyReferalFragment();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.container, newFragment).commit();
                    }else {

                        CustomToast.error(getActivity(),jsonObj.getString("message").toString()).show();
                        mBottomSheetDialog.dismiss();
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

    public class LoadReferralListInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mId;

        LoadReferralListInitiate(String id) {
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
            StringBuilder result = httpOperations.doMyReferrals(mId);
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
                        JSONObject jsonObj1 = jsonObj.getJSONObject("data");
                        //Log.d("1111221", "API_DASHBOARD_RESPONSE jsonObj1"+jsonObj1);
                        if(jsonObj1.has("referral_link")) {
                            txt_share.setVisibility(View.VISIBLE);
                            referral_link.setText("Your Referral Link: "+jsonObj1.getString("referral_link").toString());
                        }
                        if (jsonObj1.has("referral_items")) {
                            listcardview.setVisibility(View.VISIBLE);
                            JSONArray feedArray1 = jsonObj1.getJSONArray("referral_items");
                            for (int i = 0; i < feedArray1.length(); i++) {
                                AllListItem item = new AllListItem();
                                JSONObject feedObj1 = (JSONObject) feedArray1.get(i);
                                item.setPoints(feedObj1.getString("name"));
                                item.setComment(feedObj1.getString("email"));
                                item.setCreated(feedObj1.getString("status"));
                                item.setExpiry(feedObj1.getString("blance_points"));
                                dataItem.add(item);
                            }
                            start = dataItem.size();
                            myReferralsAdapter = new MyReferralsAdapter(getActivity(), dataItem);
                            mrecyclerview.setAdapter(myReferralsAdapter);
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
