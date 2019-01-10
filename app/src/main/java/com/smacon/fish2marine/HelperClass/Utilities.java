package com.smacon.fish2marine.HelperClass;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.smacon.f2mlibrary.CustomEditText;

/**
 * Created by smacon on 6/1/17.
 */

public final class Utilities {



    public static float getPxFromDp(Context context, int dp){
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }


    public static void setDialogParamsWrapContent(Context context,Dialog dialog){

        dialog.getWindow().setLayout((6 * getDeviceWidth(context))/7, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public static void setDialogParamsMatchParent(Context context,Dialog dialog){

        dialog.getWindow().setLayout((6 * getDeviceWidth(context))/7, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public static int getDeviceWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static String getTrimText(CustomEditText text){
        return text.getText().toString().trim();
    }


    public static String getTag(Context context){
        return context.getClass().getSimpleName();
    }




    public static boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isValidPhone(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    public static Intent getIntent(Context fromContext,Class<?> toClass){
        return new Intent(fromContext,toClass);
    }

    public static void showAlertDialog(Context context,String title, String message){

        AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message).
                setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        dialog.show();
    }

    public static String getErrorMessage(int responseCode,String networkMessage){
        return " Error code: ".concat(String.valueOf(responseCode)).concat("\n\t  Error Message: " ).
                concat(networkMessage);
    }
}
