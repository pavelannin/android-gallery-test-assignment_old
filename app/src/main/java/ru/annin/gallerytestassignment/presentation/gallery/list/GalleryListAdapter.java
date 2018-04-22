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

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.annin.gallerytestassignment.R;
import ru.annin.gallerytestassignment.data.entity.Photo;
import ru.annin.gallerytestassignment.presentation.common.viewholder.ItemProgressIndicatorViewHolder;

/**
 * @author Pavel Annin.
 */
public class GalleryListAdapter extends PagedListAdapter<Photo, RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_PROGRESS_INDICATOR = 1;
    private static final DiffUtil.ItemCallback<Photo> DIFF_CALLBACK = new DiffUtil.ItemCallback<Photo>() {
        @Override
        public boolean areItemsTheSame(Photo oldItem, Photo newItem) {
            return TextUtils.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(Photo oldItem, Photo newItem) {
            return TextUtils.equals(oldItem.getId(), newItem.getId());
        }
    };

    interface OnClickListener {
        void onItemClick(int position, @NonNull Photo photo);
    }


    private boolean isFooterProgressIndicatorVisible = false;
    private OnClickListener listener;


    GalleryListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_ITEM: {
                final View rootView = inflater.inflate(R.layout.item_gallery, parent, false);
                return new ItemPhotoViewHolder(rootView);
            }
            case TYPE_PROGRESS_INDICATOR: {
                final View rootView = inflater.inflate(R.layout.item_progress_indicator, parent, false);
                return new ItemProgressIndicatorViewHolder(rootView);
            }
        }
        throw new IllegalArgumentException(String.format("Unknown view type: %d", viewType));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemPhotoViewHolder) {
            final ItemPhotoViewHolder viewHolder = (ItemPhotoViewHolder) holder;
            final Photo item = getItem(position);
            if (item != null) {
                viewHolder.bindToPhoto(item);
                viewHolder.itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItemClick(position, item);
                    }
                });
            }
            setFullSpan(holder.itemView, false);
        } else if (holder instanceof ItemProgressIndicatorViewHolder) {
            final ItemProgressIndicatorViewHolder viewHolder = (ItemProgressIndicatorViewHolder) holder;
            viewHolder.toggleVisible(isFooterProgressIndicatorVisible);
            setFullSpan(holder.itemView, true);
        } else {
            throw new IllegalArgumentException(String.format("Unknown holder class: %s", holder.getClass().getName()));
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (hasFooterRow() ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (hasFooterRow() && position == getItemCount() - 1) {
            return TYPE_PROGRESS_INDICATOR;
        } else {
            return TYPE_ITEM;
        }
    }

    public void toggleFooterProgressIndicatorVisible(boolean isVisible) {
        final boolean previousState = isFooterProgressIndicatorVisible;
        final boolean hadFooterRow = hasFooterRow();
        isFooterProgressIndicatorVisible = isVisible;
        final boolean hasFooterRow = hasFooterRow();
        if (hadFooterRow != hasFooterRow) {
            if (hadFooterRow) {
                notifyItemRemoved(super.getItemCount());
            } else {
                notifyItemInserted(super.getItemCount());
            }
        } else if (hasFooterRow && previousState != isVisible) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    private boolean hasFooterRow() {
        return isFooterProgressIndicatorVisible;
    }

    private void setFullSpan(@NonNull View rootView, boolean fullSpan) {
        final ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
        if (layoutParams != null) {
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                final StaggeredGridLayoutManager.LayoutParams staggeredGridLayoutParams = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                staggeredGridLayoutParams.setFullSpan(fullSpan);
                rootView.setLayoutParams(staggeredGridLayoutParams);
            } else {
                throw new IllegalArgumentException(String.format("Unknown layout params class: %s", layoutParams.getClass().getName()));
            }
        }
    }
}
