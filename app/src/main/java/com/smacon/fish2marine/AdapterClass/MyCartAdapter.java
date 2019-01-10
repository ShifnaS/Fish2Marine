package com.smacon.fish2marine.AdapterClass;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smacon.f2mlibrary.CustomToast;
import com.smacon.f2mlibrary.Progress.AVLoadingIndicatorView;
import com.smacon.f2mlibrary.SwipableLayout.SwipeLayout;
import com.smacon.f2mlibrary.SwipeLayout.SwipeRevealLayout;
import com.smacon.f2mlibrary.SwipeLayout.ViewBinderHelper;
import com.smacon.fish2marine.HelperClass.CartListItem;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.LoginActivity;
import com.smacon.fish2marine.NavigationDrawerActivity;
import com.smacon.fish2marine.ProductDescriptionActivity;
import com.smacon.fish2marine.R;
import com.smacon.fish2marine.UpdateProductActivity;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Aiswarya on 2017/04/19.
 */

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder>  {
    private SharedPreferences sPreferences;
    private SqliteHelper helper;
    private Config mConfig;
    private Activity mContext;
    private ArrayList<CartListItem> mListItem;
    private CartListItem item;
    private String Item_ID,Customer_ID;
    int clickedPos = -1;
    AVLoadingIndicatorView indicator;
    ArrayList<CartListItem> cuttype_dataItem;
    Animation animFadein,animFadeout;
    List<HashMap<String, String>> SQLData_Item;

    public MyCartAdapter(Activity context, ArrayList<CartListItem> listitem) {
        this.mContext = context;
        this.mListItem = listitem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mycartlist_item, viewGroup, false);
        helper = new SqliteHelper(mContext, "Fish2Marine", null, 5);
        mConfig = new Config(mContext);
        sPreferences = mContext.getSharedPreferences("Fish2Marine", MODE_PRIVATE);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mListItem.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ProductName,OtherName, ProductPrice,CutType,Quantity,
                txt_ordered_qty,txt_cleaned_qty,SoldBy,NetQty,SubTotal;
        private ImageView ProductImage, Plus, Minus;
        private LinearLayout layout_soldby,edit,delete,layout;
        FrameLayout More,visible_layout,layout_indicator;

        ViewHolder(final View itemView) {
            super(itemView);

            this.ProductImage = (ImageView) itemView.findViewById(R.id.img_product);
            this.ProductName = (TextView) itemView.findViewById(R.id.txt_product_name);
            this.OtherName = (TextView) itemView.findViewById(R.id.txt_product_othername);
            this.ProductPrice = (TextView) itemView.findViewById(R.id.txt_product_price);
            this.CutType = (TextView) itemView.findViewById(R.id.txt_cuttype);
            this.txt_ordered_qty = (TextView) itemView.findViewById(R.id.txt_ordered_qty);
            this.txt_cleaned_qty = (TextView) itemView.findViewById(R.id.txt_cleaned_qty);
            this.layout_soldby = (LinearLayout) itemView.findViewById(R.id.layout_soldby);
            this.SoldBy = (TextView) itemView.findViewById(R.id.txt_soldby);
            this.NetQty = (TextView) itemView.findViewById(R.id.txt_netqty);
            this.Quantity = (TextView) itemView.findViewById(R.id.txt_quantity);
            this.SubTotal = (TextView) itemView.findViewById(R.id.txt_subtotal);
            this.More = (FrameLayout) itemView.findViewById(R.id.more);
            this.visible_layout = (FrameLayout) itemView.findViewById(R.id.visible);
            //this.view = (LinearLayout) itemView.findViewById(R.id.view);
            this.edit = (LinearLayout) itemView.findViewById(R.id.edit);
            this.delete = (LinearLayout) itemView.findViewById(R.id.delete);
            this.layout = (LinearLayout) itemView.findViewById(R.id.layout);
            this.layout_indicator = (FrameLayout) itemView.findViewById(R.id.layout_indicator);
        }

    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        item = mListItem.get(position);
        SQLData_Item = helper.getadmindetails();
        Customer_ID=SQLData_Item.get(0).get("admin_id");

        holder.More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedPos = position;
                notifyDataSetChanged();
            }
        });

        if (clickedPos == position) {

            animFadein = AnimationUtils.loadAnimation(mContext,R.anim.fab_slide_in_from_right);
           // animFadeout = AnimationUtils.loadAnimation(mContext,R.anim.fab_slide_in_from_right);
            holder.visible_layout.startAnimation(animFadein);
            holder.visible_layout.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.visible_layout.setVisibility(View.GONE);
                }
            }, 3000);
        } else {
            holder.visible_layout.setVisibility(View.GONE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* CustomToast.error(mContext,"Name: "+mListItem.get(position).getProductName()+
                        "ItemID: "+mListItem.get(position).getItemId(),Toast.LENGTH_SHORT);*/
                DeleteCart(Customer_ID,mListItem.get(position).getItemId(),holder);
                //notifyDataSetChanged();
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpdateProductActivity.class);
                intent.putExtra("ITEM_ID",mListItem.get(position).getItemId());
                intent.putExtra("NAME",mListItem.get(position).getProductName());
                intent.putExtra("ID",mListItem.get(position).getProductId());
                intent.putExtra("QTY",mListItem.get(position).getQuantity());
                if(!item.getCutType().equals("")||item.getCutType().equals("none")){
                    intent.putExtra("CUTTYPE",mListItem.get(position).getCutType());
                }
                else {
                    holder.CutType.setText("Cut Type: Not Applicable");
                }
                intent.putExtra("CUTTYPE",mListItem.get(position).getCutType());
                mContext.startActivity(intent);
            }
        });

        if(!item.getProductImage().equals("")||item.getProductImage().equals("none")){
            try {
                Picasso.with(mContext)
                        .load(mListItem.get(position).getProductImage().replaceAll(" ","%20"))
                        .placeholder(R.drawable.ic_dummy)
                        .error(R.drawable.ic_dummy)
                        .into(holder.ProductImage);
            }catch (Exception e){
            }
        } else {
            holder.ProductImage.setBackgroundResource(R.drawable.ic_dummy);
        }

        holder.ProductName.setText(item.getProductName());
        holder.OtherName.setText(item.getOtherName());
        holder.ProductPrice.setText(item.getPrice()+"/0.5Kg");
        if(!item.getCutType().equals("")||item.getCutType().equals("none")){
            holder.CutType.setText("Cut Type: "+item.getCutType());
        }
        else {

            holder.CutType.setText("Cut Type: Not Applicable");
        }

        if(item.getBeforeCleaning()<1000){
            holder.txt_ordered_qty.setText("Order Qty: "+item.getBeforeCleaning()+" gm");
        }
        else if(item.getBeforeCleaning()>=1000){
            double value=item.getBeforeCleaning()/1000;
            holder.txt_ordered_qty.setText("Order Qty: "+value+" kg");
        }

        if(item.getAfterCleaning()<1000){
            holder.txt_cleaned_qty.setText("After Cleaning: "+item.getAfterCleaning()+" gm");
        }
        else if(item.getAfterCleaning()>=1000){
            double value=item.getAfterCleaning()/1000;
            holder.txt_cleaned_qty.setText("After Cleaning: "+value+" kg");
        }

        if(item.getNetQty()<1000){
            holder.NetQty.setText("Net Quantity: "+item.getNetQty()+" gm");
        }
        else if(item.getNetQty()>=1000){
            double value=item.getNetQty()/1000;
            holder.NetQty.setText("Net Quantity: "+value+" kg");
        }

        holder.Quantity.setText("Qty: "+item.getQuantity());
        holder.SubTotal.setText(item.getItemsTotal());
        if(!item.getSoldBy().equals("")||item.getSoldBy().equals("none")){
           // holder.layout_soldby.setVisibility(View.VISIBLE);
            holder.SoldBy.setText(item.getSoldBy());
        }

    }


    private void DeleteCart(String CustomerID,String ItemID,ViewHolder holder){

        Config mConfig = new Config(mContext);
        if(mConfig.isOnline(mContext)){
            DeleteCartInitiate mDeleteCartInitiate = new DeleteCartInitiate
                    (CustomerID,ItemID,holder);
            mDeleteCartInitiate.execute((Void) null);
        }else {
            CustomToast.error(mContext,"No Internet Connection.").show();
        }
    }

    public class DeleteCartInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mCustomerID,mItemID;
        ViewHolder mholder;

        DeleteCartInitiate(String Customer_ID,String Item_ID,ViewHolder holder) {
            mCustomerID = Customer_ID;
            mItemID=Item_ID;
            mholder=holder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mholder.visible_layout.setVisibility(View.GONE);
            mholder.layout.setVisibility(View.INVISIBLE);
            mholder.layout_indicator.setVisibility(View.VISIBLE);
            helper.Delete_cartcount();
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(mContext);
            StringBuilder result = httpOperations.doDeleteCart(mCustomerID,mItemID);
            Log.d("111111", "PASSING VALUE " + mCustomerID+" "+mItemID);
            Log.d("111111", "API_DELETE_RESPONSE " + result);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(2))) {
                        mholder.layout.setVisibility(View.VISIBLE);
                        mholder.layout_indicator.setVisibility(View.GONE);
                        CustomToast.info(mContext,jsonObj.getString("message").toString()).show();
                    }else if (jsonObj.getString("status").equals(String.valueOf(1))) {
                        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
                        JSONObject jsonObj1 = jsonObj.getJSONObject("data");
                        for (int i = 0; i < jsonObj1.length(); i++) {
                            HashMap<String, String> map;
                            map = new HashMap<String, String>();
                            map.put("itemsCount", jsonObj1.getString("itemsCount").trim());
                            fillMaps.add(map);
                        }
                        helper.Insert_Count(fillMaps);
                        final List<HashMap<String, String>> Data_Item;;
                        Data_Item = helper.getCount();
                        Log.d("1111111112","Cart Count "+Data_Item.get(0).get("cartcount"));
                        mConfig.savePreferences(mContext,"CartCount",Data_Item.get(0).get("cartcount"));
                        updatecartcount(mholder);
                        Log.d("111111111",sPreferences.getString("CartCount",""));

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

    private void updatecartcount(ViewHolder holder){
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent("data_changed"));
        NavigationDrawerActivity.getInstance().updateCartCount();
        holder.layout.setVisibility(View.VISIBLE);
        holder.layout_indicator.setVisibility(View.GONE);
        mListItem.remove(holder.getAdapterPosition());
        notifyDataSetChanged();
        CustomToast.success(mContext, "Item deleted from cart").show();
    }

    /*private void AddToCart(String Quantity,String ProductID,String Cuttype,ViewHolder holder){
        Config mConfig = new Config(mContext);
        if(mConfig.isOnline(mContext)){
            LoadAddToCartInitiate mLoadAddToCartInitiate = new LoadAddToCartInitiate(
                    Customer_ID,ProductID,Cuttype,Quantity,holder);

            mLoadAddToCartInitiate.execute((Void) null);
        }else {
            CustomToast.error(mContext,"No Internet Connection.").show();
        }
    }*/

    /*public class LoadAddToCartInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mCustomerID,mProductID,mCutype,mQty;
        ViewHolder mholder;


        LoadAddToCartInitiate(String Customer_ID, String Product_ID, String CutType,String Qty,ViewHolder holder) {
            mCustomerID = Customer_ID;
            mProductID = Product_ID;
            mCutype = CutType;
            mQty = Qty;
            mholder=holder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mholder.layout.setVisibility(View.INVISIBLE);
            mholder.layout_indicator.setVisibility(View.VISIBLE);
            helper.Delete_cartcount();
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(mContext);
            Log.d("111111", "PASSING VALUE " + mCustomerID+" "+mProductID+" "+mCutype+" "+mQty);
            StringBuilder result = httpOperations.doAddToCart(mCustomerID, mProductID, mCutype,mQty);
            Log.d("111111111",result.toString());
            return result;

        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(2))) {
                        mholder.layout.setVisibility(View.VISIBLE);
                        mholder.layout_indicator.setVisibility(View.GONE);
                        CustomToast.info(mContext,jsonObj.getString("message").toString()).show();
                    }else if (jsonObj.getString("status").equals(String.valueOf(1))) {
                        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
                        JSONObject jsonObj1 = jsonObj.getJSONObject("data");
                        for (int i = 0; i < jsonObj1.length(); i++) {
                            HashMap<String, String> map;
                            map = new HashMap<String, String>();
                            map.put("itemsCount", jsonObj1.getString("itemsCount").trim());
                            fillMaps.add(map);
                        }
                        helper.Insert_Count(fillMaps);
                        final List<HashMap<String, String>> Data_Item;;
                        Data_Item = helper.getCount();
                        Log.d("1111111112","Cart Count "+Data_Item.get(0).get("cartcount"));
                        mConfig.savePreferences(mContext,"CartCount",Data_Item.get(0).get("cartcount"));
                        updatecartcount(mholder);
                        Log.d("111111111",sPreferences.getString("CartCount",""));

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
    }*/
    /*private void updatecartcount(ViewHolder holder){
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent("data_changed"));
        NavigationDrawerActivity.getInstance().updateCartCount();
        holder.layout.setVisibility(View.VISIBLE);
        holder.layout_indicator.setVisibility(View.GONE);
        CustomToast.success(mContext, "Item successfully added to cart").show();
    }*/
}
