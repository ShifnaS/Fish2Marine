package com.smacon.fish2marine.AdapterClass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smacon.f2mlibrary.CustomToast;
import com.smacon.f2mlibrary.Progress.AVLoadingIndicatorView;
import com.smacon.fish2marine.HelperClass.CartListItem;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.NavigationDrawerActivity;
import com.smacon.fish2marine.R;
import com.smacon.fish2marine.UpdateProductActivity;
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
 * Created by Aiswarya on 2017/04/19.
 */

public class PlaceOrderItemAdapter extends RecyclerView.Adapter<PlaceOrderItemAdapter.ViewHolder>  {
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

    public PlaceOrderItemAdapter(Activity context, ArrayList<CartListItem> listitem) {
        this.mContext = context;
        this.mListItem = listitem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.placeorderlist_item, viewGroup, false);
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
        private TextView ProductName,OtherName,CutType,Quantity,
                txt_ordered_qty,txt_cleaned_qty,SubTotal;
        private ImageView ProductImage;

        ViewHolder(final View itemView) {
            super(itemView);

            this.ProductImage = (ImageView) itemView.findViewById(R.id.img_product);
            this.ProductName = (TextView) itemView.findViewById(R.id.txt_product_name);
            this.OtherName = (TextView) itemView.findViewById(R.id.txt_product_othername);
            this.CutType = (TextView) itemView.findViewById(R.id.txt_cuttype);
            this.txt_ordered_qty = (TextView) itemView.findViewById(R.id.txt_ordered_qty);
            this.txt_cleaned_qty = (TextView) itemView.findViewById(R.id.txt_cleaned_qty);
            this.Quantity = (TextView) itemView.findViewById(R.id.txt_quantity);
            this.SubTotal = (TextView) itemView.findViewById(R.id.txt_subtotal);

        }

    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        item = mListItem.get(position);
        SQLData_Item = helper.getadmindetails();
        Customer_ID=SQLData_Item.get(0).get("admin_id");

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

        holder.Quantity.setText("Qty: "+item.getQuantity());
        holder.SubTotal.setText(item.getItemsTotal());

    }
}
