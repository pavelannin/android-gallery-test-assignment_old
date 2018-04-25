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

package ru.annin.gallerytestassignment.presentation.gallery;

import android.support.annotation.NonNull;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewCompat;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Annin.
 */
public class GallerySharedElementEnterCallback extends SharedElementCallback {

    private WeakReference<View> listSharedElement;
    private WeakReference<View> detailSharedElement;

    public void setListSharedElement(@NonNull final View sharedElement) {
        listSharedElement = new WeakReference<>(sharedElement);
        detailSharedElement = null;
    }

    public void setDetailSharedElement(@NonNull final View sharedElement) {
        detailSharedElement = new WeakReference<>(sharedElement);
        listSharedElement = null;
    }

    @Override
    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
        removeObsoleteElements(names, sharedElements);
        mapSharedElement(names, sharedElements, getSharedElement());
    }

    @NonNull
    private View getSharedElement() {
        if (listSharedElement != null && listSharedElement.get() != null) {
            return listSharedElement.get();
        } else if (detailSharedElement != null && detailSharedElement.get() != null ) {
            return detailSharedElement.get();
        } else {
            throw new NullPointerException("Must set a shared element before transitioning.");
        }
    }

    private void removeObsoleteElements(@NonNull List<String> names, @NonNull Map<String, View> sharedElements) {
        names.clear();
        sharedElements.clear();
    }

    private void mapSharedElement(@NonNull List<String> names, @NonNull Map<String, View> sharedElements, @NonNull View view) {
        final String transitionName = ViewCompat.getTransitionName(view);
        if (transitionName != null) {
            names.add(transitionName);
            sharedElements.put(transitionName, view);
        }
    }
}
