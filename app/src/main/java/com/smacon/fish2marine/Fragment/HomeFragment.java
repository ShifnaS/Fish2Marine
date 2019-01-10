package com.smacon.fish2marine.Fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.smacon.f2mlibrary.Button.LiquidRadioButton.LiquidRadioButton;
import com.smacon.f2mlibrary.CustomToast;
import com.smacon.f2mlibrary.MultiSnapRecyclerView.MultiSnapRecyclerView;
import com.smacon.f2mlibrary.Progress.AVLoadingIndicatorView;
import com.smacon.fish2marine.AdapterClass.BestSellerSnapperAdapter;
import com.smacon.fish2marine.AdapterClass.CategorySnapperAdapter;
import com.smacon.fish2marine.AdapterClass.FeaturedProductSnapperAdapter;
import com.smacon.fish2marine.AdapterClass.HomeViewPagerAdapter;
import com.smacon.fish2marine.AdapterClass.HotProductAdapter;
import com.smacon.fish2marine.AdapterClass.NewProductSnapperAdapter;
import com.smacon.fish2marine.HelperClass.ProductListItem;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.Interface.RecycleviewInterface;
import com.smacon.fish2marine.R;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements PlaceSelectionListener,RecycleviewInterface{
    private SharedPreferences sPreferences;
    private SqliteHelper helper;
    private Config mConfig;
    List<HashMap<String, String>> SQLData_Item ;
    private static final String LOG_TAG = "PlaceSelectionListener";
    private static final LatLng center=new LatLng(10.0158605,76.3418666);
    double radiusInMeters=15;
    private static final LatLng south=new LatLng(9.876752,76.483111);
    private static final LatLng north=new LatLng(10.141144,76.470065);
    private static final LatLng east=new LatLng(10.004988,76.615339);
    private static final LatLng west=new LatLng(9.996465,76.340976);


    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW1=LatLngBounds.builder().include(north).include(south).build();
     /*private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(9.999620, 76.314297), new LatLng(10.066215, 76.350793));*/

    private static final int REQUEST_SELECT_PLACE = 1000;

    private MultiSnapRecyclerView CategoryRecyclerView,NewProductRecyclerview,
            FeaturedRecyclerview,BestSellerRecyclerview;

    HomeViewPagerAdapter mviewPagerAdapter;
    CategorySnapperAdapter mCategoryAdapter;
    NewProductSnapperAdapter mNewProductAdapter;
    FeaturedProductSnapperAdapter mFeatuedProductAdapter;
    BestSellerSnapperAdapter mBestSellerAdapter;
    ViewPager viewPager;
    LinearLayout sliderDotspanel,location;
    private TextView searched_address;
    private int count;
    private int dotscount;
    private ImageView[] dots;
    // private TextView[] dots;
    int page_position = 0;
    int start = 0;
    ArrayList<ProductListItem> category_dataItem,slider_dataItem,
            newproduct_dataItem,spinnerdataItem,featured_dataItem,bestseller_dataItem;
    String CustomerID = "";
    // List<String> newcuttype_dataItem,cuttype_dataItem,hotcuttype_dataItem,bestcuttype_dataItem;
    //ImageView img_loader_brands;
    FrameLayout layout_loader;
    Dialog dialog,progressdialog;
    TextView btn_ok;
    RadioGroup radiogroup;
    AVLoadingIndicatorView loading;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_dashboard, container, false);
        helper = new SqliteHelper(getActivity(), "Fish2Marine", null, 5);
        sPreferences = getActivity().getSharedPreferences("Fish2Marine", MODE_PRIVATE);
        mConfig = new Config(getActivity());

        location = (LinearLayout)rootView.findViewById(R.id.location);
        searched_address=(TextView)rootView.findViewById(R.id.searched_address);

        SQLData_Item = helper.getadmindetails();
        CustomerID=SQLData_Item.get(0).get("admin_id");
        Log.d("1111221", "Customer ID "+CustomerID);

        progressdialog = new Dialog(getActivity());
        progressdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressdialog.setContentView(R.layout.dialog_progress);
        progressdialog.setCanceledOnTouchOutside(false);
        loading = (AVLoadingIndicatorView) progressdialog.findViewById(R.id.loading);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* final Dialog dialog;
                dialog=new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_location);
                dialog.show();*/

                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                        .setCountry("IN")
                        .build();
                try {

                    Intent intent = new PlaceAutocomplete.IntentBuilder
                            (PlaceAutocomplete.MODE_OVERLAY)
                            .setBoundsBias(BOUNDS_MOUNTAIN_VIEW1)
                            .setFilter(typeFilter)
                            .build(getActivity());
                    startActivityForResult(intent, REQUEST_SELECT_PLACE);
                } catch (GooglePlayServicesRepairableException |
                        GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        sliderDotspanel = (LinearLayout)rootView.findViewById(R.id.SliderDots);
        //img_loader_brands = (ImageView) rootView.findViewById(R.id.img_loader_brands);
        layout_loader=(FrameLayout)rootView.findViewById(R.id.layout_indicator);

        // NewProductSnapperAdapter firstAdapter = new NewProductSnapperAdapter(getApplicationContext(),NewProductName,NewProductPrice,NewImageId,Tag);
        NewProductRecyclerview = (MultiSnapRecyclerView)rootView.findViewById(R.id.NewProductRecyclerview);
        LinearLayoutManager NewProductManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        NewProductRecyclerview.setLayoutManager(NewProductManager);
        //firstRecyclerView.setAdapter(firstAdapter);

        //HorizontalSnapperAdapter thirdAdapter = new HorizontalSnapperAdapter(getApplicationContext(),FeaturedProductName,FeaturedProductPrice,FeaturedImageId,Tag1);
        FeaturedRecyclerview = (MultiSnapRecyclerView)rootView.findViewById(R.id.FeaturedRecyclerview);
        LinearLayoutManager FeaturedManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        FeaturedRecyclerview.setLayoutManager(FeaturedManager);
        //thirdRecyclerView.setAdapter(thirdAdapter);

        //HorizontalSnapperAdapter thirdAdapter = new HorizontalSnapperAdapter(getApplicationContext(),FeaturedProductName,FeaturedProductPrice,FeaturedImageId,Tag1);
        BestSellerRecyclerview = (MultiSnapRecyclerView)rootView.findViewById(R.id.BestSellerRecyclerview);
        LinearLayoutManager BestSellerManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        BestSellerRecyclerview.setLayoutManager(BestSellerManager);
        //thirdRecyclerView.setAdapter(thirdAdapter);

        CategoryRecyclerView = (MultiSnapRecyclerView)rootView.findViewById(R.id.CategoryRecyclerView);
        LinearLayoutManager CategoryManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        CategoryRecyclerView.setLayoutManager(CategoryManager);

        category_dataItem = new ArrayList<>();
        slider_dataItem = new ArrayList<>();
        newproduct_dataItem = new ArrayList<>();
        spinnerdataItem= new ArrayList<>();
        featured_dataItem = new ArrayList<>();
        bestseller_dataItem = new ArrayList<>();
        // newcuttype_dataItem= new ArrayList<>();
        // cuttype_dataItem= new ArrayList<>();
        // hotcuttype_dataItem= new ArrayList<>();
        // bestcuttype_dataItem= new ArrayList<>();

        String value=sPreferences.getString("DeliveryCenter_ID","");
        Log.d("11111111",value);
        if(value.equals("")){
            InitGetData("0");
        }
        else {
            InitGetData(value);

        }

       /* addBottomDots(0);
        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (page_position == slider_dataItem.size()) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }
                viewPager.setCurrentItem(page_position, true);
            }
        };

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 100, 5000);*/

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 4000);

        return rootView;
    }

    public void ShowProgressDialog(){

    }



    @Override
    public void onPlaceSelected(Place place) {
        Log.d("111111111", "Place Selected: " + place.getLatLng() +" "+place.getName());

        String s = place.getLatLng().toString();
        String[] latLng = s.substring(10, s.length() - 1).split(",");
        String sLat = latLng[0];
        String sLng = latLng[1];
        String sLoc=place.getAddress().toString();
        Log.d("111111111", "Latitude is: "+sLat+", Longtitude is: "+sLng);
        InitGetLocation(sLat,sLng,sLoc);
        searched_address.setText(place.getAddress());

        if (!TextUtils.isEmpty(place.getAttributions())){
            searched_address.setText(Html.fromHtml(place.getAttributions().toString()));
        }
    }

    @Override
    public void onError(Status status) {
        Log.e(LOG_TAG, "onError: Status = " + status.toString());
        Toast.makeText(getActivity(), "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                this.onError(status);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void userItemClick(int pos) {
        Toast.makeText(getActivity(), "Clicked User : " + newproduct_dataItem.get(pos).getnewproduct_name(), Toast.LENGTH_SHORT).show();
        // cuttypeDialog();
        dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_cuttype);
        //   btn_ok=(TextView)dialog.findViewById(R.id.btn_ok);
        //  radiogroup = (RadioGroup) dialog.findViewById(R.id.radiogroup);

        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radiogroup);
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);

        // add 5 radio buttons to the group
        LiquidRadioButton rb;
        Toast.makeText(getActivity(), "Clicked User : " + newproduct_dataItem.get(pos).getnewproduct_name(), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < newproduct_dataItem.get(pos).getCuttype_valuelist().size(); i++){
            rb = new LiquidRadioButton(getActivity());
            rb.setText(newproduct_dataItem.get(pos).getCuttype_valuelist().get(i));
            //  rb.setId(i);
            rg.addView(rb, layoutParams);
        }
        dialog.show();

    }

    public void cuttypeDialog(){
       /* final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_cuttype_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Choose a Cut type");
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        final AlertDialog b = dialogBuilder.create();
        b.show();*/

        dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_cuttype);
        btn_ok=(TextView)dialog.findViewById(R.id.btn_ok);
        radiogroup = (RadioGroup) dialog.findViewById(R.id.radiogroup);


       /* for(int i=0;i<cuttype_dataItem.size();i++) {
            LiquidRadioButton rb=new LiquidRadioButton(getActivity()); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(cuttype_dataItem.get(i).getCuttype_label());
            radiogroup.addView(rb);
        }*/

    }


    public class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (getActivity()!=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       /* if (viewPager.getCurrentItem() == 0) {
                            viewPager.setCurrentItem(1);
                        } else if (viewPager.getCurrentItem() == 1) {
                            viewPager.setCurrentItem(2);
                        } else if (viewPager.getCurrentItem() == 2) {
                            viewPager.setCurrentItem(3);
                        } else {
                            viewPager.setCurrentItem(0);
                        }*/
                        if (page_position == slider_dataItem.size()) {
                            page_position = 0;
                        } else {
                            page_position = page_position + 1;
                        }
                        viewPager.setCurrentItem(page_position, true);

                    }
                });
            }

        }
    }

    private void InitGetData(String deliverycenterID){
        Config mConfig = new Config(getActivity());
        if(mConfig.isOnline(getActivity())){
            LoadDashboardInitiate mLoadDashboardInitiate = new LoadDashboardInitiate(deliverycenterID);
            mLoadDashboardInitiate.execute((Void) null);
        }else {
            CustomToast.error(getActivity(),"No Internet Connection.").show();
        }
    }

    public class LoadDashboardInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {
        private String mDeliveryCenter;
        LoadDashboardInitiate(String DeliveryCenter_id){
            mDeliveryCenter=DeliveryCenter_id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressdialog.show();
        }
        @Override
        protected StringBuilder doInBackground(Void... params) {
            HttpOperations httpOperations = new HttpOperations(getActivity());
            StringBuilder result = httpOperations.doDashboard(mDeliveryCenter);
            Log.d("1111111", "API_DASHBOARD_RESPONSE " + result);
            return result;
        }
        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj0 = new JSONObject(result.toString());
                if (jsonObj0.has("status")){
                    if (jsonObj0.getString("status").equals(String.valueOf(1))) {
                        JSONObject jsonObj1 = jsonObj0.getJSONObject("data");
                        //Log.d("1111221", "API_DASHBOARD_RESPONSE jsonObj1"+jsonObj1);
                        if(jsonObj1.has("dashboard")){
                            JSONObject jsonObj2 = jsonObj1.getJSONObject("dashboard");
                            if (jsonObj2.has("sliderImages")){
                                JSONArray feedArray1 = jsonObj2.getJSONArray("sliderImages");
                                slider_dataItem.clear();
                                for (int i = 0; i < feedArray1.length(); i++) {
                                    ProductListItem item = new ProductListItem();
                                    JSONObject feedObj1 = (JSONObject) feedArray1.get(i);
                                    item.set_sliderimage(feedObj1.getString("imagePath"));
                                    slider_dataItem.add(item);
                                }
                                start = slider_dataItem.size();
                                mviewPagerAdapter = new HomeViewPagerAdapter(getActivity(),slider_dataItem);
                                viewPager.setAdapter(mviewPagerAdapter);
                                //count=feedArray1.length();
                                initviewpager();
                            }
                            if (jsonObj2.has("CategoryList")) {
                                JSONArray feedArray = jsonObj2.getJSONArray("CategoryList");
                                //List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
                                category_dataItem.clear();
                                for (int i = 0; i < feedArray.length(); i++) {
                                    HashMap<String, String> map;

                                    ProductListItem item = new ProductListItem();

                                    JSONObject feedObj = (JSONObject) feedArray.get(i);

                                   /* map = new HashMap<String, String>();
                                    map.put("Name", feedObj.getString("Name").trim());
                                    map.put("Id", feedObj.getString("Id").trim());
                                    fillMaps.add(map);*/

                                    item.setcategory_name(feedObj.getString("Name"));
                                    item.setcategory_path(feedObj.getString("Path"));
                                    item.setcategory_id(feedObj.getString("Id"));
                                    category_dataItem.add(item);
                                }
                               /* start = fillMaps.size();
                                Log.d("1111111",""+start);*/
                                //helper.Insert_category(fillMaps);
                                mCategoryAdapter = new CategorySnapperAdapter(getActivity(), category_dataItem);
                                CategoryRecyclerView.setAdapter(mCategoryAdapter);
                                /*SQLData_Item = helper.getCategory("Fish");
                                Log.d("1111111",SQLData_Item.get(0).get("category_id"));
                                Log.d("1111111",SQLData_Item.get(0).get("category_name"));
                                */

                            }
                            if (jsonObj2.has("new_products")) {
                                JSONArray feedArray = jsonObj2.getJSONArray("new_products");
                                newproduct_dataItem.clear();
                                for (int i = 0; i < feedArray.length(); i++) {
                                    ProductListItem item = new ProductListItem();
                                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                                    item.setnewproduct_image(feedObj.getString("imagepath"));
                                    item.setnewproduct_name(feedObj.getString("name")+" / "+feedObj.getString("cutTypeApplicable"));
                                    item.setnewproduct_Othername(feedObj.getString("NameInMalayalam"));
                                    item.setnewproduct_id(feedObj.getString("productId"));
                                    item.setnewproduct_price(feedObj.getString("price"));
                                    item.setnewproduct_specialprice(feedObj.getString("specialPrice"));
                                    item.setneworder_qty(feedObj.getString("beforeCleaning"));
                                    item.setnewcleaned_qty(feedObj.getString("afterCleaning"));
                                    item.setnewcuttype_applicable(feedObj.getString("cutTypeApplicable"));
                                    List<String> newcuttype_dataItem= new ArrayList<>();
                                    if(feedObj.getString("cutTypeApplicable").equals("1")){
                                        //newcuttype_dataItem.clear();
                                        if (feedObj.has("cutTypes")){
                                            JSONArray feedArray2 = feedObj.getJSONArray("cutTypes");

                                            for (int k = 0; k < feedArray2.length(); k++) {
                                                // ProductListItem item1 = new ProductListItem();
                                                JSONObject feedObj2 = (JSONObject) feedArray2.get(k);
                                                //  item1.setcuttype_label(feedObj2.getString("label"));
                                                // item1.setcuttype_value(feedObj2.getString("value"));
                                                //  item1.setcuttype_imageurl(feedObj2.getString("ImageUrl"));
                                                newcuttype_dataItem.add(feedObj2.getString("value"));
                                            }
                                        }

                                    }else {
                                        newcuttype_dataItem.clear();
                                    }
                                    item.setCuttype_valuelist(newcuttype_dataItem);
                                    newproduct_dataItem.add(item);
                                    //Log.d("1111228", "API_DASHBOARD_RESPONSE " + spinnerdataItem.size());
                                }
                                start = newproduct_dataItem.size();
                                mNewProductAdapter = new NewProductSnapperAdapter(getActivity(), newproduct_dataItem,CustomerID,HomeFragment.this);
                                NewProductRecyclerview.setAdapter(mNewProductAdapter);
                            }
                            if (jsonObj2.has("featured_products")) {
                                JSONArray feedArray = jsonObj2.getJSONArray("featured_products");
                                featured_dataItem.clear();
                                for (int i = 0; i < feedArray.length(); i++) {
                                    ProductListItem item = new ProductListItem();
                                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                                    item.setfeaturedproduct_image(feedObj.getString("imagepath"));
                                    item.setfeaturedproduct_name(feedObj.getString("name")+" / "+feedObj.getString("cutTypeApplicable"));
                                    item.setfeaturedproduct_Othername(feedObj.getString("NameInMalayalam"));
                                    item.setfeaturedproduct_id(feedObj.getString("productId"));
                                    item.setfeaturedproduct_price(feedObj.getString("price"));
                                    item.setfeaturedproduct_specialprice(feedObj.getString("specialPrice"));
                                    item.setfeaturedorder_qty(feedObj.getString("beforeCleaning"));
                                    item.setfeaturedcleaned_qty(feedObj.getString("afterCleaning"));
                                    item.setfeaturedcuttype_applicable(feedObj.getString("cutTypeApplicable"));
                                    List<String> cuttype_dataItem= new ArrayList<>();
                                    if(feedObj.getString("cutTypeApplicable").equals("1")){
                                        cuttype_dataItem.clear();
                                        if (feedObj.has("cutTypes")){
                                            JSONArray feedArray2 = feedObj.getJSONArray("cutTypes");

                                            for (int k = 0; k < feedArray2.length(); k++) {
                                                // ProductListItem item1 = new ProductListItem();
                                                JSONObject feedObj2 = (JSONObject) feedArray2.get(k);
                                                //  item1.setcuttype_label(feedObj2.getString("label"));
                                                // item1.setcuttype_value(feedObj2.getString("value"));
                                                //  item1.setcuttype_imageurl(feedObj2.getString("ImageUrl"));
                                                cuttype_dataItem.add(feedObj2.getString("value"));
                                            }

                                        }

                                    }else {
                                        cuttype_dataItem.clear();
                                    }
                                    item.setfeaturedCuttype_valuelist(cuttype_dataItem);
                                    featured_dataItem.add(item);
                                }
                                start = featured_dataItem.size();
                                mFeatuedProductAdapter = new FeaturedProductSnapperAdapter(getActivity(), featured_dataItem,CustomerID);
                                //Log.d("1111228", "API_DASHBOARD_RESPONSE start" + start);
                                FeaturedRecyclerview.setAdapter(mFeatuedProductAdapter);
                            }

                            if (jsonObj2.has("hot_products")) {
                                JSONArray feedArray = jsonObj2.getJSONArray("hot_products");
                                bestseller_dataItem.clear();
                                for (int i = 0; i < feedArray.length(); i++) {
                                    ProductListItem item = new ProductListItem();
                                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                                    item.setbestsellerproduct_image(feedObj.getString("imagepath"));
                                    item.setbestsellerproduct_name(feedObj.getString("name")+" / "+feedObj.getString("cutTypeApplicable"));
                                    item.setbestsellerproduct_Othername(feedObj.getString("NameInMalayalam"));
                                    item.setbestsellerproduct_id(feedObj.getString("productId"));
                                    item.setbestsellerproduct_price(feedObj.getString("price"));
                                    item.setbestsellerproduct_specialprice(feedObj.getString("specialPrice"));
                                    item.setbestsellerorder_qty(feedObj.getString("beforeCleaning"));
                                    item.setbestsellercleaned_qty(feedObj.getString("afterCleaning"));
                                    item.setbestcuttype_applicable(feedObj.getString("cutTypeApplicable"));
                                    List<String> bestcuttype_dataItem= new ArrayList<>();
                                    if(feedObj.getString("cutTypeApplicable").equals("1")){
                                        bestcuttype_dataItem.clear();
                                        if (feedObj.has("cutTypes")){
                                            JSONArray feedArray2 = feedObj.getJSONArray("cutTypes");

                                            for (int k = 0; k < feedArray2.length(); k++) {
                                                // ProductListItem item1 = new ProductListItem();
                                                JSONObject feedObj2 = (JSONObject) feedArray2.get(k);
                                                //  item1.setcuttype_label(feedObj2.getString("label"));
                                                // item1.setcuttype_value(feedObj2.getString("value"));
                                                //  item1.setcuttype_imageurl(feedObj2.getString("ImageUrl"));
                                                bestcuttype_dataItem.add(feedObj2.getString("value"));
                                            }
                                            //int start=bestcuttype_dataItem.size();
                                        }

                                    }else {
                                        bestcuttype_dataItem.clear();
                                    }
                                    item.setbestCuttype_valuelist(bestcuttype_dataItem);
                                    bestseller_dataItem.add(item);
                                }
                                start = bestseller_dataItem.size();
                                mBestSellerAdapter = new BestSellerSnapperAdapter(getActivity(), bestseller_dataItem,CustomerID);
                                BestSellerRecyclerview.setAdapter(mBestSellerAdapter);

                            }
                            progressdialog.dismiss();
                            //layout_loader.setVisibility(View.GONE);
                        }

                    }else {
                        CustomToast.info(getActivity(),"Nothing to Display",Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                CustomToast.info(getActivity(),"Please Try Again",Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                CustomToast.error(getActivity(),"No Internet Connection",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                CustomToast.info(getActivity(),"Please Try Again",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void InitGetLocation(String Lat,String Long,String Location){
        Config mConfig = new Config(getActivity());
        if(mConfig.isOnline(getActivity())){
            LoadLocationInitiate mLoadLocationInitiate = new LoadLocationInitiate(Lat,Long,CustomerID,Location);
            mLoadLocationInitiate.execute((Void) null);
        }else {
            CustomToast.error(getActivity(),"No Internet Connection.").show();
        }
    }
    public class LoadLocationInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mLatitude,mLongitude,mCustomerId,mLocation;
        LoadLocationInitiate(String latitude,String longitude,String customerid,String location) {
            mLatitude=latitude;
            mLongitude=longitude;
            mCustomerId=customerid;
            mLocation=location;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressdialog.show();
            // layout_loader.setVisibility(View.VISIBLE);
        }
        @Override
        protected StringBuilder doInBackground(Void... params) {
            HttpOperations httpOperations = new HttpOperations(getActivity());
            StringBuilder result = httpOperations.doLocation(mLatitude,mLongitude,mCustomerId,mLocation);
            Log.d("111111", "API_LOCATION_RESPONSE " + result);
            return result;
        }
        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj0 = new JSONObject(result.toString());
                if (jsonObj0.has("status")){
                    if (jsonObj0.getString("status").equals(String.valueOf(1))) {
                        JSONObject jsonObj1 = jsonObj0.getJSONObject("data");
                        mConfig.savePreferences(getActivity(),"DeliveryCenter_ID",jsonObj1.getString("delivery_centerid").trim());
                        Log.d("11111111shared","center id "+sPreferences.getString("DeliveryCenter_ID",""));
                        if(jsonObj1.getString("delivery_centerid").trim().equals("")){
                            CustomToast.error(getActivity(),"Please choose another location",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            InitGetData(jsonObj1.getString("delivery_centerid").trim());
                        }
                    }else {
                        CustomToast.info(getActivity(),"Nothing to Display",Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                CustomToast.error(getActivity(),"Please Try Again",Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                CustomToast.error(getActivity(),"No Internet Connection",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                CustomToast.error(getActivity(),"Please Try Again",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void initviewpager(){
        //dotscount = start;
        dotscount = slider_dataItem.size();
        sliderDotspanel.removeAllViews();
        Log.d("11111111","dotcount "+dotscount);
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++){

            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(5, 0, 5, 0);

            sliderDotspanel.addView(dots[i], params);

        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.active_dot));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.active_dot));
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}