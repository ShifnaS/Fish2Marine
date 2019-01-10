package com.smacon.f2mlibrary.RatingBar;

/**
 * Created by server on 15/9/17.
 */

import android.view.View;

/**
 * Created by florentchampigny on 22/12/2015.
 */
public class AnimationListener {

    private AnimationListener(){}

    public interface Start{
        void onStart();
    }

    public interface Stop{
        void onStop();
    }

    public interface Update<V extends View>{
        void update(V view, float value);
    }
}