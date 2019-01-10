/*
 * Copyright 2016 Smacon Technologies Pvt Ltd as an unpublished work. All Rights
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
 * File Name               : MaterialEditText
 * Since                  : 06/09/17
 * Version Code & Project Name : v 1.0 Fish2marinelibrary
 * Author Name             : Aiswarya Saju
 */

package com.smacon.f2mlibrary.Edittext;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.smacon.f2mlibrary.Edittext.validation.METLengthChecker;
import com.smacon.f2mlibrary.Edittext.validation.METValidator;
import com.smacon.f2mlibrary.R;

import java.util.List;

/**
 * Created by Aiswarya on 06/09/17.
 */
public class MaterialEditText extends EditText {

    @IntDef({FLOATING_LABEL_NONE, FLOATING_LABEL_NORMAL, FLOATING_LABEL_HIGHLIGHT})
    public @interface FloatingLabelType {
    }

    public static final int FLOATING_LABEL_NONE = 0;
    public static final int FLOATING_LABEL_NORMAL = 1;
    public static final int FLOATING_LABEL_HIGHLIGHT = 2;

    private int extraPaddingTop;
    private int extraPaddingBottom;
    private int extraPaddingLeft;
    private int extraPaddingRight;
    private int floatingLabelTextSize;
    private int floatingLabelTextColor;
    private int bottomTextSize;
    private int floatingLabelPadding;
    private int bottomSpacing;
    private boolean floatingLabelEnabled;
    private boolean highlightFloatingLabel;
    private int baseColor;
    private int innerPaddingTop;
    private int innerPaddingBottom;
    private int innerPaddingLeft;
    private int innerPaddingRight;
    private int primaryColor;
    private int errorColor;
    private int minCharacters;
    private int maxCharacters;
    private boolean singleLineEllipsis;
    private boolean floatingLabelAlwaysShown;
    private boolean helperTextAlwaysShown;
    private int bottomEllipsisSize;
    private int minBottomLines;
    private int minBottomTextLines;
    private float currentBottomLines;
    private float bottomLines;
    private String helperText;
    private int helperTextColor = -1;
    private String tempErrorText;
    private float floatingLabelFraction;
    private boolean floatingLabelShown;
    private float focusFraction;
    private Typeface accentTypeface;
    private Typeface typeface;
    private CharSequence floatingLabelText;
    private boolean hideUnderline;
    private int underlineColor;
    private boolean autoValidate;
    private boolean charactersCountValid;
    private boolean floatingLabelAnimating;
    private boolean checkCharactersCountAtBeginning;
    private Bitmap[] iconLeftBitmaps;
    private Bitmap[] iconRightBitmaps;
    private boolean validateOnFocusLost;
    private boolean showClearButton;
    private boolean firstShown;
    private int iconSize;
    private int iconOuterWidth;
    private int iconOuterHeight;
    private int iconPadding;
    private boolean clearButtonTouched;
    private boolean clearButtonClicking;
    private ColorStateList textColorStateList;
    private ColorStateList textColorHintStateList;
    private ArgbEvaluator focusEvaluator = new ArgbEvaluator();
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    StaticLayout textLayout;
    ObjectAnimator labelAnimator;
    ObjectAnimator labelFocusAnimator;
    ObjectAnimator bottomLinesAnimator;
    OnFocusChangeListener innerFocusChangeListener;
    OnFocusChangeListener outerFocusChangeListener;
    private List<METValidator> validators;
    private METLengthChecker lengthChecker;

    public MaterialEditText(Context context) {
        super(context);
        init(context, null);
    }

    public MaterialEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialEditText(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        iconSize = getPixel(32);
        iconOuterWidth = getPixel(48);
        iconOuterHeight = getPixel(32);

        bottomSpacing = getResources().getDimensionPixelSize(R.dimen.inner_components_spacing);
        bottomEllipsisSize = getResources().getDimensionPixelSize(R.dimen.bottom_ellipsis_height);

        int defaultBaseColor = Color.BLACK;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialEditText);
        textColorStateList = typedArray.getColorStateList(R.styleable.MaterialEditText_met_textColor);
        textColorHintStateList = typedArray.getColorStateList(R.styleable.MaterialEditText_met_textColorHint);
        baseColor = typedArray.getColor(R.styleable.MaterialEditText_met_baseColor, defaultBaseColor);

        int defaultPrimaryColor;
        TypedValue primaryColorTypedValue = new TypedValue();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.getTheme().resolveAttribute(android.R.attr.colorPrimary, primaryColorTypedValue, true);
                defaultPrimaryColor = primaryColorTypedValue.data;
            } else {
                throw new RuntimeException("SDK_INT less than LOLLIPOP");
            }
        } catch (Exception e) {
            try {
                int colorPrimaryId = getResources().getIdentifier("colorPrimary", "attr", getContext().getPackageName());
                if (colorPrimaryId != 0) {
                    context.getTheme().resolveAttribute(colorPrimaryId, primaryColorTypedValue, true);
                    defaultPrimaryColor = primaryColorTypedValue.data;
                } else {
                    throw new RuntimeException("colorPrimary not found");
                }
            } catch (Exception e1) {
                defaultPrimaryColor = baseColor;
            }
        }

        primaryColor = typedArray.getColor(R.styleable.MaterialEditText_met_primaryColor, defaultPrimaryColor);
        setFloatingLabelInternal(typedArray.getInt(R.styleable.MaterialEditText_met_floatingLabel,
                Color.parseColor("#FF9800")));
        errorColor = typedArray.getColor(R.styleable.MaterialEditText_met_errorColor, Color.parseColor("#D50000"));
        minCharacters = typedArray.getInt(R.styleable.MaterialEditText_met_minCharacters, 0);
        maxCharacters = typedArray.getInt(R.styleable.MaterialEditText_met_maxCharacters, 0);
        singleLineEllipsis = typedArray.getBoolean(R.styleable.MaterialEditText_met_singleLineEllipsis, false);
        helperText = typedArray.getString(R.styleable.MaterialEditText_met_helperText);
        helperTextColor = typedArray.getColor(R.styleable.MaterialEditText_met_helperTextColor,
                Color.parseColor("#263238"));
        minBottomTextLines = typedArray.getInt(R.styleable.MaterialEditText_met_minBottomTextLines, 0);
        String fontPathForAccent = typedArray.getString(R.styleable.MaterialEditText_met_accentTypeface);
        if (fontPathForAccent != null && !isInEditMode()) {
            accentTypeface = getCustomTypeface(fontPathForAccent);
            textPaint.setTypeface(accentTypeface);
        }
        String fontPathForView = typedArray.getString(R.styleable.MaterialEditText_met_typeface);
        if (fontPathForView != null && !isInEditMode()) {
            typeface = getCustomTypeface(fontPathForView);
            setTypeface(typeface);
        }
        floatingLabelText = typedArray.getString(R.styleable.MaterialEditText_met_floatingLabelText);
        if (floatingLabelText == null) {
            floatingLabelText = getHint();
        }
        floatingLabelPadding = typedArray.getDimensionPixelSize
                (R.styleable.MaterialEditText_met_floatingLabelPadding, bottomSpacing);
        floatingLabelTextSize = typedArray.getDimensionPixelSize
                (R.styleable.MaterialEditText_met_floatingLabelTextSize, getResources().getDimensionPixelSize
                        (R.dimen.floating_label_text_size));
        floatingLabelTextColor = typedArray.getColor
                (R.styleable.MaterialEditText_met_floatingLabelTextColor, -1);
        floatingLabelAnimating = typedArray.getBoolean
                (R.styleable.MaterialEditText_met_floatingLabelAnimating, true);
        bottomTextSize = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_bottomTextSize,
                getResources().getDimensionPixelSize(R.dimen.bottom_text_size));
        hideUnderline = typedArray.getBoolean(R.styleable.MaterialEditText_met_hideUnderline, false);
        underlineColor = typedArray.getColor(R.styleable.MaterialEditText_met_underlineColor, -2);
        autoValidate = typedArray.getBoolean(R.styleable.MaterialEditText_met_autoValidate, false);
        iconLeftBitmaps = generateIconBitmaps(typedArray.getResourceId
                (R.styleable.MaterialEditText_met_iconLeft, -1));
        iconRightBitmaps = generateIconBitmaps(typedArray.getResourceId
                (R.styleable.MaterialEditText_met_iconRight, -1));
        showClearButton = typedArray.getBoolean(R.styleable.MaterialEditText_met_clearButton, false);

        iconPadding = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_iconPadding, getPixel(16));
        floatingLabelAlwaysShown = typedArray.getBoolean
                (R.styleable.MaterialEditText_met_floatingLabelAlwaysShown, false);
        helperTextAlwaysShown = typedArray.getBoolean
                (R.styleable.MaterialEditText_met_helperTextAlwaysShown, false);
        validateOnFocusLost = typedArray.getBoolean
                (R.styleable.MaterialEditText_met_validateOnFocusLost, false);
        checkCharactersCountAtBeginning = typedArray.getBoolean
                (R.styleable.MaterialEditText_met_checkCharactersCountAtBeginning, true);
        typedArray.recycle();

        int[] paddings = new int[]{
                android.R.attr.padding, // 0
                android.R.attr.paddingLeft, // 1
                android.R.attr.paddingTop, // 2
                android.R.attr.paddingRight, // 3
                android.R.attr.paddingBottom // 4
        };
        TypedArray paddingsTypedArray = context.obtainStyledAttributes(attrs, paddings);
        int padding = paddingsTypedArray.getDimensionPixelSize(0, 0);
        innerPaddingLeft = paddingsTypedArray.getDimensionPixelSize(1, padding);
        innerPaddingTop = paddingsTypedArray.getDimensionPixelSize(2, padding);
        innerPaddingRight = paddingsTypedArray.getDimensionPixelSize(3, padding);
        innerPaddingBottom = paddingsTypedArray.getDimensionPixelSize(4, padding);
        paddingsTypedArray.recycle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(null);
        } else {
            setBackgroundDrawable(null);
        }
        if (singleLineEllipsis) {
            TransformationMethod transformationMethod = getTransformationMethod();
            setSingleLine();
            setTransformationMethod(transformationMethod);
        }
        initMinBottomLines();
        initPadding();
        initText();
        initFloatingLabel();
        initTextWatcher();
        checkCharactersCount();
    }

    private void initText() {
        if (!TextUtils.isEmpty(getText())) {
            CharSequence text = getText();
            setText(null);
            resetHintTextColor();
            setText(text);
            setSelection(text.length());
            floatingLabelFraction = 1;
            floatingLabelShown = true;
        } else {
            resetHintTextColor();
        }
        resetTextColor();
    }

    private void initTextWatcher() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkCharactersCount();
                if (autoValidate) {
                    validate();
                } else {
                    setError(null);
                }
                postInvalidate();
            }
        });
    }

    private Typeface getCustomTypeface(@NonNull String fontPath) {
        return Typeface.createFromAsset(getContext().getAssets(), fontPath);
    }

    public boolean isShowClearButton() {
        return showClearButton;
    }

    private Bitmap[] generateIconBitmaps(@DrawableRes int origin) {
        if (origin == -1) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), origin, options);
        int size = Math.max(options.outWidth, options.outHeight);
        options.inSampleSize = size > iconSize ? size / iconSize : 1;
        options.inJustDecodeBounds = false;
        return generateIconBitmaps(BitmapFactory.decodeResource(getResources(), origin, options));
    }

    private Bitmap[] generateIconBitmaps(Bitmap origin) {
        if (origin == null) {
            return null;
        }
        Bitmap[] iconBitmaps = new Bitmap[4];
        origin = scaleIcon(origin);
        iconBitmaps[0] = origin.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(iconBitmaps[0]);
        canvas.drawColor(baseColor & 0x00ffffff | (Colors.isLight(baseColor) ? 0xff000000 : 0x8a000000),
                PorterDuff.Mode.SRC_IN);
        iconBitmaps[1] = origin.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(iconBitmaps[1]);
        canvas.drawColor(primaryColor, PorterDuff.Mode.SRC_IN);
        iconBitmaps[2] = origin.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(iconBitmaps[2]);
        canvas.drawColor(baseColor & 0x00ffffff | (Colors.isLight(baseColor) ? 0x4c000000 : 0x42000000),
                PorterDuff.Mode.SRC_IN);
        iconBitmaps[3] = origin.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(iconBitmaps[3]);
        canvas.drawColor(errorColor, PorterDuff.Mode.SRC_IN);
        return iconBitmaps;
    }

    private Bitmap scaleIcon(Bitmap origin) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        int size = Math.max(width, height);
        if (size == iconSize) {
            return origin;
        } else if (size > iconSize) {
            int scaledWidth;
            int scaledHeight;
            if (width > iconSize) {
                scaledWidth = iconSize;
                scaledHeight = (int) (iconSize * ((float) height / width));
            } else {
                scaledHeight = iconSize;
                scaledWidth = (int) (iconSize * ((float) width / height));
            }
            return Bitmap.createScaledBitmap(origin, scaledWidth, scaledHeight, false);
        } else {
            return origin;
        }
    }

    public float getFloatingLabelFraction() {
        return floatingLabelFraction;
    }

    public void setFloatingLabelFraction(float floatingLabelFraction) {
        this.floatingLabelFraction = floatingLabelFraction;
        invalidate();
    }

    public float getFocusFraction() {
        return focusFraction;
    }

    public void setFocusFraction(float focusFraction) {
        this.focusFraction = focusFraction;
        invalidate();
    }

    public float getCurrentBottomLines() {
        return currentBottomLines;
    }

    public void setCurrentBottomLines(float currentBottomLines) {
        this.currentBottomLines = currentBottomLines;
        initPadding();
    }

    public boolean isFloatingLabelAlwaysShown() {
        return floatingLabelAlwaysShown;
    }

    public void setFloatingLabelAlwaysShown(boolean floatingLabelAlwaysShown) {
        this.floatingLabelAlwaysShown = floatingLabelAlwaysShown;
        invalidate();
    }

    @Nullable
    public Typeface getAccentTypeface() {
        return accentTypeface;
    }

    public void setAccentTypeface(Typeface accentTypeface) {
        this.accentTypeface = accentTypeface;
        this.textPaint.setTypeface(accentTypeface);
        postInvalidate();
    }


    private int getPixel(int dp) {
        return Density.dp2px(getContext(), dp);
    }

    private void initPadding() {
        extraPaddingTop = floatingLabelEnabled ? floatingLabelTextSize + floatingLabelPadding : floatingLabelPadding;
        textPaint.setTextSize(bottomTextSize);
        Paint.FontMetrics textMetrics = textPaint.getFontMetrics();
        extraPaddingBottom = (int) ((textMetrics.descent - textMetrics.ascent) * currentBottomLines) +
                (hideUnderline ? bottomSpacing : bottomSpacing * 2);
        extraPaddingLeft = iconLeftBitmaps == null ? 0 : (iconOuterWidth + iconPadding);
        extraPaddingRight = iconRightBitmaps == null ? 0 : (iconOuterWidth + iconPadding);
        correctPaddings();
    }

    private void initMinBottomLines() {
        boolean extendBottom = minCharacters > 0 || maxCharacters > 0 || singleLineEllipsis ||
                tempErrorText != null || helperText != null;
        currentBottomLines = minBottomLines = minBottomTextLines > 0 ? minBottomTextLines : extendBottom ? 1 : 0;
    }

    @Deprecated
    @Override
    public final void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
    }


    private void correctPaddings() {
        int buttonsWidthLeft = 0, buttonsWidthRight = 0;
        int buttonsWidth = iconOuterWidth * getButtonsCount();
        if (isRTL()) {
            buttonsWidthLeft = buttonsWidth;
        } else {
            buttonsWidthRight = buttonsWidth;
        }
        super.setPadding(innerPaddingLeft + extraPaddingLeft + buttonsWidthLeft, innerPaddingTop + extraPaddingTop,
                innerPaddingRight + extraPaddingRight + buttonsWidthRight, innerPaddingBottom + extraPaddingBottom);
    }

    private int getButtonsCount() {
        return isShowClearButton() ? 1 : 0;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!firstShown) {
            firstShown = true;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            adjustBottomLines();
        }
    }

    private boolean adjustBottomLines() {

        if (getWidth() == 0) {
            return false;
        }
        int destBottomLines;
        textPaint.setTextSize(bottomTextSize);
        if (tempErrorText != null || helperText != null) {
            Layout.Alignment alignment = (getGravity() & Gravity.RIGHT) == Gravity.RIGHT || isRTL() ?
                    Layout.Alignment.ALIGN_OPPOSITE : (getGravity() & Gravity.LEFT) == Gravity.LEFT ?
                    Layout.Alignment.ALIGN_NORMAL : Layout.Alignment.ALIGN_CENTER;
            textLayout = new StaticLayout(tempErrorText != null ? tempErrorText : helperText, textPaint,
                    getWidth() - getBottomTextLeftOffset() - getBottomTextRightOffset() - getPaddingLeft() -
                            getPaddingRight(), alignment, 1.0f, 0.0f, true);
            destBottomLines = Math.max(textLayout.getLineCount(), minBottomTextLines);
        } else {
            destBottomLines = minBottomLines;
        }
        if (bottomLines != destBottomLines) {
            getBottomLinesAnimator(destBottomLines).start();
        }
        bottomLines = destBottomLines;
        return true;
    }

    public int getInnerPaddingLeft() {
        return innerPaddingLeft;
    }

    public int getInnerPaddingRight() {
        return innerPaddingRight;
    }

    private void initFloatingLabel() {

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (floatingLabelEnabled) {
                    if (s.length() == 0) {
                        if (floatingLabelShown) {
                            floatingLabelShown = false;
                            getLabelAnimator().reverse();
                        }
                    } else if (!floatingLabelShown) {
                        floatingLabelShown = true;
                        getLabelAnimator().start();
                    }
                }
            }
        });

        innerFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (floatingLabelEnabled && highlightFloatingLabel) {
                    if (hasFocus) {
                        getLabelFocusAnimator().start();
                    } else {
                        getLabelFocusAnimator().reverse();
                    }
                }
                if (validateOnFocusLost && !hasFocus) {
                    validate();
                }
                if (outerFocusChangeListener != null) {
                    outerFocusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        };
        super.setOnFocusChangeListener(innerFocusChangeListener);
    }


    private void resetTextColor() {
        if (textColorStateList == null) {
            textColorStateList = new ColorStateList(new int[][]{new int[]{android.R.attr.state_enabled}, EMPTY_STATE_SET}, new int[]{baseColor & 0x00ffffff | 0xdf000000, baseColor & 0x00ffffff | 0x44000000});
            setTextColor(textColorStateList);
        } else {
            setTextColor(textColorStateList);
        }
    }

    private void resetHintTextColor() {
        if (textColorHintStateList == null) {
            setHintTextColor(baseColor & 0x00ffffff | 0x44000000);
        } else {
            setHintTextColor(textColorHintStateList);
        }
    }

    private void setFloatingLabelInternal(int mode) {
        switch (mode) {
            case FLOATING_LABEL_NORMAL:
                floatingLabelEnabled = true;
                highlightFloatingLabel = false;
                break;
            case FLOATING_LABEL_HIGHLIGHT:
                floatingLabelEnabled = true;
                highlightFloatingLabel = true;
                break;
            default:
                floatingLabelEnabled = false;
                highlightFloatingLabel = false;
                break;
        }
    }

    @Override
    public void setError(CharSequence errorText) {
        tempErrorText = errorText == null ? null : errorText.toString();
        if (adjustBottomLines()) {
            postInvalidate();
        }
    }
    @Override
    public CharSequence getError() {
        return tempErrorText;
    }
    private boolean isInternalValid() {
        return tempErrorText == null && isCharactersCountValid();
    }

    public boolean validate() {
        if (validators == null || validators.isEmpty()) {
            return true;
        }

        CharSequence text = getText();
        boolean isEmpty = text.length() == 0;

        boolean isValid = true;
        for (METValidator validator : validators) {
            isValid = isValid && validator.isValid(text, isEmpty);
            if (!isValid) {
                setError(validator.getErrorMessage());
                break;
            }
        }
        if (isValid) {
            setError(null);
        }

        postInvalidate();
        return isValid;
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        if (innerFocusChangeListener == null) {
            super.setOnFocusChangeListener(listener);
        } else {
            outerFocusChangeListener = listener;
        }
    }
    private ObjectAnimator getLabelAnimator() {
        if (labelAnimator == null) {
            labelAnimator = ObjectAnimator.ofFloat(this, "floatingLabelFraction", 0f, 1f);
        }
        labelAnimator.setDuration(floatingLabelAnimating ? 300 : 0);
        return labelAnimator;
    }

    private ObjectAnimator getLabelFocusAnimator() {
        if (labelFocusAnimator == null) {
            labelFocusAnimator = ObjectAnimator.ofFloat(this, "focusFraction", 0f, 1f);
        }
        return labelFocusAnimator;
    }

    private ObjectAnimator getBottomLinesAnimator(float destBottomLines) {
        if (bottomLinesAnimator == null) {
            bottomLinesAnimator = ObjectAnimator.ofFloat(this, "currentBottomLines", destBottomLines);
        } else {
            bottomLinesAnimator.cancel();
            bottomLinesAnimator.setFloatValues(destBottomLines);
        }
        return bottomLinesAnimator;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        int startX = getScrollX() + (iconLeftBitmaps == null ? 0 : (iconOuterWidth + iconPadding));
        int endX = getScrollX() + (iconRightBitmaps == null ? getWidth() : getWidth() - iconOuterWidth - iconPadding);
        int lineStartY = getScrollY() + getHeight() - getPaddingBottom();

        paint.setAlpha(255);
        if (iconLeftBitmaps != null) {
            Bitmap icon = iconLeftBitmaps[!isInternalValid() ? 3 : !isEnabled() ? 2 : hasFocus() ? 1 : 0];
            int iconLeft = startX - iconPadding - iconOuterWidth + (iconOuterWidth - icon.getWidth()) / 2;
            int iconTop = lineStartY + bottomSpacing - iconOuterHeight + (iconOuterHeight - icon.getHeight()) / 2;
            canvas.drawBitmap(icon, iconLeft, iconTop, paint);
        }
        if (iconRightBitmaps != null) {
            Bitmap icon = iconRightBitmaps[!isInternalValid() ? 3 : !isEnabled() ? 2 : hasFocus() ? 1 : 0];
            int iconRight = endX + iconPadding + (iconOuterWidth - icon.getWidth()) / 2;
            int iconTop = lineStartY + bottomSpacing - iconOuterHeight + (iconOuterHeight - icon.getHeight()) / 2;
            canvas.drawBitmap(icon, iconRight, iconTop, paint);
        }

        if (hasFocus() && showClearButton && !TextUtils.isEmpty(getText())) {
            paint.setAlpha(255);
            int buttonLeft;
            if (isRTL()) {
                buttonLeft = startX;
            } else {
                buttonLeft = endX - iconOuterWidth;
            }
        }

        if (!hideUnderline) {
            lineStartY += bottomSpacing;
            if (!isInternalValid()) {
                paint.setColor(errorColor);
                canvas.drawRect(startX, lineStartY, endX, lineStartY + getPixel(2), paint);
            } else if (!isEnabled()) {
                paint.setColor(underlineColor != -1 ? underlineColor : baseColor & 0x00ffffff | 0x44000000);
                float interval = getPixel(1);
                for (float xOffset = 0; xOffset < getWidth(); xOffset += interval * 3) {
                    canvas.drawRect(startX + xOffset, lineStartY, startX + xOffset + interval, lineStartY + getPixel(1), paint);
                }
            } else if (hasFocus()) {
                paint.setColor(primaryColor);
                canvas.drawRect(startX, lineStartY, endX, lineStartY + getPixel(2), paint);
            } else {
                paint.setColor(underlineColor != -1 ? underlineColor : baseColor & 0x00ffffff | 0x1E000000);
                canvas.drawRect(startX, lineStartY, endX, lineStartY + getPixel(1), paint);
            }
        }

        textPaint.setTextSize(bottomTextSize);
        Paint.FontMetrics textMetrics = textPaint.getFontMetrics();
        float relativeHeight = -textMetrics.ascent - textMetrics.descent;
        float bottomTextPadding = bottomTextSize + textMetrics.ascent + textMetrics.descent;

        if ((hasFocus() && hasCharactersCounter()) || !isCharactersCountValid()) {
            textPaint.setColor(isCharactersCountValid() ? (baseColor & 0x00ffffff | 0x44000000) : errorColor);
            String charactersCounterText = getCharactersCounterText();
            canvas.drawText(charactersCounterText, isRTL() ? startX : endX - textPaint.measureText(charactersCounterText),
                    lineStartY + bottomSpacing + relativeHeight, textPaint);
        }

        if (textLayout != null) {
            if (tempErrorText != null || ((helperTextAlwaysShown || hasFocus()) && !TextUtils.isEmpty(helperText))) { // error text or helper text
                textPaint.setColor(tempErrorText != null ? errorColor : helperTextColor != -1 ? helperTextColor : (baseColor &
                        0x00ffffff | 0x44000000));
                canvas.save();
                if (isRTL()) {
                    canvas.translate(endX - textLayout.getWidth(), lineStartY + bottomSpacing - bottomTextPadding);
                } else {
                    canvas.translate(startX + getBottomTextLeftOffset(), lineStartY + bottomSpacing - bottomTextPadding);
                }
                textLayout.draw(canvas);
                canvas.restore();
            }
        }

        if (floatingLabelEnabled && !TextUtils.isEmpty(floatingLabelText)) {
            textPaint.setTextSize(floatingLabelTextSize);
            textPaint.setColor((Integer) focusEvaluator.evaluate(focusFraction, floatingLabelTextColor != -1 ?
                    floatingLabelTextColor : (baseColor & 0x00ffffff | 0x44000000), primaryColor));

            float floatingLabelWidth = textPaint.measureText(floatingLabelText.toString());
            int floatingLabelStartX;
            if ((getGravity() & Gravity.RIGHT) == Gravity.RIGHT || isRTL()) {
                floatingLabelStartX = (int) (endX - floatingLabelWidth);
            } else if ((getGravity() & Gravity.LEFT) == Gravity.LEFT) {
                floatingLabelStartX = startX;
            } else {
                floatingLabelStartX = startX + (int) (getInnerPaddingLeft() + (getWidth() - getInnerPaddingLeft() -
                        getInnerPaddingRight() - floatingLabelWidth) / 2);
            }

            int distance = floatingLabelPadding;
            int floatingLabelStartY = (int) (innerPaddingTop + floatingLabelTextSize + floatingLabelPadding -
                    distance * (floatingLabelAlwaysShown ? 1 : floatingLabelFraction) + getScrollY());

            int alpha = ((int) ((floatingLabelAlwaysShown ? 1 : floatingLabelFraction) * 0xff * (0.74f *
                    focusFraction + 0.26f) * (floatingLabelTextColor != -1 ? 1 : Color.alpha(floatingLabelTextColor) / 256f)));
            textPaint.setAlpha(alpha);

            canvas.drawText(floatingLabelText.toString(), floatingLabelStartX, floatingLabelStartY, textPaint);
        }

        if (hasFocus() && singleLineEllipsis && getScrollX() != 0) {
            paint.setColor(isInternalValid() ? primaryColor : errorColor);
            float startY = lineStartY + bottomSpacing;
            int ellipsisStartX;
            if (isRTL()) {
                ellipsisStartX = endX;
            } else {
                ellipsisStartX = startX;
            }
            int signum = isRTL() ? -1 : 1;
            canvas.drawCircle(ellipsisStartX + signum * bottomEllipsisSize / 2, startY + bottomEllipsisSize / 2,
                    bottomEllipsisSize / 2, paint);
            canvas.drawCircle(ellipsisStartX + signum * bottomEllipsisSize * 5 / 2, startY + bottomEllipsisSize / 2,
                    bottomEllipsisSize / 2, paint);
            canvas.drawCircle(ellipsisStartX + signum * bottomEllipsisSize * 9 / 2, startY + bottomEllipsisSize / 2,
                    bottomEllipsisSize / 2, paint);
        }
        super.onDraw(canvas);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isRTL() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return false;
        }
        Configuration config = getResources().getConfiguration();
        return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    private int getBottomTextLeftOffset() {
        return isRTL() ? getCharactersCounterWidth() : getBottomEllipsisWidth();
    }

    private int getBottomTextRightOffset() {
        return isRTL() ? getBottomEllipsisWidth() : getCharactersCounterWidth();
    }

    private int getCharactersCounterWidth() {
        return hasCharactersCounter() ? (int) textPaint.measureText(getCharactersCounterText()) : 0;
    }

    private int getBottomEllipsisWidth() {
        return singleLineEllipsis ? (bottomEllipsisSize * 5 + getPixel(4)) : 0;
    }

    private void checkCharactersCount() {
        if ((!firstShown && !checkCharactersCountAtBeginning) || !hasCharactersCounter()) {
            charactersCountValid = true;
        } else {
            CharSequence text = getText();
            int count = text == null ? 0 : checkLength(text);
            charactersCountValid = (count >= minCharacters && (maxCharacters <= 0 || count <= maxCharacters));
        }
    }

    public boolean isCharactersCountValid() {
        return charactersCountValid;
    }

    private boolean hasCharactersCounter() {
        return minCharacters > 0 || maxCharacters > 0;
    }

    private String getCharactersCounterText() {
        String text;
        if (minCharacters <= 0) {
            text = isRTL() ? maxCharacters + " / " + checkLength(getText()) : checkLength(getText()) + " / " + maxCharacters;
        } else if (maxCharacters <= 0) {
            text = isRTL() ? "+" + minCharacters + " / " + checkLength(getText()) : checkLength(getText()) + " / " +
                    minCharacters + "+";
        } else {
            text = isRTL() ? maxCharacters + "-" + minCharacters + " / " + checkLength(getText()) : checkLength(getText())
                    + " / " + minCharacters + "-" + maxCharacters;
        }
        return text;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (singleLineEllipsis && getScrollX() > 0 && event.getAction() == MotionEvent.ACTION_DOWN && event.getX()
                < getPixel(4 * 5) && event.getY() > getHeight() - extraPaddingBottom - innerPaddingBottom && event.getY()
                < getHeight() - innerPaddingBottom) {
            setSelection(0);
            return false;
        }
        if (hasFocus() && showClearButton) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (insideClearButton(event)) {
                        clearButtonTouched = true;
                        clearButtonClicking = true;
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (clearButtonClicking && !insideClearButton(event)) {
                        clearButtonClicking = false;
                    }
                    if (clearButtonTouched) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (clearButtonClicking) {
                        if (!TextUtils.isEmpty(getText())) {
                            setText(null);
                        }
                        clearButtonClicking = false;
                    }
                    if (clearButtonTouched) {
                        clearButtonTouched = false;
                        return true;
                    }
                    clearButtonTouched = false;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    clearButtonTouched = false;
                    clearButtonClicking = false;
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean insideClearButton(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int startX = getScrollX() + (iconLeftBitmaps == null ? 0 : (iconOuterWidth + iconPadding));
        int endX = getScrollX() + (iconRightBitmaps == null ? getWidth() : getWidth() - iconOuterWidth - iconPadding);
        int buttonLeft;
        if (isRTL()) {
            buttonLeft = startX;
        } else {
            buttonLeft = endX - iconOuterWidth;
        }
        int buttonTop = getScrollY() + getHeight() - getPaddingBottom() + bottomSpacing - iconOuterHeight;
        return (x >= buttonLeft && x < buttonLeft + iconOuterWidth && y >= buttonTop && y < buttonTop + iconOuterHeight);
    }

    private int checkLength(CharSequence text) {
        if (lengthChecker==null) return text.length();
        return lengthChecker.getLength(text);
    }
}