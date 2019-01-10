package com.smacon.f2mlibrary.Helperclasses;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;

import com.smacon.f2mlibrary.R;


/**
 * Created by Kris on 2/18/2017.
 */

public class Utils {

    private Utils() {
    }

    public static Drawable tint9PatchDrawableFrame(@NonNull Context context, @ColorInt int tintColor) {
        final NinePatchDrawable toastDrawable = (NinePatchDrawable) getDrawable(context, R.drawable.toast_frame);
        toastDrawable.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
        return toastDrawable;
    }

    public static void setBackground(@NonNull View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            view.setBackground(drawable);
        else
            view.setBackgroundDrawable(drawable);
    }

    public static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return context.getDrawable(id);
        else
            return context.getResources().getDrawable(id);
    }

    public static int convertDPtoPixels(Context context, int dp) {
        Resources resource = context.getResources();
        return(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resource.getDisplayMetrics());
    }
}
