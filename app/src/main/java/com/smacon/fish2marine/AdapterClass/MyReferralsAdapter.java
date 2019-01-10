package com.smacon.fish2marine.AdapterClass;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smacon.fish2marine.HelperClass.AllListItem;
import com.smacon.fish2marine.R;

import java.util.List;

/**
 * Created by Aiswarya on 10/05/2018.
 */
public class MyReferralsAdapter extends RecyclerView.Adapter {

    private List<AllListItem> mitems;
    public Activity mContext;

    public MyReferralsAdapter(Activity mContext, List<AllListItem> items) {
        this.mitems = items;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rootView = LayoutInflater.
                from(mContext).inflate(R.layout.list_item_referrals, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {

        final AllListItem dataItem = mitems.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
        myViewHolder.name.setText(dataItem.getPoints());
        myViewHolder.email.setText(dataItem.getComment());
        myViewHolder.status.setText("Status: "+dataItem.getCreated());
        if(dataItem.getExpiry().equals("null")){
            myViewHolder.points.setText("Referral Points: 0");
        }else {
            myViewHolder.points.setText("Referral Points: "+dataItem.getExpiry());
        }
        if(dataItem.getPoints().equals("")){
            myViewHolder.name.setText("Name: -");
        }else {
            myViewHolder.name.setText("Name: "+dataItem.getPoints());
        }
        if(dataItem.getComment().equals("")){
            myViewHolder.email.setText("Email: -");
        }else {
            myViewHolder.email.setText("Email: "+dataItem.getComment());
        }

        if (position % 2 == 0) {
            myViewHolder.cardview.setBackgroundResource(R.color.white);
            //myViewHolder.image.setImageResource(R.mipmap.ic_working);
        } else {
            myViewHolder.cardview.setBackgroundResource(R.color.white);
            //myViewHolder.image.setImageResource(R.mipmap.ic_business);
        }
    }

    @Override
    public int getItemCount() {

        return mitems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name,email,status,points;
        public CardView cardview;

        public MyViewHolder(View itemView) {
            super(itemView);

            cardview = (CardView) itemView.findViewById(R.id.cardview);
            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);
            status = (TextView) itemView.findViewById(R.id.status);
            points = (TextView) itemView.findViewById(R.id.points);
        }
    }
}