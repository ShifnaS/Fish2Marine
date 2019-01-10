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
 * File Name 					: BallClipRotatePulseIndicator
 * Since 						: 8/03/16
 * Version Code & Project Name	: v 1.0 library
 * Author Name					: Athira Santhosh  athira.santhosh@neonainnovation.com
 */
package com.smacon.f2mlibrary.Progress;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by athira on 8/03/16.
 */
public class BallClipRotatePulseIndicator extends BaseIndicatorController {

    float scaleFloat1,scaleFloat2,degrees;

    @Override
    public void draw(Canvas canvas, Paint paint) {
        float circleSpacing=12;
        float x=getWidth()/2;
        float y=getHeight()/2;

        canvas.save();
        canvas.translate(x, y);
        canvas.scale(scaleFloat1, scaleFloat1);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0, 0, x / 2.5f, paint);

        canvas.restore();

        canvas.translate(x, y);
        canvas.scale(scaleFloat2, scaleFloat2);
        canvas.rotate(degrees);

        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);

        float[] startAngles=new float[]{225,45};
        for (int i = 0; i < 2; i++) {
            RectF rectF=new RectF(-x+circleSpacing,-y+circleSpacing,x-circleSpacing,y-circleSpacing);
            canvas.drawArc(rectF, startAngles[i], 90, false, paint);
        }
    }

    @Override
    public List<Animator> createAnimation() {
        ValueAnimator scaleAnim= ValueAnimator.ofFloat(1,0.3f,1);
        scaleAnim.setDuration(1000);
        scaleAnim.setRepeatCount(-1);
        scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scaleFloat1 = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        scaleAnim.start();

        ValueAnimator scaleAnim2= ValueAnimator.ofFloat(1,0.6f,1);
        scaleAnim2.setDuration(1000);
        scaleAnim2.setRepeatCount(-1);
        scaleAnim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scaleFloat2 = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        scaleAnim2.start();

        ValueAnimator rotateAnim= ValueAnimator.ofFloat(0, 180,360);
        rotateAnim.setDuration(1000);
        rotateAnim.setRepeatCount(-1);
        rotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degrees = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        rotateAnim.start();
        List<Animator> animators=new ArrayList<>();
        animators.add(scaleAnim);
        animators.add(scaleAnim2);
        animators.add(rotateAnim);
        return animators;
    }
}