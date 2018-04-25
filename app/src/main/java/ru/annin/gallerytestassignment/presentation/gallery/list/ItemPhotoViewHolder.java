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

package ru.annin.gallerytestassignment.presentation.gallery.list;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import ru.annin.gallerytestassignment.R;
import ru.annin.gallerytestassignment.data.entity.Photo;
import ru.annin.gallerytestassignment.presentation.common.widget.DynamicHeightImageView;
import ru.annin.gallerytestassignment.utils.glide.GlideApp;

/**
 * @author Pavel Annin.
 */
public class ItemPhotoViewHolder extends RecyclerView.ViewHolder {

    // View's
    private final ViewGroup containerViewGroup;
    private final DynamicHeightImageView photoImageView;

    ItemPhotoViewHolder(@NonNull View rootView) {
        super(rootView);
        containerViewGroup = rootView.findViewById(R.id.main_container);
        photoImageView = rootView.findViewById(R.id.iv_photo);
    }

    public void bindToPhoto(@NonNull Photo photo) {
        float ratio = photo.getHeight() / (float) photo.getWidth();
        photoImageView.setRatio(ratio);

        final Resources resources = itemView.getResources();
        final Drawable placeholderDrawable = VectorDrawableCompat.create(resources, R.drawable.ic_placeholder, null);
        final Drawable errorDrawable = VectorDrawableCompat.create(resources, R.drawable.ic_close_outline, null);

        GlideApp.with(photoImageView)
                .load(photo.getUrl())
                .placeholder(placeholderDrawable)
                .error(errorDrawable)
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(photo.getWidth(), photo.getHeight())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(photoImageView);
    }

    @NonNull
    public View getSharedElement() {
        return photoImageView;
    }

    public void setOnItemClickListener(View.OnClickListener listener) {
        containerViewGroup.setOnClickListener(listener);
    }
}