package com.smacon.fish2marine.AdapterClass;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.smacon.f2mlibrary.Progress.AVLoadingIndicatorView;
import com.smacon.fish2marine.HelperClass.ProductListItem;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.R;
import com.smacon.fish2marine.Util.Config;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Aiswarya on 2017/04/19.
 */

public class OrderItemDetailsAdapter extends RecyclerView.Adapter<OrderItemDetailsAdapter.ViewHolder>  {
    private SharedPreferences sPreferences;
    private SqliteHelper helper;
    private Config mConfig;
    private Context mContext;
    private ArrayList<ProductListItem> mListItem;
    private ProductListItem item;

    List<HashMap<String, String>> SQLData_Item;

    public OrderItemDetailsAdapter(Context context, ArrayList<ProductListItem> listitem) {
        this.mContext = context;
        this.mListItem = listitem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orderdetailslist_item, viewGroup, false);
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
                txt_ordered_qty,SubTotal;
        private ImageView ProductImage;

        ViewHolder(final View itemView) {
            super(itemView);

            this.ProductImage = (ImageView) itemView.findViewById(R.id.img_product);
            this.ProductName = (TextView) itemView.findViewById(R.id.txt_product_name);
            this.OtherName = (TextView) itemView.findViewById(R.id.txt_product_othername);
            this.CutType = (TextView) itemView.findViewById(R.id.txt_cuttype);
            this.txt_ordered_qty = (TextView) itemView.findViewById(R.id.txt_ordered_qty);;
            this.Quantity = (TextView) itemView.findViewById(R.id.txt_quantity);
            this.SubTotal = (TextView) itemView.findViewById(R.id.txt_subtotal);

        }

    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        item = mListItem.get(position);
        SQLData_Item = helper.getadmindetails();
        if(!item.getAllproduct_image().equals("")||item.getAllproduct_image().equals("none")){
            try {
                Picasso.with(mContext)
                        .load(mListItem.get(position).getAllproduct_image().replaceAll(" ","%20"))
                        .placeholder(R.drawable.ic_dummy)
                        .error(R.drawable.ic_dummy)
                        .into(holder.ProductImage);
            }catch (Exception e){
            }
        } else {
            holder.ProductImage.setBackgroundResource(R.drawable.ic_dummy);
        }
        holder.ProductName.setText(item.getAllproduct_name());
        holder.OtherName.setText("SKU#: "+item.getAllproduct_Othername());
        if(!item.getAllproduct_id().equals("")||item.getAllproduct_id().equals("none")){
            holder.CutType.setText("Cut Type: "+item.getAllproduct_id());
        }
        else {

            holder.CutType.setText("Cut Type: Not Applicable");
        }
        if(Integer.parseInt(item.getallcleaned_qty())<1000){
            holder.txt_ordered_qty.setText("Order Qty: "+item.getallcleaned_qty()+" gm");
        }
        else if(Integer.parseInt(item.getallcleaned_qty())>=1000){
            int value=Integer.parseInt(item.getallcleaned_qty())/1000;
            holder.txt_ordered_qty.setText("Order Qty: "+value+" kg");
        }
        holder.Quantity.setText("Qty: "+item.getallorder_qty());
        holder.SubTotal.setText("Rs. "+item.getAllproduct_price());

    }
}
