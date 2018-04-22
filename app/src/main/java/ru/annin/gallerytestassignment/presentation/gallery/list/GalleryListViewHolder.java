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

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import ru.annin.gallerytestassignment.R;
import ru.annin.gallerytestassignment.data.entity.Photo;

/**
 * @author Pavel Annin.
 */
class GalleryListViewHolder {

    private static final int SPAN_COUNT = 2;

    interface OnClickListener {
        void onRefresh();

        void onPhotoClick(int position, @NonNull Photo photo);
    }

    // View's
    private final RecyclerView recyclerView;
    private final SwipeRefreshLayout swipeRefreshLayout;

    // Adapter's
    private final GalleryListAdapter galleryAdapter;

    // Listener's
    private OnClickListener listener;

    GalleryListViewHolder(@NonNull ViewGroup rootViewGroup) {
        recyclerView = rootViewGroup.findViewById(R.id.recycler_view);
        swipeRefreshLayout = rootViewGroup.findViewById(R.id.swipe_refresh);

        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        galleryAdapter = new GalleryListAdapter();

        // Setup
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(galleryAdapter);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (listener != null) {
                listener.onRefresh();
            }
        });
        galleryAdapter.setListener((position, photo) -> {
            if (listener != null) {
                listener.onPhotoClick(position, photo);
            }
        });
    }

    @NonNull
    public GalleryListAdapter getGallery() {
        return galleryAdapter;
    }

    public void toggleRefreshingVisible(boolean isVisible) {
        swipeRefreshLayout.setRefreshing(isVisible);
    }

    public void setPhotoPosition(int position) {
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager instanceof StaggeredGridLayoutManager) {
                final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                staggeredGridLayoutManager.scrollToPosition(position);
            } else {
                throw new IllegalArgumentException(String.format("Unknown layout manager class: %s", layoutManager.getClass().getName()));
            }
        }
    }

    public void setListener(@NonNull OnClickListener listener) {
        this.listener = listener;
    }
}
