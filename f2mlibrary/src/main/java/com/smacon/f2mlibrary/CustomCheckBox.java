package com.smacon.f2mlibrary;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

/**
 * Created by smacon on 27/10/16.
 */

public final class CustomCheckBox extends AppCompatCheckBox {

    public CustomCheckBox(Context context) {
        super(context);
        init();
    }

    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        this.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "rounded_font.otf"));
    }
}
