/*
 * MIT License
 *
 * Copyright (c) 2018 Pavel Annin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.annin.gallerytestassignment.presentation.common.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * @author Pavel Annin.
 */
public class DynamicHeightImageView extends AppCompatImageView {

    private static final String BUNDLE_SUPER = "ru.annin.gallerytestassignment.bundles.super";
    private static final String BUNDLE_RATIO = "ru.annin.gallerytestassignment.bundles.ratio";

    private float ratio = 0.0f;

    public DynamicHeightImageView(Context context) {
        super(context);
    }

    public DynamicHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicHeightImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (ratio != 0) {
            int width = getMeasuredWidth();
            int height = (int) (ratio * width);
            setMeasuredDimension(width, height);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_SUPER, super.onSaveInstanceState());
        bundle.putFloat(BUNDLE_RATIO, ratio);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(@Nullable Parcelable state) {
        if (state != null && state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            ratio = bundle.getFloat(BUNDLE_RATIO, 0.0f);
            super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_SUPER));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
        requestLayout();
    }
}