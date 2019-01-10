package com.smacon.fish2marine.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smacon.fish2marine.R;

/**
 * Created by Aiswarya on 09/05/2018.
 */

public class ContactUsFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contactus, container, false);
        return rootView;
    }
}
