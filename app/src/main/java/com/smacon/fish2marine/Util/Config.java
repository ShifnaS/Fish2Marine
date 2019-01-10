/*
 * Copyright 2018 Smacon Technologies Pvt Ltd as an unpublished work. All Rights
 * Reserved.
 *
 * The information contained herein is confidential property of Cutesys Technologies
 * Pvt Ltd. The use, copying,transfer or disclosure of such information is prohibited
 * except by express written agreement with Company.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * File Name 					: Config
 * Since 						: 7/02/18
 * Version Code & Project Name	: v 1.0 Fish2Marine
 * Author Name					: Aiswarya Saju
 */

package com.smacon.fish2marine.Util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by athira on 17/10/16.
 */
public class Config {

    public Context mContext;
    private SharedPreferences sPreferences;
    private SharedPreferences.Editor prefEditor;

    public Config(Context context){

        this.mContext = context;
    }

    public void scale(View view, long delay){

        view.setScaleX(0);
        view.setScaleY(0);
        view.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(500)
                .setStartDelay(delay)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }
    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void savePreferences(Context mContext, String key ,String value) {
        sPreferences = mContext.getSharedPreferences("Fish2Marine", mContext.MODE_PRIVATE);
        prefEditor = sPreferences.edit();
        prefEditor.putString(key, value);
        prefEditor.commit();
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
