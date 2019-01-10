package com.smacon.fish2marine.AdapterClass;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.smacon.f2mlibrary.Button.LiquidRadioButton.LiquidRadioButton;
import com.smacon.f2mlibrary.CustomRadioButton;
import com.smacon.fish2marine.HelperClass.CartListItem;
import com.smacon.fish2marine.HelperClass.ProductListItem;
import com.smacon.fish2marine.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Kris on 12/29/2016.
 */
public class ShippingMethodListAdapter extends BaseAdapter {
    private Context mcontext;
    private ArrayList<CartListItem> listitem;

    public ShippingMethodListAdapter(Context context, ArrayList<CartListItem> mListitem) {
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

        CartListItem item = listitem.get(position);
        View v = LayoutInflater.from(mcontext).inflate(R.layout.list_item_shippingmethod, null);
        TextView method_title = (TextView) v.findViewById(R.id.method_title);
        final LiquidRadioButton amount = (LiquidRadioButton) v.findViewById(R.id.amount);
        method_title.setText(item.getCarrier_title());
        if(position==0) {
            amount.setChecked(true);
        }
        amount.setText(" Rs "+item.getAmount());
        amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount.setChecked(true);
            }
        });
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