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

public class CategorySnapperAdapter extends RecyclerView.Adapter<CategorySnapperAdapter.ViewHolder> {

    private Activity mContext;
    private ArrayList<ProductListItem> mListItem;
    private ProductListItem item;
    public CategorySnapperAdapter(Activity context, ArrayList<ProductListItem> listitem) {
        this.mContext=context;
        mListItem = listitem;

    }

    @Override
    public int getItemCount() {
        return mListItem.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private String id;
        private TextView ProductName;
        private ImageView ProductImage;
        ViewHolder(final View itemView) {
            super(itemView);
            this.ProductName = (TextView) itemView.findViewById(R.id.CategoryName);
            this.ProductImage=(ImageView)itemView.findViewById(R.id.CategoryIcon);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        item = mListItem.get(position);

        holder.id=item.getcategory_id();
        holder.ProductName.setText(item.getcategory_name());
        if(item.getcategory_path().equals("false")){

            holder.ProductImage.setImageResource(R.drawable.ic_dummy);

        } else {
            try {
                Picasso.with(mContext)
                        .load(mListItem.get(position).getcategory_path().replaceAll(" ","%20"))
                        .placeholder(R.drawable.ic_dummy)
                        .error(R.drawable.ic_dummy)
                        .into(holder.ProductImage);
            }catch (Exception e){
            }
        }
        holder.ProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(mContext,"This is: "+mListItem.get(position).getcategory_name(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext,ProductViewActivity.class);
                intent.putExtra("PAGE","FROM_HOME");
                intent.putExtra("NAME",mListItem.get(position).getcategory_name());
                intent.putExtra("ID",mListItem.get(position).getcategory_id());
                mContext.startActivity(intent);
            }
        });
    }

}