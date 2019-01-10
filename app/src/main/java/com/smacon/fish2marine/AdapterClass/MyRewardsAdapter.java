package com.smacon.fish2marine.AdapterClass;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smacon.fish2marine.HelperClass.AllListItem;
import com.smacon.fish2marine.R;

import java.util.List;

/**
 * Created by Aiswarya on 10/05/2018.
 */
public class MyRewardsAdapter extends RecyclerView.Adapter {

    private List<AllListItem> mitems;
    public Activity mContext;

    public MyRewardsAdapter(Activity mContext, List<AllListItem> items) {
        this.mitems = items;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rootView = LayoutInflater.
                from(mContext).inflate(R.layout.list_item_rewards, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {

        final AllListItem dataItem = mitems.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
        myViewHolder.title.setText(dataItem.getComment());
        myViewHolder.points.setText(dataItem.getPoints()+" Points");
        myViewHolder.expiry.setText("Expire On: "+dataItem.getExpiry());

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

        public TextView title,points,expiry;
        public CardView cardview;

        public MyViewHolder(View itemView) {
            super(itemView);

            cardview = (CardView) itemView.findViewById(R.id.cardview);
            title = (TextView) itemView.findViewById(R.id.title);
            points = (TextView) itemView.findViewById(R.id.points);
            expiry = (TextView) itemView.findViewById(R.id.expiry);

        }
    }
}