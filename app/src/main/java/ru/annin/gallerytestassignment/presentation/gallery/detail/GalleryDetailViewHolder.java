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

package ru.annin.gallerytestassignment.presentation.gallery.detail;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import ru.annin.gallerytestassignment.R;

/**
 * @author Pavel Annin.
 */
class GalleryDetailViewHolder {

    interface OnClickListener {
        void onBackClick();
    }

    // View's
    private final Toolbar toolbar;
    private final RecyclerView recyclerView;

    // Adapter's
    private final GalleryDetailAdapter galleryAdapter;

    // Listener's
    private OnClickListener listener;

    GalleryDetailViewHolder(@NonNull ViewGroup rootViewGroup) {
        toolbar = rootViewGroup.findViewById(R.id.toolbar);
        recyclerView = rootViewGroup.findViewById(R.id.recycler_view);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(rootViewGroup.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        final SnapHelper snapHelper = new PagerSnapHelper();
        galleryAdapter = new GalleryDetailAdapter();

        // Setup
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(galleryAdapter);
        snapHelper.attachToRecyclerView(recyclerView);

        toolbar.setNavigationOnClickListener(v -> {
            if (listener != null) {
                listener.onBackClick();
            }
        });
    }

    @NonNull
    public GalleryDetailAdapter getGallery() {
        return galleryAdapter;
    }

    public int getPhotoPosition() {
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                return linearLayoutManager.findFirstVisibleItemPosition();
            } else {
                throw new IllegalArgumentException(String.format("Unknown layout manager class: %s", layoutManager.getClass().getName()));
            }
        }
        return RecyclerView.NO_POSITION;
    }

    public void setPhotoPosition(int position) {
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                linearLayoutManager.scrollToPosition(position);
            } else {
                throw new IllegalArgumentException(String.format("Unknown layout manager class: %s", layoutManager.getClass().getName()));
            }
        }
    }

    @Nullable
    public View getSharedElementByPosition(int position) {
        final RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder != null && viewHolder instanceof ItemPhotoDetailViewHolder) {
            final ItemPhotoDetailViewHolder itemPhotoDetailViewHolder = (ItemPhotoDetailViewHolder) viewHolder;
            return itemPhotoDetailViewHolder.getSharedElement();
        }
        return null;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }
}
