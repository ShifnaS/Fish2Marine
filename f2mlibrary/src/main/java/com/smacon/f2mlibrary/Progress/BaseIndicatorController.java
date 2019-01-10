/*
 *Copyright 2015 Neona Embedded Labz as an unpublished work. All Rights Reserved.
 *
 * The information contained herein is confidential property of Neona Embedded Labz.
 * The use, copying,transfer or disclosure of such information is prohibited except
 * by express written agreement with Company.
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
 * File Name 					: BaseIndicatorController
 * Since 						: 8/03/16
 * Version Code & Project Name	: v 1.0 library
 * Author Name					: Athira Santhosh  athira.santhosh@neonainnovation.com
 */
package com.smacon.f2mlibrary.Progress;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.nineoldandroids.animation.Animator;

import java.util.List;

/**
 * Created by athira on 8/03/16.
 */
public abstract class BaseIndicatorController {

    private View mTarget;
    private List<Animator> mAnimators;

    public void setTarget(View target){
        this.mTarget=target;
    }

    public View getTarget(){
        return mTarget;
    }

    public int getWidth(){
        return mTarget.getWidth();
    }

    public int getHeight(){
        return mTarget.getHeight();
    }

    public void postInvalidate(){
        mTarget.postInvalidate();
    }

    public abstract void draw(Canvas canvas, Paint paint);

    public abstract List<Animator> createAnimation();

    public void initAnimation(){
        mAnimators=createAnimation();
    }

    public void setAnimationStatus(AnimStatus animStatus){
        if (mAnimators==null){
            return;
        }
        int count=mAnimators.size();
        for (int i = 0; i < count; i++) {
            Animator animator=mAnimators.get(i);
            boolean isRunning=animator.isRunning();
            switch (animStatus){
                case START:
                    if (!isRunning){
                        animator.start();
                    }
                    break;
                case END:
                    if (isRunning){
                        animator.end();
                    }
                    break;
                case CANCEL:
                    if (isRunning){
                        animator.cancel();
                    }
                    break;
            }
        }
    }

    public enum AnimStatus{
        START,END,CANCEL
    }
}