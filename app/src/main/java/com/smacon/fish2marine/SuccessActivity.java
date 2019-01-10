package com.smacon.fish2marine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by smacon on 30/5/18.
 */

public class SuccessActivity extends AppCompatActivity {
    private Intent intent;
    String mOrderId;
    private SharedPreferences sPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordersuccess);
        sPreferences = getSharedPreferences("Fish2Marine", MODE_PRIVATE);
        ImageView img_loader_brands = (ImageView) findViewById(R.id.img_loader_brands);
        Glide.with(this)
                .load(R.drawable.sucess)
                .into(img_loader_brands);
        TextView continue_shopping=(TextView)findViewById(R.id.continue_shopping);
        continue_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessActivity.this,NavigationDrawerActivity.class);
                intent.putExtra("PAGE","HOME");
                startActivity(intent);
                finish();
            }
        });
        TextView orderno=(TextView)findViewById(R.id.orderno);
        intent = getIntent();
        mOrderId=intent.getExtras().getString("PLACEORDER_ID");
        orderno.setText(mOrderId);

        SharedPreferences.Editor editor = sPreferences.edit();
        editor.remove("CartCount");
        editor.apply();
    }
}
