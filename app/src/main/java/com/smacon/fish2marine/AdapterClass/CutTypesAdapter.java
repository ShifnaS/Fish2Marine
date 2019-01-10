package com.smacon.fish2marine.AdapterClass;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smacon.f2mlibrary.CustomToast;
import com.smacon.fish2marine.HelperClass.ProductListItem;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.R;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Kris on 12/21/2016.
 */
public class CutTypesAdapter extends RecyclerView.Adapter {

    private List<ProductListItem> mitems;
    public Activity mContext;
    private String Product_ID,Quantity;
    private SharedPreferences sPreferences;
    private SqliteHelper helper;
    private Config mConfig;
    String CustomerID = "";
    List<HashMap<String, String>> SQLData_Item ;

    public CutTypesAdapter(Activity mContext, List<ProductListItem> items, String ID, String Quantity) {
        this.mitems = items;
        this.mContext = mContext;
        this.Product_ID=ID;
        this.Quantity=Quantity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rootView = LayoutInflater.
                from(mContext).inflate(R.layout.cuttype_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        helper = new SqliteHelper(mContext, "Fish2Marine", null, 5);
        sPreferences = mContext.getSharedPreferences("Fish2Marine", MODE_PRIVATE);
        mConfig = new Config(mContext);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {

        final ProductListItem dataItem = mitems.get(position);
        final MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
        myViewHolder.txt_cuttypeName.setText(dataItem.getCuttype_label());
        //Product_ID=dataItem.getAllproduct_id();

        if(dataItem.getcuttype_imageurl().equals("false")){
            myViewHolder.img_cuttypeImage.setImageResource(R.drawable.ic_dummy);
        } else {
            try {
                Picasso.with(mContext)
                        .load(mitems.get(position).getcuttype_imageurl().replaceAll(" ","%20"))
                        .placeholder(R.drawable.ic_dummy)
                        .error(R.drawable.ic_dummy)
                        .into(myViewHolder.img_cuttypeImage);
            }catch (Exception e){
            }
        }

        helper = new SqliteHelper(mContext, "Fish2Marine", null, 2);
        SQLData_Item = helper.getadmindetails();
        CustomerID=SQLData_Item.get(0).get("admin_id");
        myViewHolder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddToCart(CustomerID,Product_ID,dataItem.getcuttype_value(),Quantity);
                //CustomToast.success(mContext, "This is: "+dataItem.getcuttype_value()).show();

            }
        });
    }
    @Override
    public int getItemCount() {
        return mitems.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txt_cuttypeName;

        public ImageView img_cuttypeImage;

        public CardView cardview;


        public MyViewHolder(View itemView) {
            super(itemView);
            img_cuttypeImage = (ImageView) itemView.findViewById(R.id.img_cuttypeImage);
            cardview = (CardView) itemView.findViewById(R.id.cardview);
            txt_cuttypeName = (TextView) itemView.findViewById(R.id.txt_cuttypeName);

        }
    }
    private void AddToCart(String Customer_ID,String P_ID,String Cuttype,String qty){

        //customerid,productid,cuttype,qty
        Config mConfig = new Config(mContext);
        if(mConfig.isOnline(mContext)){
            CustomToast.error(mContext,"Values: "+Customer_ID+" "+P_ID+" "+Cuttype+" "+qty).show();
            LoadAddToCartInitiate mLoadAddToCartInitiate = new LoadAddToCartInitiate(
                    Customer_ID,P_ID,Cuttype,qty);
            mLoadAddToCartInitiate.execute((Void) null);
        }else {
            CustomToast.error(mContext,"No Internet Connection.").show();
        }
    }
    public class LoadAddToCartInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mCustomerID,mProductID,mCutype,mQty;

        LoadAddToCartInitiate(String Customer_ID, String Product_ID, String CutType,String Qty) {
            mCustomerID = Customer_ID;
            mProductID = Product_ID;
            mCutype = CutType;
            mQty = Qty;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*loading.setVisibility(View.VISIBLE);
            mRegister.setVisibility(View.INVISIBLE);*/
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(mContext);
            StringBuilder result = httpOperations.doAddToCart(mCustomerID, mProductID, mCutype,mQty);
            Log.d("111111", "API_CUTTYPE_RESPONSE " + result);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            /*loading.setVisibility(View.GONE);
            mRegister.setVisibility(View.VISIBLE);*/
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                Log.d("111111111","here");
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(2))) {
                       // Status = "404";
                        Log.d("111111111","here1");
                    }else if (jsonObj.getString("status").equals(String.valueOf(1))) {
                        Log.d("111111111","here2");
                        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
                        JSONObject jsonObj1 = jsonObj.getJSONObject("data");
                        Log.d("111111111","here3");
                        for (int i = 0; i < jsonObj1.length(); i++) {
                            HashMap<String, String> map;
                            map = new HashMap<String, String>();
                            map.put("itemsCount", jsonObj1.getString("itemsCount").trim());
                            Log.d("111111111","here4");
                            fillMaps.add(map);
                            Log.d("111111111","here5");
                        }
                        helper.Insert_Count(fillMaps);
                        Log.d("111111111","here6");
                        final List<HashMap<String, String>> Data_Item;
                       // Data_Item = new ArrayList<>();
                        Data_Item = helper.getCount();
                        Log.d("111111111","here7");
                        mConfig.savePreferences(mContext,"CartCount",Data_Item.get(0).get("cartcount"));
                        Log.d("111111111","here8");
                        Log.d("1111111",Data_Item.get(0).get("cartcount"));

                       // Status="200";
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
}