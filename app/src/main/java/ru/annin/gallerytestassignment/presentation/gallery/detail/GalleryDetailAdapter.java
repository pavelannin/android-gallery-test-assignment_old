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

import android.arch.paging.PagedListAdapter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import ru.annin.gallerytestassignment.R;
import ru.annin.gallerytestassignment.data.entity.Photo;
import ru.annin.gallerytestassignment.presentation.common.viewholder.ItemProgressIndicatorViewHolder;
import ru.annin.gallerytestassignment.utils.ViewUtils;

/**
 * @author Pavel Annin.
 */
public class GalleryDetailAdapter extends PagedListAdapter<Photo, RecyclerView.ViewHolder> {

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

    interface OnPhotoListener {
        void onViewLoaded(@NonNull View view, int position);
    }


    private boolean isFooterProgressIndicatorVisible = false;
    private OnPhotoListener listener;


    GalleryDetailAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_ITEM: {
                final View rootView = inflater.inflate(R.layout.item_gallery_detail, parent, false);
                return new ItemPhotoDetailViewHolder(rootView);
            }
            case TYPE_PROGRESS_INDICATOR: {
                final View rootView = inflater.inflate(R.layout.item_progress_indicator_pager, parent, false);
                return new ItemProgressIndicatorViewHolder(rootView);
            }
        }
        throw new IllegalArgumentException(String.format("Unknown view type: %d", viewType));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemPhotoDetailViewHolder) {
            final ItemPhotoDetailViewHolder viewHolder = (ItemPhotoDetailViewHolder) holder;
            final Photo item = getItem(position);
            if (item != null) {
                ViewCompat.setTransitionName(viewHolder.getSharedElement(),
                        ViewUtils.getTransitionName(viewHolder.getSharedElement(), item.getId()));
                viewHolder.setPhotoRequestListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onViewLoaded(viewHolder.getSharedElement(), viewHolder.getAdapterPosition());
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onViewLoaded(viewHolder.getSharedElement(), viewHolder.getAdapterPosition());
                        }
                        return false;
                    }
                });
                viewHolder.bindToPhoto(item);
            }
        } else if (holder instanceof ItemProgressIndicatorViewHolder) {
            final ItemProgressIndicatorViewHolder viewHolder = (ItemProgressIndicatorViewHolder) holder;
            viewHolder.toggleVisible(isFooterProgressIndicatorVisible);
        } else {
            throw new IllegalArgumentException(String.format("Unknown holder class: %s", holder.getClass().getName()));
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (hasProgressColumn() ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (hasProgressColumn() && position == getItemCount() - 1) {
            return TYPE_PROGRESS_INDICATOR;
        } else {
            return TYPE_ITEM;
        }
    }

    public void toggleFooterProgressIndicatorVisible(boolean isVisible) {
        final boolean previousState = isFooterProgressIndicatorVisible;
        final boolean hadProgressColumn = hasProgressColumn();
        isFooterProgressIndicatorVisible = isVisible;
        final boolean hasProgressColumn = hasProgressColumn();
        if (hadProgressColumn != hasProgressColumn) {
            if (hadProgressColumn) {
                notifyItemRemoved(super.getItemCount());
            } else {
                notifyItemInserted(super.getItemCount());
            }
        } else if (hasProgressColumn && previousState != isVisible) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public void setListener(OnPhotoListener listener) {
        this.listener = listener;
    }

    private boolean hasProgressColumn() {
        return isFooterProgressIndicatorVisible;
    }
}