package com.smacon.fish2marine.AdapterClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smacon.fish2marine.HelperClass.ProductListItem;
import com.smacon.fish2marine.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Kris on 12/29/2016.
 */
public class SpinnerAdapter extends BaseAdapter {
    private Context mcontext;
    private ArrayList<ProductListItem> listitem;

    public SpinnerAdapter(Context context, ArrayList<ProductListItem> mListitem) {
        this.listitem = mListitem;
        this.mcontext = context;
    }

    public int getCount() {
        return listitem.size();
    }

    public Object getItem(int i) {
        return listitem.get(i);
    }

    public long getItemId(int i) {
        return (long)i;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        ProductListItem item = listitem.get(position);
        View v = LayoutInflater.from(mcontext).inflate(R.layout.cuttype_item, null);

        TextView txt_cuttypeName = (TextView) v.findViewById(R.id.txt_cuttypeName);
        ImageView img_cuttypeImage=(ImageView) v.findViewById(R.id.img_cuttypeImage);
        txt_cuttypeName.setText(item.getCuttype_label());
        if(item.getcuttype_imageurl().equals("")||item.getcuttype_imageurl().equals("false")){

            img_cuttypeImage.setImageResource(R.drawable.ic_dummy);

        } else {
            try {
                Picasso.with(mcontext)
                        .load(listitem.get(position).getcuttype_imageurl().replaceAll(" ","%20"))
                        .placeholder(R.drawable.ic_dummy)
                        .error(R.drawable.ic_dummy)
                        .into(img_cuttypeImage);
            }catch (Exception e){
            }
        }
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}