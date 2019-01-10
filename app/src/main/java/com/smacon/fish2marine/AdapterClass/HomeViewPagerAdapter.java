package com.smacon.fish2marine.AdapterClass;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smacon.fish2marine.HelperClass.ProductListItem;
import com.smacon.fish2marine.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Sanket on 27-Feb-17.
 */

public class HomeViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<ProductListItem> mListItem;
    private ProductListItem item;

    public HomeViewPagerAdapter(Context context,ArrayList<ProductListItem> listitem) {
        this.context = context;
        this.mListItem = listitem;
    }

    @Override
    public int getCount() {
        return mListItem.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.viewpager_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        item = mListItem.get(position);
        try {
            Picasso.with(context)
                    .load(item.get_sliderimage().replaceAll(" ","%20"))
                    .placeholder(R.drawable.ic_dummy)
                    .error(R.drawable.ic_dummy)
                    .into(imageView);
        }catch (Exception e){
        }
        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<mListItem.size();i++){
                    Toast.makeText(context, "Slide"+ (i+1) +"Clicked", Toast.LENGTH_SHORT).show();
                }

               *//* if(position == 0){
                    Toast.makeText(context, "Slide 1 Clicked", Toast.LENGTH_SHORT).show();
                } else if(position == 1){
                    Toast.makeText(context, "Slide 2 Clicked", Toast.LENGTH_SHORT).show();
                } else if(position == 2){
                    Toast.makeText(context, "Slide 3 Clicked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Slide 4 Clicked", Toast.LENGTH_SHORT).show();
                }*//*

            }
        });*/
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
