package com.smacon.fish2marine.AdapterClass;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smacon.fish2marine.HelperClass.ProductListItem;
import com.smacon.fish2marine.ProductViewActivity;
import com.smacon.fish2marine.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Aiswarya on 2017/12/15.
 */

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {

    private Activity mContext;
    private ArrayList<ProductListItem> mListItem;
    private ProductListItem item;
    public DateAdapter(Activity context, ArrayList<ProductListItem> listitem) {
        this.mContext=context;
        mListItem = listitem;

    }

    @Override
    public int getItemCount() {
        return mListItem.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private String id;
        private TextView date;
        private RecyclerView horizontal_list;
        ViewHolder(final View itemView) {
            super(itemView);
            this.date = (TextView) itemView.findViewById(R.id.date);
            this.horizontal_list=(RecyclerView)itemView.findViewById(R.id.horizontal_list);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_deliverydates, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        item = mListItem.get(position);

        holder.id=item.getcategory_id();
        holder.date.setText(item.getcategory_name());

    }

}