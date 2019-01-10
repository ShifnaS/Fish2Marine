package com.smacon.fish2marine.AdapterClass;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smacon.f2mlibrary.Button.LiquidRadioButton.LiquidRadioButton;
import com.smacon.f2mlibrary.CustomToast;
import com.smacon.f2mlibrary.Progress.AVLoadingIndicatorView;
import com.smacon.fish2marine.HelperClass.ProductListItem;
import com.smacon.fish2marine.HelperClass.SqliteHelper;
import com.smacon.fish2marine.NavigationDrawerActivity;
import com.smacon.fish2marine.ProductDescriptionActivity;
import com.smacon.fish2marine.R;
import com.smacon.fish2marine.Util.Config;
import com.smacon.fish2marine.Util.HttpOperations;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Aiswarya on 2017/12/15.
 */

public class BestSellerSnapperAdapter extends RecyclerView.Adapter<BestSellerSnapperAdapter.ViewHolder> {
    private SharedPreferences sPreferences;
    private SqliteHelper helper;
    private Config mConfig;
    private Activity mContext;
    private ArrayList<ProductListItem> mListItem;
    private ProductListItem item;
    String mCuttypeValue = "";
    private String Customer_ID;
    ListView mlistview;
    AVLoadingIndicatorView indicator;
    ArrayList<ProductListItem> cuttype_dataItem;

    public BestSellerSnapperAdapter(Activity context, ArrayList<ProductListItem> listitem, String CustomerID) {
        this.mContext = context;
        this.mListItem = listitem;
        this.Customer_ID=CustomerID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.homeproductview_itemlayout, viewGroup, false);
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
        private TextView ProductName, OtherName, ProductPrice,SpecialPrice, Quantity,txt_ordered_qty,txt_cleaned_qty;
        private ImageView ProductImage, Plus, Minus;
        private LinearLayout Addtocart,layout;
        FrameLayout layout_indicator;
        AVLoadingIndicatorView indicator;
        RadioGroup group;
        ImageView close;
        TextView btn_ok;
        LinearLayout layout_cutype;
        String mCuttypeValue = "";
        String OrderQty,AfterQty;

        ViewHolder(final View itemView) {
            super(itemView);
            this.layout = (LinearLayout) itemView.findViewById(R.id.layout);
            this.layout_cutype = (LinearLayout) itemView.findViewById(R.id.layout_cutype);
            this.close = (ImageView) itemView.findViewById(R.id.close);
            this.btn_ok = (TextView) itemView.findViewById(R.id.btn_ok);
            this.layout_indicator = (FrameLayout) itemView.findViewById(R.id.layout_indicator);
            this.indicator = (AVLoadingIndicatorView) itemView.findViewById(R.id.indicator);
            this.ProductName = (TextView) itemView.findViewById(R.id.txt_product_name);
            this.ProductPrice = (TextView) itemView.findViewById(R.id.txt_product_price);
            this.OtherName = (TextView) itemView.findViewById(R.id.txt_product_othername);
            this.SpecialPrice = (TextView) itemView.findViewById(R.id.txt_product_specialprice);
            this.ProductImage = (ImageView) itemView.findViewById(R.id.img_product);
            this.txt_ordered_qty = (TextView) itemView.findViewById(R.id.txt_ordered_qty);
            this.txt_cleaned_qty = (TextView) itemView.findViewById(R.id.txt_cleaned_qty);
            this.Plus = (ImageView) itemView.findViewById(R.id.img_plus);
            this.Minus = (ImageView) itemView.findViewById(R.id.img_minus);
            this.Quantity = (TextView) itemView.findViewById(R.id.txt_quantity);
            this.Addtocart = (LinearLayout) itemView.findViewById(R.id.btn_addtocart);
            this.group=(RadioGroup)itemView.findViewById(R.id.radiogroup);

        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        item = mListItem.get(position);

        holder.ProductName.setText(item.getbestsellerproduct_name());
        holder.OtherName.setText(item.getbestsellerproduct_Othername());
        holder.OrderQty=mListItem.get(position).getbestsellerorder_qty();
        holder.AfterQty=mListItem.get(position).getbestsellercleaned_qty();
        holder.txt_ordered_qty.setText("Order Qty: "+holder.OrderQty+"gm");
        holder.txt_cleaned_qty.setText("After Cleaning: "+holder.AfterQty+"gm");
        //  Log.d("11111111","hiii"+mListItem.get(position).getbestCuttype_valuelist());

        holder.group.removeAllViews();
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        if (mListItem.get(position).getbestcuttype_applicable().equals("1")) {
            Log.d("111111111","here"+ mListItem.get(position).getbestCuttype_valuelist().size());
            for (int i = 0; i < mListItem.get(position).getbestCuttype_valuelist().size(); i++) {
                Log.d("11111111","hiiibest"+mListItem.get(position).getbestCuttype_valuelist().get(i));
                LiquidRadioButton rb = (LiquidRadioButton) inflater.inflate(R.layout.template_radiobutton, null);
                //  LiquidRadioButton rb = new LiquidRadioButton(mContext);
                rb.setPadding(5,5,5,5);
                rb.setText(mListItem.get(position).getbestCuttype_valuelist().get(i));
                holder.group.addView(rb);
            }
        }
        holder.group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        holder.mCuttypeValue=btn.getText().toString();
                        //Toast.makeText(mContext, btn.getText().toString()+""+holder.ProductName.getText(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        holder.ProductPrice.setText(item.getbestsellerproduct_price()+"/0.5Kg");
        if (item.getbestsellerproduct_specialprice().equals("")||item.getbestsellerproduct_specialprice().equals("0")){
            holder.SpecialPrice.setVisibility(View.GONE);
        }
        else {
            holder.ProductPrice.setPaintFlags(
                    holder.ProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.SpecialPrice.setText(item.getbestsellerproduct_specialprice()+"/0.5Kg");
        }

        if(!item.getbestsellerproduct_image().equals("")){
            try {
                Picasso.with(mContext)
                        .load(mListItem.get(position).getbestsellerproduct_image().replaceAll(" ","%20"))
                        .placeholder(R.drawable.ic_dummy)
                        .error(R.drawable.ic_dummy)
                        .into(holder.ProductImage);
            }catch (Exception e){
            }
        } else {
            holder.ProductImage.setBackgroundResource(R.drawable.ic_dummy);
        }

        holder.ProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CustomToast.error(v.getContext(), "This is: "+mListItem.get(position).getbestsellerproduct_name()).show();
                Intent intent = new Intent(mContext, ProductDescriptionActivity.class);
                intent.putExtra("ID",mListItem.get(position).getbestsellerproduct_id());
                intent.putExtra("NAME",mListItem.get(position).getbestsellerproduct_name());
                mContext.startActivity(intent);
            }
        });

        holder.Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(holder.Quantity.getText().toString()) + 1;
                holder.Quantity.setText(String.valueOf(number));

                Double orderqty=Double.parseDouble(holder.OrderQty)*Double.parseDouble(holder.Quantity.getText().toString());
                if(orderqty>=1000){
                    Double value=orderqty/1000;
                    holder.txt_ordered_qty.setText("Order Qty: "+value+"kg");
                }
                else {
                    holder.txt_ordered_qty.setText("Order Qty: "+orderqty+"gm");
                }

                Double afterqty=Double.parseDouble(holder.AfterQty)*Double.parseDouble(holder.Quantity.getText().toString());
                if(afterqty>=1000){
                    Double value=afterqty/1000;
                    holder.txt_cleaned_qty.setText("After Cleaning: "+value+"kg");
                }
                else {
                    holder.txt_cleaned_qty.setText("After Cleaning: "+afterqty+"gm");
                }

            }
        });

        holder.Minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(holder.Quantity.getText().toString()) >= 2) {
                    int number = Integer.parseInt(holder.Quantity.getText().toString()) - 1;
                    holder.Quantity.setText(String.valueOf(number));
                }
                Double orderqty=Double.parseDouble(holder.OrderQty)*Double.parseDouble(holder.Quantity.getText().toString());
                if(orderqty>=1000){
                    Double value=orderqty/1000;
                    holder.txt_ordered_qty.setText("Order Qty: "+value+"kg");
                }else {
                    holder.txt_ordered_qty.setText("Order Qty: "+orderqty+"gm");
                }

                Double afterqty=Double.parseDouble(holder.AfterQty)*Double.parseDouble(holder.Quantity.getText().toString());
                if(afterqty>=1000){
                    Double value=afterqty/1000;
                    holder.txt_cleaned_qty.setText("After Cleaning: "+value+"kg");
                }
                else {
                    holder.txt_cleaned_qty.setText("After Cleaning: "+afterqty+"gm");
                }

            }
        });
        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.layout.setVisibility(View.VISIBLE);
                holder.layout_cutype.setVisibility(View.GONE);
            }
        });

        holder.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mCuttypeValue.equals("")){
                    CustomToast.error(mContext,"Please choose a cut type").show();
                }
                else {
                    holder.layout.setVisibility(View.VISIBLE);
                    holder.layout_cutype.setVisibility(View.GONE);
                    AddToCart(holder.Quantity.getText().toString(),mListItem.get(position).getbestsellerproduct_id(),holder.mCuttypeValue,holder);
                }
            }
        });
        holder.Addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("111111111","Cart centerID "+sPreferences.getString("DeliveryCenter_ID",""));
                Log.d("111111111","cuttypeapplicable "+mListItem.get(position).getbestcuttype_applicable());

                if(sPreferences.getString("DeliveryCenter_ID","").equals("")){
                    CustomToast.error(v.getContext(), "Please Choose ur location"+sPreferences.getString("DeliveryCenter_ID","")).show();
                }
                else {
                    if(mListItem.get(position).getbestcuttype_applicable().equals("1")){
                        holder.layout.setVisibility(View.INVISIBLE);
                        holder.layout_cutype.setVisibility(View.VISIBLE);
                        //CutypeDialog(mListItem.get(position).getbestsellerproduct_id(),holder.Quantity.getText().toString(),holder);
                        //CutType(mListItem.get(position).getbestsellerproduct_id(),holder.Quantity.getText().toString());
                    }
                    else {
                        AddToCart(holder.Quantity.getText().toString(),mListItem.get(position).getbestsellerproduct_id(),"",holder);
                    }
                }

            }
        });
    }
   /* private void CutypeDialog(final String ProductID, final String Quantity, final ViewHolder holder){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = mContext.getLayoutInflater();
        GridLayoutManager mLayoutManager;
        final View dialogView = inflater.inflate(R.layout.custom_cuttype_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Choose a Cut type");
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        final AlertDialog b = dialogBuilder.create();
        b.show();
        indicator = (AVLoadingIndicatorView) dialogView.findViewById(R.id.indicator);
        mlistview = (ListView) dialogView.findViewById(R.id.mlistview);
        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                mCuttypeValue=cuttype_dataItem.get(position).getcuttype_value();
                AddToCart(Quantity,ProductID,mCuttypeValue,holder);
                b.dismiss();
                //Toast.makeText(mContext, "Clicked at Position"+mCuttypeValue, Toast.LENGTH_SHORT).show();
            }
        });
    }*/
   /* private void CutType(String ID,String Qty){

        Config mConfig = new Config(mContext);
        if(mConfig.isOnline(mContext)){
            LoadCutTypeInitiate mLoadCutTypeInitiate = new LoadCutTypeInitiate
                    (ID,Qty);
            mLoadCutTypeInitiate.execute((Void) null);
        }else {
            CustomToast.error(mContext,"No Internet Connection.").show();
        }
    }
*/
    /*public class LoadCutTypeInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mProductID,mQty;
        LoadCutTypeInitiate(String Product_ID,String Qty) {
            mProductID = Product_ID;
            mQty=Qty;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cuttype_dataItem = new ArrayList<>();
            indicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(mContext);
            StringBuilder result = httpOperations.doCutTypesList(mProductID);
            Log.d("111111", "API_CUTTYPE_RESPONSE " + result);
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(1))) {
                        indicator.setVisibility(View.GONE);
                        if(jsonObj.has("data")) {
                            JSONObject jsonObj1 = jsonObj.getJSONObject("data");
                            String ID=jsonObj1.getString("productId");
                            Log.d("111111", "here0 " + ID);
                            //item1.setAllproduct_id(jsonObj1.getString("productId"));
                            if (jsonObj1.has("cutTypes")) {
                                JSONArray feedArray1 = jsonObj1.getJSONArray("cutTypes");
                                for (int i = 0; i < feedArray1.length(); i++) {
                                    ProductListItem item1 = new ProductListItem();
                                    JSONObject feedObj2 = (JSONObject) feedArray1.get(i);
                                    Log.d("111111", "API_CUTTYPE_RESPONSE " + feedObj2.getString("label"));
                                    item1.setcuttype_label(feedObj2.getString("label"));
                                    item1.setcuttype_value(feedObj2.getString("value"));
                                    item1.setcuttype_imageurl(feedObj2.getString("ImageUrl"));
                                    cuttype_dataItem.add(item1);
                                    Log.d("111111", "API_CUTTYPE_SIZE " + cuttype_dataItem.size());
                                }
                                CuttypesListAdapter mCutypeListAdapter = new CuttypesListAdapter(mContext, cuttype_dataItem,mProductID,mQty);
                                mlistview.setAdapter(mCutypeListAdapter);

                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                CustomToast.info(mContext,"Please Try Again").show();
            } catch (NullPointerException e) {
                CustomToast.error(mContext,"No Internet Connection.").show();
            } catch (Exception e) {
                CustomToast.info(mContext,"Please Try Again").show();
            }
        }
    }*/

    private void AddToCart(String Quantity,String ProductID,String Cuttype,ViewHolder holder){
        Config mConfig = new Config(mContext);
        if(mConfig.isOnline(mContext)){
            LoadAddToCartInitiate mLoadAddToCartInitiate = new LoadAddToCartInitiate(
                    Customer_ID,ProductID,Cuttype,Quantity,holder);

            mLoadAddToCartInitiate.execute((Void) null);
        }else {
            CustomToast.error(mContext,"No Internet Connection.").show();
        }
    }

    public class LoadAddToCartInitiate extends AsyncTask<Void, StringBuilder, StringBuilder> {

        private String mCustomerID,mProductID,mCutype,mQty;
        ViewHolder mholder;


        LoadAddToCartInitiate(String Customer_ID, String Product_ID, String CutType,String Qty,ViewHolder holder) {
            mCustomerID = Customer_ID;
            mProductID = Product_ID;
            mCutype = CutType;
            mQty = Qty;
            mholder=holder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mholder.layout.setVisibility(View.INVISIBLE);
            mholder.layout_indicator.setVisibility(View.VISIBLE);
            helper.Delete_cartcount();
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {

            HttpOperations httpOperations = new HttpOperations(mContext);
            Log.d("111111", "PASSING VALUE " + mCustomerID+" "+mProductID+" "+mCutype+" "+mQty);
            StringBuilder result = httpOperations.doAddToCart(mCustomerID, mProductID, mCutype,mQty);
            Log.d("111111111",result.toString());
            return result;

        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                if (jsonObj.has("status")) {
                    if (jsonObj.getString("status").equals(String.valueOf(2))) {
                        mholder.layout.setVisibility(View.VISIBLE);
                        mholder.layout_indicator.setVisibility(View.GONE);
                        CustomToast.info(mContext,jsonObj.getString("message").toString()).show();
                    }else if (jsonObj.getString("status").equals(String.valueOf(1))) {
                        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
                        JSONObject jsonObj1 = jsonObj.getJSONObject("data");
                        for (int i = 0; i < jsonObj1.length(); i++) {
                            HashMap<String, String> map;
                            map = new HashMap<String, String>();
                            map.put("itemsCount", jsonObj1.getString("itemsCount").trim());
                            fillMaps.add(map);
                        }
                        mConfig.savePreferences(mContext,"CartID",jsonObj1.getString("id").trim());
                        Log.d("111111111",sPreferences.getString("CartID",""));
                        helper.Insert_Count(fillMaps);
                        final List<HashMap<String, String>> Data_Item;;
                        Data_Item = helper.getCount();
                        Log.d("1111111112","Cart Count "+Data_Item.get(0).get("cartcount"));
                        mConfig.savePreferences(mContext,"CartCount",Data_Item.get(0).get("cartcount"));
                        updatecartcount(mholder);
                        Log.d("111111111",sPreferences.getString("CartCount",""));

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                CustomToast.info(mContext,"Please Try Again").show();
            } catch (NullPointerException e) {
                CustomToast.error(mContext,"No Internet Connection.").show();
            } catch (Exception e) {
                CustomToast.info(mContext,"Please Try Again").show();
            }
        }
    }
    private void updatecartcount(ViewHolder holder){
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent("data_changed"));
        NavigationDrawerActivity.getInstance().updateCartCount();
       /* Intent intent = new Intent(mContext, ProductViewActivity.class);
        Log.d("11111111shared",sPreferences.getString("CategoryName",""));
        Log.d("11111111shared",sPreferences.getString("CategoryID",""));
        intent.putExtra("ID",sPreferences.getString("CategoryID",""));
        intent.putExtra("NAME",sPreferences.getString("CategoryName",""));
        intent.putExtra("PAGE","SUBPAGE");
       // mContext.getWindow().setExitTransition(null);
        mContext.startActivity(intent);*/
        holder.layout.setVisibility(View.VISIBLE);
        holder.layout_indicator.setVisibility(View.GONE);
        CustomToast.success(mContext, "Item successfully added to cart").show();
    }
}
