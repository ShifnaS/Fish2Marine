package com.smacon.fish2marine.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smacon.f2mlibrary.Switcher.Switcher;
import com.smacon.fish2marine.AdapterClass.MyOrdersAdapter;
import com.smacon.fish2marine.HelperClass.AllListItem;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
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

public class MyOrdersListFragment extends Fragment implements View.OnClickListener{

    private SharedPreferences sPreferences;
    private SqliteHelper helper;
    private Config mConfig;
    List<HashMap<String, String>> SQLData_Item ;
    String CustomerID = "";
    private Switcher switcher;
    private RecyclerView mrecyclerview;
    private TextView error_label_retry, empty_label_retry,totalpoints;
    LinearLayout sub_layout;
    GridLayoutManager mLayoutManager;
    MyOrdersAdapter mMyOrdersAdapter;

    ArrayList<AllListItem> dataItem;
    int start = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_orderlist, container, false);
        helper = new SqliteHelper(getActivity(), "Fish2Marine", null, 5);
        sPreferences = getActivity().getSharedPreferences("Fish2Marine", getActivity().MODE_PRIVATE);

        InitIdView(rootView);
        return rootView;
    }

    private void InitIdView(View rootView){
        switcher = new Switcher.Builder(getActivity())
                .addContentView(rootView.findViewById(R.id.mrecyclerview))
                .addErrorView(rootView.findViewById(R.id.error_view))
                .addProgressView(rootView.findViewById(R.id.progress_view))
                .setErrorLabel((TextView) rootView.findViewById(R.id.error_label))
                .setEmptyLabel((TextView) rootView.findViewById(R.id.empty_label))
                .addEmptyView(rootView.findViewById(R.id.empty_view))
                .build();

        mrecyclerview = ((RecyclerView)rootView.findViewById(R.id.mrecyclerview));
        SQLData_Item = helper.getadmindetails();
        CustomerID=SQLData_Item.get(0).get("admin_id");
        error_label_retry = ((TextView) rootView.findViewById(R.id.error_label_retry));
        empty_label_retry = ((TextView)rootView.findViewById(R.id.empty_label_retry));
        error_label_retry.setOnClickListener(this);
        empty_label_retry.setOnClickListener(this);

        dataItem = new ArrayList<>();
        InitGetData();
        mrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void InitGetData(){
        Config mConfig = new Config(getActivity());
        if(mConfig.isOnline(getActivity())){
            LoadOrderListInitiate mLoadOrderListInitiate = new LoadOrderListInitiate(CustomerID);
            mLoadOrderListInitiate.execute((Void) null);
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
        }
    }

    public class LoadOrderListInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mId;

        LoadOrderListInitiate(String id) {
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
            StringBuilder result = httpOperations.doMyOrderList(mId);
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
                        JSONObject jsonObj1 = jsonObj.getJSONObject("data");
                        if (jsonObj1.has("orders")) {
                            JSONArray feedArray1 = jsonObj1.getJSONArray("orders");
                            for (int i = 0; i < feedArray1.length(); i++) {
                                AllListItem item = new AllListItem();
                                JSONObject feedObj1 = (JSONObject) feedArray1.get(i);
                                item.setReward_id(feedObj1.getString("OrderNo"));
                                item.setPoints(feedObj1.getString("orderStatus"));
                                item.setComment(feedObj1.getString("orderDate"));
                                item.setCreated(feedObj1.getString("shipTo"));
                                item.setExpiry(feedObj1.getString("orderTotal"));

                                dataItem.add(item);
                            }
                            start = dataItem.size();
                            mMyOrdersAdapter = new MyOrdersAdapter(getActivity(), dataItem);
                            mrecyclerview.setAdapter(mMyOrdersAdapter);
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
