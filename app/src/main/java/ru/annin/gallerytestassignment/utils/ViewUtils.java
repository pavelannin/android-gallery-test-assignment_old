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

package ru.annin.gallerytestassignment.utils;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.Locale;

/**
 * @author Pavel Annin.
 */
public class ViewUtils {

    private static final String TRANSITION_NAME_FORMAT = "transition_name_id_%d_%s";

    @NonNull
    public static String getTransitionName(@IdRes int id, @NonNull String postfix) {
        return String.format(Locale.ENGLISH, TRANSITION_NAME_FORMAT, id, postfix);
    }

    @NonNull
    public static String getTransitionName(@NonNull final View view, @NonNull String postfix) {
        return getTransitionName(view.getId(), postfix);
    }
}
