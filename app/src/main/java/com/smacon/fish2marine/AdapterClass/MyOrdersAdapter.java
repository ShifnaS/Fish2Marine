package com.smacon.fish2marine.AdapterClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smacon.f2mlibrary.CustomToast;
import com.smacon.fish2marine.HelperClass.AllListItem;
import com.smacon.fish2marine.OrderDetailsActivity;
import com.smacon.fish2marine.R;

import java.util.List;

/**
 * Created by Aiswarya on 10/05/2018.
 */
public class MyOrdersAdapter extends RecyclerView.Adapter {

    private List<AllListItem> mitems;
    public Activity mContext;

    public MyOrdersAdapter(Activity mContext, List<AllListItem> items) {
        this.mitems = items;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rootView = LayoutInflater.
                from(mContext).inflate(R.layout.list_item_order, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        return new MyViewHolder(rootView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        final AllListItem dataItem = mitems.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
        myViewHolder.txt_order_id.setText("Order No: "+dataItem.getReward_id());
        myViewHolder.txt_order_date.setText("Date: "+dataItem.getComment());
        if(dataItem.getPoints().equals("Pending")){
            myViewHolder.txt_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.color2));
           // myViewHolder.txt_order_status.setTextColor(Color.parseColor("#ff8f00"));
        }
        else  if(dataItem.getPoints().equals("Processing")){
            myViewHolder.txt_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.color4));
            //myViewHolder.txt_order_status.setTextColor(R.color.color4);
        }
        else  if(dataItem.getPoints().equals("Completed")||dataItem.getPoints().equals("Delivered")){
            myViewHolder.txt_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.green));
        }
        else  if(dataItem.getPoints().equals("Rejected")){
            myViewHolder.txt_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }
        myViewHolder.txt_order_status.setText(dataItem.getPoints());
        myViewHolder.txt_order_amount.setText("Amount: Rs. "+dataItem.getExpiry());

        myViewHolder.orderitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CustomToast.success(mContext, "This is: "+mitems.get(position).getReward_id()).show();
                Intent intent = new Intent(mContext,OrderDetailsActivity.class);
                intent.putExtra("ORDER_ID",mitems.get(position).getReward_id());
                mContext.startActivity(intent);
                mContext.overridePendingTransition(R.anim.bottom_up,
                        android.R.anim.fade_out);
                mContext.finish();
            }
        });

       /* if (position % 2 == 0) {
            myViewHolder.cardview.setBackgroundResource(R.color.white);
            //myViewHolder.image.setImageResource(R.mipmap.ic_working);
        } else {
            myViewHolder.cardview.setBackgroundResource(R.color.white);
            //myViewHolder.image.setImageResource(R.mipmap.ic_business);
        }*/
    }

    @Override
    public int getItemCount() {

        return mitems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txt_order_id,txt_order_date,txt_order_status,txt_order_amount;
        public CardView orderitem;

        public MyViewHolder(View itemView) {
            super(itemView);

            //cardview = (CardView) itemView.findViewById(R.id.cardview);
            txt_order_id = (TextView) itemView.findViewById(R.id.txt_order_id);
            txt_order_date = (TextView) itemView.findViewById(R.id.txt_order_date);
            txt_order_status = (TextView) itemView.findViewById(R.id.txt_order_status);
            txt_order_amount = (TextView) itemView.findViewById(R.id.txt_order_amount);
            orderitem = (CardView) itemView.findViewById(R.id.orderitem);

        }
    }
}