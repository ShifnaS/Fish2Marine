package com.smacon.fish2marine.AdapterClass;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smacon.f2mlibrary.SwipeLayout.BaseSwipeAdapter;
import com.smacon.f2mlibrary.SwipeLayout.SwipeLayout;
import com.smacon.fish2marine.HelperClass.CartListItem;
import com.smacon.fish2marine.LoginActivity;
import com.smacon.fish2marine.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * Created by Kris on 12/9/2016.
 */
public class ListItemsAdapter extends BaseSwipeAdapter {

    ArrayList<CartListItem> mListItem;
    Activity mContext;

    private LinearLayout swipemail, swipecall, swipeview;
    CartListItem item;
    ViewHolder holder;
    View v;

    public ListItemsAdapter(Activity context, ArrayList<CartListItem> listItem) {
        mListItem = listItem;
        mContext = context;

    }
    static class ViewHolder {
        private TextView ProductName,OtherName, ProductPrice,CutType,Quantity,
                txt_ordered_qty,txt_cleaned_qty,SoldBy,NetQty,SubTotal;
        private ImageView ProductImage, Plus, Minus;
        private LinearLayout layout_soldby;
        FrameLayout More;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {

        return R.id.swipe;
    }
    @Override
    public int getCount() {
        return mListItem.size();
    }

    @Override
    public Object getItem(int location) {
        return mListItem.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {

        v = LayoutInflater.from(mContext).inflate(R.layout.mycartlist_itemcopy, null);

        holder=new ViewHolder();

        holder.ProductImage = (ImageView) v.findViewById(R.id.img_product);
        holder.ProductName = (TextView) v.findViewById(R.id.txt_product_name);
        holder.OtherName = (TextView) v.findViewById(R.id.txt_product_othername);
        holder.ProductPrice = (TextView) v.findViewById(R.id.txt_product_price);
        holder.CutType = (TextView) v.findViewById(R.id.txt_cuttype);
        holder.txt_ordered_qty = (TextView) v.findViewById(R.id.txt_ordered_qty);
        holder.txt_cleaned_qty = (TextView) v.findViewById(R.id.txt_cleaned_qty);
        holder.layout_soldby = (LinearLayout) v.findViewById(R.id.layout_soldby);
        holder.SoldBy = (TextView) v.findViewById(R.id.txt_soldby);
        holder.NetQty = (TextView) v.findViewById(R.id.txt_netqty);
        holder.Quantity = (TextView) v.findViewById(R.id.txt_quantity);
        holder.SubTotal = (TextView) v.findViewById(R.id.txt_subtotal);
        holder.More = (FrameLayout) v.findViewById(R.id.more);
        swipecall = (LinearLayout) v.findViewById(R.id.swipecall);
        swipemail= (LinearLayout) v.findViewById(R.id.swipemail);
        swipeview = (LinearLayout) v.findViewById(R.id.swipeview);

        v.setTag(holder);
       // ((SwipeLayout) (holder.More.getChildAt(position - holder.More.getFirstVisiblePosition()))).open(true);
        item = mListItem.get(position);

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
        holder.ProductPrice.setText("Rs "+item.getPrice()+"/0.5Kg");
        if(!item.getCutType().equals("")||item.getCutType().equals("none")){
            holder.CutType.setText("Cut Type: "+item.getCutType());
        }
        else {
            holder.CutType.setVisibility(View.GONE);
        }
        holder.txt_ordered_qty.setText("Order Qty: "+item.getBeforeCleaning());
        holder.txt_cleaned_qty.setText("After Cleaning: "+item.getAfterCleaning());
        holder.NetQty.setText("Net Quantity"+item.getNetQty());
        holder.Quantity.setText("Qty: "+item.getQuantity());
        holder.SubTotal.setText("Rs "+item.getItemsTotal());
        if(!item.getSoldBy().equals("")||item.getSoldBy().equals("none")){
            // holder.layout_soldby.setVisibility(View.VISIBLE);
            holder.SoldBy.setText(item.getSoldBy());
        }

        holder.More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        swipecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        swipemail.findViewById(R.id.swipemail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        swipeview.findViewById(R.id.swipeview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext,LoginActivity.class);

                mContext.startActivity(intent);
               // mContext.overridePendingTransition(R.anim.activity_open_translate,
                     //   R.anim.activity_close_scale);
                mContext.finish();
            }
        });
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        v = generateView(position,parent);
       // mItemManger.bind(v, position);
        fillValues(position, v);
        return v;
    }
}