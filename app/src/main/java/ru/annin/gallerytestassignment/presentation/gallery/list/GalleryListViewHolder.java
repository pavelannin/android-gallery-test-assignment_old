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
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import ru.annin.gallerytestassignment.R;
import ru.annin.gallerytestassignment.data.entity.Photo;
import ru.annin.gallerytestassignment.presentation.common.decoration.GridMarginDecoration;

/**
 * @author Pavel Annin.
 */
class GalleryListViewHolder {

    private static final int SPAN_COUNT = 3;
    private static final int ITEM_SPACE_DIP = 4;

    interface OnClickListener {
        void onRefresh();

        void onPhotoClick(int position, @NonNull Photo photo, @NonNull View sharedElement);
    }

    private final Resources resources;

    // View's
    private final RecyclerView recyclerView;
    private final SwipeRefreshLayout swipeRefreshLayout;

    // Adapter's
    private final GalleryListAdapter galleryAdapter;

    // Listener's
    private OnClickListener listener;

    GalleryListViewHolder(@NonNull ViewGroup rootViewGroup) {
        resources = rootViewGroup.getResources();
        recyclerView = rootViewGroup.findViewById(R.id.recycler_view);
        swipeRefreshLayout = rootViewGroup.findViewById(R.id.swipe_refresh);
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        galleryAdapter = new GalleryListAdapter();

        // Setup
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(getSpaceDecoration(ITEM_SPACE_DIP));
        recyclerView.setAdapter(galleryAdapter);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (listener != null) {
                listener.onRefresh();
            }
        });
        galleryAdapter.setListener((position, photo, sharedElement) -> {
            if (listener != null) {
                listener.onPhotoClick(position, photo, sharedElement);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    layoutManager.invalidateSpanAssignments();
                }
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
        recyclerView.scrollToPosition(position);
    }

    @Nullable
    public View getSharedElementById(int position) {
        final RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder != null && viewHolder instanceof ItemPhotoViewHolder) {
            final ItemPhotoViewHolder itemPhotoViewHolder = (ItemPhotoViewHolder) viewHolder;
            return itemPhotoViewHolder.getSharedElement();
        }
        return null;
    }

    public void setOnPreDrawPhotoListListener(@NonNull Runnable runnable) {
        recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                runnable.run();
                return true;
            }
        });
    }

    public void setListener(@NonNull OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    private RecyclerView.ItemDecoration getSpaceDecoration(int space) {
        final float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, space, resources.getDisplayMetrics());
        return new GridMarginDecoration((int) px);
    }
}
